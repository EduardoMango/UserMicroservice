package org.eduardomango.authmicroservice.services;

import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.Enum.UserProfile;
import org.eduardomango.authmicroservice.models.ProfileEntity;
import org.eduardomango.authmicroservice.models.auth.GithubUserResponse;
import org.eduardomango.authmicroservice.repositories.CredentialsRepository;
import org.eduardomango.authmicroservice.repositories.ProfileRepository;
import org.eduardomango.authmicroservice.services.interfaces.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final CredentialsRepository credentialsRepository;
    private final ProfileRepository profileRepository;
    private final JwtService tokenProvider;

    public JpaUserDetailsService(CredentialsRepository credentialsRepository, ProfileRepository profileRepository, JwtService tokenProvider) {
        this.credentialsRepository = credentialsRepository;
        this.profileRepository = profileRepository;
        this.tokenProvider = tokenProvider;
    }

    public CredentialsEntity findOrCreateUser(GithubUserResponse githubUser) {
    return credentialsRepository
        .findByUsername(githubUser.getLogin())
        .orElseGet(
            () -> {
              // create local user
              CredentialsEntity newUser = new CredentialsEntity();
              newUser.setUsername(githubUser.getLogin());
              newUser.setEmail(githubUser.getEmail());
              newUser.setCreatedAt(LocalDateTime.now());
              newUser.setOauth2Provider("Github");
              newUser.setOauth2ProviderId(String.valueOf(githubUser.getId()));
              ProfileEntity profile =
                  profileRepository
                      .findByProfile(UserProfile.CUSTOMER)
                      .orElse(new ProfileEntity(UserProfile.CUSTOMER));
              newUser.setProfile(profile);

              String refreshToken = tokenProvider.generateRefreshToken(newUser);
              newUser.setRefreshToken(refreshToken);
              return credentialsRepository.save(newUser);
            });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialsRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
