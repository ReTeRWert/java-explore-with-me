package ru.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.NewCommentDto;
import ru.ewm.service.comments.dto.UpdateCommentDto;
import ru.ewm.service.events.EventService;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.exception.InvalidEventDateException;
import ru.ewm.service.exception.InvalidOperationException;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.users.User;
import ru.ewm.service.users.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public CommentDto editCommentByAdmin(Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = getCommentIfExist(commentId);

        if (updateCommentDto.getDescription() != null) {
            comment.setDescription(updateCommentDto.getDescription());
        }

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        getCommentIfExist(commentId);

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsWithSort(SearchParams searchParams) {
        String text = searchParams.getText();

        if (text != null) {
            text = text.toLowerCase();
        }

        if (searchParams.getStart() != null && searchParams.getEnd() != null
                && searchParams.getStart().isAfter(searchParams.getEnd())) {

            throw new InvalidEventDateException("Start cannot be after end.");
        }

        PageRequest pageable = PageRequest.of(0, searchParams.getSize());

        List<Comment> foundEvents = commentRepository.getCommentsWithSort(text, searchParams.getStart(), searchParams.getEnd(),
                searchParams.getLikes(), searchParams.getDislikes(), pageable);

        if (foundEvents.isEmpty()) {
            return new ArrayList<>();
        }

        return foundEvents.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = getCommentIfExist(commentId);

        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = userService.getUserIfExist(userId);
        Event event = eventService.getEventIfExist(eventId);

        Comment comment = CommentMapper.toComment(newCommentDto);

        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setEvent(event);

        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto) {
        User author = userService.getUserIfExist(userId);
        Comment comment = getCommentIfExist(commentId);

        if (!Objects.equals(comment.getAuthor().getId(), author.getId())) {
            throw new InvalidOperationException("Only author can edit comment.");
        }

        if (LocalDateTime.now().isAfter(comment.getCreated().plusHours(2))) {
            throw new InvalidOperationException("Author can edit comment in 2 hours after publication.");
        }

        if (commentDto.getDescription() != null) {
            comment.setDescription(comment.getDescription());
        }

        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto likeComment(Long userId, Long commentId) {
        userService.getUserIfExist(userId);
        Comment comment = getCommentIfExist(commentId);

        comment.setLikes(comment.getLikes() + 1);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto dislikeComment(Long userId, Long commentId) {
        userService.getUserIfExist(userId);
        Comment comment = getCommentIfExist(commentId);

        comment.setDislikes(comment.getDislikes() + 1);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByAuthor(Long userId, Long commentId) {
        User author = userService.getUserIfExist(userId);
        Comment comment = getCommentIfExist(commentId);

        if (!Objects.equals(comment.getAuthor().getId(), author.getId())) {
            throw new InvalidOperationException("Only author can delete comment.");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Long from, Integer size) {
        eventService.getEventIfExist(eventId);

        PageRequest pageable = PageRequest.of(0, size);

        List<Comment> comments = commentRepository.findAllByEventIdIsOrderByCreatedDesc(eventId, pageable);

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId, Long from, Integer size) {
        userService.getUserIfExist(userId);

        PageRequest pageable = PageRequest.of(0, size);

        List<Comment> comments = commentRepository.findAllByAuthorIdIsOrderByCreatedDesc(userId, pageable);

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getCommentIfExist(Long commentId) {

        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(commentId, Comment.class.getSimpleName()));
    }
}
