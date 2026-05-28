package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {
    public User createToEntity(UserCreateDTO payload) {
        if (payload == null) {
            return null;
        }
        return new User(null, payload.getName(), payload.getEmail());
    }
}
