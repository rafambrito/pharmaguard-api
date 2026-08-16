package com.pharmaguard.api.auth.infrastructure.repository;

import com.pharmaguard.api.auth.infrastructure.repository.entity.UsuarioEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    Optional<UsuarioEntity> findByLogin(String login);
}