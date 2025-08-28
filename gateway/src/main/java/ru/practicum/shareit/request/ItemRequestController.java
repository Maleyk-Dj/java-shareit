package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Created request {} by user {}", requestDto, userId);
        return requestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting all requests for user {}", userId);
        return requestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting all requests made by other users for user {}", userId);
        return requestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable @Positive Long requestId) {
        log.info("Getting request {} for user {}", requestId, userId);
        return requestClient.getItemRequestById(userId, requestId);
    }
}
