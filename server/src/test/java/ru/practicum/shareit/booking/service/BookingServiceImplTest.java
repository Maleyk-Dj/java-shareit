package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item availableItem;
    private Item unavailableItem;
    private BookingRequestDto validBookingRequest;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@axample.com");
        userRepository.save(owner);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@excample.com");
        userRepository.save(booker);

        availableItem = new Item();
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
}
