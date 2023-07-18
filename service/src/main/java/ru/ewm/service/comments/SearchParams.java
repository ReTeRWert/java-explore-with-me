package ru.ewm.service.comments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchParams {
    private String text;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long from;
    private Integer size;
}
