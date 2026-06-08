package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {
    @Test
    void exceptionsWithMessageCtor() {
        assertThat(new UserNotFoundException("u").getMessage()).isEqualTo("u");
        assertThat(new ItemNotFoundException("i").getMessage()).isEqualTo("i");
        assertThat(new BookingNotFoundException("b").getMessage()).isEqualTo("b");
        assertThat(new ItemRequestNotFoundException("r").getMessage()).isEqualTo("r");
        assertThat(new ItemNotAvailableException("a").getMessage()).isEqualTo("a");
        assertThat(new UserEmailExistsException("e").getMessage()).isEqualTo("e");
        assertThat(new ForbiddenException("f").getMessage()).isEqualTo("f");
    }

    @Test
    void noArgCtors() {
        assertThat(new UserNotFoundException().getMessage()).isNotBlank();
        assertThat(new ItemNotFoundException().getMessage()).isNotBlank();
        assertThat(new BookingNotFoundException().getMessage()).isNotBlank();
        assertThat(new ItemRequestNotFoundException().getMessage()).isNotBlank();
        assertThat(new ItemNotAvailableException().getMessage()).isNotBlank();
        assertThat(new UserEmailExistsException().getMessage()).isNotBlank();
    }
}