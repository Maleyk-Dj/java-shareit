package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.AccessException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("Создаем новую вещь от пользователя с id: {}", userId);
        User owner = userService.getByIdUser(userId);
        log.info("Нашли владельца вещи {}", userId);
        Item item = ItemMapper.toEntity(itemDto, owner);
        Item saved = itemStorage.save(item);
        log.info("Вещь создана в хранилище");
        return ItemMapper.toDto(saved);
    }

    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        log.info("Обновляем вещь с id: {}", itemId);
        Item existing = getByIdItem(itemId);
        log.debug("Получили вещь {},{} из хранилища", existing.getName(), existing.getId());
        if (!existing.getOwner().getId().equals(userId)) {
            log.warn("Пользователь {} попытался изменить вещь {}, он не является владельцем", userId, itemId);
            throw new AccessException("Изменять вещь может только владелец");
        }
        updateItemFields(existing, itemDto);
        itemStorage.update(existing);
        log.debug("Вещь обновлена в хранилище {}", existing.getName());
        return ItemMapper.toDto(existing);
    }

    public ItemDto getById(Long itemId) {
        log.info("Получение вещи по id: {}", itemId);
        Item item = getByIdItem(itemId);
        log.debug("Вещь получена из хранилища с id: {}", itemId);
        return ItemMapper.toDto(item);
    }

    public Item getByIdItem(Long itemId) {
        return itemStorage.get(itemId).orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }


    public List<ItemDto> getAllByUser(Long userId) {
        log.info("Получаем список вещей одного пользователя с id: {}", userId);
        List<Item> items = itemStorage.getAllByUser(userId);
        return ItemMapper.toDto(items);
    }

    public void delete(Long id) {
        log.info("Удаляем вещь по id: {}", id);
        itemStorage.delete(id);
    }

    public List<ItemDto> search(String text) {
        log.info("Поиск вещи по имени: {}", text);
        return itemStorage.findByNameContains(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private Item updateItemFields(Item item, ItemDto dto) {
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return item;
    }
}
