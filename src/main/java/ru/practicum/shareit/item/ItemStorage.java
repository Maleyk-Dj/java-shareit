package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {

    Item save(Item item);

    Item update(Item item);

    Item get(Long id);

    List<Item> getAllByUser(Long userId);

    void delete(Long id);

    List<Item> search(String text);
}
