package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

@Repository
public class UserInMemoryStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private final HashSet<String> existingEmails = new HashSet<>();

    public User save(User payload) {
        if (payload.getId() == null) {
            payload.setId(Utils.getNextId(users));
        }
        existingEmails.add(payload.getEmail());
        users.put(payload.getId(), payload);
        return payload;
    }

    public void delete(Long id) {
        users.remove(id);
    }

    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> getByEmail(String email) {
        return users.values().stream().filter(user -> email.equals(user.getEmail())).findFirst();
    }

    public boolean emailExists(String email) {
        return existingEmails.contains(email);
    }

    public boolean existsById(long id) {
        return users.containsKey(id);
    }
}
