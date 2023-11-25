package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item), null);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = getItemById(itemId);
        List<CommentDto> commentDtos = getCommentDtos(item);
        if (item.getOwner().getId() == userId) {
            String name = itemDto.getName();
            String description = itemDto.getDescription();
            Boolean available = itemDto.getAvailable();
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setIsAvailable(available);
            }
            return ItemMapper.toItemDto(itemRepository.save(item), commentDtos);
        } else {
            throw new ForbiddenException("Запрещено изменять вещи другого пользователя.");
        }
    }

    @Override
    public ItemDto findItemDtoById(long itemId, long userId) {
        Item item = getItemById(itemId);
        List<CommentDto> commentDtos = getCommentDtos(item);
        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findAllByItem(item);
            Booking lastBooking = null;
            Booking nextBooking = null;

            Optional<Booking> lastBookingOptional = bookings.stream()
                    .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getStart)));
            if (lastBookingOptional.isPresent()) {
                lastBooking = lastBookingOptional.get();
            }

            Optional<Booking> nextBookingOptional = bookings.stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                    .min((Comparator.comparing(Booking::getStart)));
            if (nextBookingOptional.isPresent()) {
                nextBooking = nextBookingOptional.get();
            }
            return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentDtos);
        } else {
            return ItemMapper.toItemDto(item, commentDtos);
        }
    }


    @Override
    public List<ItemDto> findItemsByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);

        List<ItemDto> itemDtos = new ArrayList<>(items.size());

        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAllByItem(item);
            Booking lastBooking = null;
            Booking nextBooking = null;

            Optional<Booking> lastBookingOptional = bookings.stream()
                    .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                    .max((Comparator.comparing(Booking::getEnd)));
            if (lastBookingOptional.isPresent()) {
                lastBooking = lastBookingOptional.get();
            }

            Optional<Booking> nextBookingOptional = bookings.stream()
                    .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                    .min((Comparator.comparing(Booking::getStart)));
            if (nextBookingOptional.isPresent()) {
                nextBooking = nextBookingOptional.get();
            }
            List<CommentDto> commentDtos = getCommentDtos(item);

            ItemDto itemDto = ItemMapper.toItemDto(item, lastBooking, nextBooking, commentDtos);
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    private List<CommentDto> getCommentDtos(Item item) {
        return commentRepository.findAllByItem(item).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!text.isBlank()) {
            List<Item> items = itemRepository.findAllByDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(text);
            return items.stream()
                    .map(item -> {
                        List<CommentDto> commentDtos = getCommentDtos(item);
                        return ItemMapper.toItemDto(item, commentDtos);
                    })
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto, long itemId, long userId) {
        Item item = getItemById(itemId);
        User author = findUserById(userId);
        List<Booking> bookings = bookingRepository.findAllByItemAndBookerAndStatusOrderById(item, author, BookingStatus.APPROVED);
        if (!bookings.isEmpty()) {
            if (bookings.get(bookings.size() - 1).getEnd().isAfter(LocalDateTime.now())) {
                throw new BadRequestException("Бронирование ещё не завершилось.");
            }
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Пользователь не бронировал вещь с id=" + itemId);
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
    }
}
