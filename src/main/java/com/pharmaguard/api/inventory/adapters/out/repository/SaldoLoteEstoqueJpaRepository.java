package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaldoLoteEstoqueJpaRepository extends JpaRepository<SaldoLoteEstoqueEntity, Long> {
}
