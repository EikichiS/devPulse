package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.Repository;
import dev.simongarcia.devpulse.entities.RepositoryAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryAnalysisRepository extends JpaRepository<RepositoryAnalysis, Long> {
    List<RepositoryAnalysis> findByRepository(Repository repository);
}
