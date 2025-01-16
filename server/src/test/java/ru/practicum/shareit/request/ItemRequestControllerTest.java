package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequestAnswer;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto dto;
    private ItemRequestOutDto outDto;
    private Item item = new Item();
    private User user = new User();
    private ItemRequestAnswer requestAnswer;

    @BeforeEach
    void setUp() {
        item.setId(1L);
        user.setId(1L);

        requestAnswer = new ItemRequestAnswer();
        requestAnswer.setOwnerId(1L);
        requestAnswer.setName("name");
        requestAnswer.setItemId(1L);

        outDto = new ItemRequestOutDto();
        outDto.setId(1L);
        outDto.setItems(List.of(requestAnswer));
        outDto.setRequester(user);
        outDto.setCreated(LocalDateTime.now().toString());
        outDto.setDescription("description");

        dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setRequester(user);
        dto.setDescription("description");
        dto.setCreated(LocalDateTime.now().toString());
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(Integer.parseInt(String.valueOf(dto.getId())))))
                .andExpect(jsonPath("$.requester.id", is(Integer.parseInt(String.valueOf(dto.getRequester().getId())))))
                .andExpect(jsonPath("$.requester.email", is(dto.getRequester().getEmail())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }

    @Test
    void foundAllItems() throws Exception {
        when(itemRequestService.showAllRequests())
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(Integer.parseInt(String.valueOf(dto.getId())))))
                .andExpect(jsonPath("$[0].requester.id", is(Integer.parseInt(String.valueOf(dto.getRequester().getId())))))
                .andExpect(jsonPath("$[0].requester.email", is(dto.getRequester().getEmail())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())));
    }

    @Test
    void foundItems() throws Exception {
        when(itemRequestService.showRequests(anyLong()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(Integer.parseInt(String.valueOf(outDto.getId())))))
                .andExpect(jsonPath("$[0].requester.id", is(Integer.parseInt(String.valueOf(outDto.getRequester().getId())))))
                .andExpect(jsonPath("$[0].requester.email", is(outDto.getRequester().getEmail())))
                .andExpect(jsonPath("$[0].description", is(outDto.getDescription())));
    }

    @Test
    void foundItemsById() throws Exception {
        when(itemRequestService.showRequestsById(anyLong()))
                .thenReturn(outDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(Integer.parseInt(String.valueOf(outDto.getId())))))
                .andExpect(jsonPath("$.requester.id", is(Integer.parseInt(String.valueOf(outDto.getRequester().getId())))))
                .andExpect(jsonPath("$.requester.email", is(outDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.description", is(outDto.getDescription())));
    }
}
