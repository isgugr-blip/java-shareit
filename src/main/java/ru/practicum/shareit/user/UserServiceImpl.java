package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserEmailExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDTO;
import ru.practicum.shareit.user.storage.UserInMemoryStorage;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserInMemoryStorage userInMemoryStorage;
    private final UserMapper userMapper;

    @Override
    public User create(UserCreateDTO payload) {
        if (userInMemoryStorage.emailExists(payload.getEmail())) {
            throw new UserEmailExistsException("Пользователь с таким email существует!");
        }

        User user = userMapper.createToEntity(payload);

        return userInMemoryStorage.save(user);
    }

    @Override
    public User update(Long id, UserUpdateDTO payload) {
        if (userInMemoryStorage.getById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь с таким ID не найден!");
        }
        if (payload.getEmail() != null && userInMemoryStorage.emailExists(payload.getEmail())) {
            userInMemoryStorage.getByEmail(payload.getEmail()).ifPresent(user -> {
                if (!Objects.equals(user.getId(), id)) {
                    throw new UserEmailExistsException("Пользователь с таким email существует!");
                }
            });

        }

        User user = User.builder()
                .id(id)
                .email(payload.getEmail())
                .name(payload.getName())
                .build();

        return userInMemoryStorage.save(user);
    }

    @Override
    public User getById(Long id) {
        return userInMemoryStorage.getById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден!")
        );
    }

    @Override
    public void delete(Long id) {
        userInMemoryStorage.getById(id).ifPresentOrElse(
                user -> userInMemoryStorage.delete(id),
                () -> {
                    throw new UserNotFoundException("Пользователь не найден!");
                }
        );
    }
}
