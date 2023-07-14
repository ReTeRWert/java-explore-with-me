package ru.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.NewCommentDto;
import ru.ewm.service.comments.dto.UpdateCommentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/users/{userId}")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody @NotNull @Valid NewCommentDto newCommentDto) {
        log.info("Adding comment");

        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody @NotNull @Valid UpdateCommentDto commentDto) {
        log.info("Update comment with id: {} by user with id: {}", commentId, userId);

        return commentService.updateComment(userId, commentId, commentDto);
    }

    @PatchMapping("/comments/{commentId}/like")
    public CommentDto likeComment(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        log.info("User with id: {} like comment with id: {}", userId, commentId);

        return commentService.likeComment(userId, commentId);
    }

    @PatchMapping("/comments/{commentId}/dislike")
    public CommentDto dislikeComment(@PathVariable Long userId,
                                     @PathVariable Long commentId) {
        log.info("User with id: {} dislike comment with id: {}", userId, commentId);

        return commentService.dislikeComment(userId, commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAuthor(@PathVariable Long userId,
                                      @PathVariable Long commentId) {
        log.info("Delete comment with id: {} by user with id: {}", commentId, userId);

        commentService.deleteCommentByAuthor(userId, commentId);
    }

}
