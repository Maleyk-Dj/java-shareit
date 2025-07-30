package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.handler.exception.AccessException;
import ru.practicum.shareit.handler.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.handler.exception.NotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final RuntimeException ex) {
        return new ErrorResponse("Объект не найден", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailConflict(final EmailAlreadyExistsException ex) {
        return new ErrorResponse("Пользователь с таким email существует", ex.getMessage());
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(final AccessException ex) {
        return new ErrorResponse("Доступ запрещен", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        return new ErrorResponse("Ошибка валидации", ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAll(final Throwable ex) {
        return new ErrorResponse("Произошла непредвиденная ошибка", ex.getMessage());
    }
}
