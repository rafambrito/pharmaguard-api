package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_NEGOCIO_UNIDADE_MEDIDA_DUPLICADA;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_UNIDADE_MEDIDA_NAO_ENCONTRADA;

public class UnidadeMedidaUseCaseImpl implements UnidadeMedidaUseCase {

    private final UnidadeMedidaRepositoryPort repository;

    public UnidadeMedidaUseCaseImpl(UnidadeMedidaRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public UnidadeMedida criar(UnidadeMedida unidadeMedida) {
        Objects.requireNonNull(unidadeMedida, "unidadeMedida e obrigatoria");
        if (repository.existePorSigla(unidadeMedida.getSigla())) {
            throw new BusinessException(MSG_NEGOCIO_UNIDADE_MEDIDA_DUPLICADA);
        }
        unidadeMedida.setStatus(UnidadeMedida.Status.ATIVA);
        unidadeMedida.marcarCriacao();
        return repository.salvar(unidadeMedida);
    }

    @Override
    public UnidadeMedida atualizar(UnidadeMedida unidadeMedida) {
        Objects.requireNonNull(unidadeMedida, "unidadeMedida e obrigatoria");
        Objects.requireNonNull(unidadeMedida.getId(), "id da unidade de medida e obrigatorio");

        UnidadeMedida existente = repository.buscarPorId(unidadeMedida.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_UNIDADE_MEDIDA_NAO_ENCONTRADA));

        if (!Objects.equals(existente.getSigla(), unidadeMedida.getSigla())
                && repository.existePorSigla(unidadeMedida.getSigla())) {
            throw new BusinessException(MSG_NEGOCIO_UNIDADE_MEDIDA_DUPLICADA);
        }

        unidadeMedida.marcarAtualizacao();
        return repository.atualizar(unidadeMedida);
    }

    @Override
    public void remover(Long id) {
        Objects.requireNonNull(id, "id da unidade de medida e obrigatorio");
        repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_UNIDADE_MEDIDA_NAO_ENCONTRADA));
        repository.remover(id);
    }

    @Override
    public UnidadeMedida buscarPorId(Long id) {
        Objects.requireNonNull(id, "id da unidade de medida e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_UNIDADE_MEDIDA_NAO_ENCONTRADA));
    }

    @Override
    public List<UnidadeMedida> listarTodos() {
        return repository.listarTodos();
    }
}
