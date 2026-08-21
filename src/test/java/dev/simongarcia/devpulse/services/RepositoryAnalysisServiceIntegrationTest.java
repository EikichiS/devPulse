package dev.simongarcia.devpulse.services;

import dev.simongarcia.devpulse.entities.AnalysisJob;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.enums.AnalysisStatus;
import dev.simongarcia.devpulse.repositories.AnalysisJobRepository;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class RepositoryAnalysisServiceIntegrationTest {

    @Autowired
    private RepositoryAnalysisService repositoryAnalysisService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AnalysisJobRepository analysisJobRepository;

    @Test
    void processAnalysisGuardaResultadosReales() {
        AppUser appUser = appUserRepository.findById(8736806L).orElseThrow();

        AnalysisJob job = repositoryAnalysisService.createPendingJob(appUser);
        repositoryAnalysisService.processAnalysis(job, appUser);

        await().atMost(Duration.ofSeconds(60)).until(() ->
                analysisJobRepository.findById(job.getId())
                        .map(j -> j.getStatus() == AnalysisStatus.COMPLETED)
                        .orElse(false)
        );

        AnalysisJob completed = analysisJobRepository.findById(job.getId()).orElseThrow();
        assertThat(completed.getStatus()).isEqualTo(AnalysisStatus.COMPLETED);
    }
}
