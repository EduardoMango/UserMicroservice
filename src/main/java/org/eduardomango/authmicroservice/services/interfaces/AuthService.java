package org.eduardomango.authmicroservice.services.interfaces;

import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;
import org.eduardomango.authmicroservice.models.auth.AuthResponse;

public interface AuthService {

    CredentialsEntity authenticate(AuthRequest input);
    AuthResponse refreshAccessToken(String refreshToken);
    void updateCredentials(Long id, AuthRequest newCredentials);
    void addUser(CredentialsEntity user);
}
