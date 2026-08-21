package dev.simongarcia.devpulse.controllers;

import dev.simongarcia.devpulse.entities.AnalysisJob;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.repositories.AnalysisJobRepository;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import dev.simongarcia.devpulse.services.RepositoryAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyses")
public class AnalysisController {

    private final RepositoryAnalysisService repositoryAnalysisService;
    private final AppUserRepository appUserRepository;
    private final AnalysisJobRepository analysisJobRepository;

    public AnalysisController(RepositoryAnalysisService repositoryAnalysisService,
                              AppUserRepository appUserRepository,
                              AnalysisJobRepository analysisJobRepository) {
        this.repositoryAnalysisService = repositoryAnalysisService;
        this.appUserRepository = appUserRepository;
        this.analysisJobRepository = analysisJobRepository;
    }

    @PostMapping
    public ResponseEntity<AnalysisJob> triggerAnalysis(@AuthenticationPrincipal Jwt jwt) {
        long gitHubId = Long.parseLong(jwt.getSubject());
        AppUser appUser = appUserRepository.findById(gitHubId).orElseThrow();

        AnalysisJob job = repositoryAnalysisService.createPendingJob(appUser);
        repositoryAnalysisService.processAnalysis(job, appUser);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisJob> getAnalysis(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        long gitHubId = Long.parseLong(jwt.getSubject());
        AnalysisJob job = analysisJobRepository.findById(id).orElseThrow();

        if (job.getAppUser().getGitHubId() != gitHubId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(job);
    }

}
