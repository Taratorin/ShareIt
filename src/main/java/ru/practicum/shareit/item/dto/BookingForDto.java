package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingForDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private long bookerId;
    private BookingStatus status;
}
