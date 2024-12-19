package ru.practicum.shareit.item.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repositories.UserRepositoryImpl;

import java.util.*;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    @Autowired
    private UserRepositoryImpl userRepository;
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> showItems(Long userId) {
        return new ArrayList<Item>(items.values()).stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .toList();
    }

    @Override
    public Item addItem(Long userId, Item item) {
        checkItem(item);
        Item itemToAdd = new Item(getNextId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userRepository.getUserById(userId),
                new ItemRequest());
        items.put(itemToAdd.getId(), itemToAdd);
        return itemToAdd;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Неверный ID вещи");
        }
        if (!Objects.equals(items.get(itemId).getOwner().getId(), userId)) {
            throw new NotFoundException("Неверный владелец вещи");
        }
        if (Objects.nonNull(item.getName())) {
            items.get(itemId).setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription())) {
            items.get(itemId).setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getAvailable())) {
            items.get(itemId).setAvailable(item.getAvailable());
        }
        items.put(itemId, items.get(itemId));
        return items.get(itemId);
    }

    @Override
    public Item getItemById(Long id) {
        if (items.get(id) != null) {
            return items.get(id);
        }
        return null;
    }

    public Item checkItem(Item item) {
        if (Objects.isNull(item.getName()) || item.getName().isEmpty()) {
            throw new ValidateException("Название отсутствует");
        }
        if (Objects.isNull(item.getDescription()) || item.getDescription().isEmpty()) {
            throw new ValidateException("Описание отсутствует");
        }
        if (Objects.isNull(item.getAvailable())) {
            throw new ValidateException("Статус отсутствует");
        }
        return item;
    }

    public List<Item> foundItem(String text) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getName().equalsIgnoreCase(text) ||
                    item.getName().contains(text) ||
                    item.getDescription().equalsIgnoreCase(text) ||
                    item.getDescription().contains(text)) {
                if (item.getAvailable()) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
