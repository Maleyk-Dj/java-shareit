package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    public final UserServiceImpl userServiceImpl;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание пользователя c email {}", userDto.getEmail());
        User user = UserMapper.toEntity(userDto);
        return userServiceImpl.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя c id {}", userDto.getId());
        User user = UserMapper.toEntity(userDto);
        user.setId(id);
        return userServiceImpl.update(user);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя по id= ", id);
        return userServiceImpl.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userServiceImpl.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя по id= ", id);
        userServiceImpl.delete(id);
    }
}
