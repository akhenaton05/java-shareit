package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemGetDto toItemGetDto(Item item, Booking last, Booking next, List<Comment> comment) {
        return new ItemGetDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                last != null ? last.toString() : null,
                next != null ? next.toString() : null,
                comment
        );
    }

    public static Item dtoToItem(ItemDto dto) {
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(new User());
        item.setRequest(request);
        return item;
    }
}
