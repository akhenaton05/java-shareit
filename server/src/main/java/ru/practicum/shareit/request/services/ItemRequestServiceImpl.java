package ru.practicum.shareit.request.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestAnswer;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long authorId, ItemRequestDto dto) {
        User user = userRepository.findById(authorId).orElseThrow(NotFoundException::new);
        ItemRequest request = ItemRequestMapper.dtoToItemRequest(dto, user);

        itemRequestRepository.save(request);
        return ItemRequestMapper.requestToDto(request);
    }

    @Override
    public List<ItemRequestDto> showAllRequests() {
        return itemRequestRepository.findAll().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .map(ItemRequestMapper::requestToDto)
                .toList();
    }

    @Override
    public List<ItemRequestOutDto> showRequests(Long authorId) {
        User user = userRepository.findById(authorId).orElseThrow(NotFoundException::new);

        List<ItemRequest> requests = itemRequestRepository.findByRequesterId(user.getId());

        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findByRequestIds(
                requests.stream().map(ItemRequest::getId).toList()
        );

        Map<Long, List<ItemRequestAnswer>> answersByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(
                                item -> new ItemRequestAnswer(item.getId(), item.getName(), item.getOwner().getId()),
                                Collectors.toList()
                        )
                ));

        return requests.stream()
                .map(req -> ItemRequestMapper.dtoToRequestOut(
                        req, answersByRequestId.getOrDefault(req.getId(), List.of())
                ))
                .toList();
    }

    @Override
    public ItemRequestOutDto showRequestsById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        List<ItemRequestAnswer> answers = itemRepository.findByRequestId(request.getId()).stream()
                .map(ItemRequestMapper::itemToAnswer)
                .toList();

        return ItemRequestMapper.dtoToRequestOut(request, answers);
    }
}
