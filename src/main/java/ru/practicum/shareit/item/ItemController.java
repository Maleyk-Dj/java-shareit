package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на создание вещи с id {}", itemDto.getId());
        return itemService.create(itemDto, userId);

    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на обновление вещи с id {}", userId);
        return itemService.update(id, itemDto, userId);

    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) {
        log.info("Получен запрос на получение вещи по id {}", id);
        return itemService.getById(id);
    }

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка вещей пользователя с id {}", userId);
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameContains(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи с именем {}", text);
        return itemService.search(text);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос на удаление вещи с id {}", id);
        itemService.delete(id);
    }
}
