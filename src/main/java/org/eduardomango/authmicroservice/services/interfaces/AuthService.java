package org.eduardomango.authmicroservice.services.interfaces;

import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.eduardomango.authmicroservice.models.auth.AuthRequest;

public interface AuthService {

    CredentialsEntity authenticate(AuthRequest input);
    //void updateCredentials(Long id, AuthRequest newCredentials);
}
