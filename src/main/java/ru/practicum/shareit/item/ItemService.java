package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto create(ItemDto itemDto, Long userId) {
        log.info("Создаем новую вещь от пользователя с id: {}", userId);
        User owner = userStorage.getById(userId);
        log.info("Нашли владельца вещи {}", userId);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        Item saved = itemStorage.save(item);
        log.info("Вещь создана в хранилище");
        return ItemMapper.toItemDto(saved);
    }

    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        log.info("Обновляем вещь с id: {}", itemId);
        Item existing = itemStorage.get(itemId);
        log.debug("Получили вещь {},{} из хранилища", existing.getName(), existing.getId());
        if (!existing.getOwner().getId().equals(userId)) {
            log.warn("Пользователь {} попытался изменить вещь {}, он не является владельцем", userId, itemId);
            throw new UserNotOwnerException("Изменять вещь может только владелец");
        }
        updateItemFields(existing, itemDto);
        itemStorage.update(existing);
        log.debug("Вещь обновлена в хранилище {}", existing.getName());
        return ItemMapper.toItemDto(existing);

    }

    public ItemDto getById(Long itemId) {
        log.info("Получение вещи по id: {}", itemId);
        Item item = itemStorage.get(itemId);
        log.debug("Вещь получена из хранилища с id: {}", itemId);
        return ItemMapper.toItemDto(item);
    }


    public List<ItemDto> getAllByUser(Long userId) {
        log.info("Получаем список вещей одного пользователя с id: {}", userId);
        return itemStorage.getAllByUser(userId).stream()
                .map(item -> ItemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        log.info("Удаляем вещь по id: {}", id);
        itemStorage.delete(id);
    }

    public List<ItemDto> search(String text) {
        log.info("Поиск вещи по имени: {}", text);
        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
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
