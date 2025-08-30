package ru.practicum.shareit.request;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void valid_whenDescriptionNotBlank() {
        ItemRequestDto dto = new ItemRequestDto(
                1L, "Need ladder", LocalDateTime.now()
        );
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void invalid_whenDescriptionBlank_orNull() {
        ItemRequestDto blank = new ItemRequestDto(1L, "   ", LocalDateTime.now());
        ItemRequestDto empty = new ItemRequestDto(1L, "", LocalDateTime.now());
        ItemRequestDto nulll = new ItemRequestDto(1L, null, LocalDateTime.now());

        assertThat(validator.validate(blank)).isNotEmpty();
        assertThat(validator.validate(empty)).isNotEmpty();
        assertThat(validator.validate(nulll)).isNotEmpty();

        // Дополнительно — проверим, что именно поле "description" нарушает правило
        var violations = validator.validate(empty);
        assertThat(violations.iterator().next().getPropertyPath().toString())
                .isEqualTo("description");
    }
}