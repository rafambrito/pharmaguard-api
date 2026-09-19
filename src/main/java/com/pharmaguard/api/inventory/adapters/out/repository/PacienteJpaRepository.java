package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.PacienteEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteJpaRepository extends JpaRepository<PacienteEntity, Long> {
    boolean existsByCpf(String cpf);
    List<PacienteEntity> findAllByCpf(String cpf);
    List<PacienteEntity> findAllByNomeContainingIgnoreCaseOrderByNome(String nome);
}