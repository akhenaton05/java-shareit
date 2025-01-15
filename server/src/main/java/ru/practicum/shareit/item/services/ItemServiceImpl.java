package ru.practicum.shareit.item.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemGetDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zoned = ldt.atZone(ZoneId.of("UTC"));
        Instant instant=zoned.toInstant();
        ldt=instant.atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
        List<Booking> lastList = bookingRepository.findLastBooking(item.getId(), ldt);
        Booking last = !lastList.isEmpty() ? lastList.getFirst() : null;
        List<Booking> nextList = bookingRepository.findNextBooking(item.getId(), ldt);
        Booking next = !nextList.isEmpty() ? nextList.getFirst() : null;
        List<Comment> comments = commentRepository.findByItemId(item.getId());

        return itemRepository.findById(itemId).map(i -> ItemMapper.toItemGetDto(i, last, next, comments)).orElse(null);
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto dto) {
        Item item = ItemMapper.dtoToItem(dto);
        checkItem(item);

        Item itemToAdd = new Item();
        itemToAdd.setId(item.getId());
        itemToAdd.setDescription(item.getDescription());
        itemToAdd.setName(item.getName());
        itemToAdd.setAvailable(item.getAvailable());
        itemToAdd.setOwner(userRepository.findById(userId).orElseThrow(NotFoundException::new));
        if (item.getRequest() != null) {
            itemToAdd.setRequest(item.getRequest());
        } else {
            itemToAdd.setRequest(null);
        }
        itemRepository.save(itemToAdd);

        return ItemMapper.toItemDto(itemToAdd);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto dto) {
        Optional<Item> opItem = itemRepository.findById(itemId);
        if (opItem.isEmpty()) {
            throw new NotFoundException("Неверный ID вещи");
        }
        if (!Objects.equals(opItem.get().getOwner().getId(), userId)) {
            throw new NotFoundException("Неверный владелец вещи");
        }
        if (Objects.nonNull(dto.getName())) {
            opItem.get().setName(dto.getName());
        }
        if (Objects.nonNull(dto.getDescription())) {
            opItem.get().setDescription(dto.getDescription());
        }
        if (Objects.nonNull(dto.getAvailable())) {
            opItem.get().setAvailable(dto.getAvailable());
        }
        itemRepository.save(opItem.get());
        return ItemMapper.toItemDto(opItem.get());
    }

    @Override
    public List<ItemDto> foundItem(Long userId, Optional<String> text) {
        if (text.isPresent() && !text.get().isBlank()) {
            return itemRepository.findByNameOrDescriptionContaining(text.get()).stream()
                    .map(ItemMapper::toItemDto)
                    .toList();
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public CommentOutDto createComment(Long authorId, Long itemId, CommentDto dto) {
        Optional<Booking> booking = bookingRepository.findByBookerIdAndItemId(authorId, itemId);
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zoned = ldt.atZone(ZoneId.of("UTC"));
        Instant instant=zoned.toInstant();
        ldt=instant.atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
        if (booking.isPresent() && booking.get().getStatus().equals(Status.APPROVED) && booking.get().getEnd().isBefore(ldt)) {
            Item item = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
            User user = userRepository.findById(authorId).orElseThrow(NotFoundException::new);
            Comment comment = CommentMapper.toComment(dto, user, item);
            commentRepository.save(comment);
            return CommentMapper.toOutDto(comment);
        }
        throw new ValidateException("Ошибка введенных данных");
    }

    public Item checkItem(Item item) {
        if (Objects.isNull(item.getName()) || item.getName().isEmpty()) {
            throw new ValidateException("Название отсутствует");
        }
        if (Objects.isNull(item.getDescription()) || item.getDescription().isEmpty()) {
            throw new ValidateException("Описание отсутствует");
        }
        if (Objects.isNull(item.getAvailable())) {
            throw new ValidateException("Статус отсутствует");
        }
        return item;
    }
}
