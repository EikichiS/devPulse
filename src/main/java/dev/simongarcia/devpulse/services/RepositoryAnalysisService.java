package dev.simongarcia.devpulse.services;

import dev.simongarcia.devpulse.analysis.TechnologyDetector;
import dev.simongarcia.devpulse.clients.GitHubClient;
import dev.simongarcia.devpulse.dtos.DetectedTechnology;
import dev.simongarcia.devpulse.dtos.GitHubFileContentDto;
import dev.simongarcia.devpulse.dtos.GitHubRepoDto;
import dev.simongarcia.devpulse.entities.AnalysisJob;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.entities.Repository;
import dev.simongarcia.devpulse.entities.RepositoryAnalysis;
import dev.simongarcia.devpulse.enums.AnalysisStatus;
import dev.simongarcia.devpulse.repositories.AnalysisJobRepository;
import dev.simongarcia.devpulse.repositories.RepositoryAnalysisRepository;
import dev.simongarcia.devpulse.repositories.RepositoryRepository;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepositoryAnalysisService {

    private final GitHubClient gitHubClient;
    private final TechnologyDetector technologyDetector;
    private final RepositoryRepository repositoryRepository;
    private final RepositoryAnalysisRepository repositoryAnalysisRepository;
    private final AnalysisJobRepository analysisJobRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RepositoryAnalysisService(GitHubClient gitHubClient, TechnologyDetector technologyDetector, RepositoryRepository repositoryRepository, RepositoryAnalysisRepository repositoryAnalysisRepository, AnalysisJobRepository analysisJobRepository) {
        this.gitHubClient = gitHubClient;
        this.technologyDetector = technologyDetector;
        this.repositoryRepository = repositoryRepository;
        this.repositoryAnalysisRepository = repositoryAnalysisRepository;
        this.analysisJobRepository = analysisJobRepository;
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

    public AnalysisJob runAnalysis(AppUser appUser) {
        String accessToken = appUser.getAccessToken();

        AnalysisJob job = new AnalysisJob();
        job.setAppUser(appUser);
        job.setStatus(AnalysisStatus.PROCESSING);
        job.setStartedAt(LocalDateTime.now());
        analysisJobRepository.save(job);

        List<GitHubRepoDto> repos = gitHubClient.fetchRepos(accessToken);

        for (GitHubRepoDto repoDto : repos) {
            String owner = repoDto.fullName().split("/")[0];
            try {
                GitHubFileContentDto file = gitHubClient.fetchFileContent(accessToken, owner, repoDto.name(), "pom.xml");
                List<DetectedTechnology> detected = technologyDetector.detect(file.name(), file.content());

                Repository repository = repositoryRepository.findById(repoDto.id()).orElseGet(Repository::new);
                repository.setAppUser(appUser);
                repository.setName(repoDto.name());
                repository.setFullName(repoDto.fullName());
                repository.setPrivate(repoDto.isPrivate());
                repository.setPrimaryLanguage(repoDto.language());
                repository.setLastSyncedAt(LocalDateTime.now());
                repository.setGitRepoId(repoDto.id());

                repositoryRepository.save(repository);

                // 1. buscar o crear el Repository (findById con repoDto.id(), orElseGet(Repository::new))
                //    y setearle: gitRepoId, appUser, name, fullName, primaryLanguage, isPrivate, lastSyncedAt
                //    guardarlo con repositoryRepository.save(...)

                 RepositoryAnalysis repositoryAnalysis = new RepositoryAnalysis();
                 repositoryAnalysis.setAnalysisJob(job);
                 repositoryAnalysis.setRepository(repository);
                 repositoryAnalysis.setDetectedTechnologies(objectMapper.writeValueAsString(detected));

                 repositoryAnalysisRepository.save(repositoryAnalysis);

                // 2. crear un RepositoryAnalysis nuevo, setearle: repository (el de arriba), analysisJob (job),
                //    detectedTechnologies (objectMapper.writeValueAsString(detected))
                //    guardarlo con repositoryAnalysisRepository.save(...)

            } catch (Exception e) {
                // sin pom.xml, se salta
            }
        }

        job.setStatus(AnalysisStatus.COMPLETED);
        job.setFinishedAt(LocalDateTime.now());
        analysisJobRepository.save(job);

        return job;
    }


}
