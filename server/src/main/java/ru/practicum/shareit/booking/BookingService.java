package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingCreateDto payload);

    BookingDto update();

    BookingDto approve(long userId, long bookingId, boolean approved);

    BookingDto getById(long userId, long bookingId);

    List<BookingDto> getByBookerAndState(long userId, BookingState state, int from, int size);

    List<BookingDto> getByOwnerAndState(long userId, BookingState state, int from, int size);
}
