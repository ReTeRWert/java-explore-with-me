package ru.ewm.service.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.util.RequestState;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByIdIs(Long userId);

    Optional<Request> findFirstByEventIdIsAndRequesterIdIs(Long eventId, Long requesterId);

    List<Request> findAllByEventInAndStatusIs(List<Event> events, RequestState requestState);

    List<Request> findAllByEventIdIs(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    @Query("SELECT COUNT(r.id) " +
            "FROM Request r " +
            "WHERE r.event.id = ?1 " +
            "AND r.status = ?2")
    Integer getCountConfirmedRequest(Long eventId, RequestState state);

    List<Long>findAllByEventIdIsAndStatusIs(Long eventId, RequestState state);
}
