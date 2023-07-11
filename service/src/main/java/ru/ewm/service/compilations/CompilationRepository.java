package ru.ewm.service.compilations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findCompilationByIsPinnedIs(Boolean pinned, Pageable pageable);
}
