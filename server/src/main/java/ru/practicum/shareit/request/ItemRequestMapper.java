package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestAnswer;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest dtoToItemRequest(ItemRequestDto dto, User user) {
        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setRequester(user);

        return request;
    }

    public static ItemRequestDto requestToDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setRequester(request.getRequester());
        dto.setCreated(request.getCreated().toString());

        return dto;
    }

    public static ItemRequestOutDto dtoToRequestOut(ItemRequest request, List<ItemRequestAnswer> answers) {
        ItemRequestOutDto out = new ItemRequestOutDto();
        out.setId(request.getId());
        out.setDescription(request.getDescription());
        out.setRequester(request.getRequester());
        out.setCreated(request.getCreated().toString());
        out.setItems(answers);

        return out;
    }

    public static ItemRequestAnswer itemToAnswer(Item item) {
        ItemRequestAnswer answer = new ItemRequestAnswer();
        answer.setItemId(item.getId());
        answer.setName(item.getName());
        answer.setOwnerId(item.getOwner().getId());

        return answer;
    }
}
