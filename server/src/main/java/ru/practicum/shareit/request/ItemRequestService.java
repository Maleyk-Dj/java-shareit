package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
