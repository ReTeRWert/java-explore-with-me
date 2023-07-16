package ru.ewm.service.comments.likes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    List<Like> findAllByCommentIdIsAndIsLikeIs(Long commentId, Boolean like);

    List<Like> findAllByUserIdIsAndCommentIdIs(Long userId, Long commentId);
}
