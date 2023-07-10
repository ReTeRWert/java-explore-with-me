package ru.ewm.service.compilations;

import ru.ewm.service.compilations.dto.CompilationDto;
import ru.ewm.service.compilations.dto.NewCompilationDto;
import ru.ewm.service.events.EventMapper;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setIsPinned(newCompilationDto.getPinned());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()),

                compilation.getId(),
                compilation.getIsPinned(),
                compilation.getTitle()
        );
    }
}
