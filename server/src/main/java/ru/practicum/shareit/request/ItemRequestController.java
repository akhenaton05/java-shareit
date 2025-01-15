package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(SHARER_USER_ID) Long authorId, @RequestBody ItemRequestDto dto) {
        return itemRequestService.createItemRequest(authorId, dto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> foundAllItems() {
        return itemRequestService.showAllRequests();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestOutDto> foundItems(@RequestHeader(SHARER_USER_ID) Long authorId) {
        return itemRequestService.showRequests(authorId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestOutDto foundItemsById(@PathVariable("requestId") long requestId) {
        return itemRequestService.showRequestsById(requestId);
    }
}
