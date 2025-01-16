package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.utils.HttpHeaders;

import java.util.Optional;


@Controller
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(HttpHeaders.SHARER_USER_ID) long userId,
                                           @RequestBody @Valid BookingInDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HttpHeaders.SHARER_USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> foundBookingsByState(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long bookerId, @RequestParam Optional<String> stateOp) {
        String state;
        if (stateOp.isEmpty()) {
            state = "ALL";
        } else {
            BookingState stateParam = BookingState.from(stateOp.get()).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateOp.get()));
            state = stateOp.get();
        }
        return bookingClient.foundBookingsByState(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> foundUsersBookingsByState(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long bookerId, @RequestParam Optional<String> stateOp) {
        String state;
        if (stateOp.isEmpty()) {
            state = "ALL";
        } else {
            BookingState stateParam = BookingState.from(stateOp.get()).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateOp.get()));
            state = stateOp.get();
        }
        return bookingClient.foundUsersBookingsByState(bookerId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> reviewBooking(@RequestHeader(HttpHeaders.SHARER_USER_ID) Long userId,
                                                @PathVariable("bookingId") Long bookingId,
                                                @RequestParam(required = true) Boolean approved) {
        return bookingClient.reviewBooking(userId, bookingId, approved);
    }
}
