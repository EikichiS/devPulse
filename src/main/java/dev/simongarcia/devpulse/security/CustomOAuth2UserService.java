package dev.simongarcia.devpulse.security;

import dev.simongarcia.devpulse.entities.AppUser;
import dev.simongarcia.devpulse.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        long gitHubId = ((Number) oAuth2User.getAttributes().get("id")).longValue();
        String username = (String) oAuth2User.getAttributes().get("login");
        String avatarUrl = (String) oAuth2User.getAttributes().get("avatar_url");
        Optional<AppUser> existing = appUserRepository.findById(gitHubId);
        AppUser appUser = existing.orElseGet(AppUser::new);
        if (existing.isEmpty()) {
            appUser.setCreatedAt(LocalDateTime.now());
        }
        appUser.setAccessToken(userRequest.getAccessToken().getTokenValue());
        appUser.setUsername(username);
        appUser.setAvatarUrl(avatarUrl);
        appUser.setGitHubId(gitHubId);

        appUserRepository.save(appUser);
        return oAuth2User;
    }
}
