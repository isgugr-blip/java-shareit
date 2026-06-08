package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {
    public UserDto toDto(User payload) {
        if (payload == null) {
            return null;
        }

        return new UserDto(
                payload.getId(),
                payload.getName(),
                payload.getEmail()
        );
    }

    public User createToEntity(UserCreateDto payload) {
        if (payload == null) {
            return null;
        }
        return new User(null, payload.getName(), payload.getEmail());
    }

    public User updateToEntity(long userId, User current, UserUpdateDto payload) {
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
