package org.eduardomango.authmicroservice.repositories;

import org.eduardomango.authmicroservice.models.PermitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermitRepository
    extends JpaRepository<PermitEntity, Long>, JpaSpecificationExecutor<PermitEntity> {}
