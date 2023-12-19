package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class BookingDtoCreate {
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDtoCreate that = (BookingDtoCreate) o;
        return itemId == that.itemId && Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, itemId);
    }
}
