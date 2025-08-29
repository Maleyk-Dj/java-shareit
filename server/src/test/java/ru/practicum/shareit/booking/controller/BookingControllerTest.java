package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void testCreateBooking() throws Exception {
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto requestDto = new BookingRequestDto(1L, now.plusDays(1), now.plusDays(2));

        BookingResponseDto responseDto = new BookingResponseDto(1L, requestDto.getStart(), requestDto.getEnd(),
                BookingStatus.WAITING, new ItemDto(), new UserDto());

        when(bookingService.create(eq(userId), any(BookingRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
    }

    @Test
    void testApproveBooking() throws Exception {
        long ownerId = 1L;
        long bookingId = 1L;
        BookingResponseDto responseDto = new BookingResponseDto(bookingId, LocalDateTime.now(), LocalDateTime.now(),
                BookingStatus.APPROVED, new ItemDto(), new UserDto());

        when(bookingService.approve(eq(ownerId), eq(bookingId), eq(true)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()));
    }

    @Test
    void testGetBookingById() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        BookingResponseDto responseDto = new BookingResponseDto(bookingId, LocalDateTime.now(), LocalDateTime.now(),
                BookingStatus.WAITING, new ItemDto(), new UserDto());

        when(bookingService.getById(eq(userId), eq(bookingId)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.name()));
    }

    @Test
    void testGetAllBookingsByBooker() throws Exception {
        long userId = 1L;
        BookingResponseDto booking1 = new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING, new ItemDto(), new UserDto());
        BookingResponseDto booking2 = new BookingResponseDto(2L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.APPROVED, new ItemDto(), new UserDto());

        when(bookingService.getAllByBooker(eq(userId), eq(BookingState.ALL)))
                .thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testGetAllBookingsByOwner() throws Exception {
        long ownerId = 1L;
        BookingResponseDto booking1 = new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.WAITING, new ItemDto(), new UserDto());
        BookingResponseDto booking2 = new BookingResponseDto(2L, LocalDateTime.now(), LocalDateTime.now(), BookingStatus.APPROVED, new ItemDto(), new UserDto());

        when(bookingService.getAllByOwner(eq(ownerId), eq(BookingState.ALL)))
                .thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}
