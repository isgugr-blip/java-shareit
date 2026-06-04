package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserDto create(UserCreateDto payload);

    UserDto update(long id, UserUpdateDto payload);

    UserDto getById(long id);

    void delete(long id);
}
