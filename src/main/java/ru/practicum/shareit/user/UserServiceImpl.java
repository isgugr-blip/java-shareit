package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserEmailExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserCreateDto payload) {
        if (userRepository.findByEmail(payload.getEmail()).isPresent()) {
            throw new UserEmailExistsException("Пользователь с таким email существует!");
        }

        User user = userMapper.createToEntity(payload);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(long userId, UserUpdateDto payload) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с таким ID не найден!"));
        if (payload.getEmail() != null) {
            userRepository.findByEmail(payload.getEmail()).ifPresent(user -> {
                if (!Objects.equals(user.getId(), userId)) {
                    throw new UserEmailExistsException("Пользователь с таким email существует!");
                }
            });

        }
        User updatedUser = userMapper.updateToEntity(userId, currentUser, payload);

        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public UserDto getById(long id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден!")
        ));
    }

    @Override
    public void delete(long id) {
        userRepository.findById(id).ifPresentOrElse(
                user -> userRepository.deleteById(id),
                () -> {
                    throw new UserNotFoundException("Пользователь не найден!");
                }
        );
    }
}
