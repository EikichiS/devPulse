package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.DeveloperProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile, Long> {
}
