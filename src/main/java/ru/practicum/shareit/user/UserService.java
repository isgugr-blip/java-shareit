package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.dto.UserUpdateDTO;

public interface UserService {
    User create(UserCreateDTO payload);
    User update(Long id, UserUpdateDTO payload);
    User getById(Long id);
    void delete(Long id);
}
