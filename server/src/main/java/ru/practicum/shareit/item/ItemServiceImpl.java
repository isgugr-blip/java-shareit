package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
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
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(long userId, ItemCreateDto payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        ItemRequest itemRequest = null;
        if (payload.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(payload.getRequestId()).orElseThrow(ItemRequestNotFoundException::new);
        }
        Item item = itemMapper.createToEntity(
                user,
                itemRequest,
                payload
        );
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemUpdateDto payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        Item currentItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));
        if (!currentItem.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException("Предмет не найден!");
        }

        Item updatedItem = itemMapper.updateToEntity(user, itemId, currentItem, payload);
        return itemMapper.toDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));
        List<CommentDto> comments = commentRepository.findAllByItem_Id(itemId).stream()
                .map(commentMapper::toDto)
                .toList();

        boolean isOwner = item.getOwner() != null && item.getOwner().getId() == userId;
        Booking last = null;
        Booking next = null;
        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();
            last = bookingRepository
                    .findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateDesc(itemId, BookingStatus.APPROVED, now)
                    .orElse(null);
            next = bookingRepository
                    .findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(itemId, BookingStatus.APPROVED, now)
                    .orElse(null);
        }
        return itemMapper.toDto(item, last, next, comments);
    }

    @Override
    public List<ItemDto> getByUser(long userId) {
        List<Item> items = itemRepository.getByOwner_Id(userId);
        LocalDateTime now = LocalDateTime.now();
        List<ItemDto> result = new ArrayList<>(items.size());
        for (Item item : items) {
            Booking last = bookingRepository
                    .findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateDesc(item.getId(), BookingStatus.APPROVED, now)
                    .orElse(null);
            Booking next = bookingRepository
                    .findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(item.getId(), BookingStatus.APPROVED, now)
                    .orElse(null);
            List<CommentDto> comments = commentRepository.findAllByItem_Id(item.getId()).stream()
                    .map(commentMapper::toDto)
                    .toList();
            result.add(itemMapper.toDto(item, last, next, comments));
        }
        return result;
    }

    @Override
    public List<ItemDto> getByQuery(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByAvailabilityAndQueryInNameOrDescription(text).stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentCreateDto payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет не найден!"));

        boolean rented = bookingRepository.existsByBooker_IdAndItem_IdAndEndDateIsBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (!rented) {
            throw new ConditionsNotMetException("Комментарий может оставить только пользователь, бравший вещь в аренду.");
        }

        Comment comment = commentMapper.toEntity(user, item, payload);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}
