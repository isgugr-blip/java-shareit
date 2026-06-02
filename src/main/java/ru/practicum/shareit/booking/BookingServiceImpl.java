package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(long userId, BookingCreateDto payload) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Item item = itemRepository.findById(payload.getItemId()).orElseThrow(ItemNotFoundException::new);

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException();
        }

        return bookingMapper.toDto(
                bookingRepository.save(bookingMapper.createToEntity(user, item, payload))
        );
    }

    @Override
    public BookingDto update() {
        return null;
    }

    @Override
    public BookingDto approve(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findByIdAndItem_Owner_Id(bookingId, userId).orElse(null);

        if (booking == null) {
            if (!bookingRepository.existsById(bookingId)) {
                throw new BookingNotFoundException();
            }
            throw new ForbiddenException("Только владелец вещи может подтверждать бронирование!");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toDto(bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new));
        }

        throw new BookingNotFoundException();
    }

    @Override
    public List<BookingDto> getByBookerAndState(long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        Pageable page = PageRequest.of(from / size, size, sort);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT -> {
                return bookingRepository.findAllByStartDateIsBeforeAndEndDateIsAfterAndBooker_Id(now, now, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case PAST -> {
                return bookingRepository.findAllByEndDateIsBeforeAndBooker_Id(now, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case FUTURE -> {
                return bookingRepository.findAllByStartDateIsAfterAndBooker_Id(now, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case WAITING -> {
                return bookingRepository.findAllByStatusAndBooker_Id(BookingStatus.WAITING, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case REJECTED -> {
                return bookingRepository.findAllByStatusAndBooker_Id(BookingStatus.REJECTED, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            default -> {
                return bookingRepository.findAllByBooker_Id(userId, page).stream().map(bookingMapper::toDto).toList();
            }
        }
    }

    @Override
    public List<BookingDto> getByOwnerAndState(long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        Pageable page = PageRequest.of(from / size, size, sort);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT -> {
                return bookingRepository.findAllByStartDateIsBeforeAndEndDateIsAfterAndItem_Owner_Id(now, now, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case PAST -> {
                return bookingRepository.findAllByEndDateIsBeforeAndItem_Owner_Id(now, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case FUTURE -> {
                return bookingRepository.findAllByStartDateIsAfterAndItem_Owner_Id(now, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case WAITING -> {
                return bookingRepository.findAllByStatusAndItem_Owner_Id(BookingStatus.WAITING, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            case REJECTED -> {
                return bookingRepository.findAllByStatusAndItem_Owner_Id(BookingStatus.REJECTED, userId, page).stream().map(bookingMapper::toDto).toList();
            }
            default -> {
                return bookingRepository.findAllByItem_Owner_Id(userId, page).stream().map(bookingMapper::toDto).toList();
            }
        }
    }
}
