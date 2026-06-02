package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(long userId, ItemCreateDto payload) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        Item item = itemMapper.createToEntity(user, payload);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemCreateDto payload) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        Item currentItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));
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
        return itemMapper.toDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDto getById(long id) {
        return itemMapper.toDto(itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Предмет не найден!")));
    }

    @Override
    public List<ItemDto> getByUser(long userId) {
        Booking lastBooking = bookingRepository.findFirstByEndDateIsBeforeAndStatusAndItem_Owner_Id(LocalDateTime.now(), BookingStatus.APPROVED, userId).orElse(null);
        Booking nextBooking = bookingRepository.findFirstByStartDateIsAfterAndStatusAndItem_Owner_Id(LocalDateTime.now(), BookingStatus.APPROVED, userId).orElse(null);
        return itemRepository.getByOwner_Id(userId).stream().map(itemMapper::toDto).toList();
    }

    @Override
    public List<ItemDto> getByQuery(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByAvailabilityAndQueryInNameOrDescription(text).stream().map(itemMapper::toDto).toList();
    }
}
