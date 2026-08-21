package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoJpaRepository extends JpaRepository<MedicamentoEntity, Long> {

    boolean existsByNomeIgnoreCaseAndApresentacaoIgnoreCase(String nome, String apresentacao);

    Optional<MedicamentoEntity> findByNomeIgnoreCaseAndApresentacaoIgnoreCase(String nome, String apresentacao);
}
