package dev.simongarcia.devpulse.controllers;

import dev.simongarcia.devpulse.entities.AnalysisJob;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.repositories.AnalysisJobRepository;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import dev.simongarcia.devpulse.services.RepositoryAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public ResponseEntity<AnalysisJob> triggerAnalysis(@AuthenticationPrincipal OAuth2User principal) {
        long gitHubId = ((Number) principal.getAttributes().get("id")).longValue();
        AppUser appUser = appUserRepository.findById(gitHubId).orElseThrow();

        AnalysisJob job = repositoryAnalysisService.runAnalysis(appUser);

        return ResponseEntity.ok(job);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisJob> getAnalysis(@PathVariable String id) {
        AnalysisJob job = analysisJobRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(job);
    }

}
