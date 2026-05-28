package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public Item createToEntity(Long ownerId, ItemCreateDTO payload) {
        if(payload == null) {
            return null;
        }

        return new Item(null, payload.getName(), payload.getDescription(), payload.getAvailable(), ownerId);
    }

    public Item updateToEntity(long ownerId, long itemId, Item current, ItemCreateDTO payload) {
        if (payload == null) {
            return current;
        }

        return new Item(
                itemId,
                payload.getName() != null ? payload.getName() : current.getName(),
                payload.getDescription() != null ? payload.getDescription() : current.getDescription(),
                payload.getAvailable() != null ? payload.getAvailable() : current.getAvailable(),
                ownerId
        );
    }
}
