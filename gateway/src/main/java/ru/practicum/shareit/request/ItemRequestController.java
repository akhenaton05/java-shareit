package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @GetMapping
    public ResponseEntity<Object> foundItems(@RequestHeader(SHARER_USER_ID) Long authorId) {
        return itemRequestClient.showRequests(authorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> foundAllItems() {
        return itemRequestClient.showAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> foundItemsById(@PathVariable("requestId") long requestId) {
        return itemRequestClient.showRequestsById(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(SHARER_USER_ID) Long authorId, @RequestBody ItemRequestDto dto) {
        return itemRequestClient.createItemRequest(authorId, dto);
    }
}
