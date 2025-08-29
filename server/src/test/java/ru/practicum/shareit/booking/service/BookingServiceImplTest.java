package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private User otherUser; // Для тестов доступа
    private Item availableItem;
    private Item unavailableItem;
    private BookingRequestDto validBookingRequest;
    private Booking waitingBooking; // Создадим бронь для тестов approve/getById

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@example.com"); // Исправлен опечатка
        userRepository.save(owner);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@example.com"); // Исправлен опечатка
        userRepository.save(booker);

        otherUser = new User();
        otherUser.setName("other");
        otherUser.setEmail("other@example.com");
        userRepository.save(otherUser);

        availableItem = new Item();
        availableItem.setName("Drill");
        availableItem.setDescription("A powerful drill");
        availableItem.setAvailable(true);
        availableItem.setOwner(owner);
        itemRepository.save(availableItem);

        unavailableItem = new Item();
        unavailableItem.setName("Unavailable Tool");
        unavailableItem.setDescription("A tool not for rent");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);
        itemRepository.save(unavailableItem);

        validBookingRequest = new BookingRequestDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        waitingBooking = new Booking();
        waitingBooking.setItem(availableItem);
        waitingBooking.setBooker(booker);
        waitingBooking.setStatus(BookingStatus.WAITING);
        waitingBooking.setStart(LocalDateTime.now().plusDays(1));
        waitingBooking.setEnd(LocalDateTime.now().plusDays(2));
        bookingRepository.save(waitingBooking);
    }

    @Test
    void testCreate_Success() {
        BookingResponseDto createdBooking = bookingService.create(booker.getId(), validBookingRequest);

        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals(booker.getId(), createdBooking.getBooker().getId());
        assertEquals(availableItem.getId(), createdBooking.getItem().getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
    }

    @Test
    void testCreate_UnavailableItem_ThrowsValidationException() {
        BookingRequestDto request = new BookingRequestDto(
                unavailableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), request));
    }

    @Test
    void testCreate_BookerIsOwner_ThrowsAccessDeniedException() {
        BookingRequestDto request = new BookingRequestDto(
                availableItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(AccessDeniedException.class, () -> bookingService.create(owner.getId(), request));
    }

    @Test
    void testApprove_Success_Approved() {
        BookingResponseDto approvedBooking = bookingService.approve(owner.getId(), waitingBooking.getId(), true);

        assertNotNull(approvedBooking);
        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
        assertEquals(waitingBooking.getId(), approvedBooking.getId());

        Booking savedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        assertEquals(BookingStatus.APPROVED, savedBooking.getStatus());
    }

    @Test
    void testApprove_Success_Rejected() {
        BookingResponseDto rejectedBooking = bookingService.approve(owner.getId(), waitingBooking.getId(), false);

        assertNotNull(rejectedBooking);
        assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
        assertEquals(waitingBooking.getId(), rejectedBooking.getId());

        Booking savedBooking = bookingRepository.findById(waitingBooking.getId()).orElseThrow();
        assertEquals(BookingStatus.REJECTED, savedBooking.getStatus());
    }

    @Test
    void testApprove_BookingNotFound_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.approve(owner.getId(), 999L, true));
    }

    @Test
    void testApprove_NotOwner_ThrowsAccessDeniedException() {
        assertThrows(AccessDeniedException.class, () -> bookingService.approve(booker.getId(), waitingBooking.getId(), true));
    }

    @Test
    void testApprove_StatusAlreadyChanged_ThrowsValidationException() {
        waitingBooking.setStatus(BookingStatus.APPROVED); // Изменяем статус до вызова approve
        bookingRepository.save(waitingBooking);

        assertThrows(ValidationException.class, () -> bookingService.approve(owner.getId(), waitingBooking.getId(), true));
    }

    @Test
    void testGetById_ByOwner_Success() {
        BookingResponseDto retrievedBooking = bookingService.getById(owner.getId(), waitingBooking.getId());

        assertNotNull(retrievedBooking);
        assertEquals(waitingBooking.getId(), retrievedBooking.getId());
    }

    @Test
    void testGetById_ByBooker_Success() {
        BookingResponseDto retrievedBooking = bookingService.getById(booker.getId(), waitingBooking.getId());

        assertNotNull(retrievedBooking);
        assertEquals(waitingBooking.getId(), retrievedBooking.getId());
        assertEquals(booker.getId(), retrievedBooking.getBooker().getId());
    }

    @Test
    void testGetById_NotFound_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getById(owner.getId(), 999L));
    }

    @Test
    void testGetById_AccessDenied_ThrowsAccessDeniedException() {
        assertThrows(AccessDeniedException.class, () -> bookingService.getById(otherUser.getId(), waitingBooking.getId()));
    }

    @Test
    void testGetAllByBooker_All_Success() {
        List<BookingResponseDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testGetAllByBooker_Future_Success() {
        List<BookingResponseDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.FUTURE);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testGetAllByBooker_Past_NoBookings() {

        Booking pastBooking = new Booking();
        pastBooking.setItem(availableItem);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setStart(LocalDateTime.now().minusDays(3));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(pastBooking);

        List<BookingResponseDto> bookings = bookingService.getAllByBooker(booker.getId(), BookingState.PAST);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testGetAllByBooker_UserNotFound_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllByBooker(999L, BookingState.ALL));
    }

    @Test
    void testGetAllByOwner_All_Success() {
        List<BookingResponseDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(waitingBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testGetAllByOwner_Current_NoBookings() {

        Booking currentBooking = new Booking();
        currentBooking.setItem(availableItem);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        currentBooking.setStart(LocalDateTime.now().minusHours(1));
        currentBooking.setEnd(LocalDateTime.now().plusHours(1));
        bookingRepository.save(currentBooking);

        List<BookingResponseDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void testGetAllByOwner_UserNotFound_ThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(999L, BookingState.ALL));
    }
}