package ru.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.service.comments.dto.CommentDto;
import ru.ewm.service.comments.dto.NewCommentDto;
import ru.ewm.service.comments.dto.UpdateCommentDto;
import ru.ewm.service.comments.likes.*;
import ru.ewm.service.events.EventService;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.exception.InvalidEventDateException;
import ru.ewm.service.exception.InvalidOperationException;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.users.User;
import ru.ewm.service.users.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
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

        checkDates(searchParams.getStart(), searchParams.getEnd());

        Pageable pageable = getPageable(searchParams.getFrom(), searchParams.getSize());

        List<Comment> foundComments = commentRepository.getCommentsWithSort(text, searchParams.getStart(),
                searchParams.getEnd(), pageable);

        if (foundComments.isEmpty()) {
            return new ArrayList<>();
        }

        setLikesAndDislikes(foundComments);

        return CommentMapper.toCommentDto(foundComments);
    }


    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = getCommentIfExist(commentId);

        setLikes(comment);
        setDislikes(comment);

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
    public LikeDto likeComment(Long userId, Long commentId, Boolean newLike) {
        User user = userService.getUserIfExist(userId);
        Comment comment = getCommentIfExist(commentId);

        List<Like> likes = likeRepository.findAllByUserIdIsAndCommentIdIs(userId, commentId);

        if (!likes.isEmpty()) {
            throw new InvalidOperationException("User can't like comment twice.");
        }

        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        like.setIsLike(newLike);

        return LikeMapper.toLikeDto(likeRepository.save(like));
    }

    @Override
    public void deleteLikeByUser(Long userId, Long commentId) {
        List<Like> likes = likeRepository.findAllByUserIdIsAndCommentIdIs(userId, commentId);

        if (likes.size() != 1) {
            throw new InvalidOperationException("Like not found.");
        }

        likeRepository.deleteById(likes.get(0).getId());
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

        List<Comment> comments = commentRepository.findAllByEventIdIsOrderByCreatedDesc(eventId, getPageable(from, size));

        setLikesAndDislikes(comments);

        return CommentMapper.toCommentDto(comments);
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId, Long from, Integer size) {
        userService.getUserIfExist(userId);

        List<Comment> comments = commentRepository.findAllByAuthorIdIsOrderByCreatedDesc(userId, getPageable(from, size));

        setLikesAndDislikes(comments);

        return CommentMapper.toCommentDto(comments);
    }

    private void setLikesAndDislikes(List<Comment> comments) {

        List<Long> ids = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        List<LikeForComment> likeForComments = likeRepository.findLikeForComments(ids);

        if (likeForComments.isEmpty()) {
            return;
        }

        Map<Long, Long> likeMap = new HashMap<>();
        Map<Long, Long> dislikeMap = new HashMap<>();

        for (LikeForComment like : likeForComments) {
            if (like.getIsLike()) {
                likeMap.put(like.getCommentId(), like.getCount());
            } else {
                dislikeMap.put(like.getCommentId(), like.getCount());
            }
        }

        for (Comment comment : comments) {

            if (likeMap.get(comment.getId()) != null) {
                comment.setLikes(likeMap.get(comment.getId()));
            }

            if (dislikeMap.get(comment.getId()) != null) {
                comment.setDislikes(dislikeMap.get(comment.getId()));
            }
        }
    }

    private void setLikes(Comment comment) {
        List<Like> likes = likeRepository.findAllByCommentIdIsAndIsLikeIs(comment.getId(), true);
        comment.setLikes(likes.size());
    }

    private void setDislikes(Comment comment) {
        List<Like> likes = likeRepository.findAllByCommentIdIsAndIsLikeIs(comment.getId(), false);
        comment.setDislikes(likes.size());
    }

    private Pageable getPageable(Long from, Integer size) {
        return PageRequest.of(Math.toIntExact(from), size);
    }

    private Comment getCommentIfExist(Long commentId) {

        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(commentId, Comment.class.getSimpleName()));
    }

    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new InvalidEventDateException("Start cannot be after end.");
        }
    }
}
