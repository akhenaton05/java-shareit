package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.utils.HttpHeaders;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long authorId, @RequestBody ItemRequestDto dto) {
        return itemRequestService.createItemRequest(authorId, dto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> foundAllItems() {
        return itemRequestService.showAllRequests();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestOutDto> foundItems(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long authorId) {
        return itemRequestService.showRequests(authorId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestOutDto foundItemsById(@PathVariable("requestId") long requestId) {
        return itemRequestService.showRequestsById(requestId);
    }
}
