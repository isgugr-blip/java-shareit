package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(long userId, ItemCreateDTO payload);

    Item update(long userId, long itemId, ItemCreateDTO payload);

    Item getById(long id);

    List<Item> getByUser(long userId);

    List<Item> getByQuery(String text);
}
