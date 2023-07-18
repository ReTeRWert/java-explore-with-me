package ru.ewm.service.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT c " +
            "FROM Comment c " +
            "WHERE (:text IS NULL OR (LOWER(c.description) LIKE %:text%)) " +
            "AND ((CAST(:start AS timestamp) IS NULL OR CAST(:end AS timestamp) IS NULL) " +
            "OR (CAST(:start AS timestamp) IS NOT NULL AND CAST(:end AS timestamp) IS NOT NULL " +
            "AND c.created BETWEEN :start AND :end)) ")
    List<Comment> getCommentsWithSort(@Param(value = "text") String text,
                                      @Param(value = "start") LocalDateTime start,
                                      @Param(value = "end") LocalDateTime end,
                                      Pageable pageable);

    List<Comment> findAllByEventIdIsOrderByCreatedDesc(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorIdIsOrderByCreatedDesc(Long userId, Pageable pageable);
}
