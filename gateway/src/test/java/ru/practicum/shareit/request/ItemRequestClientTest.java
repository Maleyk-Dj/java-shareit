package ru.practicum.shareit.request;


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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    private static final String SERVER_URL = "http://localhost:9090";
    private static final String API_PREFIX = "/requests";

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build())
                .thenReturn(restTemplate);

        itemRequestClient = new ItemRequestClient(SERVER_URL, restTemplateBuilder);
    }

    @Test
    void testCreateItemRequest() {
        Long userId = 1L;

        ItemRequestDto requestDto = new ItemRequestDto(null, "Description for item request", LocalDateTime.now());
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.createItemRequest(userId, requestDto);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(uriCaptor.capture(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));

        assertEquals("", uriCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(requestDto, httpEntityCaptor.getValue().getBody());
    }

    @Test
    void testGetUserItemRequests() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.getUserItemRequests(userId);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class));

        assertEquals("", uriCaptor.getValue()); // "" так как BaseClient.get("")
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void testGetItemRequestById() {
        Long userId = 1L;
        Long requestId = 10L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(Map.of("requestId", requestId))))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.getItemRequestById(userId, requestId);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<Map> uriVariablesCaptor = ArgumentCaptor.forClass(Map.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class),
                uriVariablesCaptor.capture());

        assertEquals("/{requestId}", uriCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(Map.of("requestId", requestId), uriVariablesCaptor.getValue());
    }
}
