package dev.simongarcia.devpulse.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepoDto(
        long id,
        String name,
        @JsonProperty("full_name") String fullName,
        String language,
        @JsonProperty("private") boolean isPrivate
) {
}
