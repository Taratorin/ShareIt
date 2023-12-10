package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class UserBookingDto {
    private long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserBookingDto that = (UserBookingDto) o;
        return id == that.id;
    }
}
