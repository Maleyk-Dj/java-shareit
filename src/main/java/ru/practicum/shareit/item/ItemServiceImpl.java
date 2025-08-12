package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserServiceImpl userServiceImpl;


    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userServiceImpl.getByIdUser(userId);
        Item item = ItemMapper.toEntity(itemDto, owner);
        Item saved = itemRepository.save(item);
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        Item existing = getByIdItem(itemId);
        if (!existing.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Изменять вещь может только владелец");
        }
        updateItemFields(existing, itemDto);
        return ItemMapper.toDto(existing);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = getByIdItem(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public Item getByIdItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> getAllByUser(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return ItemMapper.toDto(items);
    }

    @Override
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of(); // <- пустой список, как требуют тесты
        }
        return itemRepository.search(text.trim())
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public Item getEntityOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
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
