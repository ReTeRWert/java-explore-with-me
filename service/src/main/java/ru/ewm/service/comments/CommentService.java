package ru.ewm.service.comments;

import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.NewCommentDto;
import ru.ewm.service.comments.dto.UpdateCommentDto;
import ru.ewm.service.comments.likes.LikeDto;

import java.util.List;

public interface CommentService {

    CommentDto editCommentByAdmin(Long commentId, UpdateCommentDto updateCommentDto);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> getCommentsWithSort(SearchParams searchParams);

    CommentDto getCommentById(Long commentId);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto);

    LikeDto likeComment(Long userId, Long commentId, Boolean like);

    void deleteLikeByUser(Long userId, Long commentId);

    void deleteCommentByAuthor(Long userId, Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Long from, Integer size);

    List<CommentDto> getCommentsByUserId(Long userId, Long from, Integer size);
}
