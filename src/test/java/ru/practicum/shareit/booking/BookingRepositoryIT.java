package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
class BookingRepositoryIT {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingRepositoryIT(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        userRepository.save(getUsers().get(0));
        userRepository.save(getUsers().get(1));
        itemRepository.save(getItems().get(0));
        itemRepository.save(getItems().get(1));
        bookingRepository.save(getBookings().get(0));
        bookingRepository.save(getBookings().get(1));
    }

    @Test
    void findAllByItemAndStatusOrderByStartDesc_whenStatusPresent() {
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartDesc(getItems().get(0), BookingStatus.APPROVED);
        assertThat(bookings, equalTo(List.of(getBookings().get(0))));
    }

    @Test
    void findAllByItemAndStatusOrderByStartDesc_whenStatusAbsent() {
        List<Booking> bookings = bookingRepository.findAllByItemAndStatusOrderByStartDesc(getItems().get(0), BookingStatus.REJECTED);
        assertThat(bookings, equalTo(List.of()));
    }

    @Test
    void findAllByItemInAndStatusOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(getItems(), BookingStatus.APPROVED);
        assertThat(bookings, equalTo(getBookings()));
    }

    @Test
    void existsByItemAndBookerAndStatusAndEndLessThanEqual() {
        Boolean isExists = bookingRepository.existsByItemAndBookerAndStatusAndEndLessThanEqual(getItems().get(1), getUsers().get(1), BookingStatus.APPROVED, LocalDateTime.now());
        assertThat(isExists, equalTo(true));
    }

    @Test
    void existsByItemAndBookerAndStatus() {
        Boolean isExists = bookingRepository.existsByItemAndBookerAndStatus(getItems().get(0), getUsers().get(0), BookingStatus.APPROVED);
        assertThat(isExists, equalTo(true));
    }


    private List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(
                Item.builder()
                        .id(1)
                        .name("Вещь №1")
                        .description("Описание вещи №1")
                        .isAvailable(true)
                        .owner(getUsers().get(0))
                        .build()
        );
        items.add(
                Item.builder()
                        .id(2)
                        .name("Вещь №2")
                        .description("Описание вещи №2")
                        .isAvailable(true)
                        .owner(getUsers().get(1))
                        .build()
        );
        return items;
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(1)
                .name("Владелец вещи №1")
                .email("email1@email.com")
                .build());
        users.add(User.builder()
                .id(2)
                .name("Владелец вещи №2")
                .email("email2@email.com")
                .build());
        return users;
    }

    private List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(
                Booking.builder()
                        .id(1)
                        .start(LocalDateTime.of(2023, 12, 12, 12, 0))
                        .end(LocalDateTime.of(2023, 12, 13, 12, 0))
                        .item(getItems().get(0))
                        .booker(getUsers().get(0))
                        .status(BookingStatus.APPROVED)
                        .build()
        );
        bookings.add(
                Booking.builder()
                        .id(2)
                        .start(LocalDateTime.of(2023, 12, 1, 12, 0))
                        .end(LocalDateTime.of(2023, 12, 2, 12, 0))
                        .item(getItems().get(1))
                        .booker(getUsers().get(1))
                        .status(BookingStatus.APPROVED)
                        .build()
        );
        return bookings;
    }
}