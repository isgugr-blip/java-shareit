package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemCreateDto payload);

    ItemDto update(long userId, long itemId, ItemCreateDto payload);

    ItemDto getById(long id);

    List<ItemDto> getByUser(long userId);

    List<ItemDto> getByQuery(String text);
}
