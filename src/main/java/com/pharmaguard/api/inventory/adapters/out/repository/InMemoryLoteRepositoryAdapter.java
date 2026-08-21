package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.LoteUseCase.LoteRepositoryPort;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import java.util.List;
import java.util.Optional;

public class InMemoryLoteRepositoryAdapter implements LoteRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemoryLoteRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public Lote salvar(Lote lote) {
        if (lote.getId() == null) {
            lote.setId(store.nextId());
        }
        store.lotes.put(lote.getId(), lote);
        return lote;
    }

    @Override
    public void remover(Long id) {
        store.lotes.remove(id);
    }

    @Override
    public Optional<Lote> buscarPorMedicamentoIdEId(Long medicamentoId, Long loteId) {
        Lote lote = store.lotes.get(loteId);
        if (lote == null || !medicamentoId.equals(lote.getMedicamento().getId())) {
            return Optional.empty();
        }
        return Optional.of(lote);
    }

    @Override
    public List<Lote> listarPorMedicamento(Long medicamentoId) {
        return store.lotes.values().stream()
                .filter(lote -> medicamentoId.equals(lote.getMedicamento().getId()))
                .toList();
    }

    @Override
    public boolean existePorNumeroLoteEMedicamento(String numeroLote, Long medicamentoId) {
        return store.lotes.values().stream().anyMatch(lote ->
                medicamentoId.equals(lote.getMedicamento().getId())
                        && lote.getNumeroLote().equalsIgnoreCase(numeroLote));
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long id) {
        return Optional.ofNullable(store.medicamentos.get(id));
    }
}