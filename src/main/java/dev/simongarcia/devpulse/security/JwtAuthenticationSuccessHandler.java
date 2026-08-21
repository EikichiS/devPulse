package dev.simongarcia.devpulse.security;

import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;

    public JwtAuthenticationSuccessHandler(JwtService jwtService, AppUserRepository appUserRepository) {
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        long gitHubId = ((Number) oAuth2User.getAttributes().get("id")).longValue();
        AppUser appUser = appUserRepository.findById(gitHubId).orElseThrow();

        String token = jwtService.generateToken(appUser);

        response.sendRedirect("http://localhost:5173/?token=" + token);
    }
}
