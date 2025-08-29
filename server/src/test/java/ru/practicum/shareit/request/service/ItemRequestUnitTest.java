package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {


    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {

        user = new User(1L, "Test User", "test@example.com");
        request1 = new ItemRequest(1L, "Description 1", user, LocalDateTime.now().minusHours(1));
        request2 = new ItemRequest(2L, "Description 2", user, LocalDateTime.now());
    }

    @Test
    void getUserItemRequests_whenRequestsExist_returnsListOfItemRequestDto() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(request2, request1));

        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(user.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request2.getDescription(), result.get(0).getDescription());
        assertEquals(request1.getDescription(), result.get(1).getDescription());
    }

    @Test
    void getUserItemRequests_whenNoRequestsExist_returnsEmptyList() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(user.getId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}