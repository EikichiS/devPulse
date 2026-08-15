package dev.simongarcia.devpulse.analysis;

import dev.simongarcia.devpulse.dtos.DetectedTechnology;
import dev.simongarcia.devpulse.dtos.TechnologyRule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class TechnologyDetector {

    private final Map<String, String> knownDependencies;

    public TechnologyDetector() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("technologies.json");
        List<TechnologyRule> rules = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<TechnologyRule>>() {}
        );

        Map<String, String> map = new HashMap<>();
        for (TechnologyRule rule : rules) {
            map.put(rule.dependencyKeyword(), rule.technologyName());
        }
        this.knownDependencies = map;
    }

    public List<DetectedTechnology> detect(String fileName, String base64Content) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(base64Content);
        String fileContent = new String(decodedBytes, StandardCharsets.UTF_8);

        List<DetectedTechnology> detected = new ArrayList<>();
        for (Map.Entry<String, String> entry : knownDependencies.entrySet()) {
            if (fileContent.contains(entry.getKey())) {
                detected.add(new DetectedTechnology(entry.getValue(), fileName, entry.getKey()));
            }
        }

        return detected;
    }
}

