package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserStorage implements UserStorage {

    protected HashMap<Long, User> users = new HashMap<>();
    protected long currentId = 0;


    @Override
    public User save(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден");
        }
        users.remove(id);
    }
}
