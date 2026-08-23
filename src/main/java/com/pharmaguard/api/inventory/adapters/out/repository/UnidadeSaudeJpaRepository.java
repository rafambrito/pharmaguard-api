package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeSaudeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pharmaguard.api.inventory.domain.UnidadeSaude;

public interface UnidadeSaudeJpaRepository extends JpaRepository<UnidadeSaudeEntity, Long> {
	boolean existsByIdentificacaoIgnoreCase(String identificacao);

	boolean existsByIdAndStatus(Long id, UnidadeSaude.Status status);
}