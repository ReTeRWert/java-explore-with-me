package ru.ewm.service.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ewm.service.compilations.CompilationService;
import ru.ewm.service.compilations.dto.CompilationDto;
import ru.ewm.service.compilations.dto.NewCompilationDto;
import ru.ewm.service.compilations.dto.UpdateCompilationRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/admin/compilations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @NotNull @Valid NewCompilationDto newCompilationDto) {
        log.info("Adding new compilation.");

        return compilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @NotNull UpdateCompilationRequest compilationRequest) {
        log.info("Update compilation with id: {}", compId);

        return compilationService.updateCompilation(compId, compilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Deleting compilation with id: {}", compId);

        compilationService.deleteCompilation(compId);
    }
}
