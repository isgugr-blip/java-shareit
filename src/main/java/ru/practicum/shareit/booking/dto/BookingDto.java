package ru.practicum.shareit.booking.dto;

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
    private LocalDateTime start;
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
