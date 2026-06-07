package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserEmailExistsException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService service;

    @Test
    void createAndUpdateAndFetchRoundTrip() {
        UserDto created = service.create(new UserCreateDto("Alice", "alice@example.com"));
        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("alice@example.com");

        UserDto fetched = service.getById(created.getId());
        assertThat(fetched.getName()).isEqualTo("Alice");

        UserDto updated = service.update(created.getId(), new UserUpdateDto("Alice2", null));
        assertThat(updated.getName()).isEqualTo("Alice2");
        assertThat(updated.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void duplicateEmailRejected() {
        service.create(new UserCreateDto("A", "dup@example.com"));
        assertThatThrownBy(() -> service.create(new UserCreateDto("B", "dup@example.com")))
                .isInstanceOf(UserEmailExistsException.class);
    }
}
