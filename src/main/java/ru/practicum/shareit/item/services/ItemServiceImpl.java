package ru.practicum.shareit.item.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repositories.ItemRepositoryImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ItemRepositoryImpl itemRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository.showItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto dto) {
        return ItemMapper.toItemDto(itemRepository.addItem(userId, ItemMapper.dtoToItem(dto)));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        return ItemMapper.toItemDto(itemRepository.updateItem(userId, itemId, ItemMapper.dtoToItem(dto)));
    }

    @Override
    public List<ItemDto> foundItem(Optional<String> text) {
        if (text.isPresent() && !text.get().isBlank()) {
            return itemRepository.foundItem(text.get()).stream()
                    .map(ItemMapper::toItemDto)
                    .toList();
        }
        return Collections.emptyList();
    }
}
