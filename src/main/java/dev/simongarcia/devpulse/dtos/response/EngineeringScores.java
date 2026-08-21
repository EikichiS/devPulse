package dev.simongarcia.devpulse.dtos.response;

public record EngineeringScores(
        int totalReposAnalyzed,
        int activityScore,
        int testingScore,
        int ciScore,
        int docsScore
) {
}
