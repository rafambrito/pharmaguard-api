package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.MedicamentoUseCase.MedicamentoRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryMedicamentoRepositoryAdapter implements MedicamentoRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemoryMedicamentoRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public Medicamento salvar(Medicamento medicamento) {
        if (medicamento.getId() == null) {
            medicamento.setId(store.nextId());
        }
        store.medicamentos.put(medicamento.getId(), medicamento);
        return medicamento;
    }

    @Override
    public Medicamento atualizar(Medicamento medicamento) {
        store.medicamentos.put(medicamento.getId(), medicamento);
        return medicamento;
    }

    @Override
    public void remover(Long id) {
        store.medicamentos.remove(id);
        store.lotes.entrySet().removeIf(entry -> id.equals(entry.getValue().getMedicamento().getId()));
    }

    @Override
    public Optional<Medicamento> buscarPorId(Long id) {
        return Optional.ofNullable(store.medicamentos.get(id));
    }

    @Override
    public List<Medicamento> listarTodos() {
        return new ArrayList<>(store.medicamentos.values());
    }

    @Override
    public boolean existePorNomeEApresentacao(String nome, String apresentacao) {
        return store.medicamentos.values().stream().anyMatch(medicamento ->
                medicamento.getNome().equalsIgnoreCase(nome)
                        && medicamento.getApresentacao().equalsIgnoreCase(apresentacao));
    }

    @Override
    public Optional<Categoria> buscarCategoriaPorId(Long id) {
        return Optional.ofNullable(store.categorias.get(id));
    }

    @Override
    public Optional<UnidadeMedida> buscarUnidadeMedidaPorId(Long id) {
        return Optional.ofNullable(store.unidadesMedida.get(id));
    }
}