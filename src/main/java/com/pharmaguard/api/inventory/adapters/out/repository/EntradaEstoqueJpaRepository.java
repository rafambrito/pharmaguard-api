package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.EntradaEstoqueEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface EntradaEstoqueJpaRepository extends JpaRepository<EntradaEstoqueEntity, Long>,
        JpaSpecificationExecutor<EntradaEstoqueEntity> {

    default List<EntradaEstoqueEntity> findByFiltros(Long medicamentoId, Long loteId, Long unidadeId) {
        Specification<EntradaEstoqueEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (medicamentoId != null) {
                predicates.add(cb.equal(root.get("medicamento").get("id"), medicamentoId));
            }
            if (loteId != null) {
                predicates.add(cb.equal(root.get("lote").get("id"), loteId));
            }
            if (unidadeId != null) {
                predicates.add(cb.equal(root.get("unidadeSaude").get("id"), unidadeId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return findAll(spec, Sort.by(Sort.Direction.DESC, "dataEntrada"));
    }
}
