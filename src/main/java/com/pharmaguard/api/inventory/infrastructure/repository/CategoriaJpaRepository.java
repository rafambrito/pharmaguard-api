package com.pharmaguard.api.inventory.infrastructure.repository;

import com.pharmaguard.api.inventory.infrastructure.repository.entity.CategoriaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<CategoriaEntity> findByNomeIgnoreCase(String nome);
}
