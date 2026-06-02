package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid BookingCreateDto payload) {
        return bookingService.create(userId, payload);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrReject(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getByBookerAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookingService.getByBookerAndState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwnerAndState(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookingService.getByOwnerAndState(userId, state, from, size);
    }
}
