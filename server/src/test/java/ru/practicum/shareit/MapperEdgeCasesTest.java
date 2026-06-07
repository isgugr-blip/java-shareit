package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapperEdgeCasesTest {
    @Test
    void itemMapperHandlesNulls() {
        ItemMapper m = new ItemMapper();
        assertThat(m.toDto(null)).isNull();
        assertThat(m.toDto(null, null, null, null)).isNull();
        assertThat(m.createToEntity(new User(1L, "n", "e@e"), null, null)).isNull();

        Item current = new Item(7L, "Old", "Desc", true, new User(1L, "n", "e@e"), null);
        Item same = m.updateToEntity(new User(1L, "n", "e@e"), 7L, current, null);
        assertThat(same).isSameAs(current);

        Item it = new Item(1L, "I", "D", true, null, null);
        assertThat(m.toDto(it).getOwner()).isNull();
    }

    @Test
    void itemMapperToBookingHandlesNullBooker() {
        ItemMapper m = new ItemMapper();
        Item it = new Item(1L, "I", "D", true, new User(2L, "o", "o@e"), null);
        Booking b = new Booking(5L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), it, null, BookingStatus.APPROVED);
        var dto = m.toDto(it, b, null, List.of());
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getBookerId()).isNull();
        assertThat(dto.getNextBooking()).isNull();
    }

    @Test
    void commentMapperHandlesNull() {
        assertThat(new CommentMapper().toDto((Comment) null)).isNull();
    }

    @Test
    void commentMapperRoundTrip() {
        CommentMapper m = new CommentMapper();
        Comment c = new Comment(1L, "txt", null, new User(7L, "Alice", "a@e"), LocalDateTime.of(2025, 1, 2, 3, 4, 5));
        CommentDto dto = m.toDto(c);
        assertThat(dto.getAuthorName()).isEqualTo("Alice");
        assertThat(dto.getText()).isEqualTo("txt");
    }

    @Test
    void bookingMapperHandlesNulls() {
        BookingMapper m = new BookingMapper();
        assertThat(m.toDto(null)).isNull();
        assertThat(m.createToEntity(new User(1L, "n", "e"), new Item(), null)).isNull();
    }

    @Test
    void userMapperHandlesNulls() {
        UserMapper m = new UserMapper();
        assertThat(m.toDto(null)).isNull();
        assertThat(m.createToEntity(null)).isNull();
        User current = new User(1L, "old", "old@e");
        assertThat(m.updateToEntity(1L, current, null)).isSameAs(current);
        User onlyName = m.updateToEntity(1L, current, new UserUpdateDto("new", null));
        assertThat(onlyName.getName()).isEqualTo("new");
        assertThat(onlyName.getEmail()).isEqualTo("old@e");
    }

    @Test
    void itemRequestMapperHandlesNulls() {
        ItemRequestMapper m = new ItemRequestMapper();
        assertThat(m.toDto(null)).isNull();
        assertThat(m.toDto(null, List.of())).isNull();
        assertThat(m.createToEntity(new User(1L, "n", "e"), LocalDateTime.now(), null)).isNull();

        ItemRequest r = new ItemRequest(1L, "d", new User(1L, "n", "e"), LocalDateTime.now());
        var dto = m.toDto(r, null);
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void itemUpdateLeavesOptionalFieldsAsCurrent() {
        ItemMapper m = new ItemMapper();
        User owner = new User(1L, "n", "e");
        Item current = new Item(7L, "Old", "OldDesc", true, owner, null);
        Item updated = m.updateToEntity(owner, 7L, current, new ItemUpdateDto(null, null, null));
        assertThat(updated.getName()).isEqualTo("Old");
        assertThat(updated.getDescription()).isEqualTo("OldDesc");
        assertThat(updated.getAvailable()).isTrue();
    }
}
