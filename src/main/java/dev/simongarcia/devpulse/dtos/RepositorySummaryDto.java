package dev.simongarcia.devpulse.dtos;

import java.util.List;
import java.util.Map;

public record RepositorySummaryDto(String name,
                                   String primaryLanguage,
                                   Map<String, Long> languageBreakdown,
                                   List<DetectedTechnology> detectedTechnologies,
                                   boolean isDockerized,
                                   boolean hasCi,
                                   boolean hasTests) {
}
