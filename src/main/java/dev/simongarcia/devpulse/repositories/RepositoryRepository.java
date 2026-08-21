package dev.simongarcia.devpulse.repositories;

import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.entities.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {

    List<Repository> findByAppUserAndIsPrivateFalse(AppUser appUser);
}
