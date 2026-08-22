package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.HistoricoEstoqueUseCase.HistoricoEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InMemoryHistoricoEstoqueRepositoryAdapter implements HistoricoEstoqueRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemoryHistoricoEstoqueRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public List<MovimentacaoEstoque> listar(Long medicamentoId,
            Long loteId,
            MovimentacaoEstoque.Tipo tipo,
            LocalDate dataInicial,
            LocalDate dataFinal) {
        return store.movimentacoesEstoque.values().stream()
                .filter(movimentacao -> medicamentoId == null
                        || medicamentoId.equals(movimentacao.getMedicamento().getId()))
                .filter(movimentacao -> loteId == null
                        || (movimentacao.getLote() != null && loteId.equals(movimentacao.getLote().getId())))
                .filter(movimentacao -> tipo == null || tipo == movimentacao.getTipo())
                .filter(movimentacao -> dentroDoPeriodo(movimentacao.getDataMovimentacao(), dataInicial, dataFinal))
                .toList();
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return Optional.ofNullable(store.medicamentos.get(medicamentoId));
    }

    private boolean dentroDoPeriodo(LocalDateTime dataMovimentacao, LocalDate dataInicial, LocalDate dataFinal) {
        if (dataMovimentacao == null) {
            return true;
        }

        LocalDate data = dataMovimentacao.toLocalDate();
        if (dataInicial != null && data.isBefore(dataInicial)) {
            return false;
        }
        if (dataFinal != null && data.isAfter(dataFinal)) {
            return false;
        }
        return true;
    }
}
