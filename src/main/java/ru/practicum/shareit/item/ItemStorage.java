package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item save(Item item);

    Item update(Item item);

    Optional<Item> get(Long id);

    List<Item> getAllByUser(Long userId);

    void delete(Long id);

    List<Item> findByNameContains(String text);
}
