package ru.practicum.shareit.item;


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
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    private static final String SERVER_URL = "http://localhost:9090";
    private static final String API_PREFIX = "/items";

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build())
                .thenReturn(restTemplate);

        itemClient = new ItemClient(SERVER_URL, restTemplateBuilder);
    }

    @Test
    void testCreateItem() {
        Long userId = 1L;
        ItemDto itemDto = new ItemDto(null, "Test Item", "Description", true, null);
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.createItem(userId, itemDto);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(uriCaptor.capture(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));

        assertEquals("", uriCaptor.getValue()); // "" так как в BaseClient.post("")
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(itemDto, httpEntityCaptor.getValue().getBody());
    }

    @Test
    void updateItem_ok() {
        long userId = 42L;
        long itemId = 99L;
        ItemDto dto = new ItemDto(itemId, "Drill", "Good drill",true,777L);

        ResponseEntity<Object> expected = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class))
        ).thenReturn(expected);

        // act
        ResponseEntity<Object> actual = itemClient.updateItem(userId, itemId, dto);

        // assert
        assertEquals(expected, actual);

        ArgumentCaptor<String> uriCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<?>> entityCap = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCap.capture(),
                eq(HttpMethod.PATCH),
                entityCap.capture(),
                eq(Object.class)
        );

        // путь без API_PREFIX (он уже настроен в DefaultUriBuilderFactory)
        assertEquals("/" + itemId, uriCap.getValue());

        HttpEntity<?> sent = entityCap.getValue();
        assertEquals(String.valueOf(userId), sent.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(dto, sent.getBody());
    }

    @Test
    void testGetAllByUser() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.getAllByUser(userId);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class));

        assertEquals("", uriCaptor.getValue()); // "" так как в BaseClient.get("")
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void testSearch() {
        String searchText = "drill";
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), eq(Map.of("text", searchText))))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.search(searchText);

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

        assertEquals("/search?text={text}", uriCaptor.getValue());

        assertEquals(Map.of("text", searchText), uriVariablesCaptor.getValue());
    }

    @Test
    void testDeleteItem() {
        Long userId = 1L;
        Long itemId = 10L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.NO_CONTENT);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.deleteItem(userId, itemId);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                httpMethodCaptor.capture(),
                httpEntityCaptor.capture(),
                eq(Object.class));

        assertEquals("/" + itemId, uriCaptor.getValue());
        assertEquals(HttpMethod.DELETE, httpMethodCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void testAddComment() {
        Long userId = 1L;
        Long itemId = 10L;
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.addComment(userId, itemId, commentDto);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                httpMethodCaptor.capture(),
                httpEntityCaptor.capture(),
                eq(Object.class));

        assertEquals("/" + itemId + "/comment", uriCaptor.getValue());
        assertEquals(HttpMethod.POST, httpMethodCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(commentDto, httpEntityCaptor.getValue().getBody());
    }

    @Test
    void testGetItemById() {
        Long userId = 1L;
        Long itemId = 10L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.getItemById(userId, itemId);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class));

        assertEquals("/" + itemId, uriCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }
}
