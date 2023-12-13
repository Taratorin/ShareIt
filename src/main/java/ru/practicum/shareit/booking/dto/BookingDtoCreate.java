package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.valid.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingDtoCreate {
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    @Min(1)
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
