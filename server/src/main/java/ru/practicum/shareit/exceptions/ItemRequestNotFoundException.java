package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException() {
        super("Запрос не найден!");
    }

    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
