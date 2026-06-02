package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
        super("Предмет не найден!");
    }
    public ItemNotFoundException(String message) {
        super(message);
    }
}
