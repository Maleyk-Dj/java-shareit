package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.ValidBookingDates;

import java.time.LocalDateTime;

@Getter
@Setter
@ValidBookingDates
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
