package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface MovimentacaoEstoqueJpaRepository extends JpaRepository<MovimentacaoEstoqueEntity, Long>,
        JpaSpecificationExecutor<MovimentacaoEstoqueEntity> {

    default List<MovimentacaoEstoqueEntity> findByFiltros(Long medicamentoId,
            Long loteId,
            MovimentacaoEstoque.Tipo tipo,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal,
            Long unidadeId) {
        Specification<MovimentacaoEstoqueEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (medicamentoId != null) {
                predicates.add(cb.equal(root.get("medicamento").get("id"), medicamentoId));
            }
            if (loteId != null) {
                predicates.add(cb.equal(root.get("lote").get("id"), loteId));
            }
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }
            if (unidadeId != null) {
                predicates.add(cb.equal(root.get("unidadeSaude").get("id"), unidadeId));
            }
            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataMovimentacao"), dataInicial));
            }
            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataMovimentacao"), dataFinal));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return findAll(spec, Sort.by(Sort.Direction.DESC, "dataMovimentacao"));
    }

    default List<MovimentacaoEstoqueEntity> findByPeriodoObrigatorio(Long medicamentoId,
            MovimentacaoEstoque.Tipo tipo,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal,
            Long unidadeId) {
        Specification<MovimentacaoEstoqueEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (medicamentoId != null) {
                predicates.add(cb.equal(root.get("medicamento").get("id"), medicamentoId));
            }
            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }
            if (unidadeId != null) {
                predicates.add(cb.equal(root.get("unidadeSaude").get("id"), unidadeId));
            }
            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataMovimentacao"), dataInicial));
            }
            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataMovimentacao"), dataFinal));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return findAll(spec, Sort.by(Sort.Direction.DESC, "dataMovimentacao"));
    }
}
