package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;
    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemCreateDTO payload) {
        return itemService.create(userId, payload);
    }
    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,  @RequestBody ItemCreateDTO payload) {
        return itemService.update(userId, itemId, payload);
    }
    @GetMapping("/{itemId}")
    public Item getById(@PathVariable long itemId) {
        return itemService.getById(itemId);
    }
    @GetMapping
    public List<Item> getByUser(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.getByUser(userId);
    }
    @GetMapping("/search")
    public List<Item> getByQuery(@RequestParam String text) {
        return itemService.getByQuery(text);
    }
}
