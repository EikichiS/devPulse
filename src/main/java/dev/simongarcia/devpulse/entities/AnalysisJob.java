package dev.simongarcia.devpulse.entities;

import dev.simongarcia.devpulse.enums.AnalysisStatus;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_jobs")
@Getter
@Setter
public class AnalysisJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String errorMessage;
}
