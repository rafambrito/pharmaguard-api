package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase.UnidadeMedidaRepositoryPort;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUnidadeMedidaRepositoryAdapter implements UnidadeMedidaRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemoryUnidadeMedidaRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public UnidadeMedida salvar(UnidadeMedida unidadeMedida) {
        if (unidadeMedida.getId() == null) {
            unidadeMedida.setId(store.nextId());
        }
        store.unidadesMedida.put(unidadeMedida.getId(), unidadeMedida);
        return unidadeMedida;
    }

    @Override
    public UnidadeMedida atualizar(UnidadeMedida unidadeMedida) {
        store.unidadesMedida.put(unidadeMedida.getId(), unidadeMedida);
        return unidadeMedida;
    }

    @Override
    public void remover(Long id) {
        store.unidadesMedida.remove(id);
    }

    @Override
    public Optional<UnidadeMedida> buscarPorId(Long id) {
        return Optional.ofNullable(store.unidadesMedida.get(id));
    }

    @Override
    public List<UnidadeMedida> listarTodos() {
        return new ArrayList<>(store.unidadesMedida.values());
    }

    @Override
    public boolean existePorSigla(String sigla) {
        return store.unidadesMedida.values().stream()
                .anyMatch(unidadeMedida -> unidadeMedida.getSigla().equalsIgnoreCase(sigla));
    }
}