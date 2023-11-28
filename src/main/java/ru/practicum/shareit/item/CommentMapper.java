package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;

@UtilityClass
public class CommentMapper {

    public CommentDtoCreate toCommentDtoCreate(Comment comment) {
        return CommentDtoCreate.builder()
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public Comment toComment(CommentDtoCreate commentDtoCreate) {
        return Comment.builder()
                .text(commentDtoCreate.getText())
                .build();
    }

}
