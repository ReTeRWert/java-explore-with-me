package ru.ewm.service.comments;

import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.NewCommentDto;

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
}
