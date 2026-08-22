package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.SaldoEstoqueUseCase.SaldoEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;

import java.util.List;
import java.util.Optional;

public class InMemorySaldoEstoqueRepositoryAdapter implements SaldoEstoqueRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemorySaldoEstoqueRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return Optional.ofNullable(store.medicamentos.get(medicamentoId));
    }

    @Override
    public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId) {
        return store.lotes.values().stream()
                .filter(lote -> medicamentoId.equals(lote.getMedicamento().getId()))
                .map(lote -> new SaldoLoteEstoque(
                    lote.getMedicamento().getId(),
                        lote.getId(),
                        lote.getNumeroLote(),
                        lote.getDataValidade(),
                        saldoAtualDoLote(lote.getId())))
                .toList();
    }

            @Override
            public List<SaldoLoteEstoque> listarTodosOsSaldos() {
            return store.lotes.values().stream()
                .map(lote -> new SaldoLoteEstoque(
                    lote.getMedicamento().getId(),
                    lote.getId(),
                    lote.getNumeroLote(),
                    lote.getDataValidade(),
                    saldoAtualDoLote(lote.getId())))
                .toList();
            }

    @Override
    public int consultarQuantidadeReservada(Long medicamentoId) {
        return 0;
    }

    private int saldoAtualDoLote(Long loteId) {
        Integer saldoPersistido = store.saldosPorLote.get(loteId);
        if (saldoPersistido != null) {
            return saldoPersistido;
        }
        var lote = store.lotes.get(loteId);
        if (lote == null) {
            throw new IllegalArgumentException("lote nao encontrado para consultar saldo");
        }
        return lote.getQuantidadeInicial();
    }
}
