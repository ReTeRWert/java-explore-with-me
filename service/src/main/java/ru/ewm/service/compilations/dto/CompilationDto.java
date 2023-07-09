package ru.ewm.service.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ewm.service.events.dto.ShortEventDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private List<ShortEventDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
