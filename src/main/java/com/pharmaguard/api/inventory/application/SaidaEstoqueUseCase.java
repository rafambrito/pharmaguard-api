package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;

import java.util.List;
import java.util.Optional;

public interface SaidaEstoqueUseCase {

    SaidaEstoque registrar(Long medicamentoId, SaidaEstoque saida);

    SaidaEstoque buscarPorId(Long id);

    List<SaidaEstoque> listar(Long medicamentoId);

    interface SaidaEstoqueRepositoryPort {

        SaidaEstoque salvar(SaidaEstoque saida);

        Optional<SaidaEstoque> buscarPorId(Long id);

        List<SaidaEstoque> listar(Long medicamentoId);

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);

        List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId);

        int baixarSaldoLote(Long loteId, int quantidade);

        MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao);
    }
}
