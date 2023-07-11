package ru.ewm.service.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.service.events.EventRepository;
import ru.ewm.service.events.enums.EventState;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.exception.InvalidOperationException;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.requests.dto.RequestDto;
import ru.ewm.service.requests.dto.RequestStatusUpdateRequest;
import ru.ewm.service.requests.dto.RequestStatusUpdateResult;
import ru.ewm.service.users.User;
import ru.ewm.service.users.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterIdIs(userId);

        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        User requester = userService.getUserIfExist(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(eventId, Event.class.getSimpleName()));

        Optional<Request> optionalRequest = requestRepository
                .findFirstByEventIdIsAndRequesterIdIs(event.getId(), requester.getId());

        optionalRequest.ifPresent(r -> {
            throw new InvalidOperationException("Request already exist");
        });

        if (event.getInitiator().equals(requester)) {
            throw new InvalidOperationException("Initiator can't be requester.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidOperationException("Event is not published.");
        }


        List<Long> confirmedRequests = findConfirmedRequestsById(eventId);

        if (event.getParticipantLimit() != 0 && confirmedRequests.size() == event.getParticipantLimit()) {
            throw new InvalidOperationException("Limit over");
        }


        Request newRequest = new Request();

        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequester(requester);
        newRequest.setEvent(event);

        if (!event.getIsRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestState.CONFIRMED);
        } else {
            newRequest.setStatus(RequestState.PENDING);
        }

        return RequestMapper.toRequestDto(requestRepository.save(newRequest));
    }

    private List<Long> findConfirmedRequestsById(Long eventId) {

        List<Request> requests = requestRepository.findAllByEventIdIsAndStatusIs(eventId, RequestState.CONFIRMED);

        return requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        User requester = userService.getUserIfExist(userId);
        Request request = getRequestIfExist(requestId);

        if (!request.getRequester().equals(requester)) {
            throw new InvalidOperationException("This user cannot cancel a request.");
        }

        request.setStatus(RequestState.CANCELED);

        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        User initiator = userService.getUserIfExist(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(eventId, Event.class.getSimpleName()));

        if (!event.getInitiator().equals(initiator)) {
            throw new InvalidOperationException("User is not event initiator");
        }

        List<Request> foundRequests = requestRepository.findAllByEventIdIs(eventId);

        return foundRequests
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestStatusUpdateResult updateRequests(Long userId, Long eventId, RequestStatusUpdateRequest request) {
        User initiator = userService.getUserIfExist(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(eventId, Event.class.getSimpleName()));

        if (!event.getInitiator().equals(initiator)) {
            throw new InvalidOperationException("User is not event initiator.");
        }

        if (!event.getIsRequestModeration() || event.getParticipantLimit() == 0) {
            throw new InvalidOperationException("Request does not need confirmation.");
        }

        int confirmedRequests = findConfirmedRequests(List.of(event)).size();

        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(request.getRequestIds());
        RequestStatusUpdateResult requestStatusUpdateResult = new RequestStatusUpdateResult();
        List<RequestDto> requestDtos;

        switch (request.getStatus()) {
            case REJECTED:
                for (Request participationRequest : requestsToUpdate) {

                    if (!RequestState.PENDING.equals(participationRequest.getStatus())) {
                        throw new InvalidOperationException("Request must be in PENDING.");
                    }

                    participationRequest.setStatus(RequestState.REJECTED);
                }

                requestDtos = requestRepository.saveAll(requestsToUpdate)
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList());

                requestStatusUpdateResult.setRejectedRequests(requestDtos);
                break;

            case CONFIRMED:
                for (Request participationRequest : requestsToUpdate) {

                    if (!RequestState.PENDING.equals(participationRequest.getStatus())) {
                        throw new InvalidOperationException("Request must be in PENDING.");
                    }

                    if (confirmedRequests == event.getParticipantLimit()) {
                        throw new InvalidOperationException("Limit is over.");
                    }

                    participationRequest.setStatus(RequestState.CONFIRMED);
                    confirmedRequests++;
                }

                requestDtos = requestRepository.saveAll(requestsToUpdate)
                        .stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList());

                requestStatusUpdateResult.setConfirmedRequests(requestDtos);
                break;

            default:
                throw new InvalidOperationException("Invalid status");
        }

        return requestStatusUpdateResult;
    }

    @Override
    public List<Request> findConfirmedRequests(List<Event> events) {
        return requestRepository.findAllByEventInAndStatusIs(events, RequestState.CONFIRMED);
    }

    @Override
    public Request getRequestIfExist(Long requestId) {
        Optional<Request> request = requestRepository.findById(requestId);

        return request
                .orElseThrow(()
                        -> new NotFoundException(requestId, Request.class.getSimpleName()));
    }
}
