package dev.simongarcia.devpulse.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "developer_profiles")
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
