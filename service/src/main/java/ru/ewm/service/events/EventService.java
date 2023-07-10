package ru.ewm.service.events;

import ru.ewm.service.events.dto.*;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.events.searchParams.AdminSearchParams;
import ru.ewm.service.events.searchParams.PublicSearchParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<FullEventDto> searchEventsByAdmin(AdminSearchParams adminSearchParams);

    FullEventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<FullEventDto> getEventsForPrivate(Long userId, Long from, Integer size);

    FullEventDto addEvent(Long userId, NewEventDto newEventDto);

    FullEventDto getEventByIdForPrivate(Long userId, Long eventId);

    FullEventDto updateEventByIdForPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<FullEventDto> searchEventsByPublic(PublicSearchParams searchParams);

    FullEventDto getEventByIdForPublic(Long id, HttpServletRequest request);

    Event updateEvent(Event eventToUpdate, UpdateEventRequest updateEventRequest);

    Map<Long, Long> getStats(List<Event> events, Boolean unique);

    Event getEventIfExist(Long eventId);
}
