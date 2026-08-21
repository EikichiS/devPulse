package dev.simongarcia.devpulse.security;

import dev.simongarcia.devpulse.entities.AppUser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }


    public String generateToken(AppUser appUser){
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(appUser.getGitHubId()))
                .claim("username", appUser.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(24, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }
}
