package ru.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.comments.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentsWithSort(@RequestParam(required = false) String text,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                @RequestParam(required = false) Integer likes,
                                                @RequestParam(required = false) Integer dislikes,
                                                @RequestParam(defaultValue = "0") Long from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get comments.");

        SearchParams searchParams = new SearchParams(
                text,
                start,
                end,
                likes,
                dislikes,
                from,
                size
        );

        return commentService.getCommentsWithSort(searchParams);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("Get comment with id: {}", commentId);

        return commentService.getCommentById(commentId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getCommentsByEventId(@PathVariable Long eventId,
                                                 @RequestParam(defaultValue = "0") Long from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get comments by event id: {}", eventId);

        return commentService.getCommentsByEventId(eventId, from, size);
    }
}
