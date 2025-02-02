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

    /** Retrieves the credentials of a user authenticated via GitHub
     * If not present in database (new user), creates a new user and stores it in the database
     *
     * @param githubUser user authenticated via GitHub
     * @return credentials of the user
     */
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

    /**
     * Retrieves a user from the database based on the provided username.
     * @param username of the user to be retrieved
     * @return the user details of the user
     * @throws UsernameNotFoundException when the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialsRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
