package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private final BookingMapper bookingMapper = new BookingMapper();
    private BookingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingMapper);
    }

    private User user(long id) {
        return new User(id, "u" + id, "u" + id + "@ex.com");
    }

    private Item item(long id, User owner, boolean available) {
        Item i = new Item();
        i.setId(id);
        i.setName("name");
        i.setDescription("desc");
        i.setAvailable(available);
        i.setOwner(owner);
        return i;
    }

    private Booking booking(long id, User booker, Item item, BookingStatus status) {
        return new Booking(id, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, status);
    }

    @Test
    void createPersistsBooking() {
        User booker = user(2);
        Item it = item(7, user(1), true);
        BookingCreateDto payload = new BookingCreateDto();
        payload.setItemId(7L);
        payload.setStart(LocalDateTime.now().plusDays(1));
        payload.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(it));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(100L);
            return b;
        });

        BookingDto dto = service.create(2L, payload);
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void createUnavailableItemThrows() {
        Item it = item(7, user(1), false);
        BookingCreateDto payload = new BookingCreateDto();
        payload.setItemId(7L);
        payload.setStart(LocalDateTime.now().plusDays(1));
        payload.setEnd(LocalDateTime.now().plusDays(2));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(it));

        assertThatThrownBy(() -> service.create(2L, payload)).isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void createOwnerBookingThrows() {
        Item it = item(7, user(1), true);
        BookingCreateDto payload = new BookingCreateDto();
        payload.setItemId(7L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(it));

        assertThatThrownBy(() -> service.create(1L, payload)).isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void createUnknownUserThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        BookingCreateDto payload = new BookingCreateDto();
        payload.setItemId(7L);
        assertThatThrownBy(() -> service.create(99L, payload)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createUnknownItemThrows() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        BookingCreateDto payload = new BookingCreateDto();
        payload.setItemId(99L);
        assertThatThrownBy(() -> service.create(2L, payload)).isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void approveSetsApproved() {
        Booking b = booking(10, user(2), item(7, user(1), true), BookingStatus.WAITING);
        when(bookingRepository.findByIdAndItem_Owner_Id(10L, 1L)).thenReturn(Optional.of(b));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingDto dto = service.approve(1L, 10L, true);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveByNonOwnerThrowsForbidden() {
        when(bookingRepository.findByIdAndItem_Owner_Id(10L, 99L)).thenReturn(Optional.empty());
        when(bookingRepository.existsById(10L)).thenReturn(true);
        assertThatThrownBy(() -> service.approve(99L, 10L, true)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void approveMissingBookingThrows() {
        when(bookingRepository.findByIdAndItem_Owner_Id(10L, 1L)).thenReturn(Optional.empty());
        when(bookingRepository.existsById(10L)).thenReturn(false);
        assertThatThrownBy(() -> service.approve(1L, 10L, true)).isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void approveAlreadyApprovedThrows() {
        Booking b = booking(10, user(2), item(7, user(1), true), BookingStatus.APPROVED);
        when(bookingRepository.findByIdAndItem_Owner_Id(10L, 1L)).thenReturn(Optional.of(b));
        assertThatThrownBy(() -> service.approve(1L, 10L, true)).isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void getByIdAsBooker() {
        Booking b = booking(10, user(2), item(7, user(1), true), BookingStatus.WAITING);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(b));
        assertThat(service.getById(2L, 10L).getId()).isEqualTo(10L);
    }

    @Test
    void getByIdNotParticipantThrows() {
        Booking b = booking(10, user(2), item(7, user(1), true), BookingStatus.WAITING);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(b));
        assertThatThrownBy(() -> service.getById(99L, 10L)).isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void getByBookerStateAllReturnsList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(bookingRepository.findAllByBooker_Id(eq(2L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking(10, user(2), item(7, user(1), true), BookingStatus.WAITING))));
        assertThat(service.getByBookerAndState(2L, BookingState.ALL, 0, 10)).hasSize(1);
    }

    @Test
    void getByBookerStateCurrentDelegates() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(bookingRepository.findAllByStartDateIsBeforeAndEndDateIsAfterAndBooker_Id(any(), any(), eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByBookerAndState(2L, BookingState.CURRENT, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerStateRejectedDelegates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(bookingRepository.findAllByStatusAndItem_Owner_Id(eq(BookingStatus.REJECTED), eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByOwnerAndState(1L, BookingState.REJECTED, 0, 10)).isEmpty();
    }

    @Test
    void getByBookerUnknownUserThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByBookerAndState(99L, BookingState.ALL, 0, 10))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getByBookerStatePastDelegates() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(bookingRepository.findAllByEndDateIsBeforeAndBooker_Id(any(), eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByBookerAndState(2L, BookingState.PAST, 0, 10)).isEmpty();
    }

    @Test
    void getByBookerStateFutureDelegates() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(bookingRepository.findAllByStartDateIsAfterAndBooker_Id(any(), eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByBookerAndState(2L, BookingState.FUTURE, 0, 10)).isEmpty();
    }

    @Test
    void getByBookerStateWaitingDelegates() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(bookingRepository.findAllByStatusAndBooker_Id(eq(BookingStatus.WAITING), eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByBookerAndState(2L, BookingState.WAITING, 0, 10)).isEmpty();
    }

    @Test
    void getByBookerStateRejectedDelegates() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(bookingRepository.findAllByStatusAndBooker_Id(eq(BookingStatus.REJECTED), eq(2L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByBookerAndState(2L, BookingState.REJECTED, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerStateAllDelegates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(bookingRepository.findAllByItem_Owner_Id(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByOwnerAndState(1L, BookingState.ALL, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerStateCurrentDelegates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(bookingRepository.findAllByStartDateIsBeforeAndEndDateIsAfterAndItem_Owner_Id(any(), any(), eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByOwnerAndState(1L, BookingState.CURRENT, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerStatePastDelegates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(bookingRepository.findAllByEndDateIsBeforeAndItem_Owner_Id(any(), eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByOwnerAndState(1L, BookingState.PAST, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerStateFutureDelegates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(bookingRepository.findAllByStartDateIsAfterAndItem_Owner_Id(any(), eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByOwnerAndState(1L, BookingState.FUTURE, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerStateWaitingDelegates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(bookingRepository.findAllByStatusAndItem_Owner_Id(eq(BookingStatus.WAITING), eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of()));
        assertThat(service.getByOwnerAndState(1L, BookingState.WAITING, 0, 10)).isEmpty();
    }

    @Test
    void getByOwnerUnknownUserThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByOwnerAndState(99L, BookingState.ALL, 0, 10))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getByIdAsOwnerOk() {
        Booking b = booking(10, user(2), item(7, user(1), true), BookingStatus.WAITING);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(b));
        assertThat(service.getById(1L, 10L).getId()).isEqualTo(10L);
    }

    @Test
    void getByIdMissingThrows() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1L, 99L)).isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    void approveRejectsBooking() {
        Booking b = booking(10, user(2), item(7, user(1), true), BookingStatus.WAITING);
        when(bookingRepository.findByIdAndItem_Owner_Id(10L, 1L)).thenReturn(Optional.of(b));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        BookingDto dto = service.approve(1L, 10L, false);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }
}
