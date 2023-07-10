package ru.ewm.service.events.searchParams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ewm.service.events.enums.SortTypes;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicSearchParams extends SearchParams {
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private SortTypes sort;
    private String ip;


    public PublicSearchParams(List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Integer size, String text, Boolean paid, Boolean onlyAvailable, SortTypes sort, String ip) {
        super(categories, rangeStart, rangeEnd, from, size);

        this.text = text;
        this.paid = paid;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
        this.ip = ip;
    }
}
