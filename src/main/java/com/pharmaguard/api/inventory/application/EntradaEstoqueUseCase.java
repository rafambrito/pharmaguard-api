package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;

import java.util.List;
import java.util.Optional;

public interface EntradaEstoqueUseCase {

    EntradaEstoque registrar(Long unidadeId, Long medicamentoId, Long loteId, EntradaEstoque entrada);

    default EntradaEstoque registrar(Long medicamentoId, Long loteId, EntradaEstoque entrada) {
        return registrar(1L, medicamentoId, loteId, entrada);
    }

    EntradaEstoque buscarPorId(Long id);

    List<EntradaEstoque> listar(Long medicamentoId, Long loteId);

    default List<EntradaEstoque> listar(Long unidadeId, Long medicamentoId, Long loteId) {
        return listar(medicamentoId, loteId);
    }

    interface EntradaEstoqueRepositoryPort {

        EntradaEstoque salvar(EntradaEstoque entrada);

        Optional<EntradaEstoque> buscarPorId(Long id);

        List<EntradaEstoque> listar(Long medicamentoId, Long loteId);

        default List<EntradaEstoque> listarPorUnidade(Long unidadeId, Long medicamentoId, Long loteId) {
            return listar(medicamentoId, loteId);
        }

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);

        Optional<Lote> buscarLotePorMedicamentoIdEId(Long medicamentoId, Long loteId);

        default boolean unidadeAtiva(Long unidadeId) {
            return true;
        }

        int creditarSaldoLote(Long loteId, int quantidade);

        default int creditarSaldoLote(Long unidadeId, Long loteId, int quantidade) {
            return creditarSaldoLote(loteId, quantidade);
        }

        MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao);
    }
}
