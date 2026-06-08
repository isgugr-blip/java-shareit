package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestCreateDto payload);

    List<ItemRequestDto> getAllByAuthor(long userId, int from, int size);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getById(long userId, long requestId);
}
