package ru.practicum.shareit.request.services;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long authorId, ItemRequestDto dto);
    List<ItemRequestDto> showAllRequests();
    List<ItemRequestOutDto> showRequests(Long authorId);
    ItemRequestOutDto showRequestsById(Long requestId);
}
