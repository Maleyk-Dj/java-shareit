package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.ConflictException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ErrorHandlerTest {


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

    private UserDto testUserDto;
    private User testUser; // Сущность User для сервиса

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto(null, "Test User", "test@example.com");
        testUser = new User(1L, "Test User", "test@example.com");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testHandleNotFoundException() {

        when(userService.getById(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));

        ResponseEntity<Object> response = restTemplate.getForEntity(getBaseUrl() + "/users/{userId}", Object.class, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Объект не найден", ((java.util.LinkedHashMap) response.getBody()).get("error"));
        assertEquals("Пользователь не найден", ((java.util.LinkedHashMap) response.getBody()).get("message"));
    }

    @Test
    void testHandleConflictException() {

        when(userService.create(any(User.class))).thenThrow(new ConflictException("Пользователь с таким email уже существует."));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(testUserDto, headers);

        ResponseEntity<Object> response = restTemplate.postForEntity(getBaseUrl() + "/users", requestEntity, Object.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Конфликт данных", ((java.util.LinkedHashMap) response.getBody()).get("error"));
        assertEquals("Пользователь с таким email уже существует.", ((java.util.LinkedHashMap) response.getBody()).get("message"));
    }

    @Test
    void testHandleAccessDeniedException() {

        when(userService.getById(anyLong())).thenThrow(new AccessDeniedException("Доступ запрещен."));

        ResponseEntity<Object> response = restTemplate.getForEntity(getBaseUrl() + "/users/{userId}", Object.class, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Доступ запрещен", ((java.util.LinkedHashMap) response.getBody()).get("error"));
        assertEquals("Доступ запрещен.", ((java.util.LinkedHashMap) response.getBody()).get("message"));
    }

    @Test
    void testHandleValidationException() {

        when(userService.getById(anyLong())).thenThrow(new ValidationException("Ошибка валидации данных."));

        ResponseEntity<Object> response = restTemplate.getForEntity(getBaseUrl() + "/users/{userId}", Object.class, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ошибка валидации", ((java.util.LinkedHashMap) response.getBody()).get("error"));
        assertEquals("Ошибка валидации данных.", ((java.util.LinkedHashMap) response.getBody()).get("message"));
    }

    @Test
    void testHandleThrowable() {

        when(userService.getById(anyLong())).thenThrow(new RuntimeException("Что-то пошло не так."));

        ResponseEntity<Object> response = restTemplate.getForEntity(getBaseUrl() + "/users/{userId}", Object.class, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Произошла непредвиденная ошибка", ((java.util.LinkedHashMap) response.getBody()).get("error"));
        assertEquals("Что-то пошло не так.", ((java.util.LinkedHashMap) response.getBody()).get("message"));
    }
}

