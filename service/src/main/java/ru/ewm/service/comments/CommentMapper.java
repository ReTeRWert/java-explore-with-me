package ru.ewm.service.comments;

import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.NewCommentDto;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment toComment(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setDescription(newCommentDto.getDescription());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getDescription(),
                comment.getCreated(),
                comment.getAuthor().getId(),
                comment.getEvent().getId(),
                comment.getLikes(),
                comment.getDislikes()
        );
    }

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
