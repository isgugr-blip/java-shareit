package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {
    public UserDTO toDto(User payload) {
        if (payload == null) {
            return null;
        }

        return new UserDTO(
                payload.getId(),
                payload.getName(),
                payload.getEmail()
        );
    }

    public User createToEntity(UserCreateDTO payload) {
        if (payload == null) {
            return null;
        }
        return new User(null, payload.getName(), payload.getEmail());
    }

    public User updateToEntity(long userId, User current, UserUpdateDTO payload) {
        if (payload == null) {
            return current;
        }

        return new User(
                userId,
                payload.getName() != null ? payload.getName() : current.getName(),
                payload.getEmail() != null ? payload.getEmail() : current.getEmail()
        );
    }
}
