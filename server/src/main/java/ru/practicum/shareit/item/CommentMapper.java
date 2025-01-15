package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDto dto, User author, Item item) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        return comment;
    }

    public static CommentOutDto toOutDto(Comment comment) {
        CommentOutDto dto = new CommentOutDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setItemName(comment.getItem().getName());
        dto.setCreated(comment.getCreated().toString());
        return dto;
    }
}
