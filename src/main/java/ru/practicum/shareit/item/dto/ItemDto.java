package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private ItemRequestDto request;
}
