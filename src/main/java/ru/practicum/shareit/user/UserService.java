package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto create(User user) {
        log.info("Создание нового пользователя с email: {}", user.getEmail());
        if (emailExists(user.getEmail())) {
            log.warn("Попытка создать пользователя с существующим email: {}", user.getEmail());
            throw new EmailAlreadyExistsException("Пользователь с email " + user.getEmail() + "уже существует");
        }
        User created = userStorage.save(user);
        log.debug("Пользователь сохранен в хранилище: {}", created);
        return UserMapper.toDto(created);
    }

    public UserDto update(User user) {
        log.info("Обновление пользователя с id: {}", user.getId());
        User existingUser = getByIdUser(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            boolean emailTaken = userStorage.getAll().stream()
                    .anyMatch(u -> !u.getId().equals(existingUser.getId()) &&
                            u.getEmail().equalsIgnoreCase(user.getEmail()));
            if (emailTaken) {
                log.warn("Попытка обновления пользователя с существующим email", user.getEmail());
                throw new EmailAlreadyExistsException("Пользователь с таким email " + user.getEmail() + " уже существует");
            }
            existingUser.setEmail(user.getEmail());
        }
        userStorage.update(existingUser);
        log.debug("Пользователь обновлен в хранилище: ", existingUser);
        return UserMapper.toDto(existingUser);
    }

    public UserDto getById(Long id) {
        log.info("Получение пользователя по id: {}", id);
        User user = getByIdUser(id);
        log.debug("Пользователь получен из хранилища с id:{}", id);
        return UserMapper.toDto(user);
    }

    public User getByIdUser(Long id) {
        return userStorage.getById(id).orElseThrow(() -> new NotFoundException("Пользователь с id= " + id + " не найден"));
    }

    public List<UserDto> getAll() {
        log.info("Получение списка всех пользователей ");
        return userStorage.getAll().stream()
                .map(user -> UserMapper.toDto(user))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        log.info("Удаление пользователя с id:{}", id);
        userStorage.delete(id);
    }

    private boolean emailExists(String email) {
        log.debug("Проверка пользователей с уже существующими email");
        return userStorage.getAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
