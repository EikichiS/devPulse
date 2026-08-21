package dev.simongarcia.devpulse.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "repository_analyses")
@Getter
@Setter
public class RepositoryAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;
    @ManyToOne
    @JoinColumn(name = "analysis_job_id")
    private AnalysisJob analysisJob;
    private String languageBreakdown;
    private String detectedTechnologies;
    private boolean hasCi;
    private boolean isDockerized;
    private boolean hasTests;
    private boolean hasReadme;
}
