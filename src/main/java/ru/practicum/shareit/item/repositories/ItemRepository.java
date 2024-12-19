package ru.practicum.shareit.item.repositories;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> showItems(Long userId);

    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item checkItem(Item item);

    Item getItemById(Long id);

    long getNextId();
}
