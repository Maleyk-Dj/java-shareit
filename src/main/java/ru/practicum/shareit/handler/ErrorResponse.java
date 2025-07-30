package ru.practicum.shareit.handler;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
}
