package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.RepositoryAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryAnalysisRepository extends JpaRepository<RepositoryAnalysis, Long> {
}
