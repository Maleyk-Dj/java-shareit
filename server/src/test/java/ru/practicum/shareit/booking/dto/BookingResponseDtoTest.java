package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingResponseDtoSerialization() throws IOException {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 12, 0);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");

        UserDto bookerDto = new UserDto();
        bookerDto.setId(2L);
        bookerDto.setName("Test Booker");

        BookingResponseDto dto = new BookingResponseDto(
                1L,
                start,
                end,
                BookingStatus.APPROVED,
                itemDto,
                bookerDto
        );

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1); // <-- Исправлено здесь

        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T10:00:00");

        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T12:00:00");

        assertThat(result).hasJsonPathStringValue("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        assertThat(result).hasJsonPathNumberValue("$.item.id");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1); // <-- Исправлено здесь

        assertThat(result).hasJsonPathNumberValue("$.booker.id");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2); // <-- Исправлено здесь
    }
}