package com.pharmaguard.api.supplier.adapters.out.repository;

import com.pharmaguard.api.supplier.adapters.out.repository.entity.ContatoFornecedorEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoFornecedorJpaRepository extends JpaRepository<ContatoFornecedorEntity, Long> {

    Optional<ContatoFornecedorEntity> findByIdAndFornecedor_Id(Long id, Long fornecedorId);

    List<ContatoFornecedorEntity> findAllByFornecedor_Id(Long fornecedorId);

    void deleteByIdAndFornecedor_Id(Long id, Long fornecedorId);
}