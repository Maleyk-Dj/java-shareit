package ru.practicum.shareit.item;


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
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemDto itemDto;
    private CommentCreateDto commentCreateDto;
    private Long userId;
    private Long itemId;
    private ResponseEntity<Object> mockResponseEntity;
    private Map<String, String> mockResponseBody;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 1L;
        itemDto = new ItemDto(null, "Test Item", "Test Description", true, null);
        commentCreateDto = new CommentCreateDto("Test comment text");
        mockResponseBody = Collections.emptyMap();
        mockResponseEntity = new ResponseEntity<>(mockResponseBody, HttpStatus.OK);
    }

    @Test
    void createItem_Success() throws Exception {
        when(itemClient.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void createItem_InvalidDto_BadRequest() throws Exception {
        ItemDto invalidItemDto = new ItemDto(null, "", "Description", true, null); // Пустое имя

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(invalidItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_Success() throws Exception {
        ItemDto updateDto = new ItemDto(null, "Updated Name", "Updated Desc", false, null);
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void getAllByUser_Success() throws Exception {
        when(itemClient.getAllByUser(anyLong()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void search_Success() throws Exception {
        String searchText = "text";
        when(itemClient.search(anyString()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void deleteItem_Success() throws Exception {
        when(itemClient.deleteItem(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void addComment_Success() throws Exception {
        when(itemClient.addComment(anyLong(), anyLong(), any(CommentCreateDto.class)))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

    @Test
    void addComment_InvalidDto_BadRequest() throws Exception {
        CommentCreateDto invalidCommentDto = new CommentCreateDto("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(invalidCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_Success() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(mockResponseEntity);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponseBody)));
    }

}
