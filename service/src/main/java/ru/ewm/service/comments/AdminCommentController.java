package ru.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.UpdateCommentDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("admin/comments")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto editCommentByAdmin(@PathVariable Long commentId,
                                         @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Edit comment with id {}", commentId);

        return commentService.editCommentByAdmin(commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Delete comment with id: {}", commentId);

        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/user/{userId}")
    public List<CommentDto> getCommentsByUserId(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get comments by user id: {}", userId);

        return commentService.getCommentsByUserId(userId, from, size);
    }

}
