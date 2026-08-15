package dev.simongarcia.devpulse.dtos;

public record DetectedTechnology(
        String name,
        String evidenceFile,
        String evidenceMatch
) {
}
