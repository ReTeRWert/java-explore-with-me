package ru.ewm.service.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ewm.service.events.model.Event;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterIdIs(Long userId);

    Optional<Request> findFirstByEventIdIsAndRequesterIdIs(Long eventId, Long requesterId);

    List<Request> findAllByEventInAndStatusIs(List<Event> events, RequestState requestState);

    List<Request> findAllByEventIdIs(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdIsAndStatusIs(Long eventId, RequestState state);
}
