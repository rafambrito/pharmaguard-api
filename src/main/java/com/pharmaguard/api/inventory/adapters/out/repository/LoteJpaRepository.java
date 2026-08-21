package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteJpaRepository extends JpaRepository<LoteEntity, Long> {

    boolean existsByNumeroLoteIgnoreCaseAndMedicamento_Id(String numeroLote, Long medicamentoId);

    Optional<LoteEntity> findByIdAndMedicamento_Id(Long id, Long medicamentoId);

    List<LoteEntity> findAllByMedicamento_Id(Long medicamentoId);
}
