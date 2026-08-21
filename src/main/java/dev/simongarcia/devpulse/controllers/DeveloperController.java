package dev.simongarcia.devpulse.controllers;

import dev.simongarcia.devpulse.dtos.DetectedTechnology;
import dev.simongarcia.devpulse.dtos.RepositorySummaryDto;
import dev.simongarcia.devpulse.dtos.response.DeveloperProfileResponse;
import dev.simongarcia.devpulse.dtos.response.EngineeringScores;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.entities.DeveloperProfile;
import dev.simongarcia.devpulse.entities.Repository;
import dev.simongarcia.devpulse.entities.RepositoryAnalysis;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import dev.simongarcia.devpulse.repositories.DeveloperProfileRepository;
import dev.simongarcia.devpulse.repositories.RepositoryAnalysisRepository;
import dev.simongarcia.devpulse.repositories.RepositoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final AppUserRepository appUserRepository;
    private final RepositoryRepository repositoryRepository;
    private final RepositoryAnalysisRepository repositoryAnalysisRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeveloperController(AppUserRepository appUserRepository,
                               RepositoryRepository repositoryRepository,
                               RepositoryAnalysisRepository repositoryAnalysisRepository,
                               DeveloperProfileRepository developerProfileRepository) {
        this.appUserRepository = appUserRepository;
        this.repositoryRepository = repositoryRepository;
        this.repositoryAnalysisRepository = repositoryAnalysisRepository;
        this.developerProfileRepository = developerProfileRepository;
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<DeveloperProfileResponse> getProfile(@PathVariable String
                                                                       username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow();
        List<Repository> publicRepos =
                repositoryRepository.findByAppUserAndIsPrivateFalse(appUser);

        List<RepositorySummaryDto> summaries = publicRepos.stream()
                .map(this::toSummary)
                .toList();

        EngineeringScores scores = developerProfileRepository.findByAppUser(appUser)
                .map(this::toScores)
                .orElse(new EngineeringScores(0, 0, 0, 0, 0));

        return ResponseEntity.ok(new DeveloperProfileResponse(appUser.getUsername(),
                appUser.getAvatarUrl(), scores, summaries));
    }

    private EngineeringScores toScores(DeveloperProfile profile) {
        return new EngineeringScores(
                profile.getTotalReposAnalyzed(),
                profile.getActivityScore(),
                profile.getTestingScore(),
                profile.getCiScore(),
                profile.getDocsScore()
        );
    }

    private RepositorySummaryDto toSummary(Repository repository) {
        List<RepositoryAnalysis> analyses =
                repositoryAnalysisRepository.findByRepository(repository);

        List<DetectedTechnology> technologies = List.of();
        Map<String, Long> languageBreakdown = Map.of();
        boolean isDockerized = false;
        boolean hasCi = false;
        boolean hasTests = false;

        if (!analyses.isEmpty()) {
            RepositoryAnalysis latest = analyses.getLast();
            technologies = parseTechnologies(latest.getDetectedTechnologies());
            languageBreakdown = parseLanguageBreakdown(latest.getLanguageBreakdown());
            isDockerized = latest.isDockerized();
            hasCi = latest.isHasCi();
            hasTests = latest.isHasTests();
        }

        return new RepositorySummaryDto(repository.getName(), repository.getPrimaryLanguage(),
                languageBreakdown, technologies, isDockerized, hasCi, hasTests);
    }

    private List<DetectedTechnology> parseTechnologies(String json) {
        try {
            return objectMapper.readValue(json, new
                    TypeReference<List<DetectedTechnology>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private Map<String, Long> parseLanguageBreakdown(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Long>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
