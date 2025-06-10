package org.eduardomango.authmicroservice.services.impl;

import org.eduardomango.authmicroservice.exceptions.InvalidParametersException;
import org.eduardomango.authmicroservice.exceptions.InvalidPasswordException;
import org.eduardomango.authmicroservice.exceptions.InvalidUsernameException;
import org.eduardomango.authmicroservice.exceptions.UserNotFoundException;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.Enum.UserProfile;
import org.eduardomango.authmicroservice.models.ProfileEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;
import org.eduardomango.authmicroservice.models.auth.AuthResponse;
import org.eduardomango.authmicroservice.repositories.CredentialsRepository;
import org.eduardomango.authmicroservice.repositories.ProfileRepository;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CredentialsRepository credentialsRepository;
    private final JwtServiceImpl tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    public AuthServiceImpl(CredentialsRepository credentialsRepository, JwtServiceImpl tokenProvider, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ProfileRepository profileRepository) {
        this.credentialsRepository = credentialsRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
    }

    /** Authenticate a user by username and password
     *
     * @param input containing username and password
     * @return the credentials of the user if it was correctly authenticated
     * otherwise, throws exceptions to capture the error
     */
    public CredentialsEntity authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );
        return credentialsRepository.findByUsername(input.username()).orElseThrow();
    }

    /** Receives a refresh token from a user and returns a new access token
     * as well as updates the refresh token and returns it
     *
     * @param refreshToken refresh token of the user. Must be the same as the database
     * @return AuthResponse, object containing the new access token and the new refresh token
     */
    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        String username = tokenProvider.extractUsername(refreshToken);

        CredentialsEntity user = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token does not match");
        }

        if (!tokenProvider.validateRefreshToken(refreshToken, user)) {
            throw new IllegalArgumentException("Refresh token expired or invalid");
        }

        String newAccessToken = tokenProvider.generateToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        credentialsRepository.save(user);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }


    /** Receives a user id and a new credentials object,
     * updates the username or password or both of the user in the database
     *
     * @param id the id of the user to update
     * @param newCredentials new credentials containing the username, password or both
     */
    public void updateCredentials(Long id, AuthRequest newCredentials) {
        Optional<CredentialsEntity> credentialsOptional = credentialsRepository.findById(id);

        if (credentialsOptional.isEmpty()) {
            throw new UserNotFoundException("user not found");
        }
        CredentialsEntity credentials = credentialsOptional.get();

        if (newCredentials.username() == null && newCredentials.password() == null) {
            throw new InvalidParametersException
                    ("At least username or password must be provided");
        }

        if (newCredentials.username() != null) {
            if (newCredentials.username().length() < 6) {
                throw new InvalidUsernameException
                        ("Username must be at least 6 characters long");
            }
            credentials.setUsername(newCredentials.username());
        }

        if (newCredentials.password() != null) {
            if (newCredentials.password().length() < 6) {
                throw new InvalidPasswordException
                        ("Password must be at least 6 characters long");
            }
            credentials.setPassword(passwordEncoder.encode(newCredentials.password()));
        }

        credentialsRepository.save(credentials);
    }

    /**
     * Receives a user object, encodes its password,
     * assigns a default profile (CUSTOMER),
     * sets the creation timestamp, and saves it to the database.
     *
     * @param user The user credentials to be added to the database.
     */
    public void addUser(CredentialsEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        ProfileEntity profile =
                profileRepository
                        .findByProfile(UserProfile.CUSTOMER)
                        .orElse(new ProfileEntity(UserProfile.CUSTOMER));
        user.setProfile(profile);
        credentialsRepository.save(user);
    }
}
