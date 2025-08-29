package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.ConflictException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HandlerTest {

    private MockMvc mvc;

    @RestController
    static class DummyController {
        @GetMapping("/test/notfound")
        public void nf() {
            throw new NotFoundException("Бронирование не найдено");
        }

        @GetMapping("/test/conflict")
        public void conflict() {
            throw new ConflictException("Email уже используется");
        }

        @GetMapping("/test/forbidden")
        public void forbidden() {
            throw new AccessDeniedException("Нет прав");
        }

        @GetMapping("/test/validation")
        public void validation() {
            throw new ValidationException("Некорректный период");
        }
    }

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(new DummyController())
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void validation_mappedTo400_withBody() throws Exception {
        mvc.perform(get("/test/validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"))
                .andExpect(jsonPath("$.message").value("Некорректный период"));
    }
}