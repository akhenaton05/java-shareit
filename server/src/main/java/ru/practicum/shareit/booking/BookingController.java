package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.utils.HttpHeaders;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private BookingService bookingService;

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingOutDto getBookingById(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                        @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingOutDto> foundBookingsByState(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long bookerId, @RequestParam Optional<String> state) {
        return bookingService.foundBookingsByState(bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingOutDto> foundUsersBookingsByState(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long bookerId, @RequestParam Optional<String> state) {
        return bookingService.foundUsersBookingsByState(bookerId, state);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutDto createBooking(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                       @RequestBody BookingInDto dto) {
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto reviewBooking(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                       @PathVariable("bookingId") Long bookingId, @RequestParam(required = true) Boolean approved) {
        return bookingService.reviewBooking(userId, bookingId, approved);
    }
}
