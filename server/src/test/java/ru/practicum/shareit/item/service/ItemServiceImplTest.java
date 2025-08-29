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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


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
}
