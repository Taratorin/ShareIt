package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto saveBooking(BookingDtoCreate bookingDtoCreate, long bookerId) {
        User booker = findUserById(bookerId);
        Item item = findItemById(bookingDtoCreate.getItemId());
        if (booker == item.getOwner()) {
            throw new NotFoundException("Запрещено бронировать свои вещи.");
        }
        if (item.getIsAvailable()) {
            Booking booking = BookingMapper.toBooking(bookingDtoCreate);
            booking.setBooker(booker);
            booking.setStatus(BookingStatus.WAITING);
            booking.setItem(item);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }
    }

    @Override
    public BookingDto bookingApprove(long bookingId, long userId, String approved) {
        Booking booking = findBookingById(bookingId);
        Item item = booking.getItem();
        if (item.getOwner().getId() == userId) {
            if (approved.equals("true")) {
                if (booking.getStatus() == BookingStatus.APPROVED) {
                    throw new BadRequestException("Статус бронирования уже APPROVED.");
                }
                booking.setStatus(BookingStatus.APPROVED);
            } else if (approved.equals("false")) {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new NotFoundException("Это вещь другого пользователя.");
        }
    }

    @Override
    public BookingDto findBookingDtoById(long bookingId, long userId) {
        if (isValidRequest(bookingId, userId)) {
            return BookingMapper.toBookingDto(findBookingById(bookingId));
        } else {
            throw new NotFoundException("Получение данных может быть выполнено либо автором бронирования," +
                    " либо владельцем вещи, к которой относится бронирование.");
        }
    }

    @Override
    public List<BookingDto> findBookingDto(long userId, BookingState state) {
        User user = findUserById(userId);
        List<Booking> bookings = null;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerOrderByIdDesc(user);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderById(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfterOrderByIdDesc(user, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerAndStatusOrderByIdDesc(user, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerAndStatusOrderByIdDesc(user, BookingStatus.REJECTED);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findBookingDtoForOwner(long userId, BookingState state) {
        User user = findUserById(userId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingForOwner(user);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingForOwner(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingForOwner(user, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingForOwner(user, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findWaitingBookingForOwner(user);
                break;
            case REJECTED:
                bookings = bookingRepository.findRejectedBookingForOwner(user);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена."));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не существует."));
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не существует."));
    }

    private boolean isValidRequest(long bookingId, long userId) {
        Booking booking = findBookingById(bookingId);
        return (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId);
    }

}