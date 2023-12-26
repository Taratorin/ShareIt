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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
class BookingDtoCreateTest {

    @Autowired
    private JacksonTester<BookingDtoCreate> tester;

    @Test
    public void writingStartAndEndInJson() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();

        JsonContent<BookingDtoCreate> result = tester.write(bookingDtoCreate);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoCreate.getStart().format(ISO_LOCAL_DATE_TIME));
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoCreate.getEnd().format(ISO_LOCAL_DATE_TIME));
    }

    @Test
    public void writingStartAndEndInJson_whenResultNotEqual() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();

        JsonContent<BookingDtoCreate> result = tester.write(bookingDtoCreate);
        assertThat(result).hasJsonPath("$.start");
        bookingDtoCreate.setStart(bookingDtoCreate.getStart().plusDays(1));
        bookingDtoCreate.setEnd(bookingDtoCreate.getEnd().plusDays(1));
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isNotEqualTo(bookingDtoCreate.getStart().format(ISO_LOCAL_DATE_TIME));
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isNotEqualTo(bookingDtoCreate.getEnd().format(ISO_LOCAL_DATE_TIME));
    }

    @Test
    public void writingItemIdInJson() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        JsonContent<BookingDtoCreate> result = tester.write(bookingDtoCreate);
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo((int) bookingDtoCreate.getItemId());
    }

    @Test
    public void writingItemIdInJson_whenItemIdNotEquals() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        JsonContent<BookingDtoCreate> result = tester.write(bookingDtoCreate);
        bookingDtoCreate.setItemId(10);
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isNotEqualTo((int) bookingDtoCreate.getItemId());
    }

    @Test
    public void readingJsonInObject_whenObjectsAreEqual() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        String bookingDtoCreatecontent = "{\n" +
                "    \"itemId\": 1,\n" +
                "    \"start\": \"2023-12-01T12:00:00\",\n" +
                "    \"end\": \"2023-12-02T12:00:00\"\n" +
                "}";
        BookingDtoCreate result = tester.parseObject(bookingDtoCreatecontent);
        assertEquals(result, bookingDtoCreate);
    }

    @Test
    public void readingJsonInObject_whenStartNotEqual() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        String bookingDtoCreatecontent = "{\n" +
                "    \"itemId\": 1,\n" +
                "    \"start\": \"2023-12-10T12:00:00\",\n" +
                "    \"end\": \"2023-12-02T12:00:00\"\n" +
                "}";
        BookingDtoCreate result = tester.parseObject(bookingDtoCreatecontent);
        assertNotEquals(result, bookingDtoCreate);
    }

    @Test
    public void readingJsonInObject_whenEndNotEqual() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        String bookingDtoCreatecontent = "{\n" +
                "    \"itemId\": 1,\n" +
                "    \"start\": \"2023-12-01T12:00:00\",\n" +
                "    \"end\": \"2023-12-20T12:00:00\"\n" +
                "}";
        BookingDtoCreate result = tester.parseObject(bookingDtoCreatecontent);
        assertNotEquals(result, bookingDtoCreate);
    }

    @Test
    public void readingJsonInObject_whenItemIdNotEqual() throws IOException {
        BookingDtoCreate bookingDtoCreate = getBookingDtoCreate();
        String bookingDtoCreatecontent = "{\n" +
                "    \"itemId\": 10,\n" +
                "    \"start\": \"2023-12-01T12:00:00\",\n" +
                "    \"end\": \"2023-12-02T12:00:00\"\n" +
                "}";
        BookingDtoCreate result = tester.parseObject(bookingDtoCreatecontent);
        assertNotEquals(result, bookingDtoCreate);
    }

    private BookingDtoCreate getBookingDtoCreate() {
        return BookingDtoCreate.builder()
                .start(LocalDateTime.of(2023,12,1,12,0))
                .end(LocalDateTime.of(2023,12,2,12,0))
                .itemId(1)
                .build();
    }
}