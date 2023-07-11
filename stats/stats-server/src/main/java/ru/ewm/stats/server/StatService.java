package ru.ewm.stats.server;


import dto.EndpointHitDto;
import dto.ViewStatsDto;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) throws ValidationException;

    EndpointHitDto saveNewEndpoint(EndpointHitDto newEndpoint);
}
