package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemRequestDto = new ItemRequestDto(1L, "I need a new drill.", LocalDateTime.now(), List.of());
    }

    @Test
    void testCreateRequest() throws Exception {

        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void testGetUserItemRequests() throws Exception {

        ItemRequestDto request2 = new ItemRequestDto(2L, "I need a saw.", LocalDateTime.now(), List.of());
        when(itemRequestService.getUserItemRequests(anyLong())).thenReturn(List.of(itemRequestDto, request2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[1].description").value(request2.getDescription()));
    }

    @Test
    void testGetAllItemRequests() throws Exception {

        ItemRequestDto request2 = new ItemRequestDto(2L, "I need a screwdriver.", LocalDateTime.now(), List.of());
        when(itemRequestService.getAllItemRequests(anyLong())).thenReturn(List.of(request2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(request2.getDescription()));
    }

    @Test
    void testGetItemRequestById() throws Exception {

        long requestId = 1L;
        ItemDtoForRequest item1 = new ItemDtoForRequest(1L, "Drill", 2L);
        itemRequestDto.setItems(List.of(item1));

        when(itemRequestService.getItemRequestById(anyLong(), eq(requestId))).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value("Drill"));
    }
}

