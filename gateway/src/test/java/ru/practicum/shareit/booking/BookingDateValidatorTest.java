package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDateValidatorTest {

    private final BookingDateValidator validator = new BookingDateValidator();

    @Test
    void valid_whenStartBeforeEnd_andBothInFuture() {
        LocalDateTime now = LocalDateTime.now();
        var dto = new BookItemRequestDto(1L, now.plusHours(1), now.plusHours(2));

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void invalid_whenStartNull() {
        LocalDateTime now = LocalDateTime.now();
        var dto = new BookItemRequestDto(1L, null, now.plusHours(2));

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void invalid_whenEndNull() {
        LocalDateTime now = LocalDateTime.now();
        var dto = new BookItemRequestDto(1L, now.plusHours(1), null);

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void invalid_whenStartEqualsEnd() {
        LocalDateTime t = LocalDateTime.now().plusHours(1);
        var dto = new BookItemRequestDto(1L, t, t);

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void invalid_whenStartAfterEnd() {
        LocalDateTime now = LocalDateTime.now();
        var dto = new BookItemRequestDto(1L, now.plusHours(3), now.plusHours(2));

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void invalid_whenStartInPast_orEndInPast() {
        LocalDateTime now = LocalDateTime.now();
        assertFalse(validator.isValid(new BookItemRequestDto(1L, now.minusMinutes(1), now.plusHours(1)), null));
        assertFalse(validator.isValid(new BookItemRequestDto(1L, now.plusHours(1), now.minusMinutes(1)), null));
    }
}