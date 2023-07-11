package ru.ewm.service.events.searchParams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ewm.service.events.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminSearchParams extends SearchParams {
    private List<Long> users;
    private List<EventState> states;

    public AdminSearchParams(List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from,
                             Integer size, List<Long> users, List<EventState> states) {

        super(categories, rangeStart, rangeEnd, from, size);

        this.users = users;
        this.states = states;
    }
}
