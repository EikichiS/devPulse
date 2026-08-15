package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {
}
