package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking payload) {
        if (payload == null) {
            return null;
        }

        return new BookingDto(
            payload.getId(),
                payload.getStartDate(),
                payload.getEndDate(),
                new BookingDto.BookingBookerDto(
                        payload.getBooker().getId()
                ),
                new BookingDto.BookingItemDto(
                        payload.getItem().getId(),
                        payload.getItem().getName()
                ),
                payload.getStatus()
        );
    }

    public Booking createToEntity(User user, Item item, BookingCreateDto payload) {
        if (payload == null) {
            return null;
        }
        return new Booking(
                null,
                payload.getStart(),
                payload.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }
}
