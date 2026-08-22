package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.EntradaEstoqueUseCase.EntradaEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;

import java.util.List;
import java.util.Optional;

public class InMemoryEntradaEstoqueRepositoryAdapter implements EntradaEstoqueRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemoryEntradaEstoqueRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public EntradaEstoque salvar(EntradaEstoque entrada) {
        if (entrada.getId() == null) {
            entrada.setId(store.nextId());
        }
        store.entradasEstoque.put(entrada.getId(), entrada);
        return entrada;
    }

    @Override
    public Optional<EntradaEstoque> buscarPorId(Long id) {
        return Optional.ofNullable(store.entradasEstoque.get(id));
    }

    @Override
    public List<EntradaEstoque> listar(Long medicamentoId, Long loteId) {
        return store.entradasEstoque.values().stream()
                .filter(entrada -> medicamentoId == null
                        || medicamentoId.equals(entrada.getMedicamento().getId()))
                .filter(entrada -> loteId == null
                        || loteId.equals(entrada.getLote().getId()))
                .toList();
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return Optional.ofNullable(store.medicamentos.get(medicamentoId));
    }

    @Override
    public Optional<Lote> buscarLotePorMedicamentoIdEId(Long medicamentoId, Long loteId) {
        Lote lote = store.lotes.get(loteId);
        if (lote == null || !medicamentoId.equals(lote.getMedicamento().getId())) {
            return Optional.empty();
        }
        return Optional.of(lote);
    }

    @Override
    public int creditarSaldoLote(Long loteId, int quantidade) {
        int saldoAtual = saldoAtualDoLote(loteId);
        int novoSaldo = saldoAtual + quantidade;
        store.saldosPorLote.put(loteId, novoSaldo);
        return novoSaldo;
    }

    @Override
    public MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao) {
        if (movimentacao.getId() == null) {
            movimentacao.setId(store.nextId());
        }
        store.movimentacoesEstoque.put(movimentacao.getId(), movimentacao);
        return movimentacao;
    }

    private int saldoAtualDoLote(Long loteId) {
        Integer saldoPersistido = store.saldosPorLote.get(loteId);
        if (saldoPersistido != null) {
            return saldoPersistido;
        }
        Lote lote = store.lotes.get(loteId);
        if (lote == null) {
            throw new IllegalArgumentException("lote nao encontrado para atualizar saldo");
        }
        return lote.getQuantidadeInicial();
    }
}
