package dev.simongarcia.devpulse.analysis;

import dev.simongarcia.devpulse.clients.GitHubClient;
import dev.simongarcia.devpulse.dtos.DetectedTechnology;
import dev.simongarcia.devpulse.dtos.GitHubFileContentDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TechnologyDetectorTest {

    @Test
    void detectaSpringBootEnPomXmlReal() throws Exception {
        GitHubClient client = new GitHubClient();
        TechnologyDetector detector = new TechnologyDetector();
        String token = System.getenv("GITHUB_PAT");

        GitHubFileContentDto file = client.fetchFileContent(token, "EikichiS", "colo-colo-backend", "pom.xml");
        List<DetectedTechnology> detected = detector.detect(file.name(), file.content());

        assertThat(detected).extracting(DetectedTechnology::name).contains("Spring Boot (Web/MVC)");
    }

}
