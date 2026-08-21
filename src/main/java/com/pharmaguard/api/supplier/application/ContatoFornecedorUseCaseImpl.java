package com.pharmaguard.api.supplier.application;

import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import com.pharmaguard.api.supplier.domain.ContatoFornecedor;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ContatoFornecedorUseCaseImpl implements ContatoFornecedorUseCase {

    private final ContatoFornecedorRepositoryPort repository;

    public ContatoFornecedorUseCaseImpl(ContatoFornecedorRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public ContatoFornecedor salvar(Long fornecedorId, ContatoFornecedor contato) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        Objects.requireNonNull(contato, "contato e obrigatorio");

        Fornecedor fornecedor = repository.buscarFornecedorPorId(fornecedorId)
                .orElseThrow(() -> new ResourceNotFoundException("fornecedor nao encontrado"));

        garantirContatoNaoDuplicado(fornecedorId, contato, null);
        fornecedor.adicionarContato(contato);
        contato.setAtivo(true);
        contato.marcarCriacao();
        return repository.salvar(fornecedorId, contato);
    }

    @Override
    public ContatoFornecedor atualizar(Long fornecedorId, ContatoFornecedor contato) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        Objects.requireNonNull(contato, "contato e obrigatorio");
        Objects.requireNonNull(contato.getId(), "id do contato e obrigatorio");

        repository.buscarFornecedorPorId(fornecedorId)
                .orElseThrow(() -> new ResourceNotFoundException("fornecedor nao encontrado"));

        ContatoFornecedor existente = repository.buscarPorId(fornecedorId, contato.getId())
                .orElseThrow(() -> new ResourceNotFoundException("contato nao encontrado"));

        garantirContatoNaoDuplicado(fornecedorId, contato, existente.getId());
        contato.setFornecedor(existente.getFornecedor());
        contato.setDataCriacao(existente.getDataCriacao());
        contato.marcarAtualizacao();
        return repository.atualizar(fornecedorId, contato);
    }

    @Override
    public void deletar(Long fornecedorId, Long contatoId) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        Objects.requireNonNull(contatoId, "contatoId e obrigatorio");

        repository.buscarPorId(fornecedorId, contatoId)
                .orElseThrow(() -> new ResourceNotFoundException("contato nao encontrado"));
        repository.deletar(fornecedorId, contatoId);
    }

    @Override
    public Optional<ContatoFornecedor> buscarPorId(Long fornecedorId, Long contatoId) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        Objects.requireNonNull(contatoId, "contatoId e obrigatorio");
        return repository.buscarPorId(fornecedorId, contatoId);
    }

    @Override
    public List<ContatoFornecedor> listarPorFornecedor(Long fornecedorId) {
        Objects.requireNonNull(fornecedorId, "fornecedorId e obrigatorio");
        repository.buscarFornecedorPorId(fornecedorId)
                .orElseThrow(() -> new ResourceNotFoundException("fornecedor nao encontrado"));
        return repository.listarPorFornecedor(fornecedorId);
    }

    private void garantirContatoNaoDuplicado(Long fornecedorId, ContatoFornecedor contato, Long contatoIdAtual) {
        boolean duplicado = repository.listarPorFornecedor(fornecedorId).stream()
                .filter(contatoExistente -> contatoIdAtual == null || !Objects.equals(contatoExistente.getId(), contatoIdAtual))
                .anyMatch(contatoExistente -> Objects.equals(contatoExistente.getCanalPrincipal(), contato.getCanalPrincipal())
                        && Objects.equals(normalizar(contatoExistente.getNome()), normalizar(contato.getNome())));

        if (duplicado) {
            throw new IllegalArgumentException("contato duplicado para o mesmo fornecedor");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim().toLowerCase();
    }
}