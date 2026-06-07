package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializesBookingWithCustomDatePattern() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 2, 3, 4, 5);
        LocalDateTime end = LocalDateTime.of(2025, 1, 3, 3, 4, 5);
        BookingDto dto = new BookingDto(
                7L,
                start,
                end,
                new BookingDto.BookingBookerDto(11L),
                new BookingDto.BookingItemDto(22L, "Drill"),
                BookingStatus.WAITING
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-02T03:04:05");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-03T03:04:05");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(22);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
