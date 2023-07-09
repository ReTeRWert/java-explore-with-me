package ru.ewm.service.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.client.StatClient;
import ru.ewm.service.categories.Category;
import ru.ewm.service.events.dto.*;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.exception.AccessException;
import ru.ewm.service.exception.InvalidEventDateException;
import ru.ewm.service.exception.InvalidOperationException;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.requests.Request;
import ru.ewm.service.requests.RequestService;
import ru.ewm.service.users.User;
import ru.ewm.service.util.EventState;
import ru.ewm.service.util.ExistValidator;
import ru.ewm.service.util.SortTypes;
import ru.ewm.service.util.StateAction;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestService requestService;
    private final StatClient statClient;
    private final ExistValidator existValidator;

    @Override
    public List<FullEventDto> searchEventsByAdmin(List<Long> users,
                                                  List<EventState> states,
                                                  List<Long> categories,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Long from,
                                                  Integer size) {

        int startPage = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(startPage, size);
        List<Event> search = eventRepository.searchEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        List<FullEventDto> fullEvents = search.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> views = getStats(search, false);

        if (!views.isEmpty()) {
            fullEvents.forEach(e -> e.setViews(views.get(e.getId())));
        }

        for (FullEventDto event : fullEvents) {
            Integer confirmedRequests = eventRepository.findConfirmedRequests(event.getId());

            if (confirmedRequests != null) {
                event.setConfirmedRequests(confirmedRequests);
            }
        }

        return fullEvents;
    }

    @Override
    public FullEventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event updateEvent = existValidator.getEventIfExist(eventId);


        if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidEventDateException("The start date of the event to be modified must be no earlier than an hour from the date of publication.");
        }

        if (!updateEvent.getState().equals(EventState.PENDING)) {
            throw new InvalidOperationException("An event can be published only if it is in the waiting state for publication.");
        }

        Event updatedEvent = updateEvent(updateEvent, updateEventAdminRequest);
        StateAction updatedEventStateAction = updateEventAdminRequest.getStateAction();

        if (updatedEventStateAction == null) {
            return EventMapper.toEventFullDto(eventRepository.save(updatedEvent));
        }

        switch (updatedEventStateAction) {

            case PUBLISH_EVENT:
                updatedEvent.setPublishedOn(LocalDateTime.now());
                updatedEvent.setState(EventState.PUBLISHED);
                break;

            case REJECT_EVENT:
                updatedEvent.setState(EventState.CANCELED);
                break;

            default:
                throw new InvalidOperationException("Invalid state");
        }

        return EventMapper.toEventFullDto(eventRepository.save(updatedEvent));
    }

    @Override
    public List<FullEventDto> getEventsForPrivate(Long userId, Long from, Integer size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<Event> foundEvents = eventRepository
                .findAllByIdIsGreaterThanEqualAndInitiatorIdIs(from, userId, pageRequest);

        List<FullEventDto> fullEventDtos = foundEvents.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> views = getStats(foundEvents, false);

        if (!views.isEmpty()) {
            fullEventDtos.forEach(e -> e.setViews(views.get(e.getId())));
        }

        List<Request> confirmedRequests = requestService.findConfirmedRequests(foundEvents);

        for (FullEventDto fullDto : fullEventDtos) {
            fullDto.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(request -> request.getEvent().getId().equals(fullDto.getId()))
                    .count());
        }

        return fullEventDtos;
    }

    @Override
    public FullEventDto addEvent(Long userId, NewEventDto newEventDto) {

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventDateException("The date and time at which the event is scheduled cannot be earlier than two hours from the current moment");
        }

        User initiator = existValidator.getUserIfExist(userId);
        Category category = existValidator.getCategoryIfExist(newEventDto.getCategory());

        Event newEvent = EventMapper.toEvent(newEventDto);

        newEvent.setCategory(category);
        newEvent.setInitiator(initiator);
        newEvent.setState(EventState.PENDING);
        newEvent.setCreatedOn(LocalDateTime.now());
        Event check = eventRepository.save(newEvent);
        return EventMapper.toEventFullDto(check);
    }

    @Override
    public FullEventDto getEventByIdForPrivate(Long userId, Long eventId) {
        Event eventToReturn = existValidator.getEventIfExist(eventId);

        if (!Objects.equals(eventToReturn.getInitiator().getId(), userId)) {
            throw new NotFoundException(eventId, Event.class.getSimpleName());
        }

        FullEventDto fullEventDto = EventMapper.toEventFullDto(eventToReturn);

        if (EventState.PUBLISHED.equals(fullEventDto.getState())) {
            Map<Long, Long> views = getStats(List.of(eventToReturn), false);
            fullEventDto.setViews(views.get(eventToReturn.getId()));

            List<Request> confirmedRequests = requestService.findConfirmedRequests(List.of(eventToReturn));
            fullEventDto.setConfirmedRequests(confirmedRequests.size());
        }

        return fullEventDto;
    }

    @Override
    public FullEventDto updateEventByIdForPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event eventToUpdate = existValidator.getEventIfExist(eventId);

        if (!Objects.equals(eventToUpdate.getInitiator().getId(), userId)) {
            throw new InvalidOperationException("Event could be updated only by initiator");
        }

        Event updatedEvent = updateEvent(eventToUpdate, updateEvent);
        StateAction updatedEventStateAction = updateEvent.getStateAction();

        if (updatedEventStateAction == null) {
            return EventMapper.toEventFullDto(eventRepository.save(updatedEvent));
        }

        switch (updateEvent.getStateAction()) {
            case SEND_TO_REVIEW:
                updatedEvent.setState(EventState.PENDING);
                break;

            case CANCEL_REVIEW:
                updatedEvent.setState(EventState.CANCELED);
                break;

            default:
                throw new InvalidOperationException("Invalid state");
        }

        return EventMapper.toEventFullDto(eventRepository.save(updatedEvent));
    }

    @Override
    public List<FullEventDto> searchEventsByPublic(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   SortTypes sort,
                                                   Long from,
                                                   Integer size,
                                                   String ip) {

        if (text != null) {
            text = text.toLowerCase();
        }

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new InvalidEventDateException("Start event cannot be after his end.");
        }

        PageRequest pageable = PageRequest.of(0, size);
        List<Event> foundEvents = eventRepository.searchEventsByPublic(text, categories, rangeStart, rangeEnd, paid, pageable);

        if (foundEvents.isEmpty()) {
            return new ArrayList<>();
        }

        List<FullEventDto> fullEventDtos = foundEvents.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

        Map<Long, Long> views = getStats(foundEvents, false);

        fullEventDtos.forEach(e -> e.setViews(views.get(e.getId())));

        List<Request> confirmedRequests = requestService.findConfirmedRequests(foundEvents);

        for (FullEventDto fullDto : fullEventDtos) {
            fullDto.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(request -> request.getEvent().getId().equals(fullDto.getId()))
                    .count());
        }

        LocalDateTime timestamp = LocalDateTime.now();
        statClient.saveNewEndpoint("ewm-main-service", "/events", ip, timestamp);
        foundEvents.forEach(event -> statClient.saveNewEndpoint("ewm-main-service", "/events/" + event.getId(), ip, timestamp));

        if (SortTypes.VIEWS.equals(sort)) {
            return fullEventDtos.stream()
                    .sorted(Comparator.comparing(FullEventDto::getViews))
                    .collect(Collectors.toList());
        }

        return fullEventDtos.stream()
                .sorted(Comparator.comparing(FullEventDto::getEventDate))
                .collect(Collectors.toList());

    }

    @Override
    public FullEventDto getEventByIdForPublic(Long eventId, HttpServletRequest request) {
        Event event = existValidator.getEventIfExist(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(eventId, Event.class.getSimpleName());
        }

        FullEventDto fullEventDto = EventMapper.toEventFullDto(event);

        Map<Long, Long> views = getStats(List.of(event), true);
        fullEventDto.setViews(views.get(event.getId()));

        List<Request> confirmedRequests = requestService.findConfirmedRequests(List.of(event));

        fullEventDto.setConfirmedRequests(confirmedRequests.size());

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        statClient.saveNewEndpoint("ewm-main-service", uri, ip, LocalDateTime.now());

        return fullEventDto;
    }

    @Override
    public Event updateEvent(Event eventToUpdate, UpdateEventRequest updateEventRequest) {
        if (EventState.PUBLISHED.equals(eventToUpdate.getState())) {
            throw new InvalidOperationException("Invalid state");
        }

        if (updateEventRequest.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null) {
            Category category = existValidator.getCategoryIfExist(updateEventRequest.getCategory());
            eventToUpdate.setCategory(category);
        }

        if (updateEventRequest.getDescription() != null) {
            eventToUpdate.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidEventDateException("Invalid date");
            }
            eventToUpdate.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getLocation() != null) {
            eventToUpdate.setLocation(updateEventRequest.getLocation());
        }

        if (updateEventRequest.getPaid() != null) {
            eventToUpdate.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            eventToUpdate.setTitle(updateEventRequest.getTitle());
        }

        return eventToUpdate;
    }

    @Override
    public Map<Long, Long> getStats(List<Event> events,
                                    Boolean unique) {

        Optional<LocalDateTime> start = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (start.isEmpty()) {
            return new HashMap<>();
        }

        LocalDateTime timestamp = LocalDateTime.now();

        List<Long> ids = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        String startTime = start.get().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<String> uris = ids.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statClient.getViewStats(startTime, endTime, uris, unique);
        List<ViewStatsDto> stats;
        ObjectMapper mapper = new ObjectMapper();

        try {

            stats = Arrays.asList(mapper.readValue(
                    mapper.writeValueAsString(
                            response.getBody()), ViewStatsDto[].class));

        } catch (IOException e) {
            throw new AccessException("Access error");
        }

        Map<Long, Long> views = new HashMap<>();

        for (Long id : ids) {

            Optional<Long> viewsOptional = stats.stream()
                    .filter(s -> s.getUri().equals("/events/" + id))
                    .map(ViewStatsDto::getHits)
                    .findFirst();

            Long eventViews = viewsOptional.orElse(0L);
            views.put(id, eventViews);
        }

        return views;
    }
}
