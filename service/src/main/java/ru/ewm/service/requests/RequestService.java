package ru.ewm.service.requests;

import ru.ewm.service.events.model.Event;
import ru.ewm.service.requests.dto.RequestDto;
import ru.ewm.service.requests.dto.RequestStatusUpdateRequest;
import ru.ewm.service.requests.dto.RequestStatusUpdateResult;

import java.util.List;

public interface RequestService {

    List<RequestDto> getUserRequests(Long userId);

    RequestDto addRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getEventRequests(Long userId, Long eventId);

    RequestStatusUpdateResult updateRequests(Long userId, Long eventId, RequestStatusUpdateRequest request);

    List<Request> findConfirmedRequests(List<Event> events);
}
