package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(BookingDtoCreate bookingDtoCreate, long bookerId);

    BookingDto bookingApprove(long bookingId, long userId, String approved);

    BookingDto findBookingDtoById(long bookingId, long userId);

    List<BookingDto> findBookingDto(long userId, BookingState state, int from, int size);

    List<BookingDto> findBookingDtoForOwner(long userId, BookingState bookingState, int from, int size);
}
