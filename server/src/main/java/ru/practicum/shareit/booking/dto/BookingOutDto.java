package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOutDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String start;
    private String end;
    private Item item;
    private User booker;
    private Status status;
}
