package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating item {} by user {}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long id,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Updating item {} by user {}", id, userId);
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all items for user {}", userId);
        return itemClient.getAllByUser(userId);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Searching items with text {}", text);
        return itemClient.search(text);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Deleting item {} for user {}", id, userId);
        return itemClient.deleteItem(userId, id);
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody @Valid CommentCreateDto dto) {
        log.info("Adding comment {} by user {} for item {}", dto, userId, itemId);
        return itemClient.addComment(userId, itemId, dto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable @Positive Long id) {
        log.info("Getting item {} by user {}", id, userId);
        return itemClient.getItemById(userId, id);
    }


}
