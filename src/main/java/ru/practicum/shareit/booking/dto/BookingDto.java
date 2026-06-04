package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
    private BookingBookerDto booker;
    private BookingItemDto item;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    public static class BookingBookerDto {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    public static class BookingItemDto {
        private Long id;
        private String name;
    }
}
