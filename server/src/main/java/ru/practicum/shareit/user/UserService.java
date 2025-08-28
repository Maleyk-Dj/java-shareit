package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(User user);

    UserDto update(User user);

    UserDto getById(Long id);

    User getByIdUser(Long id);

    List<UserDto> getAll();

    void delete(Long id);

    User getEntityOrThrow(Long id);


}
