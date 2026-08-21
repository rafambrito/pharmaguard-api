package com.pharmaguard.api.supplier.application;

import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import java.util.Objects;
import java.util.Optional;

public class LeadTimeFornecedorUseCaseImpl implements LeadTimeFornecedorUseCase {

    private final LeadTimeFornecedorRepositoryPort repository;

    public LeadTimeFornecedorUseCaseImpl(LeadTimeFornecedorRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public Fornecedor atualizar(Long fornecedorId, Integer leadTimeDias) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        Objects.requireNonNull(leadTimeDias, "leadTimeDias e obrigatorio");

        Fornecedor fornecedor = repository.buscarPorId(fornecedorId)
                .orElseThrow(() -> new ResourceNotFoundException("fornecedor nao encontrado"));

        fornecedor.setLeadTimeDias(leadTimeDias);
        fornecedor.marcarAtualizacao();
        return repository.atualizar(fornecedor);
    }

    @Override
    public Optional<Fornecedor> consultar(Long fornecedorId) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        return repository.buscarPorId(fornecedorId);
    }
}