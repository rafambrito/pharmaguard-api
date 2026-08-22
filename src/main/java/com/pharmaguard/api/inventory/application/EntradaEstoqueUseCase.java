package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;

import java.util.List;
import java.util.Optional;

public interface EntradaEstoqueUseCase {

    EntradaEstoque registrar(Long medicamentoId, Long loteId, EntradaEstoque entrada);

    EntradaEstoque buscarPorId(Long id);

    List<EntradaEstoque> listar(Long medicamentoId, Long loteId);

    interface EntradaEstoqueRepositoryPort {

        EntradaEstoque salvar(EntradaEstoque entrada);

        Optional<EntradaEstoque> buscarPorId(Long id);

        List<EntradaEstoque> listar(Long medicamentoId, Long loteId);

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);

        Optional<Lote> buscarLotePorMedicamentoIdEId(Long medicamentoId, Long loteId);

        int creditarSaldoLote(Long loteId, int quantidade);

        MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao);
    }
}
