package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.UserNotOwnerException;
import ru.practicum.shareit.user.EmailAlreadyExistsException;
import ru.practicum.shareit.user.UserNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final RuntimeException ex) {
        return new ErrorResponse("Пользователь не найден", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleEmailConflict(final EmailAlreadyExistsException ex) {
        return new ErrorResponse("Пользователь с таким email существует", ex.getMessage());
    }

    @ExceptionHandler(UserNotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(final UserNotOwnerException ex) {
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
