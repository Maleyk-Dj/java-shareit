package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    UserClient userClient;

    @Test
    void create_ok() throws Exception {
        var in = new UserDto(null, "Alice", "alice@mail.com");
        var out = new UserDto(1L, "Alice", "alice@mail.com");

        when(userClient.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(out));

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Alice")))
                .andExpect(jsonPath("$.email", equalTo("alice@mail.com")));

        var captor = ArgumentCaptor.forClass(UserDto.class);
        verify(userClient).create(captor.capture());
        var sent = captor.getValue();
        // убеждаемся, что контроллер передал в клиент правильное тело
        assert sent.getId() == null;
        assert "Alice".equals(sent.getName());
        assert "alice@mail.com".equals(sent.getEmail());
    }

    @Test
    void update_ok() throws Exception {
        long id = 5L;
        var in = new UserDto(id, "Bob", "bob@upd.com");
        var out = new UserDto(id, "Bob", "bob@upd.com");

        when(userClient.update(eq(id), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(out));

        mvc.perform(patch("/users/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(5)))
                .andExpect(jsonPath("$.name", equalTo("Bob")))
                .andExpect(jsonPath("$.email", equalTo("bob@upd.com")));

        var dtoCap = ArgumentCaptor.forClass(UserDto.class);
        verify(userClient).update(eq(id), dtoCap.capture());
        assert "Bob".equals(dtoCap.getValue().getName());
    }

    @Test
    void get_ok() throws Exception {
        long id = 7L;
        var out = new UserDto(id, "Carol", "c@mail.com");

        when(userClient.get(id)).thenReturn(ResponseEntity.ok(out));

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(7)))
                .andExpect(jsonPath("$.name", equalTo("Carol")))
                .andExpect(jsonPath("$.email", equalTo("c@mail.com")));

        verify(userClient).get(id);
    }

    @Test
    void getAll_ok() throws Exception {
        var out = new UserDto[]{
                new UserDto(1L, "A", "a@a"),
                new UserDto(2L, "B", "b@b")
        };

        when(userClient.getAll()).thenReturn(ResponseEntity.ok(out));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[1].id", equalTo(2)));

        verify(userClient).getAll();
    }

    @Test
    void delete_ok() throws Exception {
        long id = 9L;
        when(userClient.delete(id)).thenReturn(ResponseEntity.noContent().build());

        mvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userClient).delete(id);
    }
}