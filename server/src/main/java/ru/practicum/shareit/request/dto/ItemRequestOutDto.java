package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequestAnswer;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestOutDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String description;
    private User requester;
    private String created;
    private List<ItemRequestAnswer> items;
}
