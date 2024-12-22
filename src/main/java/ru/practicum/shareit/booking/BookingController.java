package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.services.BookingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingOutDto getBookingById(@RequestHeader(SHARER_USER_ID) Long userId,
                                        @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingOutDto> foundBookingsByState(@RequestHeader(SHARER_USER_ID) Long bookerId, @RequestParam Optional<String> state) {
        return bookingService.foundBookingsByState(bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingOutDto> foundUsersBookingsByState(@RequestHeader(SHARER_USER_ID) Long bookerId, @RequestParam Optional<String> state) {
        return bookingService.foundUsersBookingsByState(bookerId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutDto createBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                       @RequestBody BookingInDto dto) {
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto reviewBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                       @PathVariable("bookingId") Long bookingId, @RequestParam(required = true) Boolean approved) {
        return bookingService.reviewBooking(userId, bookingId, approved);
    }
}
