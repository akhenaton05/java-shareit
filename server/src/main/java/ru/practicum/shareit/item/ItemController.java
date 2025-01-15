package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems(@RequestHeader(SHARER_USER_ID) Long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemGetDto getItemById(@PathVariable("itemId") long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID) Long userId,
                              @RequestBody ItemDto dto) {
        return itemService.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(SHARER_USER_ID) Long userId,
                              @PathVariable("itemId") long itemId, @RequestBody ItemDto dto) {
        return itemService.updateItem(userId, itemId, dto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> foundItem(@RequestHeader(SHARER_USER_ID) Long userId, @RequestParam Optional<String> text) {
        return itemService.foundItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentOutDto createComment(@RequestHeader(SHARER_USER_ID) Long authorId,
                                       @PathVariable("itemId") long itemId,
                                       @RequestBody CommentDto dto) {
        return itemService.createComment(authorId, itemId, dto);
    }
}
