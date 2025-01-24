package org.eduardomango.authmicroservice.repositories;

import org.eduardomango.authmicroservice.models.Enum.UserProfile;
import org.eduardomango.authmicroservice.models.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository
    extends JpaRepository<ProfileEntity, Long>, JpaSpecificationExecutor<ProfileEntity> {

    Optional<ProfileEntity> findByProfile(UserProfile profile);
}
