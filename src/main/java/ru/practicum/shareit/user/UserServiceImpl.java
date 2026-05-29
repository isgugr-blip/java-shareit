package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserEmailExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.dto.UserDTO;
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
    public UserDTO create(UserCreateDTO payload) {
        if (userInMemoryStorage.emailExists(payload.getEmail())) {
            throw new UserEmailExistsException("Пользователь с таким email существует!");
        }

        User user = userMapper.createToEntity(payload);

        return userMapper.toDto(userInMemoryStorage.save(user));
    }

    @Override
    public UserDTO update(long userId, UserUpdateDTO payload) {
        User currentUser = userInMemoryStorage.getById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с таким ID не найден!"));
        if (payload.getEmail() != null && userInMemoryStorage.emailExists(payload.getEmail())) {
            userInMemoryStorage.getByEmail(payload.getEmail()).ifPresent(user -> {
                if (!Objects.equals(user.getId(), userId)) {
                    throw new UserEmailExistsException("Пользователь с таким email существует!");
                }
            });

        }
        User updatedUser = userMapper.updateToEntity(userId, currentUser, payload);

        return userMapper.toDto(userInMemoryStorage.save(updatedUser));
    }

    @Override
    public UserDTO getById(long id) {
        return userMapper.toDto(userInMemoryStorage.getById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден!")
        ));
    }

    @Override
    public void delete(long id) {
        userInMemoryStorage.getById(id).ifPresentOrElse(
                user -> userInMemoryStorage.delete(id),
                () -> {
                    throw new UserNotFoundException("Пользователь не найден!");
                }
        );
    }
}
