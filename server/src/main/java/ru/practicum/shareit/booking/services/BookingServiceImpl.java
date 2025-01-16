package ru.practicum.shareit.booking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.States;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ValidateException("Неверный Id владельца или бронирующего");
        }
        return BookingMapper.toBookingDto(booking);
    }


    @Override
    public List<BookingOutDto> foundBookingsByState(Long bookerId, Optional<String> opState) {
        return stateBookingValidator(bookerId, opState);
    }

    @Override
    public List<BookingOutDto> foundUsersBookingsByState(Long bookerId, Optional<String> opState) {
        List<Item> items = itemRepository.findByOwnerId(bookerId);
        if (items.isEmpty()) {
            throw new ValidateException("У пользователя нету вещей");
        }

        States state = opState.map(States::valueOf).orElse(States.ALL);
        LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
        return switch (state) {
            case ALL -> bookingRepository.findByAllItems(items).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case CURRENT -> bookingRepository.findByItemsAndStatus(items, Status.APPROVED).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case PAST -> bookingRepository.findAllByBookerIdAndPast(bookerId, currentDate).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case FUTURE -> bookingRepository.findAllByBookerIdAndFuture(bookerId, currentDate).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case WAITING -> bookingRepository.findByItemsAndStatus(items, Status.WAITING).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case REJECTED -> bookingRepository.findByItemsAndStatus(items, Status.REJECTED).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
        };

    }

    @Override
    @Transactional
    public BookingOutDto reviewBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);
        Item item = booking.getItem();
        if (!Objects.equals(userId, item.getOwner().getId())) {
            throw new ValidateException("Пользователь не является владельцем вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            booking.getItem().setAvailable(false);
        } else booking.setStatus(Status.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingOutDto createBooking(Long userId, BookingInDto dto) {
        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(NotFoundException::new);
        if (!item.getAvailable()) {
            throw new ValidateException("Запрашиваемый предмет недоступен");
        }
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Booking booking = BookingMapper.inToBooking(dto, user, item);
        dateTimeChecker(booking);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    public void dateTimeChecker(Booking booking) {
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidateException("Ошибка указания начального/конечного времени");
        }
        if (booking.getStart().toLocalDate().isBefore(LocalDate.now())) {
            throw new ValidateException("Начальное время не может быть в прошлом");
        }
    }

    public List<BookingOutDto> stateBookingValidator(Long bookerId, Optional<String> opState) {
        States state = opState.map(States::valueOf).orElse(States.ALL);
        LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
        return switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(bookerId).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.APPROVED).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case PAST -> bookingRepository.findAllByBookerIdAndPast(bookerId, currentDate).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case FUTURE -> bookingRepository.findAllByBookerIdAndFuture(bookerId, currentDate).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case WAITING -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED).stream()
                    .map(BookingMapper::toBookingDto)
                    .toList();
        };
    }
}
