package dev.simongarcia.devpulse.clients;

import dev.simongarcia.devpulse.dtos.GitHubFileContentDto;
import dev.simongarcia.devpulse.dtos.GitHubRepoDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GitHubClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader("Accept", "application/vnd.github+json")
            .build();

    public List<GitHubRepoDto> fetchRepos(String accessToken) {
        return restClient.get()
                .uri("/user/repos")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubRepoDto>>() {});
    }

    public Map<String, Long> fetchLanguages(String accessToken, String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/languages", owner, repo)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Long>>() {});
    }

    public GitHubFileContentDto fetchFileContent(String accessToken, String owner, String repo, String path) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .body(GitHubFileContentDto.class);
    }

    public boolean pathExists(String accessToken, String owner, String repo, String path) {
        try {
            restClient.get()
                    .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
