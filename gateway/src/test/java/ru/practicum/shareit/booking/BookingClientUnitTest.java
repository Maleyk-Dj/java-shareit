package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientUnitTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    private static final String SERVER_URL = "http://localhost:9090"; // Для инициализации клиента
    private static final String API_PREFIX = "/bookings"; // Префикс, который добавляет сам BookingClient

    @BeforeEach
    void setUp() {
        when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class)))
                .thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build())
                .thenReturn(restTemplate);

        bookingClient = new BookingClient(SERVER_URL, restTemplateBuilder);
    }

    @Test
    void testCreateBooking() {
        Long userId = 1L;
        BookItemRequestDto requestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.create(userId, requestDto);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(uriCaptor.capture(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));

        assertEquals("", uriCaptor.getValue());

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals(userId.toString(), capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(requestDto, capturedEntity.getBody());
    }

    @Test
    void testApproveBooking() {
        Long ownerId = 1L;
        Long bookingId = 10L;
        boolean approved = true;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.approve(ownerId, bookingId, approved);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<Map> uriVariablesCaptor = ArgumentCaptor.forClass(Map.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                httpMethodCaptor.capture(),
                httpEntityCaptor.capture(),
                eq(Object.class),
                uriVariablesCaptor.capture());

        assertEquals("/" + bookingId + "?approved={approved}", uriCaptor.getValue());
        assertEquals(HttpMethod.PATCH, httpMethodCaptor.getValue());
        assertEquals(ownerId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(Map.of("approved", approved), uriVariablesCaptor.getValue());
    }

    @Test
    void testGetBookings() {
        Long userId = 1L;
        BookingState state = BookingState.CURRENT;
        Integer from = 0;
        Integer size = 10;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getBookings(userId, state, from, size);

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

        assertEquals("?state={state}&from={from}&size={size}", uriCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
        assertEquals(Map.of("state", state.name(), "from", from, "size", size), uriVariablesCaptor.getValue());
    }

    @Test
    void testGetBooking() {
        Long userId = 1L;
        Long bookingId = 10L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getBooking(userId, bookingId);

        assertEquals(expectedResponse, actualResponse);

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate, times(1)).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                httpEntityCaptor.capture(),
                eq(Object.class));

        assertEquals("/" + bookingId, uriCaptor.getValue());
        assertEquals(userId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void testGetOwnerBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;

        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(new Object(), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getOwnerBookings(ownerId, state, from, size);

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

        assertEquals("/owner?state={state}&from={from}&size={size}", uriCaptor.getValue());
        assertEquals(ownerId.toString(), httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));

        Map<String, Object> capturedParameters = uriVariablesCaptor.getValue();
        assertEquals(state.name(), capturedParameters.get("state"));
        assertEquals(from, capturedParameters.get("from"));
        assertEquals(size, capturedParameters.get("size"));
    }
}