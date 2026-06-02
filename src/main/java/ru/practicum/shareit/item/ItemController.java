package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemCreateDto payload) {
        return itemService.create(userId, payload);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,  @RequestBody ItemCreateDto payload) {
        return itemService.update(userId, itemId, payload);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public List<ItemDto> getByUser(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.getByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getByQuery(@RequestParam String text) {
        return itemService.getByQuery(text);
    }
}
