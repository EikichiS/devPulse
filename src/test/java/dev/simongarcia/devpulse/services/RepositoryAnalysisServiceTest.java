package dev.simongarcia.devpulse.services;

import dev.simongarcia.devpulse.analysis.TechnologyDetector;
import dev.simongarcia.devpulse.clients.GitHubClient;
import dev.simongarcia.devpulse.dtos.DetectedTechnology;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryAnalysisServiceTest {
    @Test
    void analyzeAllReposDetectaTecnologiasReales() throws Exception {
        GitHubClient client = new GitHubClient();
        TechnologyDetector detector = new TechnologyDetector();
        RepositoryAnalysisService service = new RepositoryAnalysisService(client, detector,null,null,null);
        String token = System.getenv("GITHUB_PAT");

        Map<String, List<DetectedTechnology>> results = service.analyzeAllRepos(token);

        assertThat(results).containsKey("colo-colo-backend");
        assertThat(results.get("colo-colo-backend"))
                .extracting(DetectedTechnology::name)
                .contains("Spring Boot (Web/MVC)");
    }

}
