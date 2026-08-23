package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;

import java.util.List;
import java.util.Optional;

public interface SaidaEstoqueUseCase {

    SaidaEstoque registrar(Long unidadeId, Long medicamentoId, SaidaEstoque saida);

    default SaidaEstoque registrar(Long medicamentoId, SaidaEstoque saida) {
        return registrar(1L, medicamentoId, saida);
    }

    SaidaEstoque buscarPorId(Long id);

    List<SaidaEstoque> listar(Long medicamentoId);

    default List<SaidaEstoque> listar(Long unidadeId, Long medicamentoId) {
        return listar(medicamentoId);
    }

    interface SaidaEstoqueRepositoryPort {

        SaidaEstoque salvar(SaidaEstoque saida);

        Optional<SaidaEstoque> buscarPorId(Long id);

        List<SaidaEstoque> listar(Long medicamentoId);

        default List<SaidaEstoque> listar(Long unidadeId, Long medicamentoId) {
            return listar(medicamentoId);
        }

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);

        default boolean unidadeAtiva(Long unidadeId) {
            return true;
        }

        List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId);

        default List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long unidadeId, Long medicamentoId) {
            return listarSaldosPorMedicamento(medicamentoId);
        }

        int baixarSaldoLote(Long loteId, int quantidade);

        default int baixarSaldoLote(Long unidadeId, Long loteId, int quantidade) {
            return baixarSaldoLote(loteId, quantidade);
        }

        MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao);
    }
}
