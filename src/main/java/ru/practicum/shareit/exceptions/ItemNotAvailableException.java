package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException() {
        super("Предмет недоступен для аренды!");
    }
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
