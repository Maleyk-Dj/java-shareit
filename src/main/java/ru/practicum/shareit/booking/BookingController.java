package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.handler.exception.ValidationException;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    // 1) Создание брони (статус WAITING)
    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid BookingRequestDto request) {
        return bookingService.create(userId, request);
    }

    // 2) Подтверждение/отклонение брони владельцем
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam("approved") boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    // 3) Получение брони по id (доступно бронирующему или владельцу вещи)
    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    // 4) Список всех броней текущего пользователя (booker), с фильтром state
    @GetMapping
    public List<BookingResponseDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                   String state) {
        return bookingService.getAllByBooker(userId, parseState(state));
    }

    // 5) Список броней для всех вещей текущего владельца, с фильтром state
    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                  String state) {
        return bookingService.getAllByOwner(ownerId, parseState(state));
    }

    // утилита парсинга state из строки (ALL/CURRENT/PAST/FUTURE/WAITING/REJECTED) ---
    private BookingState parseState(String raw) {
        if (raw == null || raw.isBlank()) return BookingState.ALL;
        try {
            return BookingState.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + raw);
        }
    }
}

