package dev.simongarcia.devpulse.entities;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "repositories")
@Getter
@Setter
public class Repository {
    @Id
    private long gitRepoId;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    private String name;
    private String fullName;
    private String primaryLanguage;
    private boolean isPrivate;
    private LocalDateTime lastSyncedAt;
}
