package dev.simongarcia.devpulse.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id
    private long gitHubId;
    private String username;
    private String avatarUrl;
    private String accessToken;
    private LocalDateTime createdAt;

}
