package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.services.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemDto dto;
    private ItemGetDto getDto;
    private CommentDto commentDto;
    private CommentOutDto commentOutDto;

    @BeforeEach
    void setUp() {
        dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Name");
        dto.setDescription("Descr");
        dto.setAvailable(true);

        getDto = new ItemGetDto();
        getDto.setId(1L);
        getDto.setName("Name");
        getDto.setDescription("Descr");
        getDto.setAvailable(true);
        getDto.setRequest(1L);
        getDto.setLastBooking("LAST");
        getDto.setNextBooking("NEXT");
        getDto.setComments(List.of());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("comment");
        commentDto.setItemId(1L);

        commentOutDto = new CommentOutDto();
        commentOutDto.setId(1L);
        commentOutDto.setCreated("now");
        commentOutDto.setAuthorName("NAME");
        commentOutDto.setText("TEXT");
        commentOutDto.setItemName("ITEM NAME");
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(anyLong()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].available", is(dto.getAvailable())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong()))
                .thenReturn(getDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(getDto.getName())))
                .andExpect(jsonPath("$.available", is(getDto.getAvailable())))
                .andExpect(jsonPath("$.description", is(getDto.getDescription())))
                .andExpect(jsonPath("$.nextBooking", is(getDto.getNextBooking())))
                .andExpect(jsonPath("$.lastBooking", is(getDto.getLastBooking())));
    }

    @Test
    void getItemWithSearch() throws Exception {
        when(itemService.foundItem(anyLong(), any()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/items/search?text=abc")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].available", is(dto.getAvailable())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(dto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.available", is(dto.getAvailable())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentOutDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName", is(commentOutDto.getItemName())))
                .andExpect(jsonPath("$.text", is(commentOutDto.getText())))
                .andExpect(jsonPath("$.created", is(commentOutDto.getCreated())))
                .andExpect(jsonPath("$.authorName", is(commentOutDto.getAuthorName())));
    }
}
