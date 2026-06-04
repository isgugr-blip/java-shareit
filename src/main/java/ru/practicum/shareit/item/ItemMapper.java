package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toDto(Item payload) {
        return toDto(payload, null, null, new ArrayList<>());
    }

    public ItemDto toDto(Item payload, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        if (payload == null) {
            return null;
        }

        ItemDto dto = new ItemDto();
        dto.setId(payload.getId());
        dto.setName(payload.getName());
        dto.setDescription(payload.getDescription());
        dto.setAvailable(payload.getAvailable());
        dto.setOwner(payload.getOwner() != null ? payload.getOwner().getId() : null);
        dto.setLastBooking(toBookingDto(lastBooking));
        dto.setNextBooking(toBookingDto(nextBooking));
        dto.setComments(comments != null ? comments : new ArrayList<>());
        return dto;
    }

    private ItemBookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new ItemBookingDto(
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getBooker() != null ? booking.getBooker().getId() : null
        );
    }

    public Item createToEntity(User owner, ItemCreateDto payload) {
        if (payload == null) {
            return null;
        }

        return new Item(null, payload.getName(), payload.getDescription(), payload.getAvailable(), owner, null);
    }

    public Item updateToEntity(User owner, long itemId, Item current, ItemUpdateDto payload) {
        if (payload == null) {
            return current;
        }

        return new Item(
                itemId,
                payload.getName() != null ? payload.getName() : current.getName(),
                payload.getDescription() != null ? payload.getDescription() : current.getDescription(),
                payload.getAvailable() != null ? payload.getAvailable() : current.getAvailable(),
                owner,
                current.getRequestId()
        );
    }
}
