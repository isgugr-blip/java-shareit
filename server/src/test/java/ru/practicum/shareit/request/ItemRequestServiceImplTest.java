package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;

    private final ItemRequestMapper mapper = new ItemRequestMapper();
    private ItemRequestServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ItemRequestServiceImpl(mapper, requestRepository, itemRepository, userRepository);
    }

    private User user(long id) {
        return new User(id, "u" + id, "u" + id + "@example.com");
    }

    private ItemRequest request(long id, User author) {
        ItemRequest r = new ItemRequest();
        r.setId(id);
        r.setDescription("d" + id);
        r.setAuthor(author);
        r.setCreated(LocalDateTime.now());
        return r;
    }

    @Test
    void createPersistsRequest() {
        User author = user(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(requestRepository.save(any(ItemRequest.class))).thenAnswer(inv -> {
            ItemRequest r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        ItemRequestDto dto = service.create(1L, new ItemRequestCreateDto("need a drill"));
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getDescription()).isEqualTo("need a drill");
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void createUnknownUserThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(99L, new ItemRequestCreateDto("x")))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllByAuthorReturnsRequestsWithItems() {
        User author = user(1);
        ItemRequest r1 = request(11L, author);
        ItemRequest r2 = request(12L, author);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByAuthor_Id(eqL(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(r1, r2)));
        Item item = new Item();
        item.setId(100L);
        item.setName("Drill");
        item.setOwner(user(2));
        item.setRequestId(r1);
        when(itemRepository.findAllByRequestId_IdIn(anyList())).thenReturn(List.of(item));

        List<ItemRequestDto> result = service.getAllByAuthor(1L, 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getId()).isEqualTo(100L);
        assertThat(result.get(0).getItems().get(0).getOwnerId()).isEqualTo(2L);
        assertThat(result.get(1).getItems()).isEmpty();
    }

    @Test
    void getAllByAuthorUnknownUserThrows() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> service.getAllByAuthor(99L, 0, 10))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllExcludesOwnRequests() {
        User u1 = user(1);
        User u2 = user(2);
        ItemRequest mine = request(10L, u1);
        ItemRequest others = request(11L, u2);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mine, others)));
        when(itemRepository.findAllByRequestId_IdIn(anyList())).thenReturn(List.of());

        List<ItemRequestDto> result = service.getAll(1L, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(11L);
    }

    @Test
    void getByIdReturnsWithItems() {
        User author = user(1);
        ItemRequest r = request(20L, author);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(20L)).thenReturn(Optional.of(r));
        Item item = new Item();
        item.setId(200L);
        item.setName("Hammer");
        item.setOwner(user(3));
        item.setRequestId(r);
        when(itemRepository.findAllByRequestId_Id(20L)).thenReturn(List.of(item));

        ItemRequestDto dto = service.getById(1L, 20L);
        assertThat(dto.getId()).isEqualTo(20L);
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Hammer");
    }

    @Test
    void getByIdMissingThrows() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1L, 99L))
                .isInstanceOf(ItemRequestNotFoundException.class);
    }

    private static long eqL(long v) {
        return org.mockito.ArgumentMatchers.eq(v);
    }
}
