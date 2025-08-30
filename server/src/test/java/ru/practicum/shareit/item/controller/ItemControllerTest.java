package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, null, null, null, null);
    }

    @Test
    void testCreateItem() throws Exception {

        long userId = 1L;
        when(itemService.create(any(ItemDto.class), eq(userId))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"));
    }

    @Test
    void testUpdateItem() throws Exception {

        long userId = 1L;
        long itemId = 1L;
        ItemDto updatedDto = new ItemDto(itemId, "New Drill", "Updated description", false, null, null, null, null, null);

        when(itemService.update(eq(itemId), any(ItemDto.class), eq(userId))).thenReturn(updatedDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("New Drill"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testGetAllByUser() throws Exception {

        long userId = 1L;
        ItemDto item2 = new ItemDto(2L, "Saw", "Hand saw", true, null, null, null, null, null);
        when(itemService.getAllByUser(eq(userId))).thenReturn(List.of(itemDto, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].name").value("Saw"));
    }

    @Test
    void testSearchItems() throws Exception {

        when(itemService.search(anyString())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void testDeleteItem() throws Exception {

        long userId = 1L;
        long itemId = 1L;
        doNothing().when(itemService).delete(anyLong());

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(itemId);
    }

    @Test
    void testAddComment() throws Exception {

        long userId = 1L;
        long itemId = 1L;
        CommentCreateDto commentCreateDto = new CommentCreateDto("Great item!");
        CommentDto commentDto = new CommentDto(1L, "Great item!", "User", LocalDateTime.now());

        when(commentService.add(eq(itemId), eq(userId), any(CommentCreateDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }

    @Test
    void testGetItemById() throws Exception {

        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDtoWithComments = new ItemDto(itemId, "Drill", "Powerful drill", true, null, null, List.of(new CommentDto()), null, null);

        when(itemService.getById(eq(itemId))).thenReturn(itemDtoWithComments);
        when(commentService.listForItem(eq(itemId))).thenReturn(List.of(new CommentDto()));

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.comments", hasSize(1)));
    }
}