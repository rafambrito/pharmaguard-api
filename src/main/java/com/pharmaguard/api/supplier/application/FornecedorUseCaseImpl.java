package com.pharmaguard.api.supplier.application;

import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import com.pharmaguard.api.supplier.domain.FornecedorIdentidadeUnicaPort;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FornecedorUseCaseImpl implements FornecedorUseCase {

    private final FornecedorRepositoryPort repository;

    public FornecedorUseCaseImpl(FornecedorRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        Objects.requireNonNull(fornecedor, "fornecedor e obrigatorio");

        fornecedor.validarIdentidadeUnica(new FornecedorIdentidadeUnicaPort() {
            @Override
            public boolean existePorCodigo(String codigo) {
                return codigo != null && repository.existePorCodigo(codigo);
            }

            @Override
            public boolean existePorDocumento(String documento) {
                return documento != null && repository.existePorDocumento(documento);
            }
        });

        fornecedor.setStatus(Fornecedor.Status.ATIVO);
        fornecedor.marcarCriacao();
        return repository.salvar(fornecedor);
    }

    @Override
    public Fornecedor atualizar(Fornecedor fornecedor) {
        Objects.requireNonNull(fornecedor, "fornecedor e obrigatorio");
        Objects.requireNonNull(fornecedor.getId(), "id do fornecedor e obrigatorio");

        Fornecedor existente = repository.buscarPorId(fornecedor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("fornecedor nao encontrado"));

        if (fornecedor.getCodigo() != null && !Objects.equals(existente.getCodigo(), fornecedor.getCodigo())
                && repository.existePorCodigo(fornecedor.getCodigo())) {
            throw new BusinessException("codigo de fornecedor ja cadastrado");
        }

        if (fornecedor.getDocumento() != null && !Objects.equals(existente.getDocumento(), fornecedor.getDocumento())
                && repository.existePorDocumento(fornecedor.getDocumento())) {
            throw new BusinessException("documento de fornecedor ja cadastrado");
        }

        fornecedor.setStatus(existente.getStatus());
        fornecedor.setDataCriacao(existente.getDataCriacao());
        fornecedor.marcarAtualizacao();
        return repository.atualizar(fornecedor);
    }

    @Override
    public void deletar(Long id) {
        Objects.requireNonNull(id, "id do fornecedor e obrigatorio");
        repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("fornecedor nao encontrado"));
        repository.deletar(id);
    }

    @Override
    public Optional<Fornecedor> buscarPorId(Long id) {
        Objects.requireNonNull(id, "id do fornecedor e obrigatorio");
        return repository.buscarPorId(id);
    }

    @Override
    public List<Fornecedor> buscarTodos() {
        return repository.buscarTodos();
    }

    @Override
    public Optional<Fornecedor> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("codigo e obrigatorio");
        }
        return repository.buscarPorCodigo(codigo.trim());
    }

    @Override
    public Optional<Fornecedor> buscarPorDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("documento e obrigatorio");
        }
        return repository.buscarPorDocumento(documento.trim());
    }
}