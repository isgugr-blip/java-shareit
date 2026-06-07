package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest payload) {
        return toDto(payload, List.of());
    }

    public ItemRequestDto toDto(ItemRequest payload, List<Item> items) {
        if (payload == null) {
            return null;
        }
        List<ItemRequestResponseDto> itemDtos = items == null ? new ArrayList<>() : items.stream()
                .map(item -> new ItemRequestResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getOwner() != null ? item.getOwner().getId() : null))
                .toList();
        return new ItemRequestDto(
                payload.getId(),
                payload.getDescription(),
                payload.getCreated(),
                itemDtos
        );
    }

    public ItemRequest createToEntity(User author, LocalDateTime created, ItemRequestCreateDto payload) {
        if (payload == null) {
            return null;
        }
        return new ItemRequest(null, payload.getDescription(), author, created);
    }
}
