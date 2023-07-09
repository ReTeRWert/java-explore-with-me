package ru.ewm.stats.server;

import dto.ViewStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "AND e.uri IN (?3) " +
            "GROUP BY e.app, e.uri")
    List<ViewStatsDto> getStatsWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "AND e.uri IN (?3) " +
            "GROUP BY e.app, e.uri")
    List<ViewStatsDto> getStatsWithUriWithUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "GROUP BY e.app, e.uri")
    List<ViewStatsDto> getStatsWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "GROUP BY e.app, e.uri")
    List<ViewStatsDto> getStatsWithoutUriWithUnique(LocalDateTime start, LocalDateTime end);
}
