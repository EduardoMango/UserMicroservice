package org.eduardomango.authmicroservice.repositories;

import org.eduardomango.authmicroservice.models.CredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository
    extends JpaRepository<CredentialsEntity, Long>, JpaSpecificationExecutor<CredentialsEntity> {

    Optional<CredentialsEntity> findByUsername(String username);

    Optional<CredentialsEntity> findByOauth2ProviderId(String oauth2ProviderId);

    Optional<CredentialsEntity> findByRefreshToken(String refreshToken);
}
