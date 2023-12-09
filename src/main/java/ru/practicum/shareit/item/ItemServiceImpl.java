package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreateUpdate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;


    @Override
    public ItemDtoCreateUpdate saveItem(ItemDtoCreateUpdate itemDtoCreateUpdate, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        Item item = ItemMapper.toItem(itemDtoCreateUpdate);
        item.setOwner(user);
        Long requestId = itemDtoCreateUpdate.getRequestId();
        if (requestId != null) {
            ItemRequest request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не существует."));
            item.setRequest(request);
        }
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDtoCreateUpdate(savedItem);
    }

    @Override
    public ItemDtoCreateUpdate updateItem(ItemDtoCreateUpdate itemDtoCreateUpdate, long itemId, long userId) {
        Item item = findItemById(itemId);
        if (item.getOwner().getId() == userId) {
            String name = itemDtoCreateUpdate.getName();
            String description = itemDtoCreateUpdate.getDescription();
            Boolean available = itemDtoCreateUpdate.getAvailable();
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setIsAvailable(available);
            }
            return ItemMapper.toItemDtoCreateUpdate(itemRepository.save(item));
        } else {
            throw new ForbiddenException("Запрещено изменять вещи другого пользователя.");
        }
    }

    @Override
    public ItemDto findItemDtoById(long itemId, long userId) {
        Item item = findItemById(itemId);
        List<CommentDto> commentDtos = getCommentDtos(item);
        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartDesc(item, BookingStatus.APPROVED);
            LocalDateTime now = LocalDateTime.now();
            Booking lastBooking = getLastOrNextBooking(bookings, true, now);
            Booking nextBooking = getLastOrNextBooking(bookings, false, now);
            return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentDtos);
        } else {
            return ItemMapper.toItemDto(item, commentDtos);
        }
    }

    @Override
    public List<ItemDto> findItemsByUserId(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId, pageable);
        Map<Item, List<Booking>> bookingMap = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(items, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem));
        Map<Item, List<Comment>> commentMap = commentRepository.findAllByItemIn(items).stream()
                .collect(Collectors.groupingBy(Comment::getItem));
        List<ItemDto> itemDtos = new ArrayList<>(items.size());
        LocalDateTime now = LocalDateTime.now();
        for (Item item : items) {
            List<Booking> bookings = bookingMap.getOrDefault(item, List.of());
            List<Comment> comments = commentMap.getOrDefault(item, List.of());
            List<CommentDto> commentDtos = comments.stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList());
            Booking lastBooking = getLastOrNextBooking(bookings, true, now);
            Booking nextBooking = getLastOrNextBooking(bookings, false, now);
            ItemDto itemDto = ItemMapper.toItemDto(item, lastBooking, nextBooking, commentDtos);
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        if (!text.isBlank()) {
            int pageNumber = from / size;
            Pageable pageable = PageRequest.of(pageNumber, size);
            List<Item> items = itemRepository
                    .findAllByIsAvailableIsTrueAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(text, text, pageable);
            Map<Item, List<Comment>> commentMap = commentRepository.findAllByItemIn(items).stream()
                    .collect(Collectors.groupingBy(Comment::getItem));
            return items.stream().map(item -> {
                List<CommentDto> commentDtos = commentMap.getOrDefault(item, List.of()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
                return ItemMapper.toItemDto(item, commentDtos);
            }).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public CommentDto saveComment(CommentDtoCreate commentDtoCreate, long itemId, long userId) {
        Item item = findItemById(itemId);
        User author = findUserById(userId);
        if (bookingRepository.existsByItemAndBookerAndStatus(item, author, BookingStatus.APPROVED)) {
            if (!bookingRepository
                    .existsByItemAndBookerAndStatusAndEndLessThanEqual(item, author, BookingStatus.APPROVED, LocalDateTime.now())) {
                throw new BadRequestException("Бронирование ещё не завершилось.");
            }
            Comment comment = CommentMapper.toComment(commentDtoCreate);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Пользователь не бронировал вещь с id=" + itemId);
        }
    }

    private Booking getLastOrNextBooking(List<Booking> bookings, boolean flag, LocalDateTime now) {
        if (flag) {
            Booking lastBooking = null;
            Optional<Booking> lastBookingOptional = bookings.stream()
                    .filter(x -> !x.getStart().isAfter(now))
                    .findFirst();
            if (lastBookingOptional.isPresent()) {
                lastBooking = lastBookingOptional.get();
            }
            return lastBooking;
        } else {
            Booking nextBooking = null;
            Optional<Booking> nextBookingOptional = bookings.stream()
                    .filter(x -> x.getStart().isAfter(now))
                    .reduce((first, second) -> second);
            if (nextBookingOptional.isPresent()) {
                nextBooking = nextBookingOptional.get();
            }
            return nextBooking;
        }
    }

    private List<CommentDto> getCommentDtos(Item item) {
        return commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
    }
}