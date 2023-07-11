package ru.ewm.service.events;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.events.dto.FullEventDto;
import ru.ewm.service.events.enums.SortTypes;
import ru.ewm.service.events.searchParams.PublicSearchParams;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<FullEventDto> searchEventsByPublic(@RequestParam(required = false) String text,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) Boolean paid,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                   LocalDateTime rangeStart,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                   LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                   @RequestParam(required = false) SortTypes sort,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size,
                                                   HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        PublicSearchParams searchParams = new PublicSearchParams(
                categories,
                rangeStart,
                rangeEnd,
                from,
                size,
                text,
                paid,
                onlyAvailable,
                sort,
                ip
        );

        return eventService.searchEventsByPublic(searchParams);
    }

    @GetMapping("/{id}")
    public FullEventDto getEventByIdByPublic(@PathVariable Long id, HttpServletRequest request) {

        return eventService.getEventByIdForPublic(id, request);
    }
}
