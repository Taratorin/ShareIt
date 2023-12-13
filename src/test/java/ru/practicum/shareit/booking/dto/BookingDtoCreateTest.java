package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoCreateTest {

    @Autowired
    private JacksonTester<BookingDtoCreate> tester;

    @Test
    public void test() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();

        JsonContent<BookingDtoCreate> result = tester.write(bookingDtoCreate);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoCreate.getStart().format(ISO_LOCAL_DATE_TIME));
    }

    private BookingDtoCreate getBookingDtoCreate() {
        return BookingDtoCreate.builder()
                .start(LocalDateTime.of(2023,12,1,12,0))
                .end(LocalDateTime.of(2023,12,2,12,0))
                .build();
    }
}