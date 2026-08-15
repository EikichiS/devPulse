package dev.simongarcia.devpulse.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubFileContentDto(
        String name,
        String path,
        String content,
        String encoding

) {
}
