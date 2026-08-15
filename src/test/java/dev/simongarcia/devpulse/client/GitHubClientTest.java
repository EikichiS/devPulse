package dev.simongarcia.devpulse.client;

import dev.simongarcia.devpulse.clients.GitHubClient;
import dev.simongarcia.devpulse.dtos.GitHubFileContentDto;
import dev.simongarcia.devpulse.dtos.GitHubRepoDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GitHubClientTest {

    @Test
    void fetchReposDevuelveDatosReales() {
        GitHubClient client = new GitHubClient();
        String token = System.getenv("GITHUB_PAT");

        List<GitHubRepoDto> repos = client.fetchRepos(token);

        assertThat(repos).isNotEmpty();
    }

    @Test
    void fetchLanguagesDevuelveDatosReales() {
        GitHubClient client = new GitHubClient();
        String token = System.getenv("GITHUB_PAT");

        Map<String, Long> languages = client.fetchLanguages(token, "EikichiS", "colo-colo-backend");

        assertThat(languages).containsKey("Java");
    }

    @Test
    void fetchFileContentDevuelveElPomXml() {
        GitHubClient client = new GitHubClient();
        String token = System.getenv("GITHUB_PAT");

        GitHubFileContentDto file = client.fetchFileContent(token, "EikichiS", "colo-colo-backend", "pom.xml");

        assertThat(file.encoding()).isEqualTo("base64");
        assertThat(file.content()).isNotBlank();
    }



}
