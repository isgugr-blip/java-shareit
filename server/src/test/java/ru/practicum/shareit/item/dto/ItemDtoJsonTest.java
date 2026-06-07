package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serializesItemWithBookingsAndComments() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 5, 6, 7, 8, 9);
        ItemDto dto = new ItemDto(
                1L,
                "Drill",
                "Cordless",
                true,
                10L,
                new ItemBookingDto(100L, created.minusDays(1), created.minusHours(1), 11L),
                new ItemBookingDto(101L, created.plusHours(1), created.plusDays(1), 12L),
                List.of(new CommentDto(50L, "great", "Alice", created))
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2025-05-06T07:08:09");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2025-05-05T07:08:09");
    }
}
