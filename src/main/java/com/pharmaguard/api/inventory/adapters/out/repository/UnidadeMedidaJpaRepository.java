package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeMedidaJpaRepository extends JpaRepository<UnidadeMedidaEntity, Long> {

    boolean existsBySiglaIgnoreCase(String sigla);

    Optional<UnidadeMedidaEntity> findBySiglaIgnoreCase(String sigla);
}
