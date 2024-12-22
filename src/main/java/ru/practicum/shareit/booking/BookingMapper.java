package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingOutDto toBookingDto(Booking booking) {
        BookingOutDto dto = new BookingOutDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart().toString());
        dto.setEnd(booking.getEnd().toString());
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        dto.setStatus(booking.getStatus());

        System.out.println(dto);
        return dto;
    }

    public static Booking inToBooking(BookingInDto dto, User user, Item item) {
        LocalDateTime dateOfStart = LocalDateTime.parse(dto.getStart());
        LocalDateTime dateOfEnd = LocalDateTime.parse(dto.getEnd());

        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dateOfStart);
        booking.setEnd(dateOfEnd);
        booking.setItem(item);
        booking.setBooker(user);

        return booking;
    }
}
