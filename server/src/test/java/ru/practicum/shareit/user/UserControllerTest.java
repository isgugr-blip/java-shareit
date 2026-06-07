package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;

    @Test
    void createReturnsUser() throws Exception {
        UserDto dto = new UserDto(1L, "Alice", "a@example.com");
        when(userService.create(any())).thenReturn(dto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserCreateDto("Alice", "a@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("a@example.com"));
    }

    @Test
    void getByIdReturnsUser() throws Exception {
        when(userService.getById(5)).thenReturn(new UserDto(5L, "Bob", "b@example.com"));

        mvc.perform(get("/users/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob"));
    }

    @Test
    void getByIdNotFoundIs404() throws Exception {
        when(userService.getById(99)).thenThrow(new UserNotFoundException());

        mvc.perform(get("/users/{id}", 99)).andExpect(status().isNotFound());
    }

    @Test
    void updateUserReturnsUpdated() throws Exception {
        UserDto dto = new UserDto(1L, "A2", "x@example.com");
        when(userService.update(eq(1L), any())).thenReturn(dto);

        mvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserUpdateDto("A2", "x@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A2"));
    }

    @Test
    void deleteInvokesService() throws Exception {
        mvc.perform(delete("/users/{id}", 1)).andExpect(status().isOk());
        verify(userService).delete(1L);
    }
}
