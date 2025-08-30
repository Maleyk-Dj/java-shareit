package ru.practicum.shareit.user;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    private static final String SERVER_URL = "http://localhost:9090";

    @BeforeEach
    void setUp() {

        when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build())
                .thenReturn(restTemplate);

        userClient = new UserClient(SERVER_URL, restTemplateBuilder);
    }

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto(null, "Alice", "alice@mail.com");
        ResponseEntity<Object> expected = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> resp = userClient.create(dto);
        assertEquals(expected, resp);

        ArgumentCaptor<String> uri = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uri.capture(),
                eq(HttpMethod.POST),
                entity.capture(),
                eq(Object.class)
        );

        assertEquals("", uri.getValue());

        HttpEntity<?> sent = entity.getValue();
        assertEquals(MediaType.APPLICATION_JSON, sent.getHeaders().getContentType());

        assertNull(sent.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(dto, sent.getBody());
    }

    @Test
    void testGetUser() {
        Long id = 7L;
        ResponseEntity<Object> expected = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> resp = userClient.get(id);
        assertEquals(expected, resp);

        ArgumentCaptor<String> uri = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uri.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(Object.class)
        );

        assertEquals("/" + id, uri.getValue());
        assertNull(entity.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void testGetAllUsers() {
        ResponseEntity<Object> expected = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expected);

        ResponseEntity<Object> resp = userClient.getAll();
        assertEquals(expected, resp);

        ArgumentCaptor<String> uri = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uri.capture(),
                eq(HttpMethod.GET),
                entity.capture(),
                eq(Object.class)
        );

        assertEquals("", uri.getValue());
        assertNull(entity.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }
}
