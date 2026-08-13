package dev.simongarcia.devpulse.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
public class Repository {
    @Id
    private String gitRepoId;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    private String name;
    private String fullName;
    private String primaryLanguage;
    private boolean isPrivate;
    private LocalDateTime lastSyncedAt;
}
