package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping(path = "/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(SHARER_USER_ID) Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") long itemId) {
        return itemClient.getItemById(itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                             @RequestBody ItemDto dto) {
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                             @PathVariable("itemId") long itemId, @RequestBody ItemDto dto) {
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> foundItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                            @RequestParam Optional<String> text) {
        if (text.isEmpty()) {
            return (ResponseEntity<Object>) Collections.emptyList();
        }
        return itemClient.foundItem(userId, text.get());
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(SHARER_USER_ID) Long authorId,
                                                @PathVariable("itemId") long itemId,
                                                @RequestBody CommentDto dto) {
        return itemClient.createComment(authorId, itemId, dto);
    }
}
