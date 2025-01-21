package org.eduardomango.authmicroservice.repositories;

import org.eduardomango.authmicroservice.models.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository
    extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {}
