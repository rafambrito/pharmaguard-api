package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Objects;

public class UnidadeSaudeUseCaseImpl implements UnidadeSaudeUseCase {
    private final UnidadeSaudeRepositoryPort repository;

    public UnidadeSaudeUseCaseImpl(UnidadeSaudeRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public UnidadeSaude criar(UnidadeSaude unidade) {
        Objects.requireNonNull(unidade, "unidade e obrigatoria");
        if (repository.existePorIdentificacao(unidade.getIdentificacao())) {
            throw new BusinessException("unidade de saude ja cadastrada");
        }
        unidade.setStatus(UnidadeSaude.Status.ATIVA);
        unidade.marcarCadastro();
        return repository.salvar(unidade);
    }

    @Override
    public UnidadeSaude atualizar(UnidadeSaude unidade) {
        Objects.requireNonNull(unidade, "unidade e obrigatoria");
        Objects.requireNonNull(unidade.getId(), "id da unidade de saude e obrigatorio");
        UnidadeSaude existente = buscarPorId(unidade.getId());
        if (!existente.getIdentificacao().equalsIgnoreCase(unidade.getIdentificacao())
                && repository.existePorIdentificacao(unidade.getIdentificacao())) {
            throw new BusinessException("unidade de saude ja cadastrada");
        }
        unidade.marcarAtualizacao();
        return repository.atualizar(unidade);
    }

    @Override
    public UnidadeSaude buscarPorId(Long id) {
        Objects.requireNonNull(id, "id da unidade de saude e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("unidade de saude nao encontrada"));
    }

    @Override public List<UnidadeSaude> listarTodos() { return repository.listarTodos(); }

    @Override
    public void inativar(Long id) {
        UnidadeSaude unidade = buscarPorId(id);
        unidade.setStatus(UnidadeSaude.Status.INATIVA);
        unidade.marcarAtualizacao();
        repository.atualizar(unidade);
    }
}