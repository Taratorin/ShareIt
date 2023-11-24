package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto saveBooking(BookingDto bookingDto, long bookerId);

    BookingDto bookingApprove(long bookingId, long userId, String approved);

    BookingDto findBookingDtoById(long bookingId, long userId);

    List<BookingDto> findBookingDto(long userId, String state);

    List<BookingDto> findBookingDtoForOwner(long userId, String state);
}
