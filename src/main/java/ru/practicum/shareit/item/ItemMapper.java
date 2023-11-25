package ru.practicum.shareit.item;

import lombok.Data;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.BookingForDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking) {
        BookingForDto lastBookingForDto = null;
        BookingForDto nextBookingForDto = null;
        if (lastBooking != null) {
            lastBookingForDto = new BookingForDto(lastBooking.getId(), lastBooking.getStart(), lastBooking.getEnd(), lastBooking.getItem(), lastBooking.getBooker().getId(), lastBooking.getStatus());
        }
        if (nextBooking != null) {
            nextBookingForDto = new BookingForDto(nextBooking.getId(), nextBooking.getStart(), nextBooking.getEnd(), nextBooking.getItem(), nextBooking.getBooker().getId(), nextBooking.getStatus());
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .lastBooking(lastBookingForDto)
                .nextBooking(nextBookingForDto)
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .build();
    }

}
