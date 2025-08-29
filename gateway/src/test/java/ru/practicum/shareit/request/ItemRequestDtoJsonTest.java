package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serialize_ok() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                123L,
                "Need a cordless drill",
                LocalDateTime.of(2025, 1, 2, 3, 4, 5)
        );

        var content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(123);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("Need a cordless drill");
        // Boot 3 + JavaTimeModule => ISO-8601 без зоны
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-02T03:04:05");
    }

    @Test
    void deserialize_ok() throws Exception {
        String body = """{"id": 7,
  "description": "Hammer needed",
  "created": "2025-08-30T12:34:56"}""";

        ItemRequestDto dto = json.parseObject(body);

        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getDescription()).isEqualTo("Hammer needed");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 8, 30, 12, 34, 56));
    }
}
