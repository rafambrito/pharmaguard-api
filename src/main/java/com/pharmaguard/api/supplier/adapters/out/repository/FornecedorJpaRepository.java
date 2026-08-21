package com.pharmaguard.api.supplier.adapters.out.repository;

import com.pharmaguard.api.supplier.adapters.out.repository.entity.FornecedorEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorJpaRepository extends JpaRepository<FornecedorEntity, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByDocumento(String documento);

    Optional<FornecedorEntity> findByCodigoIgnoreCase(String codigo);

    Optional<FornecedorEntity> findByDocumento(String documento);
}