package ru.ewm.stats.server;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final EndpointRepository endpointRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

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

    @Transactional
    @Override
    public EndpointHitDto saveNewEndpoint(EndpointHitDto newEndpoint) {
        EndpointHit endpointHit = EndpointMapper.toEndpointHit(newEndpoint);

        return EndpointMapper.toEndpointHitDto(endpointRepository.save(endpointHit));
    }
}