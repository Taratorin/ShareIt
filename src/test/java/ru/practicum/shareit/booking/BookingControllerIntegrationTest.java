package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;


    @SneakyThrows
    @Test
    void createBooking_whenStartIsNull_thenBadRequest() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        bookingDtoCreate.setStart(null);
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).saveBooking(bookingDtoCreate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void createBooking_whenEndIsNull_thenBadRequest() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        bookingDtoCreate.setEnd(null);
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).saveBooking(bookingDtoCreate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void createBooking_whenStartEqualsEnd_thenBadRequest() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        bookingDtoCreate.setEnd(bookingDtoCreate.getStart());
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).saveBooking(bookingDtoCreate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void createBooking_whenItemIdIsNotValid_thenBadRequest() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        bookingDtoCreate.setItemId(-10L);
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).saveBooking(bookingDtoCreate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookerIdIsNotValid() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .header("X-Sharer-User-Id", -1L)
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).saveBooking(bookingDtoCreate, eq(anyLong()));
    }

    @SneakyThrows
    @Test
    void createBooking_whenNotFoundExceptionThrown() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        long bookerId = 1L;
        when(bookingService.saveBooking(bookingDtoCreate, bookerId)).thenThrow(new NotFoundException(""));
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
        verify(bookingService, times(1)).saveBooking(bookingDtoCreate, bookerId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBadRequestExceptionThrown() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        long bookerId = 1L;
        when(bookingService.saveBooking(bookingDtoCreate, bookerId)).thenThrow(new BadRequestException(""));
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(bookingService, times(1)).saveBooking(bookingDtoCreate, bookerId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenAllIsValid() {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        long bookerId = 1L;
        when(bookingService.saveBooking(bookingDtoCreate, bookerId)).thenReturn(getBookingDto());
        String result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, times(1)).saveBooking(bookingDtoCreate, bookerId);
        assertThat(result, equalTo(objectMapper.writeValueAsString(getBookingDto())));
    }

    @SneakyThrows
    @Test
    void bookingApprove() {

    }

    @SneakyThrows
    @Test
    void findBookingDto() {
    }

    @SneakyThrows
    @Test
    void testFindBookingDto() {
    }

    @SneakyThrows
    @Test
    void findBookingDtoForOwner() {
    }

    private BookingDtoCreate getBookingDtoCreate() {
        return BookingDtoCreate.builder()
                .start(LocalDateTime.of(2024, 12, 1, 12, 0))
                .end(LocalDateTime.of(2024, 12, 2, 12, 0))
                .itemId(1)
                .build();
    }

    private BookingDto getBookingDto() {
        return BookingMapper.toBookingDto(getBooking());
    }

    private Booking getBooking() {
        Booking booking = BookingMapper.toBooking(getBookingDtoCreate());
        booking.setId(1);
        booking.setItem(getItem());
        booking.setBooker(getUsers().get(1));
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    private List<User> getUsers() {
        return new ArrayList<>(
                List.of(
                        User.builder()
                                .id(1)
                                .name("Name of User 1")
                                .email("email1@email.com")
                                .build(),
                        User.builder()
                                .id(2)
                                .name("Name of User 2")
                                .email("email2@email.com")
                                .build()
                )
        );
    }

    private Item getItem() {
        return Item.builder()
                .id(1)
                .name("My new thing")
                .description("Description of my new thing")
                .isAvailable(true)
                .owner(getUsers().get(0))
                .build();
    }
}