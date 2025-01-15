package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.*;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemGetDto getItemById(long itemId);

    ItemDto createItem(Long userId, ItemDto dto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto dto);

    List<ItemDto> foundItem(Optional<String> text);

    CommentOutDto createComment(Long authorId, Long itemId, CommentDto dto);
}
