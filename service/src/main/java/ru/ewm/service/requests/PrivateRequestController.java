package ru.ewm.service.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.requests.dto.RequestDto;
import ru.ewm.service.requests.dto.RequestStatusUpdateRequest;
import ru.ewm.service.requests.dto.RequestStatusUpdateResult;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping("/requests")
    public List<RequestDto> getUserRequests(@PathVariable Long userId) {

        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId,
                                 @RequestParam(value = "eventId") Long eventId) {

        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {

        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getEventRequests(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {

        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public RequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @RequestBody @NotNull @Valid RequestStatusUpdateRequest request) {

        return requestService.updateRequests(userId, eventId, request);
    }
}
