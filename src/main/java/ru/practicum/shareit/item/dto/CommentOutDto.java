package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutDto {
    private Long id;
    private String text;
    private String itemName;
    private String authorName;
    private String created;
}
