package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.handler.exception.NotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    protected HashMap<Long, Item> items = new HashMap<>();
    protected long currentId = 0;

    @Override
    public Item save(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NotFoundException("Вещь с указанным id= " + item.getId() + " не найден");
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }


    @Override
    public List<Item> getAllByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Вещь с указанным id= " + id + " не найден");
        }
        items.remove(id);
    }

    public List<Item> findByNameContains(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lower = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
