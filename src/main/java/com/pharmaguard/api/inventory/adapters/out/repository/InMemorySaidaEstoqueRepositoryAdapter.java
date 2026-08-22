package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.SaidaEstoqueUseCase.SaidaEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;

import java.util.List;
import java.util.Optional;

public class InMemorySaidaEstoqueRepositoryAdapter implements SaidaEstoqueRepositoryPort {

    private final InMemoryInventoryStore store;

    public InMemorySaidaEstoqueRepositoryAdapter(InMemoryInventoryStore store) {
        this.store = store;
    }

    @Override
    public SaidaEstoque salvar(SaidaEstoque saida) {
        if (saida.getId() == null) {
            saida.setId(store.nextId());
        }
        store.saidasEstoque.put(saida.getId(), saida);
        return saida;
    }

    @Override
    public Optional<SaidaEstoque> buscarPorId(Long id) {
        return Optional.ofNullable(store.saidasEstoque.get(id));
    }

    @Override
    public List<SaidaEstoque> listar(Long medicamentoId) {
        return store.saidasEstoque.values().stream()
                .filter(saida -> medicamentoId == null
                        || medicamentoId.equals(saida.getMedicamento().getId()))
                .toList();
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
                        lote.getId(),
                        lote.getNumeroLote(),
                        lote.getDataValidade(),
                        saldoAtualDoLote(lote.getId())))
                .toList();
    }

    @Override
    public int baixarSaldoLote(Long loteId, int quantidade) {
        int saldoAtual = saldoAtualDoLote(loteId);
        int novoSaldo = saldoAtual - quantidade;
        if (novoSaldo < 0) {
            throw new IllegalArgumentException("saldo insuficiente no lote informado");
        }
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
        var lote = store.lotes.get(loteId);
        if (lote == null) {
            throw new IllegalArgumentException("lote nao encontrado para atualizar saldo");
        }
        return lote.getQuantidadeInicial();
    }
}
