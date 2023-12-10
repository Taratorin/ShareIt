package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.ArrayList;
import java.util.List;

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
        if (booker.getId() == item.getOwner().getId()) {
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
    public List<BookingDto> findBookingDto(long userId, BookingState state, int from, int size) {
        QBooking booking = QBooking.booking;
        BooleanExpression eq = booking.booker.id.eq(userId);
        return getBookingDtos(userId, state, from, size, eq);
    }

    @Override
    public List<BookingDto> findBookingDtoForOwner(long userId, BookingState state, int from, int size) {
        QBooking booking = QBooking.booking;
        BooleanExpression eq = booking.item.owner.id.eq(userId);
        return getBookingDtos(userId, state, from, size, eq);
    }

    private List<BookingDto> getBookingDtos(long userId, BookingState state, int from, int size, BooleanExpression eq) {
        findUserById(userId);
        int pageNumber = from / size;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(eq);
        if (!state.equals(BookingState.ALL)) {
            conditions.add(makeStateCondition(state));
        }
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of(pageNumber, size, sort);
        Page<Booking> bookings = bookingRepository.findAll(finalCondition, pageRequest);
        return BookingMapper.toBookingDto(bookings);
    }

    private BooleanExpression makeStateCondition(BookingState state) {
        switch (state) {
            case CURRENT:
                return QBooking.booking.start.before(LocalDateTime.now())
                        .and(QBooking.booking.end.after(LocalDateTime.now()));
            case PAST:
                return QBooking.booking.end.before(LocalDateTime.now());
            case FUTURE:
                return QBooking.booking.start.after(LocalDateTime.now());
            case WAITING:
                return QBooking.booking.status.eq(BookingStatus.WAITING);
            case REJECTED:
            default:
                return QBooking.booking.status.eq(BookingStatus.REJECTED);
        }
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