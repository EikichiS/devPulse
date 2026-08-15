package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob,String> {
}
