package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {

        owner = new User(1L, "Owner", "owner@example.com");
        item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
    }

    @Test
    void testGetById_Success() {

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto foundItem = itemService.getById(item.getId());

        assertNotNull(foundItem);
        assertEquals(item.getId(), foundItem.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void testGetAllByUser_ReturnsListOfItems() {

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getAllByUser(owner.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        verify(itemRepository, times(1)).findAllByOwnerId(owner.getId());
    }

    @Test
    void testGetAllByUser_ReturnsEmptyList() {

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemDto> items = itemService.getAllByUser(owner.getId());

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void testDelete_Success() {

        itemService.delete(item.getId());

        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    @Test
    void testSearch_WithText_ReturnsMatchingItems() {

        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> items = itemService.search("drill");

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        verify(itemRepository, times(1)).search("drill");
    }

    @Test
    void testSearch_WithEmptyText_ReturnsEmptyList() {

        List<ItemDto> items = itemService.search("");

        assertNotNull(items);
        assertTrue(items.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void testSearch_WithBlankText_ReturnsEmptyList() {

        List<ItemDto> items = itemService.search("   ");

        assertNotNull(items);
        assertTrue(items.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }

    @Test
    void testSearch_WithNullText_ReturnsEmptyList() {

        List<ItemDto> items = itemService.search(null);

        assertNotNull(items);
        assertTrue(items.isEmpty());
        verify(itemRepository, never()).search(anyString());
    }
}