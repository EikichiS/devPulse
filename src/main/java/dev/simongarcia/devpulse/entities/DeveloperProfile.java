package dev.simongarcia.devpulse.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "developer_profiles")
@Getter
@Setter
public class DeveloperProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "app_user_id", unique = true)
    private AppUser appUser;
    private String languageBreakdown;
    private int totalReposAnalyzed;
    private int activityScore;
    private int testingScore;
    private int ciScore;
    private int docsScore;
}
