package ru.practicum.shareit.booking.services;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingOutDto getBookingById(Long userId, Long bookingId);

    BookingOutDto createBooking(Long bookerId, BookingInDto dto);

    List<BookingOutDto> foundBookingsByState(Long bookerId, Optional<String> state);

    List<BookingOutDto> foundUsersBookingsByState(Long bookerId, Optional<String> state);

    BookingOutDto reviewBooking(Long userId, Long bookingId, Boolean approved);
}
