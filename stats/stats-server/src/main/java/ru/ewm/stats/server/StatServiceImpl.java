package ru.ewm.stats.server;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final EndpointRepository endpointRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws ValidationException {

        if (start.isAfter(end)) {
            throw new ValidationException("Start date must be before end date.");
        }

        if (uris != null) {

            if (unique) {
                return endpointRepository.getStatsWithUriWithUnique(start, end, uris)
                        .stream()
                        .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                        .collect(Collectors.toList());
            }

            return endpointRepository.getStatsWithUri(start, end, uris)
                    .stream()
                    .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                    .collect(Collectors.toList());
        } else {

            if (unique) {
                return endpointRepository.getStatsWithoutUriWithUnique(start, end)
                        .stream()
                        .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                        .collect(Collectors.toList());
            }

            return endpointRepository.getStatsWithoutUri(start, end)
                    .stream()
                    .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EndpointHitDto saveNewEndpoint(EndpointHitDto newEndpoint) {
        EndpointHit endpointHit = EndpointMapper.toEndpointHit(newEndpoint);

        return EndpointMapper.toEndpointHitDto(endpointRepository.saveAndFlush(endpointHit));
    }
}
