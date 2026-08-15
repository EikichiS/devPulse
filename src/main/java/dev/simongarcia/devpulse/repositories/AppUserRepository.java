package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
