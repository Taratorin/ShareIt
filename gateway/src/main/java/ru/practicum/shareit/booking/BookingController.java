package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApprove(@PathVariable @Min(1) long bookingId,
                                     @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId,
                                     @RequestParam @NotBlank String approved) {
        log.info("Получен запрос PATCH /bookings — подтверждение запроса на бронирование");
        return bookingClient.bookingApprove(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingDtoForOwner(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId,
                                                   @RequestParam(defaultValue = "1") @Min(0) int from,
                                                   @RequestParam(defaultValue = "10") @Min(1) int size,
                                                   @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос GET /bookings/owner — получение списка бронирований для всех вещей текущего пользователя");
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        return bookingClient.findBookingDtoForOwner(userId, state, from, size);
    }
}