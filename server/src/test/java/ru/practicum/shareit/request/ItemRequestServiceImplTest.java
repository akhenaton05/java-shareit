package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestAnswer;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.request.services.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user = new User();
    private Item item = new Item();
    private ItemRequestDto dto;
    private ItemRequestOutDto outDto;
    private ItemRequest request;
    private ItemRequestAnswer answer;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        request = new ItemRequest();
        request.setCreated(LocalDateTime.now().plusDays(1));
        request.setDescription("desc");
        request.setId(1L);
        request.setRequester(user);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(user);
        item.setRequest(request);
        item.setAvailable(true);

        dto = new ItemRequestDto();
        dto.setCreated(LocalDateTime.now().plusDays(1).toString());
        dto.setRequester(user);
        dto.setId(1L);
        dto.setDescription("description");

        answer = new ItemRequestAnswer();
        answer.setItemId(1L);
        answer.setOwnerId(1L);
        answer.setName("name");

        outDto = new ItemRequestOutDto();
        outDto.setId(1L);
        outDto.setItems(List.of(answer));
        outDto.setDescription("description");
        outDto.setCreated(LocalDateTime.now().plusDays(1).toString());
        outDto.setRequester(user);
    }

    @Test
    void createRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        ItemRequestDto createdRequest = itemRequestService.createItemRequest(1L, dto);

        assertEquals(1L, createdRequest.getId());
        assertEquals(createdRequest.getRequester(), dto.getRequester());
        assertEquals(createdRequest.getDescription(), dto.getDescription());

        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void showAllRequests() {
        when(itemRequestRepository.findAll())
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = itemRequestService.showAllRequests();

        assertEquals(1L, requests.getFirst().getId());
        assertEquals(requests.getFirst().getRequester(), request.getRequester());
        assertEquals(requests.getFirst().getDescription(), request.getDescription());
    }

    @Test
    void showRequests() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findByRequesterId(anyLong()))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIds(any()))
                .thenReturn(List.of(item));

        List<ItemRequestOutDto> requests = itemRequestService.showRequests(1L);

        assertEquals(1L, requests.getFirst().getId());
        assertEquals(requests.getFirst().getRequester(), request.getRequester());
        assertEquals(requests.getFirst().getDescription(), request.getDescription());
    }

    @Test
    void showRequestsById() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));
        when(itemRepository.findByRequestId(any()))
                .thenReturn(List.of(item));

        ItemRequestOutDto requests = itemRequestService.showRequestsById(1L);

        assertEquals(1L, requests.getId());
        assertEquals(requests.getRequester(), request.getRequester());
        assertEquals(requests.getDescription(), request.getDescription());
    }
}
