package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

public class BookingDateValidator implements ConstraintValidator<ValidBookingDates, BookItemRequestDto> {

    @Override
    public boolean isValid(BookItemRequestDto booking, ConstraintValidatorContext context) {
        if (booking.getStart() == null || booking.getEnd() == null) return false;

        return booking.getStart().isBefore(booking.getEnd())
                && booking.getStart().isAfter(LocalDateTime.now())
                && booking.getEnd().isAfter(LocalDateTime.now());
    }
}
