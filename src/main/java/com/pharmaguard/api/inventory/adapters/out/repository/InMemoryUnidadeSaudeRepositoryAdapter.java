package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCase.UnidadeSaudeRepositoryPort;
import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUnidadeSaudeRepositoryAdapter implements UnidadeSaudeRepositoryPort {
    private final InMemoryInventoryStore store;
    public InMemoryUnidadeSaudeRepositoryAdapter(InMemoryInventoryStore store) { this.store = store; }
    @Override public UnidadeSaude salvar(UnidadeSaude u) { if (u.getId() == null) u.setId(store.nextId()); store.unidadesSaude.put(u.getId(), u); return u; }
    @Override public UnidadeSaude atualizar(UnidadeSaude u) { store.unidadesSaude.put(u.getId(), u); return u; }
    @Override public Optional<UnidadeSaude> buscarPorId(Long id) { return Optional.ofNullable(store.unidadesSaude.get(id)); }
    @Override public List<UnidadeSaude> listarTodos() { return new ArrayList<>(store.unidadesSaude.values()); }
    @Override public boolean existePorIdentificacao(String id) { return store.unidadesSaude.values().stream().anyMatch(u -> u.getIdentificacao().equalsIgnoreCase(id)); }

    public boolean unidadeAtiva(Long id) {
        return buscarPorId(id).map(u -> u.getStatus() == UnidadeSaude.Status.ATIVA).orElse(false);
    }
}