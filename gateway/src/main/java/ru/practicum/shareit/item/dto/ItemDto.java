package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {


    private Long id;
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255)
    private String name;
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 512)
    private String description;
    @NotNull(message = "Available must be specified")
    private Boolean available;
    private Long requestId;

}
