package ru.practicum.shareit.request;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) return null;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        if (request == null) return null;
        List<ItemDtoForRequest> itemDtos = items.stream()
                .map(item -> new ItemDtoForRequest(item.getId(), item.getName(), item.getOwner().getId()))
                .collect(Collectors.toList());
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos);
    }

    public static ItemRequest toEntity(ItemRequestDto dto) {
        if (dto == null) return null;
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setCreated(dto.getCreated());
        return request;
    }
}
