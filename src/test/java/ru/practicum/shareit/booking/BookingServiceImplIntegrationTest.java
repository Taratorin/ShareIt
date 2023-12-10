package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private BookingServiceImpl service;
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DirtiesContext
    void findBookingDto() {
        List<User> users = easyRandom.objects(User.class, 3).collect(Collectors.toList());
        User user1 = userRepository.save(users.get(0));
        User user2 = userRepository.save(users.get(1));
        User user3 = userRepository.save(users.get(2));
        users.clear();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        List<ItemRequest> itemRequests = easyRandom.objects(ItemRequest.class, 3).collect(Collectors.toList());
        itemRequests.get(0).setRequestor(user1);
        itemRequests.get(1).setRequestor(user2);
        itemRequests.get(2).setRequestor(user3);
        ItemRequest itemRequest1 = requestRepository.save(itemRequests.get(0));
        ItemRequest itemRequest2 = requestRepository.save(itemRequests.get(1));
        ItemRequest itemRequest3 = requestRepository.save(itemRequests.get(2));
        itemRequests.clear();
        itemRequests.add(itemRequest1);
        itemRequests.add(itemRequest2);
        itemRequests.add(itemRequest3);
        List<Item> items = easyRandom.objects(Item.class, 3).collect(Collectors.toList());
        for (int i = 0; i < itemRequests.size(); i++) {
            itemRequests.get(i).setRequestor(users.get(i));
            items.get(i).setOwner(users.get(i));
            items.get(i).setRequest(itemRequests.get(i));
        }
        Item item1 = itemRepository.save(items.get(0));
        Item item2 = itemRepository.save(items.get(1));
        Item item3 = itemRepository.save(items.get(2));
        items.clear();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        List<Booking> bookings = easyRandom.objects(Booking.class, 3).collect(Collectors.toList());
        for (int i = 0; i < bookings.size(); i++) {
            bookings.get(i).setItem(items.get(i));
            bookings.get(i).setBooker(users.get(i));
        }
        Booking booking1 = bookingRepository.save(bookings.get(0));
        Booking booking2 = bookingRepository.save(bookings.get(1));
        Booking booking3 = bookingRepository.save(bookings.get(2));
        bookings.clear();
        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);

        BookingDto bookingDtoExpected = BookingMapper.toBookingDto(booking1);
        List<BookingDto> bookingDto = service.findBookingDto(user1.getId(), BookingState.ALL, 1, 10);
        assertThat(bookingDto.get(0).getId(), equalTo(bookingDtoExpected.getId()));
        assertThat(bookingDto.get(0).getItem(), equalTo(bookingDtoExpected.getItem()));
        assertThat(bookingDto.get(0).getStatus(), equalTo(bookingDtoExpected.getStatus()));
        assertThat(bookingDto.get(0).getBooker(), equalTo(bookingDtoExpected.getBooker()));
    }
}