package ru.ewm.service.events;

import ru.ewm.service.events.dto.*;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.util.SortTypes;
import ru.ewm.service.util.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<FullEventDto> searchEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Integer size);

    FullEventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<FullEventDto> getEventsForPrivate(Long userId, Long from, Integer size);

    FullEventDto addEvent(Long userId, NewEventDto newEventDto);

    FullEventDto getEventByIdForPrivate(Long userId, Long eventId);

    FullEventDto updateEventByIdForPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<FullEventDto> searchEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, SortTypes sort, Long from,
                                            Integer size, String ip);

    public FullEventDto getEventByIdForPublic(Long id, HttpServletRequest request);

    Event updateEvent(Event eventToUpdate, UpdateEventRequest updateEventRequest);

    Map<Long, Long> getStats(List<Event> events,
                             Boolean unique);
}
