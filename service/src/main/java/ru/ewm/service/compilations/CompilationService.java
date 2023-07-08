package ru.ewm.service.compilations;

import org.springframework.web.bind.annotation.PathVariable;
import ru.ewm.service.compilations.dto.CompilationDto;
import ru.ewm.service.compilations.dto.NewCompilationDto;
import ru.ewm.service.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationRequest);

    void deleteCompilation(@PathVariable Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Long from, Integer size);

    CompilationDto getCompilation(@PathVariable Long compId);
}
