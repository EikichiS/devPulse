package dev.simongarcia.devpulse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.simongarcia.devpulse.security.EncryptedStringConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@Getter
@Setter
public class AppUser {
    @Id
    private long gitHubId;
    private String username;
    private String avatarUrl;
    @JsonIgnore
    @Convert(converter = EncryptedStringConverter.class)
    private String accessToken;
    private LocalDateTime createdAt;

}
