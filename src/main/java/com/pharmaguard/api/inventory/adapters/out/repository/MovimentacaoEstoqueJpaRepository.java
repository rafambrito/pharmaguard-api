package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoqueEntity, Long> {

    @Query("""
            select m
            from MovimentacaoEstoqueEntity m
            where (:medicamentoId is null or m.medicamento.id = :medicamentoId)
              and (:loteId is null or m.lote.id = :loteId)
              and (:tipo is null or m.tipo = :tipo)
              and (:unidadeId is null or m.unidadeSaude.id = :unidadeId)
              and (:dataInicial is null or m.dataMovimentacao >= :dataInicial)
              and (:dataFinal is null or m.dataMovimentacao <= :dataFinal)
            order by m.dataMovimentacao desc
            """)
    List<MovimentacaoEstoqueEntity> findByFiltros(@Param("medicamentoId") Long medicamentoId,
            @Param("loteId") Long loteId,
            @Param("tipo") MovimentacaoEstoque.Tipo tipo,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal,
            @Param("unidadeId") Long unidadeId);
}
