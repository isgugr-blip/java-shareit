package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemInMemoryStorage;
import ru.practicum.shareit.user.storage.UserInMemoryStorage;
import ru.practicum.shareit.utils.Utils;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemInMemoryStorage itemInMemoryStorage;
    private final UserInMemoryStorage userInMemoryStorage;
    private final ItemMapper itemMapper;

    @Override
    public Item create(long userId, ItemCreateDTO payload) {
        if(!userInMemoryStorage.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден!");
        }
        Item item = itemMapper.createToEntity(userId, payload);
        return itemInMemoryStorage.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemCreateDTO payload) {
        if(!userInMemoryStorage.existsById(userId)) {
            throw new UserNotFoundException("Пользователь не найден!");
        } else {
            Item currentItem = itemInMemoryStorage.getById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));
            if (!currentItem.getOwner().equals(userId)) {
                throw new ItemNotFoundException("Предмет не найден!");
            }

            if(payload.getName() != null && payload.getName().trim().isEmpty()) {
                throw new ConditionsNotMetException("Имя не может быть пустым!");
            }
            if(payload.getDescription() != null && payload.getDescription().trim().isEmpty()) {
                throw new ConditionsNotMetException("Описание не может быть пустым!");
            }
            Item updatedItem = itemMapper.updateToEntity(userId, itemId, currentItem, payload);
            return itemInMemoryStorage.save(updatedItem);
        }
    }

    @Override
    public Item getById(long id) {
        return itemInMemoryStorage.getById(id).orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));
    }

    @Override
    public List<Item> getByUser(long userId) {
        return itemInMemoryStorage.getByUser(userId);
    }

    @Override
    public List<Item> getByQuery(String text) {
        return itemInMemoryStorage.getByQuery(text);
    }
}
