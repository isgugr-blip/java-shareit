package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private final ItemMapper itemMapper = new ItemMapper();
    private final CommentMapper commentMapper = new CommentMapper();
    private ItemServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository, itemMapper, commentMapper);
    }

    private User user(long id) {
        return new User(id, "u" + id, "u" + id + "@ex.com");
    }

    private Item item(long id, User owner) {
        Item i = new Item();
        i.setId(id);
        i.setName("name" + id);
        i.setDescription("desc" + id);
        i.setAvailable(true);
        i.setOwner(owner);
        return i;
    }

    @Test
    void createPersistsAndReturnsDto() {
        User owner = user(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            i.setId(7L);
            return i;
        });

        ItemDto dto = service.create(1L, new ItemCreateDto("D", "Cordless", true, null));
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getOwner()).isEqualTo(1L);
    }

    @Test
    void createWithRequest() {
        User owner = user(1);
        ItemRequest req = new ItemRequest(5L, "d", user(2), LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(5L)).thenReturn(Optional.of(req));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            i.setId(7L);
            return i;
        });

        ItemDto dto = service.create(1L, new ItemCreateDto("D", "Cordless", true, 5L));
        assertThat(dto.getId()).isEqualTo(7L);
    }

    @Test
    void createUnknownRequestThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(1L, new ItemCreateDto("D", "Cordless", true, 99L)))
                .isInstanceOf(ItemRequestNotFoundException.class);
    }

    @Test
    void createUnknownUserThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(99L, new ItemCreateDto("D", "Cordless", true, null)))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateUpdatesFields() {
        User owner = user(1);
        Item current = item(7L, owner);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(current));
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        ItemDto dto = service.update(1L, 7L, new ItemUpdateDto("new", null, false));
        assertThat(dto.getName()).isEqualTo("new");
        assertThat(dto.getAvailable()).isFalse();
    }

    @Test
    void updateForeignOwnerThrows() {
        Item current = item(7L, user(1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2)));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(current));

        assertThatThrownBy(() -> service.update(2L, 7L, new ItemUpdateDto("new", null, null)))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void updateBlankNameThrows() {
        Item current = item(7L, user(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(current));
        assertThatThrownBy(() -> service.update(1L, 7L, new ItemUpdateDto("  ", null, null)))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void getByIdAsOwnerIncludesBookings() {
        Item it = item(7L, user(1));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(it));
        when(commentRepository.findAllByItem_Id(7L)).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateDesc(
                eq(7L), eq(BookingStatus.APPROVED), any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(
                eq(7L), eq(BookingStatus.APPROVED), any())).thenReturn(Optional.empty());

        ItemDto dto = service.getById(1L, 7L);
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void getByIdMissingThrows() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1L, 99L)).isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getByUserReturnsList() {
        when(itemRepository.getByOwner_Id(1L)).thenReturn(List.of(item(1L, user(1))));
        when(commentRepository.findAllByItem_Id(anyLong())).thenReturn(List.of());
        when(bookingRepository.findFirstByItem_IdAndStatusAndEndDateIsBeforeOrderByEndDateDesc(
                anyLong(), any(), any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(
                anyLong(), any(), any())).thenReturn(Optional.empty());

        assertThat(service.getByUser(1L)).hasSize(1);
    }

    @Test
    void getByQueryReturnsEmptyForBlank() {
        assertThat(service.getByQuery("")).isEmpty();
    }

    @Test
    void getByQueryDelegatesToRepo() {
        when(itemRepository.findByAvailabilityAndQueryInNameOrDescription("drill"))
                .thenReturn(List.of(item(1L, user(1))));
        assertThat(service.getByQuery("drill")).hasSize(1);
    }

    @Test
    void addCommentWithoutRentalThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(item(7L, user(2))));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndEndDateIsBeforeAndStatus(
                eq(1L), eq(7L), any(), eq(BookingStatus.APPROVED))).thenReturn(false);

        assertThatThrownBy(() -> service.addComment(1L, 7L, new CommentCreateDto("nice")))
                .isInstanceOf(ConditionsNotMetException.class);
    }

    @Test
    void addCommentOk() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1)));
        when(itemRepository.findById(7L)).thenReturn(Optional.of(item(7L, user(2))));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndEndDateIsBeforeAndStatus(
                eq(1L), eq(7L), any(), eq(BookingStatus.APPROVED))).thenReturn(true);
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(50L);
            return c;
        });

        CommentDto dto = service.addComment(1L, 7L, new CommentCreateDto("nice"));
        assertThat(dto.getId()).isEqualTo(50L);
        assertThat(dto.getText()).isEqualTo("nice");
    }
}
