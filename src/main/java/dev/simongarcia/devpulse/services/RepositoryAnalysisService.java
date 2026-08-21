package dev.simongarcia.devpulse.services;

import dev.simongarcia.devpulse.analysis.TechnologyDetector;
import dev.simongarcia.devpulse.clients.GitHubClient;
import dev.simongarcia.devpulse.dtos.DetectedTechnology;
import dev.simongarcia.devpulse.dtos.GitHubFileContentDto;
import dev.simongarcia.devpulse.dtos.GitHubRepoDto;
import dev.simongarcia.devpulse.entities.AnalysisJob;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.entities.DeveloperProfile;
import dev.simongarcia.devpulse.entities.Repository;
import dev.simongarcia.devpulse.entities.RepositoryAnalysis;
import dev.simongarcia.devpulse.enums.AnalysisStatus;
import dev.simongarcia.devpulse.repositories.AnalysisJobRepository;
import dev.simongarcia.devpulse.repositories.DeveloperProfileRepository;
import dev.simongarcia.devpulse.repositories.RepositoryAnalysisRepository;
import dev.simongarcia.devpulse.repositories.RepositoryRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RepositoryAnalysisService {

    private static final Set<String> TESTING_FRAMEWORKS = Set.of(
            "JUnit 5", "Mockito", "AssertJ", "Testcontainers", "Jest", "Vitest", "Cypress"
    );

    private final GitHubClient gitHubClient;
    private final TechnologyDetector technologyDetector;
    private final RepositoryRepository repositoryRepository;
    private final RepositoryAnalysisRepository repositoryAnalysisRepository;
    private final AnalysisJobRepository analysisJobRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RepositoryAnalysisService(GitHubClient gitHubClient, TechnologyDetector technologyDetector, RepositoryRepository repositoryRepository, RepositoryAnalysisRepository repositoryAnalysisRepository, AnalysisJobRepository analysisJobRepository, DeveloperProfileRepository developerProfileRepository) {
        this.gitHubClient = gitHubClient;
        this.technologyDetector = technologyDetector;
        this.repositoryRepository = repositoryRepository;
        this.repositoryAnalysisRepository = repositoryAnalysisRepository;
        this.analysisJobRepository = analysisJobRepository;
        this.developerProfileRepository = developerProfileRepository;
    }

    public Map<String, List<DetectedTechnology>> analyzeAllRepos(String accessToken) {
        List<GitHubRepoDto> repos = gitHubClient.fetchRepos(accessToken);
        Map<String, List<DetectedTechnology>> results = new HashMap<>();

        for (GitHubRepoDto repo : repos) {
            String owner = repo.fullName().split("/")[0];
            try {
                GitHubFileContentDto file = gitHubClient.fetchFileContent(accessToken, owner, repo.name(), "pom.xml");
                List<DetectedTechnology> detected = technologyDetector.detect(file.name(), file.content());
                results.put(repo.name(), detected);
            } catch (Exception e) {
                // este repo no tiene pom.xml (probablemente no es Java) — se salta, no es un error
            }
        }

        return results;
    }

    public AnalysisJob createPendingJob(AppUser appUser) {
        AnalysisJob job = new AnalysisJob();
        job.setAppUser(appUser);
        job.setStatus(AnalysisStatus.PENDING);
        job.setStartedAt(LocalDateTime.now());
        return analysisJobRepository.save(job);
    }

    @Async
    public void processAnalysis(AnalysisJob job, AppUser appUser) {
        job.setStatus(AnalysisStatus.PROCESSING);
        analysisJobRepository.save(job);

        String accessToken = appUser.getAccessToken();
        List<GitHubRepoDto> repos = gitHubClient.fetchRepos(accessToken);

        for (GitHubRepoDto repoDto : repos) {
            String owner = repoDto.fullName().split("/")[0];

            // 1. el repo siempre se guarda, tenga o no tecnologías detectables
            Repository repository = repositoryRepository.findById(repoDto.id()).orElseGet(Repository::new);
            repository.setAppUser(appUser);
            repository.setName(repoDto.name());
            repository.setFullName(repoDto.fullName());
            repository.setPrivate(repoDto.isPrivate());
            repository.setPrimaryLanguage(repoDto.language());
            repository.setLastSyncedAt(LocalDateTime.now());
            repository.setGitRepoId(repoDto.id());
            repositoryRepository.save(repository);

            RepositoryAnalysis repositoryAnalysis = new RepositoryAnalysis();
            repositoryAnalysis.setAnalysisJob(job);
            repositoryAnalysis.setRepository(repository);

            // 2. desglose de lenguajes — ya lo teníamos construido, ahora sí se usa
            try {
                Map<String, Long> languages = gitHubClient.fetchLanguages(accessToken, owner, repoDto.name());
                repositoryAnalysis.setLanguageBreakdown(objectMapper.writeValueAsString(languages));
            } catch (Exception e) {
                repositoryAnalysis.setLanguageBreakdown("{}");
            }

            // 3. detección de tecnologías — el archivo a pedir depende del lenguaje del repo,
            //    no se intenta pom.xml a ciegas en repos que obviamente no son Java
            String dependencyFile = determineDependencyFile(repoDto.language());
            List<DetectedTechnology> detected = List.of();
            if (dependencyFile != null) {
                try {
                    GitHubFileContentDto file = gitHubClient.fetchFileContent(accessToken, owner, repoDto.name(), dependencyFile);
                    detected = technologyDetector.detect(file.name(), file.content());
                } catch (Exception e) {
                    // el archivo esperado no existe en este repo puntual, se sigue con lista vacía
                }
            }
            repositoryAnalysis.setDetectedTechnologies(objectMapper.writeValueAsString(detected));

            // 4. señales de madurez del proyecto
            repositoryAnalysis.setDockerized(gitHubClient.pathExists(accessToken, owner, repoDto.name(), "Dockerfile"));
            repositoryAnalysis.setHasCi(gitHubClient.pathExists(accessToken, owner, repoDto.name(), ".github/workflows"));
            repositoryAnalysis.setHasTests(detected.stream().anyMatch(t -> TESTING_FRAMEWORKS.contains(t.name())));
            repositoryAnalysis.setHasReadme(gitHubClient.pathExists(accessToken, owner, repoDto.name(), "README.md"));

            repositoryAnalysisRepository.save(repositoryAnalysis);
        }

        updateDeveloperProfile(appUser, repos);

        job.setStatus(AnalysisStatus.COMPLETED);
        job.setFinishedAt(LocalDateTime.now());
        analysisJobRepository.save(job);
    }

    private void updateDeveloperProfile(AppUser appUser, List<GitHubRepoDto> githubRepos) {
        List<Repository> repos = repositoryRepository.findByAppUserAndIsPrivateFalse(appUser);
        int total = repos.size();
        int ciCount = 0;
        int testCount = 0;
        int docsCount = 0;
        Map<String, Long> aggregateLanguages = new HashMap<>();

        for (Repository repo : repos) {
            List<RepositoryAnalysis> analyses = repositoryAnalysisRepository.findByRepository(repo);
            if (analyses.isEmpty()) {
                continue;
            }
            RepositoryAnalysis latest = analyses.getLast();

            if (latest.isHasCi()) {
                ciCount++;
            }
            if (latest.isHasTests()) {
                testCount++;
            }
            if (latest.isHasReadme()) {
                docsCount++;
            }

            Map<String, Long> languages = parseLanguageBreakdown(latest.getLanguageBreakdown());
            languages.forEach((lang, bytes) -> aggregateLanguages.merge(lang, bytes, Long::sum));
        }

        // actividad: repos públicos con un push en los últimos 90 días, según lo que GitHub ya informa
        // en /user/repos (campo pushed_at) — no hace falta ninguna llamada nueva a la API para esto.
        long recentlyActiveCount = githubRepos.stream()
                .filter(r -> !r.isPrivate())
                .filter(r -> isRecentlyPushed(r.pushedAt()))
                .count();
        int activityTotal = (int) githubRepos.stream().filter(r -> !r.isPrivate()).count();

        DeveloperProfile profile = developerProfileRepository.findByAppUser(appUser).orElseGet(DeveloperProfile::new);
        profile.setAppUser(appUser);
        profile.setTotalReposAnalyzed(total);
        profile.setCiScore(scoreFromRatio(ciCount, total));
        profile.setTestingScore(scoreFromRatio(testCount, total));
        profile.setDocsScore(scoreFromRatio(docsCount, total));
        profile.setActivityScore(scoreFromRatio((int) recentlyActiveCount, activityTotal));

        try {
            profile.setLanguageBreakdown(objectMapper.writeValueAsString(aggregateLanguages));
        } catch (Exception e) {
            profile.setLanguageBreakdown("{}");
        }

        developerProfileRepository.save(profile);
    }

    private boolean isRecentlyPushed(String pushedAt) {
        if (pushedAt == null) {
            return false;
        }
        try {
            Instant pushedInstant = Instant.parse(pushedAt);
            return pushedInstant.isAfter(Instant.now().minus(90, ChronoUnit.DAYS));
        } catch (Exception e) {
            return false;
        }
    }

    private int scoreFromRatio(int count, int total) {
        if (total == 0) {
            return 0;
        }
        return Math.round((count / (float) total) * 20);
    }

    private Map<String, Long> parseLanguageBreakdown(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Long>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String determineDependencyFile(String language) {
        if (language == null) {
            return null;
        }
        return switch (language) {
            case "Java" -> "pom.xml";
            case "JavaScript", "TypeScript", "Vue" -> "package.json";
            default -> null;
        };
    }




}
