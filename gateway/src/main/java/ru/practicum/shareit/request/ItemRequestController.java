package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.HttpHeaders;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> foundItems(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long authorId) {
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
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long authorId, @RequestBody ItemRequestDto dto) {
        return itemRequestClient.createItemRequest(authorId, dto);
    }
}
