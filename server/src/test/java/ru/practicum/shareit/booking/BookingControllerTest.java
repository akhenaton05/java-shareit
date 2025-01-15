package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.model.Item;
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

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private BookingInDto inDto;
    private BookingOutDto outDto;
    private User user = new User();
    private Item item = new Item();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        item.setId(1L);

        inDto = new BookingInDto();
        inDto.setId(1L);
        inDto.setItemId(1L);
        inDto.setEnd(LocalDateTime.now().plusDays(2).toString());
        inDto.setStart(LocalDateTime.now().minusDays(1).toString());

        outDto = new BookingOutDto();
        outDto.setId(1L);
        outDto.setStatus(Status.APPROVED);
        outDto.setBooker(user);
        outDto.setItem(item);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingInDto.class)))
                .thenReturn(outDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(outDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(outDto.getStart())))
                .andExpect(jsonPath("$.end", is(outDto.getEnd())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(outDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(outDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(outDto.getStart())))
                .andExpect(jsonPath("$.end", is(outDto.getEnd())));
    }

    @Test
    void foundBookingsByState() throws Exception {
        when(bookingService.foundBookingsByState(anyLong(), any()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/bookings?state=text")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(outDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(outDto.getStart())))
                .andExpect(jsonPath("$[0].end", is(outDto.getEnd())));
    }

    @Test
    void foundUsersBookingsByState() throws Exception {
        when(bookingService.foundUsersBookingsByState(anyLong(), any()))
                .thenReturn(List.of(outDto));

        mvc.perform(get("/bookings/owner?state=text")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is(outDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].start", is(outDto.getStart())))
                .andExpect(jsonPath("$[0].end", is(outDto.getEnd())));
    }

    @Test
    void reviewBooking() throws Exception {
        when(bookingService.reviewBooking(anyLong(), anyLong(), any()))
                .thenReturn(outDto);

        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(inDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(outDto.getStatus().toString())))
                .andExpect(jsonPath("$.start", is(outDto.getStart())))
                .andExpect(jsonPath("$.end", is(outDto.getEnd())));
    }
}
