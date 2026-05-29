package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemInMemoryStorage {
    private final HashMap<Long, Item> items = new HashMap<>();

    public Item save(Item payload) {
        if (payload.getId() == null) {
            payload.setId(Utils.getNextId(items));
        }
        items.put(payload.getId(), payload);
        return payload;
    }

    public void delete(Long id) {
        items.remove(id);
    }

    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> getByUser(long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId)).toList();
    }

    public List<Item> getByQuery(String query) {
        return items.values().stream().filter(item -> {
            if (query != null && !query.trim().isEmpty()) {
                return item.getAvailable() && (
                        item.getName().toLowerCase().contains(query.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(query.toLowerCase())
                );
            } else {
                return false;
            }
        }).toList();
    }
}
