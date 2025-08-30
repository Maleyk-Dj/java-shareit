package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private BookItemRequestDto bookItemRequestDto;
    private Long userId;
    private Long bookingId;
    private ResponseEntity<Object> mockResponseEntity;

    private Map<String, String> mockResponseBody;

    @BeforeEach
    void setUp() {
        userId = 1L;
        bookingId = 1L;
        bookItemRequestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mockResponseBody = Collections.emptyMap(); // Представляет собой пустой JSON-объект {}

        mockResponseEntity = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);
    }

    @Test
    void createBooking_Success() throws Exception {
        when(bookingClient.create(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void getBookings_AllState_Success() throws Exception {
        when(bookingClient.getBookings(anyLong(), eq(BookingState.ALL), anyInt(), anyInt()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void getBookingById_Success() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void approveBooking_True_Success() throws Exception {
        when(bookingClient.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void approveBooking_False_Success() throws Exception {
        when(bookingClient.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void getBookingsForOwner_CurrentState_Success() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), eq(BookingState.CURRENT), anyInt(), anyInt()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

}