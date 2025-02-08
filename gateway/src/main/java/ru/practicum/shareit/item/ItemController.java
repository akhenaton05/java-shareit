package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.HttpHeaders;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping(path = "/items")
@Slf4j
@Validated
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") long itemId) {
        return itemClient.getItemById(itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                             @RequestBody ItemDto dto) {
        return itemClient.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                             @PathVariable("itemId") long itemId, @RequestBody ItemDto dto) {
        return itemClient.updateItem(userId, itemId, dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> foundItem(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                            @RequestParam Optional<String> text) {
        if (text.isEmpty()) {
            return (ResponseEntity<Object>) Collections.emptyList();
        }
        return itemClient.foundItem(userId, text.get());
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long authorId,
                                                @PathVariable("itemId") long itemId,
                                                @RequestBody CommentDto dto) {
        return itemClient.createComment(authorId, itemId, dto);
    }
}
