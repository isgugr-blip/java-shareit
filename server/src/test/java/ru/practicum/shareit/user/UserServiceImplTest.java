package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserEmailExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    private final UserMapper userMapper = new UserMapper();
    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void createPersistsAndReturnsDto() {
        UserCreateDto payload = new UserCreateDto("Alice", "a@example.com");
        when(userRepository.findByEmail(payload.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto dto = service.create(payload);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("a@example.com");
    }

    @Test
    void createDuplicateEmailThrows() {
        UserCreateDto payload = new UserCreateDto("Alice", "dup@example.com");
        when(userRepository.findByEmail("dup@example.com"))
                .thenReturn(Optional.of(new User(99L, "other", "dup@example.com")));

        assertThatThrownBy(() -> service.create(payload)).isInstanceOf(UserEmailExistsException.class);
    }

    @Test
    void updateChangesFields() {
        User current = new User(1L, "Old", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(current));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto dto = service.update(1L, new UserUpdateDto("New", "new@example.com"));

        assertThat(dto.getName()).isEqualTo("New");
        assertThat(dto.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateUnknownUserThrows() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, new UserUpdateDto("x", "x@e.com")))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateEmailTakenByAnotherThrows() {
        User current = new User(1L, "A", "a@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(current));
        when(userRepository.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(new User(2L, "B", "taken@example.com")));

        assertThatThrownBy(() -> service.update(1L, new UserUpdateDto(null, "taken@example.com")))
                .isInstanceOf(UserEmailExistsException.class);
    }

    @Test
    void getByIdReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "A", "a@e.com")));
        assertThat(service.getById(1L).getName()).isEqualTo("A");
    }

    @Test
    void getByIdMissingThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(1L)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteCallsRepo() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "A", "a@e.com")));
        service.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteMissingThrows() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(1L)).isInstanceOf(UserNotFoundException.class);
    }
}
