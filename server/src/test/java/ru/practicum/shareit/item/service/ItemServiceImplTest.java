package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void testCreateItem_Success_NoRequest() {

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto createdItemDto = itemService.create(itemDto, owner.getId());

        assertNotNull(createdItemDto);
        assertNotNull(createdItemDto.getId());
        assertEquals("Test Item", createdItemDto.getName());
        assertEquals("Test Description", createdItemDto.getDescription());

        Optional<Item> savedItem = itemRepository.findById(createdItemDto.getId());
        assertTrue(savedItem.isPresent());
        assertEquals(owner.getId(), savedItem.get().getOwner().getId());
        assertNull(savedItem.get().getRequest());
    }

    @Test
    void testCreateItem_Success_WithRequest() {

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        userRepository.save(owner);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("I need a new drill.");
        itemRequest.setRequestor(owner);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());

        ItemDto createdItemDto = itemService.create(itemDto, owner.getId());

        assertNotNull(createdItemDto);
        assertNotNull(createdItemDto.getId());

        Optional<Item> savedItem = itemRepository.findById(createdItemDto.getId());
        assertTrue(savedItem.isPresent());
        assertNotNull(savedItem.get().getRequest());
        assertEquals(itemRequest.getId(), savedItem.get().getRequest().getId());
    }

    @Test
    void testCreateItem_RequestNotFound_ThrowsException() {

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(999L); // Несуществующий ID запроса

        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, owner.getId()));
    }

    @Test
    void testUpdate_Success() {

        User owner = new User();
        owner.setName("Malika Dja");
        owner.setEmail("malika.dja@bk.com");
        userRepository.save(owner);

        Item item = new Item();
        item.setName("Laptop");
        item.setDescription("Good laptop");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Laptop");
        updateDto.setDescription("New description");
        updateDto.setAvailable(false);

        itemService.update(item.getId(), updateDto, owner.getId());

        Item updatedItem = itemRepository.findById(item.getId()).orElse(null);

        assertNotNull(updatedItem);
        assertEquals("New Laptop", updatedItem.getName());
        assertEquals("New description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void testUpdate_NotFound() {

        User user = new User();
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        userRepository.save(user);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Non Existent Item");

        assertThrows(NotFoundException.class, () -> itemService.update(999L, updateDto, user.getId()));
    }

    @Test
    void testUpdate_AccessDenied() {

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        userRepository.save(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Item description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        User nonOwner = new User();
        nonOwner.setName("Non Owner");
        nonOwner.setEmail("non.owner@example.com");
        userRepository.save(nonOwner);

        assertThrows(AccessDeniedException.class, () -> itemService.update(item.getId(), new ItemDto(), nonOwner.getId()));
    }

    @Test
    void testGetAllItemRequests() {

        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("one@test.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("two@test.com");
        userRepository.save(user2);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request by User One");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().minusDays(2));
        itemRequestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request by User Two");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request2);

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests(user1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Request by User Two", result.get(0).getDescription());
    }

    @Test
    void testGetItemRequestById() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        userRepository.save(user);

        ItemRequest request = new ItemRequest();
        request.setDescription("I need a drill.");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        itemRequestRepository.save(request);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getItemRequestById(user.getId(), request.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals("I need a drill.", result.getDescription());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Drill", result.getItems().get(0).getName());
    }
}
