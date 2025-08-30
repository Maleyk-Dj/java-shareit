package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.ConflictException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto create(User user) {
        if (emailExists(user.getEmail())) {
            throw new ConflictException("Пользователь с email " + user.getEmail() + "уже существует");
        }
        User created = repository.save(user);
        return UserMapper.toDto(created);
    }

    @Override
    public UserDto update(User user) {
        User existingUser = repository.findById(user.getId()).orElseThrow(() -> new NotFoundException("Пользователь с id "
                + user.getId() + " не найден"));
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existingUser.setEmail(user.getEmail());
        }
        User updated = repository.save(existingUser);
        return UserMapper.toDto(updated);

    }

    @Override
    public UserDto getById(Long id) {
        User user = getByIdUser(id);
        return UserMapper.toDto(user);
    }

    @Override
    public User getByIdUser(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id= " + id + " не найден"));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(user -> UserMapper.toDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public User getEntityOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    private boolean emailExists(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }
}
