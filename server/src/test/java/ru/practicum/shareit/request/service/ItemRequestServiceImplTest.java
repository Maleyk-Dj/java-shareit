package ru.practicum.shareit.request.service;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Test User");
        requestor.setEmail("test@example.com");
        userRepository.save(requestor);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("I need a new drill.");
        itemRequestDto.setCreated(LocalDateTime.now());
    }

    @Test
    void testCreate_Success() {
        ItemRequestDto createdDto = itemRequestService.create(requestor.getId(), itemRequestDto);

        assertNotNull(createdDto);
        assertNotNull(createdDto.getId());
        assertEquals(itemRequestDto.getDescription(), createdDto.getDescription());

        ItemRequest savedRequest = itemRequestRepository.findById(createdDto.getId()).orElse(null);
        assertNotNull(savedRequest);
        assertEquals(requestor.getId(), savedRequest.getRequestor().getId());
    }

    @Test
    void testCreate_UserNotFound_ThrowsException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, itemRequestDto));
    }
}
