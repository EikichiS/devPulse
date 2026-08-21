package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.entities.DeveloperProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile, Long> {
    Optional<DeveloperProfile> findByAppUser(AppUser appUser);
}
