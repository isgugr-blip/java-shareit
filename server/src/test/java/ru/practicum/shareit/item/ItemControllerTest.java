package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String HDR = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;

    private ItemDto sampleDto() {
        return new ItemDto(1L, "Drill", "Cordless", true, 7L, null, null, new ArrayList<>());
    }

    @Test
    void createReturnsItem() throws Exception {
        when(itemService.create(eq(7L), any())).thenReturn(sampleDto());
        ItemCreateDto payload = new ItemCreateDto("Drill", "Cordless", true, null);

        mvc.perform(post("/items").header(HDR, 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateReturnsItem() throws Exception {
        when(itemService.update(eq(7L), eq(1L), any())).thenReturn(sampleDto());

        mvc.perform(patch("/items/{id}", 1).header(HDR, 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemUpdateDto("X", null, null))))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdReturnsItem() throws Exception {
        when(itemService.getById(7L, 1L)).thenReturn(sampleDto());
        mvc.perform(get("/items/{id}", 1).header(HDR, 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByIdNotFound() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenThrow(new ItemNotFoundException("nope"));
        mvc.perform(get("/items/{id}", 1).header(HDR, 7)).andExpect(status().isNotFound());
    }

    @Test
    void getByUserReturnsList() throws Exception {
        when(itemService.getByUser(7L)).thenReturn(List.of(sampleDto()));
        mvc.perform(get("/items").header(HDR, 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchReturnsList() throws Exception {
        when(itemService.getByQuery("drill")).thenReturn(List.of(sampleDto()));
        mvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addCommentReturnsDto() throws Exception {
        CommentDto comment = new CommentDto(5L, "great", "Alice", LocalDateTime.of(2025, 1, 1, 12, 0, 0));
        when(itemService.addComment(eq(7L), eq(1L), any())).thenReturn(comment);
        mvc.perform(post("/items/{id}/comment", 1).header(HDR, 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new CommentCreateDto("great"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("great"))
                .andExpect(jsonPath("$.authorName").value("Alice"));
    }
}
