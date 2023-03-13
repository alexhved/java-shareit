package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public Comment mapToComment(CommentRequestDto commentRequestDto, User user, Item item) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
    }

    public CommentResponseDto mapToCommentResponseDto(Comment savedComment) {
        return CommentResponseDto.builder()
                .id(savedComment.getId())
                .text(savedComment.getText())
                .authorName(savedComment.getAuthor().getName())
                .created(savedComment.getCreated())
                .build();
    }
}
