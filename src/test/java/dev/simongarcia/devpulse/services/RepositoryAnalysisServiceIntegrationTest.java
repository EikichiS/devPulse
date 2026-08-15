package dev.simongarcia.devpulse.services;

import dev.simongarcia.devpulse.entities.AnalysisJob;
import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.enums.AnalysisStatus;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RepositoryAnalysisServiceIntegrationTest {

    @Autowired
    private RepositoryAnalysisService repositoryAnalysisService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void runAnalysisGuardaResultadosReales() {
        AppUser appUser = appUserRepository.findById(8736806L).orElseThrow();

        AnalysisJob job = repositoryAnalysisService.runAnalysis(appUser);

        assertThat(job.getStatus()).isEqualTo(AnalysisStatus.COMPLETED);
    }
}
