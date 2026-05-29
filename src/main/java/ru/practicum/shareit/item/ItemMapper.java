package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class ItemMapper {
    public ItemDto toDto(Item payload) {
        if (payload == null) {
            return null;
        }

        return new ItemDto(
                payload.getId(),
                payload.getName(),
                payload.getDescription(),
                payload.getAvailable(),
                payload.getOwner().getId(),
                null
        );
    }

    public Item createToEntity(User owner, ItemCreateDTO payload) {
        if (payload == null) {
            return null;
        }

        return new Item(null, payload.getName(), payload.getDescription(), payload.getAvailable(), owner);
    }

    public Item updateToEntity(User owner, long itemId, Item current, ItemCreateDTO payload) {
        if (payload == null) {
            return current;
        }

        return new Item(
                itemId,
                payload.getName() != null ? payload.getName() : current.getName(),
                payload.getDescription() != null ? payload.getDescription() : current.getDescription(),
                payload.getAvailable() != null ? payload.getAvailable() : current.getAvailable(),
                owner
        );
    }
}
