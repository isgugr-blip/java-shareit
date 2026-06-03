package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemCreateDto payload);

    ItemDto update(long userId, long itemId, ItemUpdateDto payload);

    ItemDto getById(long userId, long itemId);

    List<ItemDto> getByUser(long userId);

    List<ItemDto> getByQuery(String text);

    CommentDto addComment(long userId, long itemId, CommentCreateDto payload);
}
