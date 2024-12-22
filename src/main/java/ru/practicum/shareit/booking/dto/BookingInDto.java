package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String start;
    private String end;
    private Long itemId;
}
