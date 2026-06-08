package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serializesRequestWithItems() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                3L,
                "need a drill",
                LocalDateTime.of(2025, 6, 7, 8, 9, 10),
                List.of(new ItemRequestResponseDto(99L, "Drill", 7L))
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("need a drill");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(99);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(7);
    }
}
