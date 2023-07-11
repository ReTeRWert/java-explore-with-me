package ru.ewm.service.events;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.events.dto.FullEventDto;
import ru.ewm.service.events.dto.UpdateEventAdminRequest;
import ru.ewm.service.events.enums.EventState;
import ru.ewm.service.events.searchParams.AdminSearchParams;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<FullEventDto> searchEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                  @RequestParam(required = false) List<EventState> states,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                  LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {

        AdminSearchParams adminSearchParams = new AdminSearchParams(
                categories,
                rangeStart,
                rangeEnd,
                from,
                size,
                users,
                states
        );

        return eventService.searchEventsByAdmin(adminSearchParams);

    }

    @PatchMapping("/{eventId}")
    public FullEventDto updateEventByIdForAdmin(@PathVariable Long eventId,
                                                @RequestBody @NotNull @Valid UpdateEventAdminRequest updateEventAdminRequest) {

        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}
