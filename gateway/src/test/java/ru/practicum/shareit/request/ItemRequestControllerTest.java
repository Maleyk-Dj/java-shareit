package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestDto itemRequestDto;
    private Long userId;
    private Long requestId;
    private ResponseEntity<Object> mockResponseEntity;
    private Map<String, String> mockResponseBody;

    @BeforeEach
    void setUp() {
        userId = 1L;
        requestId = 1L;
        itemRequestDto = new ItemRequestDto(null, "Description for request", LocalDateTime.now());
        mockResponseBody = Collections.emptyMap();
        mockResponseEntity = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);
    }

    @Test
    void createRequest_Success() throws Exception {
        when(itemRequestClient.createItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void createRequest_InvalidDto_BadRequest() throws Exception {
        ItemRequestDto invalidRequestDto = new ItemRequestDto(null, "", LocalDateTime.now()); // Пустое описание

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(invalidRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserItemRequests_Success() throws Exception {
        when(itemRequestClient.getUserItemRequests(anyLong()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void getAllItemRequest_Success() throws Exception {
        when(itemRequestClient.getAllItemRequests(anyLong()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void getItemRequestById_Success() throws Exception {
        when(itemRequestClient.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }
}