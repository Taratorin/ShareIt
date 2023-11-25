package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
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

    @Override
    public ItemDto saveItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = getItemById(itemId);
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
            return ItemMapper.toItemDto(itemRepository.save(item));
        } else {
            throw new ForbiddenException("Запрещено изменять вещи другого пользователя.");
        }
    }

    @Override
    public ItemDto findItemDtoById(long itemId, long userId) {
        Item item = getItemById(itemId);
        if (item.getOwner().getId() == userId) {
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

            return ItemMapper.toItemDto(item, lastBooking, nextBooking);
        } else {
            return ItemMapper.toItemDto(item);
        }
    }


    @Override
    public List<ItemDto> findItemsByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
        List<Item> items = itemRepository.findAllByOwnerId(userId);

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
            ItemDto itemDto = ItemMapper.toItemDto(item, lastBooking, nextBooking);
            itemDtos.add(itemDto);
        }
        
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (!text.isBlank()) {
            List<Item> items = itemRepository.findAllByDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(text);
            return items.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }
}
