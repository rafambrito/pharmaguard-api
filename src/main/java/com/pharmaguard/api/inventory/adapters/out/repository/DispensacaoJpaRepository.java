package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.DispensacaoEntity;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DispensacaoJpaRepository extends JpaRepository<DispensacaoEntity, Long>, JpaSpecificationExecutor<DispensacaoEntity> {
    default List<DispensacaoEntity> findByFiltros(Long unidadeId, Long pacienteId, Long medicamentoId,
            LocalDateTime dataInicial, LocalDateTime dataFinal) {
        Specification<DispensacaoEntity> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (unidadeId != null) predicates.add(criteriaBuilder.equal(root.get("saidaEstoque").get("unidadeSaude").get("id"), unidadeId));
            if (pacienteId != null) predicates.add(criteriaBuilder.equal(root.get("paciente").get("id"), pacienteId));
            if (medicamentoId != null) predicates.add(criteriaBuilder.equal(root.get("saidaEstoque").get("medicamento").get("id"), medicamentoId));
            if (dataInicial != null) predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("saidaEstoque").get("dataSaida"), dataInicial));
            if (dataFinal != null) predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("saidaEstoque").get("dataSaida"), dataFinal));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return findAll(specification, Sort.by(Sort.Direction.DESC, "saidaEstoque.dataSaida"));
    }
}