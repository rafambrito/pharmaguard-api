package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaidaEstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaidaEstoqueJpaRepository extends JpaRepository<SaidaEstoqueEntity, Long> {

    List<SaidaEstoqueEntity> findAllByMedicamento_IdOrderByDataSaidaDesc(Long medicamentoId);

    List<SaidaEstoqueEntity> findAllByMedicamento_IdAndUnidadeSaude_IdOrderByDataSaidaDesc(
            Long medicamentoId, Long unidadeId);
}
