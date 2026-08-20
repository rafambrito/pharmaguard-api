package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_NEGOCIO_MEDICAMENTO_DUPLICADO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_CATEGORIA_NAO_ENCONTRADA;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_UNIDADE_MEDIDA_NAO_ENCONTRADA;

public class MedicamentoUseCaseImpl implements MedicamentoUseCase {

    private final MedicamentoRepositoryPort repository;

    public MedicamentoUseCaseImpl(MedicamentoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public Medicamento criar(Medicamento medicamento, Long categoriaId, Long unidadeMedidaId) {
        Objects.requireNonNull(medicamento, "medicamento e obrigatorio");

        Categoria categoria = resolverCategoria(categoriaId);
        UnidadeMedida unidadeMedida = resolverUnidadeMedida(unidadeMedidaId);

        if (repository.existePorNomeEApresentacao(medicamento.getNome(), medicamento.getApresentacao())) {
            throw new BusinessException(MSG_NEGOCIO_MEDICAMENTO_DUPLICADO);
        }

        medicamento.setCategoria(categoria);
        medicamento.setUnidadeMedida(unidadeMedida);
        medicamento.setStatus(Medicamento.Status.ATIVO);
        medicamento.marcarCriacao();
        return repository.salvar(medicamento);
    }

    @Override
    public Medicamento atualizar(Medicamento medicamento, Long categoriaId, Long unidadeMedidaId) {
        Objects.requireNonNull(medicamento, "medicamento e obrigatorio");
        Objects.requireNonNull(medicamento.getId(), "id do medicamento e obrigatorio");

        Medicamento existente = repository.buscarPorId(medicamento.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO));

        Categoria categoria = resolverCategoria(categoriaId);
        UnidadeMedida unidadeMedida = resolverUnidadeMedida(unidadeMedidaId);

        boolean nomeOuApresentacaoAlterados = !Objects.equals(existente.getNome(), medicamento.getNome())
                || !Objects.equals(existente.getApresentacao(), medicamento.getApresentacao());

        if (nomeOuApresentacaoAlterados
                && repository.existePorNomeEApresentacao(medicamento.getNome(), medicamento.getApresentacao())) {
            throw new BusinessException(MSG_NEGOCIO_MEDICAMENTO_DUPLICADO);
        }

        medicamento.setCategoria(categoria);
        medicamento.setUnidadeMedida(unidadeMedida);
        medicamento.marcarAtualizacao();
        return repository.atualizar(medicamento);
    }

    @Override
    public void remover(Long id) {
        Objects.requireNonNull(id, "id do medicamento e obrigatorio");
        repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO));
        repository.remover(id);
    }

    @Override
    public Medicamento buscarPorId(Long id) {
        Objects.requireNonNull(id, "id do medicamento e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO));
    }

    @Override
    public List<Medicamento> listarTodos() {
        return repository.listarTodos();
    }

    private Categoria resolverCategoria(Long categoriaId) {
        Objects.requireNonNull(categoriaId, "categoriaId e obrigatorio");
        return repository.buscarCategoriaPorId(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_CATEGORIA_NAO_ENCONTRADA));
    }

    private UnidadeMedida resolverUnidadeMedida(Long unidadeMedidaId) {
        Objects.requireNonNull(unidadeMedidaId, "unidadeMedidaId e obrigatorio");
        return repository.buscarUnidadeMedidaPorId(unidadeMedidaId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_UNIDADE_MEDIDA_NAO_ENCONTRADA));
    }
}
