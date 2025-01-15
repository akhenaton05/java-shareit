package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.services.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServerImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemDto dto;
    private Item item;
    private ItemGetDto getDto;
    private Booking booking;
    private CommentDto commentDto;
    private CommentOutDto commentOutDto;
    private User user;

    @BeforeEach
    void setUp() {
        dto = new ItemDto();
        dto.setId(1L);
        dto.setName("name");
        dto.setDescription("description");
        dto.setAvailable(true);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(new User());
        item.setRequest(new ItemRequest());
        item.setAvailable(true);

        getDto = new ItemGetDto();
        getDto.setId(1L);
        getDto.setName("Name");
        getDto.setDescription("Descr");
        getDto.setAvailable(true);
        getDto.setRequest(1L);
        getDto.setLastBooking("LAST");
        getDto.setNextBooking("NEXT");
        getDto.setComments(List.of());

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.APPROVED);
        booking.setEnd(LocalDateTime.now().plusDays(1));

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("comment");
        commentDto.setItemId(1L);

        commentOutDto = new CommentOutDto();
        commentOutDto.setId(1L);
        commentOutDto.setCreated("now");
        commentOutDto.setAuthorName("NAME");
        commentOutDto.setText("comment");
        commentOutDto.setItemName("ITEM NAME");

        user = new User();
        user.setId(1L);
    }

    @Test
    void createItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto createdItem = itemService.createItem(1L, dto);

        assertEquals(1L, createdItem.getId());
        assertEquals(dto.getDescription(), createdItem.getDescription());
        assertEquals(dto.getName(), createdItem.getName());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItemWithNoStatus() {
        dto.setAvailable(null);
        Exception exception = assertThrows(ValidateException.class, () -> itemService.createItem(1L, dto));

        String expectedMessage = "Статус отсутствует";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createItemWithNoDescription() {
        dto.setDescription(null);
        Exception exception = assertThrows(ValidateException.class, () -> itemService.createItem(1L, dto));

        String expectedMessage = "Описание отсутствует";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createItemWithNoTitle() {
        dto.setName(null);
        Exception exception = assertThrows(ValidateException.class, () -> itemService.createItem(1L, dto));

        String expectedMessage = "Название отсутствует";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getItems() {
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(List.of(item));

        List<ItemDto> itemsList = itemService.getItems(1L);

        assertEquals(itemsList.size(), 1);
        assertEquals(itemsList.getFirst().getId(), 1L);
        assertEquals(itemsList.getFirst().getDescription(), item.getDescription());
        assertEquals(itemsList.getFirst().getName(), item.getName());
        assertEquals(itemsList.getFirst().getAvailable(), item.getAvailable());

        verify(itemRepository).findByOwnerId(anyLong());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findLastBooking(any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findNextBooking(any(), any()))
                .thenReturn(Collections.emptyList());

        ItemGetDto getItem = itemService.getItemById(1L);

        assertEquals(getItem.getId(), 1L);
        assertEquals(getItem.getDescription(), item.getDescription());
        assertEquals(getItem.getName(), item.getName());
        assertEquals(getItem.getAvailable(), item.getAvailable());
        assertEquals(getItem.getRequest(), item.getRequest().getId());
        assertEquals(getItem.getLastBooking(), booking.toString());
        assertNull(getItem.getNextBooking());

        verify(itemRepository, times(2)).findById(anyLong());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        User userUpdate = new User();
        userUpdate.setId(1L);
        item.setOwner(userUpdate);

        ItemDto getItem = itemService.updateItem(1L, 1L, dto);

        assertEquals(getItem.getId(), 1L);
        assertEquals(getItem.getDescription(), item.getDescription());
        assertEquals(getItem.getName(), item.getName());
        assertEquals(getItem.getAvailable(), item.getAvailable());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateItemWithWrongItemId() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, dto));

        String expectedMessage = "Неверный ID вещи";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateItemWithWrongOwnerId() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        Exception exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, dto));

        String expectedMessage = "Неверный владелец вещи";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void foundItem() {
        when(itemRepository.findByNameOrDescriptionContaining(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> itemList = itemService.foundItem(1L, Optional.of("text"));

        assertEquals(itemList.size(), 1);
        assertEquals(itemList.getFirst().getId(), 1L);
        assertEquals(itemList.getFirst().getDescription(), item.getDescription());
        assertEquals(itemList.getFirst().getName(), item.getName());
        assertEquals(itemList.getFirst().getAvailable(), item.getAvailable());
    }

    @Test
    void createComment() {
        when(bookingRepository.findByBookerIdAndItemId(1L, 1L))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        CommentOutDto comment = itemService.createComment(1L, 1L, commentDto);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
    }
}
