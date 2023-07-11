package ru.ewm.service.events;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.events.dto.FullEventDto;
import ru.ewm.service.events.dto.NewEventDto;
import ru.ewm.service.events.dto.UpdateEventUserRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Validated
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<FullEventDto> getEventsForPrivate(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {

        return eventService.getEventsForPrivate(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto addEvent(@PathVariable Long userId,
                                 @RequestBody @NotNull @Valid NewEventDto newEventDto) {

        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public FullEventDto getEventByIdForPrivate(@PathVariable Long userId,
                                               @PathVariable Long eventId) {

        return eventService.getEventByIdForPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEventByIdForPrivate(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @RequestBody @Valid UpdateEventUserRequest updateEvent) {

        return eventService.updateEventByIdForPrivate(userId, eventId, updateEvent);
    }
}
