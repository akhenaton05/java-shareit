package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDateTime;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingOutDto> json;

    private BookingOutDto outDto;
    private Item item;
    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("email@email.ru");
        user.setName("name");

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setOwner(new User());
        item.setRequest(new ItemRequest());
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.APPROVED);
        booking.setEnd(LocalDateTime.now());

        outDto = new BookingOutDto();
        outDto.setId(1L);
        outDto.setStart(LocalDateTime.of(2025, 01, 1, 12, 00, 00).toString());
        outDto.setEnd(LocalDateTime.of(2025, 01, 1, 12, 00, 00).toString());
        outDto.setStatus(Status.APPROVED);
        outDto.setBooker(user);
        outDto.setItem(item);
    }

    @Test
    void testBookingOutDtoSerializing() throws Exception {
        JsonContent<BookingOutDto> result = json.write(outDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T12:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T12:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("email@email.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
    }

    @Test
    void deserializeBookingDto() throws IOException {
        String jsonString = "{" +
                "\"id\": 1," +
                "\"start\": \"2025-01-01T12:00\"," +
                "\"end\": \"2025-01-01T12:00\"," +
                "\"itemId\": 1," +
                "\"booker\": {" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"email\": \"email@email.ru\"" +
                "}," +
                "\"item\": {" +
                "\"id\": 1," +
                "\"name\": \"name\"," +
                "\"description\": \"description\"," +
                "\"available\": true" +
                "}" +
                "}";
        BookingOutDto deserializedOutDto = json.parse(jsonString).getObject();

        assertNull(deserializedOutDto.getId());
        assertEquals(LocalDateTime.of(2025, 01, 1, 12, 0, 0).toString(), deserializedOutDto.getStart());
        assertEquals(LocalDateTime.of(2025, 01, 1, 12, 0, 0).toString(), deserializedOutDto.getEnd());
        assertEquals(1L, deserializedOutDto.getItem().getId());

        User booker = deserializedOutDto.getBooker();
        assertEquals(1L, booker.getId());
        assertEquals("name", booker.getName());
        assertEquals("email@email.ru", booker.getEmail());

        Item item = deserializedOutDto.getItem();
        assertEquals(1L, item.getId());
        assertEquals("name", item.getName());
        assertEquals("description", item.getDescription());
        assertTrue(item.getAvailable());
    }
}
