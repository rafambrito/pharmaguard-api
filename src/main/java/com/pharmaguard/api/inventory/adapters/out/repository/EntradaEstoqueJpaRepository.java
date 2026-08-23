package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.EntradaEstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntradaEstoqueJpaRepository extends JpaRepository<EntradaEstoqueEntity, Long> {

    @Query("""
            select e
            from EntradaEstoqueEntity e
            where (:medicamentoId is null or e.medicamento.id = :medicamentoId)
              and (:loteId is null or e.lote.id = :loteId)
              and (:unidadeId is null or e.unidadeSaude.id = :unidadeId)
            order by e.dataEntrada desc
            """)
    List<EntradaEstoqueEntity> findByFiltros(@Param("medicamentoId") Long medicamentoId,
            @Param("loteId") Long loteId, @Param("unidadeId") Long unidadeId);
}
