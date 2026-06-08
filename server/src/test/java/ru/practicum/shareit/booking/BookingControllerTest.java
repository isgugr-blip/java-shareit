package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.BookingNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String HDR = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService service;

    private BookingDto sample() {
        return new BookingDto(
                1L,
                LocalDateTime.of(2025, 1, 2, 3, 4, 5),
                LocalDateTime.of(2025, 1, 3, 3, 4, 5),
                new BookingDto.BookingBookerDto(11L),
                new BookingDto.BookingItemDto(22L, "Drill"),
                BookingStatus.WAITING
        );
    }

    @Test
    void createBookingReturnsDto() throws Exception {
        when(service.create(eq(11L), any())).thenReturn(sample());
        ObjectMapper m = mapper.copy().registerModule(new JavaTimeModule());
        BookingCreateDto payload = new BookingCreateDto();
        payload.setItemId(22L);
        payload.setStart(LocalDateTime.now().plusDays(1));
        payload.setEnd(LocalDateTime.now().plusDays(2));

        mvc.perform(post("/bookings").header(HDR, 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(m.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveReturnsDto() throws Exception {
        BookingDto approved = sample();
        approved.setStatus(BookingStatus.APPROVED);
        when(service.approve(11L, 1L, true)).thenReturn(approved);

        mvc.perform(patch("/bookings/{id}", 1).header(HDR, 11).param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getByIdReturnsDto() throws Exception {
        when(service.getById(11L, 1L)).thenReturn(sample());
        mvc.perform(get("/bookings/{id}", 1).header(HDR, 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new BookingNotFoundException());
        mvc.perform(get("/bookings/{id}", 1).header(HDR, 11)).andExpect(status().isNotFound());
    }

    @Test
    void getByBookerStateAll() throws Exception {
        when(service.getByBookerAndState(eq(11L), eq(BookingState.ALL), anyInt(), anyInt()))
                .thenReturn(List.of(sample()));
        mvc.perform(get("/bookings").header(HDR, 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByOwnerStateAll() throws Exception {
        when(service.getByOwnerAndState(eq(11L), eq(BookingState.ALL), anyInt(), anyInt()))
                .thenReturn(List.of(sample()));
        mvc.perform(get("/bookings/owner").header(HDR, 11))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
