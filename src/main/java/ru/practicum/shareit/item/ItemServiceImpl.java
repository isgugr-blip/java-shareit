package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemInMemoryStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserInMemoryStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemInMemoryStorage itemInMemoryStorage;
    private final UserInMemoryStorage userInMemoryStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(long userId, ItemCreateDTO payload) {
        User user = userInMemoryStorage.getById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        Item item = itemMapper.createToEntity(user, payload);
        return itemMapper.toDto(itemInMemoryStorage.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemCreateDTO payload) {
        User user = userInMemoryStorage.getById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        Item currentItem = itemInMemoryStorage.getById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));
        if (!currentItem.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException("Предмет не найден!");
        }

        if (payload.getName() != null && payload.getName().trim().isEmpty()) {
            throw new ConditionsNotMetException("Имя не может быть пустым!");
        }
        if (payload.getDescription() != null && payload.getDescription().trim().isEmpty()) {
            throw new ConditionsNotMetException("Описание не может быть пустым!");
        }
        Item updatedItem = itemMapper.updateToEntity(user, itemId, currentItem, payload);
        return itemMapper.toDto(itemInMemoryStorage.save(updatedItem));
    }

    @Override
    public ItemDto getById(long id) {
        return itemMapper.toDto(
                itemInMemoryStorage.getById(id).orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"))
        );
    }

    @Override
    public List<ItemDto> getByUser(long userId) {
        return itemInMemoryStorage.getByUser(userId).stream().map(itemMapper::toDto).toList();
    }

    @Override
    public List<ItemDto> getByQuery(String text) {
        return itemInMemoryStorage.getByQuery(text).stream().map(itemMapper::toDto).toList();
    }
}
