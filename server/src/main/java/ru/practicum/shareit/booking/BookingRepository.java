package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    List<Booking> findAllByItemAndStatusOrderByStartDesc(Item item, BookingStatus status);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatus status);

    Boolean existsByItemAndBookerAndStatusAndEndLessThanEqual(Item item, User user, BookingStatus status, LocalDateTime end);

    Boolean existsByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);

}
