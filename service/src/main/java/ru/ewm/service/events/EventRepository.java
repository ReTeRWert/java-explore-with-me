package ru.ewm.service.events;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ewm.service.events.enums.EventState;
import ru.ewm.service.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByIdIsGreaterThanEqualAndInitiatorIdIs(Long id, Long initiatorId, Pageable pageable);

    @Query(" SELECT count(r.id) " +
            "FROM Request as r " +
            "WHERE r.event.id = ?1 " +
            "AND r.status = 'CONFIRMED'" +
            "GROUP BY r.id")
    Integer findConfirmedRequests(Long id);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (:users is null OR e.initiator.id IN :users) " +
            "AND (:states is null OR e.state IN :states) " +
            "AND (:categories is null OR e.category.id IN :categories)" +
            "AND ((cast(:start as timestamp) Is null OR cast(:end as timestamp) Is null) " +
            "OR (cast(:start as timestamp) Is Not Null AND cast(:end as timestamp) Is Not Null " +
            "AND e.eventDate BETWEEN :start AND :end))"
    )
    List<Event> searchEventsByAdmin(@Param("users") List<Long> users,
                                    @Param("states") List<EventState> states,
                                    @Param("categories") List<Long> categories,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND ((CAST(:start AS timestamp) IS NULL OR cast(:end AS timestamp) IS NULL) " +
            "OR (CAST(:start AS timestamp) IS NOT NULL AND CAST(:end AS timestamp) IS NOT NULL " +
            "AND e.eventDate BETWEEN :start AND :end)) " +
            "AND (:text IS NULL OR (LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text%)) " +
            "AND (:paid IS NULL OR e.isPaid = :paid)")
    List<Event> searchEventsByPublic(@Param("text") String text,
                                     @Param("categories") List<Long> categories,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     Boolean paid,
                                     Pageable pageable);

    List<Event> findEventByCategoryId(Long categoryId);
}