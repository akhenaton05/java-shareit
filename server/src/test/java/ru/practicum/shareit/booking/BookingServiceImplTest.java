package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.services.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingInDto inDto;
    private BookingOutDto outDto;
    private Item item;
    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(new User());
        item.setRequest(new ItemRequest());
        item.setAvailable(true);

        inDto = new BookingInDto();
        inDto.setId(1L);
        inDto.setItemId(1L);
        inDto.setEnd(LocalDateTime.now().plusDays(2).toString());
        inDto.setStart(LocalDateTime.now().plusDays(1).toString());

        outDto = new BookingOutDto();
        outDto.setId(1L);
        outDto.setStatus(Status.APPROVED);
        outDto.setBooker(user);
        outDto.setItem(item);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.APPROVED);
        booking.setEnd(LocalDateTime.now());
    }

    @Test
    void createBooking() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingOutDto createdBooking = bookingService.createBooking(1L, inDto);

        assertEquals(1L, createdBooking.getId());
        assertEquals(inDto.getStart(), createdBooking.getStart());
        assertEquals(inDto.getEnd(), createdBooking.getEnd());
        assertEquals(inDto.getItemId(), createdBooking.getItem().getId());
    }

    @Test
    void createBookingWithWrongStartTime() {
        inDto.setStart(LocalDateTime.now().minusDays(2).toString());

        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(ValidateException.class, () -> bookingService.createBooking(1L, inDto));

        String expectedMessage = "Начальное время не может быть в прошлом";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingWithWrongUserId() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, inDto));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingForNotAvailableItem() {
        item.setAvailable(false);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        Exception exception = assertThrows(ValidateException.class, () -> bookingService.createBooking(1L, inDto));

        String expectedMessage = "Запрашиваемый предмет недоступен";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));

        verify(userRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById() {
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        BookingOutDto foundBooking = bookingService.getBookingById(1L, 1L);

        assertEquals(1L, foundBooking.getId());
        assertEquals(foundBooking.getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getStatus(), booking.getStatus());
    }

    @Test
    void getBookingByIdWithWrongBooker() {
        user.setId(2L);
        item.setId(2L);
        booking.setBooker(user);
        booking.setItem(item);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        Exception exception = assertThrows(ValidateException.class, () -> bookingService.getBookingById(1L, 1L));

        String expectedMessage = "Неверный Id владельца или бронирующего";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));
    }

    @Test
    void getBookingByState() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(bookingRepository.findAllByBookerId(any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundBookingsByState(1L, Optional.of("ALL"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void getBookingWithWrongState() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> bookingService.foundBookingsByState(1L, Optional.of("asda")));
    }

    @Test
    void foundUsersBookingsByStateAll() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findByAllItems(any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundUsersBookingsByState(1L, Optional.of("ALL"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void foundUsersBookingsByStateCurrent() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findByItemsAndStatus(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundUsersBookingsByState(1L, Optional.of("CURRENT"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void foundUsersBookingsByStatePast() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByBookerIdAndPast(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundUsersBookingsByState(1L, Optional.of("PAST"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void foundUsersBookingsByStateFuture() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByBookerIdAndFuture(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundUsersBookingsByState(1L, Optional.of("FUTURE"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void foundUsersBookingsByStateWaiting() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findByItemsAndStatus(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundUsersBookingsByState(1L, Optional.of("WAITING"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void foundUsersBookingsByStateRejected() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findByItemsAndStatus(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingOutDto> foundBooking = bookingService.foundUsersBookingsByState(1L, Optional.of("REJECTED"));

        assertEquals(1L, foundBooking.getFirst().getId());
        assertEquals(foundBooking.getFirst().getBooker().getId(), booking.getBooker().getId());
        assertEquals(foundBooking.getFirst().getStatus(), booking.getStatus());
    }

    @Test
    void foundUsersBookingsByStateWithWrongItem() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        when(itemRepository.findByOwnerId(any()))
                .thenReturn(List.of());

        Exception exception = assertThrows(ValidateException.class, () -> bookingService.foundUsersBookingsByState(1L, Optional.of("ALL")));

        String expectedMessage = "У пользователя нету вещей";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));
    }

    @Test
    void reviewBookingWithWrongOwner() {
        booking.setItem(item);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        Exception exception = assertThrows(ValidateException.class, () -> bookingService.reviewBooking(1L, 1L, true));

        String expectedMessage = "Пользователь не является владельцем вещи";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage, equalTo(expectedMessage));
    }

    @Test
    void reviewBooking() {
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        item.setOwner(user);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingOutDto resultDto = bookingService.reviewBooking(1L, 1L, true);

        assertEquals(1L, resultDto.getId());
        assertEquals(resultDto.getStart(), booking.getStart().toString());
        assertEquals(resultDto.getEnd(), booking.getEnd().toString());
        assertEquals(resultDto.getItem().getId(), booking.getItem().getId());
    }
}
