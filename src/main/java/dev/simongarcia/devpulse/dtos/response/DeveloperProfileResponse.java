package dev.simongarcia.devpulse.dtos.response;

import dev.simongarcia.devpulse.dtos.RepositorySummaryDto;

import java.util.List;

public record DeveloperProfileResponse(
        String username,
        String avatarUrl,
        EngineeringScores scores,
        List<RepositorySummaryDto> repositories
) {
}
