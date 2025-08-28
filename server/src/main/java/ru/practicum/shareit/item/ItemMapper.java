package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequest(ItemRequestMapper.toDto(item.getRequest())); // ok if null
        return dto;
    }

    public static Item toEntity(ItemDto itemDto, User owner) {
        if (itemDto == null) return null;
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        // НЕ создаем новый request здесь; резолвим в сервисе по requestId
        return item;
    }


    public static List<ItemDto> toDto(Collection<Item> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
