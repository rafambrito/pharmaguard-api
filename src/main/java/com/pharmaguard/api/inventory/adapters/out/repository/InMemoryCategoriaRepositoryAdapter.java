package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.CategoriaUseCase.CategoriaRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryCategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemoryCategoriaRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        if (categoria.getId() == null) {
            categoria.setId(store.nextId());
        }
        store.categorias.put(categoria.getId(), categoria);
        return categoria;
    }

    @Override
    public Categoria atualizar(Categoria categoria) {
        store.categorias.put(categoria.getId(), categoria);
        return categoria;
    }

    @Override
    public void remover(Long id) {
        store.categorias.remove(id);
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return Optional.ofNullable(store.categorias.get(id));
    }

    @Override
    public List<Categoria> listarTodos() {
        return new ArrayList<>(store.categorias.values());
    }

    @Override
    public boolean existePorNome(String nome) {
        return store.categorias.values().stream().anyMatch(categoria -> categoria.getNome().equalsIgnoreCase(nome));
    }
}