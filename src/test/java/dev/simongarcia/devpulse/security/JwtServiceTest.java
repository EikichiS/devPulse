package dev.simongarcia.devpulse.security;

import dev.simongarcia.devpulse.entities.AppUser;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtServiceTest {

    @Test
    void generateTokenDevuelveUnJwtValido() {
        SecretKey secretKey = Keys.hmacShaKeyFor("una-clave-de-prueba-de-al-menos-32-caracteres".getBytes(StandardCharsets.UTF_8));
        JwtService jwtService = new JwtService(secretKey);

        AppUser appUser = new AppUser();
        appUser.setGitHubId(8736806L);
        appUser.setUsername("EikichiS");

        String token = jwtService.generateToken(appUser);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }
}
