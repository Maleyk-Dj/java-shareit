package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    User save(User user);

    User update(User user);

    User getById(Long id);

    List<User> getAll();

    void delete(Long id);
}
