package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_NEGOCIO_CATEGORIA_DUPLICADA;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_CATEGORIA_NAO_ENCONTRADA;

public class CategoriaUseCaseImpl implements CategoriaUseCase {

    private final CategoriaRepositoryPort repository;

    public CategoriaUseCaseImpl(CategoriaRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public Categoria criar(Categoria categoria) {
        Objects.requireNonNull(categoria, "categoria e obrigatoria");
        if (repository.existePorNome(categoria.getNome())) {
            throw new BusinessException(MSG_NEGOCIO_CATEGORIA_DUPLICADA);
        }
        categoria.setStatus(Categoria.Status.ATIVA);
        categoria.marcarCriacao();
        return repository.salvar(categoria);
    }

    @Override
    public Categoria atualizar(Categoria categoria) {
        Objects.requireNonNull(categoria, "categoria e obrigatoria");
        Objects.requireNonNull(categoria.getId(), "id da categoria e obrigatorio");

        Categoria existente = repository.buscarPorId(categoria.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_CATEGORIA_NAO_ENCONTRADA));

        if (!Objects.equals(existente.getNome(), categoria.getNome())
                && repository.existePorNome(categoria.getNome())) {
            throw new BusinessException(MSG_NEGOCIO_CATEGORIA_DUPLICADA);
        }

        categoria.marcarAtualizacao();
        return repository.atualizar(categoria);
    }

    @Override
    public void remover(Long id) {
        Objects.requireNonNull(id, "id da categoria e obrigatorio");
        repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_CATEGORIA_NAO_ENCONTRADA));
        repository.remover(id);
    }

    @Override
    public Categoria buscarPorId(Long id) {
        Objects.requireNonNull(id, "id da categoria e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_CATEGORIA_NAO_ENCONTRADA));
    }

    @Override
    public List<Categoria> listarTodos() {
        return repository.listarTodos();
    }
}
