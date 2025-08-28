package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = findUser(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        findUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotWithItems(userId);
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        findUser(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        List<Item> items = itemRepository.findByRequestIdOrderByIdAsc(requestId);

        // Собираем DTO и гарантируем, что items — массив, не null
        ItemRequestDto dto = ItemRequestMapper.toDto(request);
        if (dto.getItems() == null) {
            dto.setItems(new ArrayList<>()); // гарантия: не null
        }
        dto.setItems(
                items.stream()
                        .map(i -> new ItemDtoForRequest(i.getId(), i.getName(), i.getOwner().getId()))
                        .toList()
        );
        return dto;
    }

    @Transactional
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
