package ru.ewm.service.comments.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findAllByCommentIdIsAndIsLikeIs(Long commentId, Boolean like);

    List<Like> findAllByUserIdIsAndCommentIdIs(Long userId, Long commentId);

    @Query(value = "SELECT new ru.ewm.service.comments.likes.LikeForComment (l.comment.id, COUNT(l.isLike), l.isLike) " +
            "FROM Like AS l " +
            "WHERE comment_id IN (?1) " +
            "GROUP BY comment_id, is_like")
    List<LikeForComment> findLikeForComments(List<Long> commentIds);
}
