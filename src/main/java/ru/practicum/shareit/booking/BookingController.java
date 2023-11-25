package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import static ru.practicum.shareit.config.Constants.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader(X_SHARER_USER_ID) @Min(1) long bookerId) {
        log.info("Получен запрос POST /bookings — добавление бронирования");
        return bookingService.saveBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingApprove(@PathVariable long bookingId,
                                     @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId,
                                     @RequestParam @NotNull String approved) {
        log.info("Получен запрос PATCH /bookings — подтверждение запроса на бронирование");
        return bookingService.bookingApprove(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingDto(@PathVariable long bookingId,
                                     @RequestHeader(X_SHARER_USER_ID) @Min(1) long userId) {
        log.info("Получен запрос GET /bookings/{bookingId} — получение бронирования по id");
        return bookingService.findBookingDtoById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> findBookingDto(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос GET /bookings — получение списка всех бронирований текущего пользователя");
        return bookingService.findBookingDto(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingDtoForOwner(@RequestHeader(X_SHARER_USER_ID) @Min(1) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен запрос GET /bookings/owner — получение списка бронирований для всех вещей текущего пользователя");
        return bookingService.findBookingDtoForOwner(userId, state);
    }

}
