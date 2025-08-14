package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.handler.exception.AccessDeniedException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public BookingResponseDto create(Long userId, BookingRequestDto request) {
        // получаем сущности из доменных сервисов
        User booker = userService.getEntityOrThrow(userId);
        Item item = itemService.getEntityOrThrow(request.getItemId());

        // бизнес-валидации
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Нельзя бронировать свою вещь");
        }
        // даты уже проверяются @Valid + @ValidBookingDates на DTO

        Booking booking = BookingMapper.toEntity(request, item, booker); // статус = WAITING
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // только владелец вещи может подтверждать / отклонять
        Long realOwnerId = booking.getItem().getOwner().getId();
        if (!realOwnerId.equals(ownerId)) {
            throw new AccessDeniedException("Только владелец вещи может изменить статус бронирования");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменён");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        // доступ: booker или владелец вещи
        boolean allowed = booking.getBooker().getId().equals(userId)
                || booking.getItem().getOwner().getId().equals(userId);
        if (!allowed) {
            throw new AccessDeniedException("Нет доступа к бронированию");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllByBooker(Long userId, BookingState state) {
        // убедимся, что пользователь существует
        userService.getEntityOrThrow(userId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list;
        switch (state) {
            case CURRENT -> list = bookingRepository
                    .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> list = bookingRepository
                    .findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> list = bookingRepository
                    .findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> list = bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> list = bookingRepository
                    .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            case ALL -> list = bookingRepository
                    .findByBookerIdOrderByStartDesc(userId);
            default -> throw new ValidationException("Неизвестное состояние: " + state);
        }
        return list.stream().map(BookingMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllByOwner(Long ownerId, BookingState state) {
        // убедимся, что владелец существует
        userService.getEntityOrThrow(ownerId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> list;

        switch (state) {
            case CURRENT -> list = bookingRepository
                    .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> list = bookingRepository
                    .findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> list = bookingRepository
                    .findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> list = bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> list = bookingRepository
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default -> list = bookingRepository
                    .findByItemOwnerIdOrderByStartDesc(ownerId);
        }

        return list.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
