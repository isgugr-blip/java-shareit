package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemCreateDTO payload);

    ItemDto update(long userId, long itemId, ItemCreateDTO payload);

    ItemDto getById(long id);

    List<ItemDto> getByUser(long userId);

    List<ItemDto> getByQuery(String text);
}
