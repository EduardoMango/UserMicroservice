package org.eduardomango.authmicroservice.services.impl;

import org.eduardomango.authmicroservice.exceptions.InvalidParametersException;
import org.eduardomango.authmicroservice.exceptions.InvalidPasswordException;
import org.eduardomango.authmicroservice.exceptions.InvalidUsernameException;
import org.eduardomango.authmicroservice.exceptions.UserNotFoundException;
import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;
import org.eduardomango.authmicroservice.models.auth.AuthResponse;
import org.eduardomango.authmicroservice.repositories.CredentialsRepository;
import org.eduardomango.authmicroservice.services.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final CredentialsRepository credentialsRepository;
    private final JwtServiceImpl tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(CredentialsRepository credentialsRepository, JwtServiceImpl tokenProvider, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.credentialsRepository = credentialsRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public CredentialsEntity authenticate(AuthRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );
        return credentialsRepository.findByUsername(input.username()).orElseThrow();
    }

    public AuthResponse refreshAccessToken(String refreshToken) {
        CredentialsEntity user = credentialsRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (!tokenProvider.validateRefreshToken(refreshToken,user)) {
            throw new IllegalArgumentException("Refresh token expired or invalid");
        }

        String newAccessToken = tokenProvider.generateToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken);
        credentialsRepository.save(user);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }


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
}
