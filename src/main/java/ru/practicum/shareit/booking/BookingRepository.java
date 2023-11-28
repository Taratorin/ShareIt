package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByIdDesc(User user);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerAndStartAfterOrderByIdDesc(User user, LocalDateTime time);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderById(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndStatusOrderByIdDesc(User user, BookingStatus status);

    List<Booking> findAllByItemAndStatusOrderByStartDesc(Item item, BookingStatus status);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus status);

    Boolean existsByItemAndBookerAndStatusAndEndLessThanEqual(Item item, User user, BookingStatus status, LocalDateTime end);

    Boolean existsByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);

    @Query("select booking from Booking as booking join booking.item as it where it.owner = ?1 order by booking.id desc")
    List<Booking> findAllBookingForOwner(User user);

    @Query("select booking from Booking as booking join booking.item as it where it.owner = ?1 and booking.start > ?2 order by booking.id desc")
    List<Booking> findFutureBookingForOwner(User user, LocalDateTime time);

    @Query("select booking from Booking as booking join booking.item as it where it.owner = ?1 and booking.start < ?2 and booking.end > ?3 order by booking.id desc")
    List<Booking> findCurrentBookingForOwner(User user, LocalDateTime start, LocalDateTime end);

    @Query("select booking from Booking as booking join booking.item as it where it.owner = ?1 and booking.end < ?2 order by booking.id desc")
    List<Booking> findPastBookingForOwner(User user, LocalDateTime time);

    @Query("select booking from Booking as booking join booking.item as it where it.owner = ?1 and booking.status = 'WAITING' order by booking.id desc")
    List<Booking> findWaitingBookingForOwner(User user);

    @Query("select booking from Booking as booking join booking.item as it where it.owner = ?1 and booking.status = 'REJECTED' order by booking.id desc")
    List<Booking> findRejectedBookingForOwner(User user);

}
