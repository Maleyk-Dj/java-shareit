package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

public final class BookingMapper {
    private BookingMapper() {
    }

    public static Booking toEntity(BookingRequestDto dto, Item item, User booker) {
        Booking b = new Booking();
        b.setStart(dto.getStart());
        b.setEnd(dto.getEnd());
        b.setItem(item);
        b.setBooker(booker);
        b.setStatus(BookingStatus.WAITING);
        return b;
    }

    public static BookingResponseDto toDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(ItemMapper.toDto(booking.getItem()));
        dto.setBooker(UserMapper.toDto(booking.getBooker()));
        return dto;
    }
}
