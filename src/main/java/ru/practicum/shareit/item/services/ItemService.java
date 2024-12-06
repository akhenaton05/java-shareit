package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto getItemById(long itemId);

    ItemDto createItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto dto);

    List<ItemDto> foundItem(Optional<String> text);
}
