package ru.practicum.shareit.booking.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking futureBooking;
    private Booking currentBooking;
    private Booking pastBooking;
    private Booking waitingBooking;
    private Booking rejectedBooking;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com");
        booker = new User(2L, "Booker", "booker@example.com");
        item = new Item(1L, "Test Item", "Description", true, owner, null);

        LocalDateTime now = LocalDateTime.now();

        futureBooking = new Booking(1L, now.plusDays(1), now.plusDays(2), item, booker, BookingStatus.WAITING);
        currentBooking = new Booking(2L, now.minusHours(1), now.plusHours(1), item, booker, BookingStatus.APPROVED);
        pastBooking = new Booking(3L, now.minusDays(2), now.minusDays(1), item, booker, BookingStatus.APPROVED);
        waitingBooking = new Booking(4L, now.plusHours(2), now.plusHours(3), item, booker, BookingStatus.WAITING);
        rejectedBooking = new Booking(5L, now.plusHours(4), now.plusHours(5), item, booker, BookingStatus.REJECTED);
    }

    @Test
    void testGetAllByBooker_All() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong())).thenReturn(List.of(futureBooking, waitingBooking));

        List<BookingResponseDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllByBooker_Current() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(currentBooking));

        List<BookingResponseDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.CURRENT);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByBooker_Past() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(List.of(pastBooking));

        List<BookingResponseDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.PAST);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByBooker_Future() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(List.of(futureBooking));

        List<BookingResponseDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByBooker_Waiting() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any())).thenReturn(List.of(waitingBooking));

        List<BookingResponseDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByBooker_Rejected() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any())).thenReturn(List.of(rejectedBooking));

        List<BookingResponseDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.REJECTED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByOwner_All() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(List.of(futureBooking, waitingBooking));

        List<BookingResponseDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetAllByOwner_Current() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any())).thenReturn(List.of(currentBooking));

        List<BookingResponseDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(currentBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByOwner_Past() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any())).thenReturn(List.of(pastBooking));

        List<BookingResponseDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.PAST);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pastBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByOwner_Future() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any())).thenReturn(List.of(futureBooking));

        List<BookingResponseDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.FUTURE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(futureBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByOwner_Waiting() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any())).thenReturn(List.of(waitingBooking));

        List<BookingResponseDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(waitingBooking.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByOwner_Rejected() {
        when(userService.getEntityOrThrow(anyLong())).thenReturn(owner);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any())).thenReturn(List.of(rejectedBooking));

        List<BookingResponseDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.REJECTED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rejectedBooking.getId(), result.get(0).getId());
    }
}