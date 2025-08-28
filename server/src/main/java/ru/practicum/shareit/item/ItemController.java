package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentService;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items by user={}, name={}", userId, itemDto.getName());
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /items/{} by user={}", id, userId);
        return itemService.update(id, itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items owner={}", userId);
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("GET /items/search text='{}'", text);
        return itemService.search(text);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("DELETE /items/{} by user={}", id, userId);
        itemService.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentCreateDto dto) {
        log.info("POST /items/{}/comment by user={}", itemId, userId);
        return commentService.add(itemId, userId, dto);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long id) {
        log.info("GET /items/{} by user={}", id, userId);
        ItemDto dto = itemService.getById(id);
        dto.setComments(commentService.listForItem(id));
        return dto;
    }
}