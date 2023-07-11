package ru.ewm.service.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.service.compilations.dto.CompilationDto;
import ru.ewm.service.compilations.dto.NewCompilationDto;
import ru.ewm.service.compilations.dto.UpdateCompilationRequest;
import ru.ewm.service.events.EventRepository;
import ru.ewm.service.events.EventService;
import ru.ewm.service.events.dto.ShortEventDto;
import ru.ewm.service.events.model.Event;
import ru.ewm.service.exception.NotFoundException;
import ru.ewm.service.requests.Request;
import ru.ewm.service.requests.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final RequestService requestService;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events;
        List<Long> eventIds = newCompilationDto.getEvents();

        if (eventIds != null && !eventIds.isEmpty()) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        } else {
            events = new ArrayList<>();
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);

        compilation.setEvents(events);


        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));


        Map<Long, Long> views = eventService.getStats(events, false);

        if (!views.isEmpty()) {
            compilationDto.getEvents().forEach(e -> e.setViews(views.get(e.getId())));
        }

        List<Request> confirmedRequests = requestService.findConfirmedRequests(events);

        for (ShortEventDto shortDto : compilationDto.getEvents()) {
            shortDto.setConfirmedRequests((int) confirmedRequests
                    .stream()
                    .filter(request -> request.getEvent().getId().equals(shortDto.getId()))
                    .count());
        }

        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        if (updateRequest.getTitle() != null && updateRequest.getTitle().length() > 50) {
            throw new IllegalArgumentException("Title name is too long");
        }

        Compilation compilationToUpdate = getCompilationIfExist(compId);

        if (updateRequest.getPinned() != null) {
            compilationToUpdate.setIsPinned(updateRequest.getPinned());
        }

        if (updateRequest.getTitle() != null) {
            compilationToUpdate.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateRequest.getEvents());
            compilationToUpdate.setEvents(events);
        }

        CompilationDto updatedCompilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilationToUpdate));

        Map<Long, Long> views = eventService.getStats(compilationToUpdate.getEvents(), false);

        if (!views.isEmpty()) {
            updatedCompilationDto.getEvents().forEach(e -> e.setViews(views.get(e.getId())));
        }

        List<Request> confirmedRequests = requestService.findConfirmedRequests(compilationToUpdate.getEvents());

        for (ShortEventDto shortDto : updatedCompilationDto.getEvents()) {
            shortDto.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(request -> request.getEvent().getId().equals(shortDto.getId()))
                    .count());
        }

        return updatedCompilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        getCompilationIfExist(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Long from, Integer size) {
        int startPage = Math.toIntExact(from / size);
        Pageable pageable = PageRequest.of(startPage, size);

        List<Compilation> compilations = compilationRepository.findCompilationByIsPinnedIs(pinned, pageable);

        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = getCompilationIfExist(compId);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);

        Map<Long, Long> views = eventService.getStats(compilation.getEvents(), false);
        if (!views.isEmpty()) {
            setViewsToDto(views, compilationDto);
        }

        List<Request> confirmedRequests = requestService.findConfirmedRequests(compilation.getEvents());

        for (ShortEventDto shortDto : compilationDto.getEvents()) {
            shortDto.setConfirmedRequests((int) confirmedRequests.stream()
                    .filter(request -> request.getEvent().getId().equals(shortDto.getId()))
                    .count());
        }

        return CompilationMapper.toCompilationDto(compilation);
    }

    private void setViewsToDto(Map<Long, Long> views, CompilationDto compilationDto) {
        compilationDto.getEvents().forEach(e -> e.setViews(views.get(e.getId())));
    }

    @Override
    public Compilation getCompilationIfExist(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);

        return compilation.orElseThrow(()
                -> new NotFoundException(compId, Compilation.class.getSimpleName()));
    }
}
