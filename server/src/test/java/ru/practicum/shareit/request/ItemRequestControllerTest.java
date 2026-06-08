package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HDR = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService service;

    private ItemRequestDto sample() {
        return new ItemRequestDto(1L, "need drill", LocalDateTime.of(2025, 6, 7, 8, 9, 10), List.of());
    }

    @Test
    void createReturnsDto() throws Exception {
        when(service.create(eq(7L), any())).thenReturn(sample());

        mvc.perform(post("/requests").header(HDR, 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemRequestCreateDto("need drill"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("need drill"));
    }

    @Test
    void getAllByAuthorReturnsList() throws Exception {
        when(service.getAllByAuthor(eq(7L), anyInt(), anyInt())).thenReturn(List.of(sample()));
        mvc.perform(get("/requests").header(HDR, 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllReturnsList() throws Exception {
        when(service.getAll(eq(7L), anyInt(), anyInt())).thenReturn(List.of(sample()));
        mvc.perform(get("/requests/all").header(HDR, 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByIdReturnsDto() throws Exception {
        when(service.getById(7L, 1L)).thenReturn(sample());
        mvc.perform(get("/requests/{id}", 1).header(HDR, 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByIdMissing404() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenThrow(new ItemRequestNotFoundException());
        mvc.perform(get("/requests/{id}", 1).header(HDR, 7)).andExpect(status().isNotFound());
    }
}
