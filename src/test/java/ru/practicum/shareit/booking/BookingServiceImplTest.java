package ru.practicum.shareit.booking;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl service;

    @Test
    void saveBooking_whenOwnerCreateBooking_returnNotFoundException() {
        User user = getUsers().get(0);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getItem()));

        assertThrows(NotFoundException.class,
                () -> service.saveBooking(getBookingDtoCreate(), 1));
    }

    @Test
    void saveBooking_whenUserRepositoryReturnEmptyOptional_returnNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.saveBooking(getBookingDtoCreate(), 1));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenItemRepositoryReturnEmptyOptional_returnNotFoundException() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.saveBooking(getBookingDtoCreate(), 1));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void saveBooking_whenItemIsNotAvailable_returnBadRequestException() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        Item item = getItem();
        item.setIsAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class,
                () -> service.saveBooking(getBookingDtoCreate(), 1));
    }

    @Test
    void saveBooking_whenAllIsValid() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        Item item = getItem();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any())).thenReturn(getBooking());

        BookingDto bookingDto = service.saveBooking(getBookingDtoCreate(), 2);
        assertThat(bookingDto, equalTo(getBookingDto()));
        verify(bookingRepository).save(any());
    }

    @Test
    void bookingApprove_whenOwnerAnotherUser_thenNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(getBooking()));

        assertThrows(NotFoundException.class,
                () -> service.bookingApprove(1L, 10L, "true"));
    }

    @Test
    void bookingApprove_whenBookingRepositoryReturnEmptyOptional_thenNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.bookingApprove(1L, 10L, "true"));
    }

    @Test
    void bookingApprove_whenStatusIsRejected() {
        Booking booking = getBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto bookingDto = service.bookingApprove(1L, 1L, "false");

        assertThat(bookingDto, equalTo(BookingMapper.toBookingDto(booking)));
        verify(bookingRepository).save(any());
    }

    @Test
    void bookingApprove_whenStatusIsAlreadyApproved_thenBadRequestException() {
        Booking booking = getBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class,
                () -> service.bookingApprove(1L, 1L, "true"));
    }

    @Test
    void bookingApprove_whenStatusIsApproved() {
        Booking booking = getBooking();
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking bookingApproved = getBooking();
        when(bookingRepository.save(any())).thenReturn(bookingApproved);

        BookingDto bookingDto = service.bookingApprove(1L, 1L, "true");

        assertThat(bookingDto, equalTo(BookingMapper.toBookingDto(booking)));
        verify(bookingRepository).save(any());
    }

    @Test
    void findBookingDtoById_whenNotValidId_thenNotFoundException() {
        Booking booking = getBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> service.findBookingDtoById(1L, 10L));
    }

    @Test
    void findBookingDtoById_whenAllIsValid() {
        Booking booking = getBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = service.findBookingDtoById(1L, 1L);
        assertThat(bookingDto, equalTo(getBookingDto()));
    }

    @Test
    void findBookingDto_whenStateAll() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDto(1, BookingState.ALL, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    @Test
    void findBookingDto_whenStateCurrent() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDto(1, BookingState.CURRENT, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    @Test
    void findBookingDto_whenStatePast() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDto(1, BookingState.PAST, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    @Test
    void findBookingDto_whenStateFuture() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDto(1, BookingState.FUTURE, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    @Test
    void findBookingDto_whenStateWaiting() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDto(1, BookingState.WAITING, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    @Test
    void findBookingDto_whenStateRejected() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDto(1, BookingState.REJECTED, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    @Test
    void findBookingDtoForOwner() {
        User user = getUsers().get(1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        List<Booking> bookingsList = new ArrayList<>(List.of(getBooking()));
        PageImpl<Booking> bookings = new PageImpl<>(bookingsList);
        when(bookingRepository.findAll((Predicate) any(), (Pageable) any())).thenReturn(bookings);

        List<BookingDto> bookingDto = service.findBookingDtoForOwner(1, BookingState.ALL, 1, 10);
        assertThat(bookingDto, equalTo(List.of(getBookingDto())));
    }

    private List<User> getUsers() {
        return new ArrayList<>(
                List.of(
                        User.builder()
                                .id(1)
                                .name("Имя пользователя 1")
                                .email("email1@email.com")
                                .build(),
                        User.builder()
                                .id(2)
                                .name("Имя пользователя 2")
                                .email("email2@email.com")
                                .build()
                )
        );
    }

    private Item getItem() {
        return Item.builder()
                .name("Моя новая вещь")
                .description("Описание моей новой вещи")
                .isAvailable(true)
                .owner(getUsers().get(0))
                .build();
    }

    private BookingDtoCreate getBookingDtoCreate() {
        return BookingDtoCreate.builder()
                .start(LocalDateTime.of(2023,12,1,12,0))
                .end(LocalDateTime.of(2023,12,2,12,0))
                .build();
    }

    private Booking getBooking() {
        Booking booking = BookingMapper.toBooking(getBookingDtoCreate());
        booking.setId(1);
        booking.setItem(getItem());
        booking.setBooker(getUsers().get(1));
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    private BookingDto getBookingDto() {
        return BookingMapper.toBookingDto(getBooking());
    }
}