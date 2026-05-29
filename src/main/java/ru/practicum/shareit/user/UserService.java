package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.dto.UserUpdateDTO;

public interface UserService {
    UserDTO create(UserCreateDTO payload);

    UserDTO update(long id, UserUpdateDTO payload);

    UserDTO getById(long id);

    void delete(long id);
}
