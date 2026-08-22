package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoricoEstoqueUseCase {

    List<MovimentacaoEstoque> listar(
            Long medicamentoId,
            Long loteId,
            MovimentacaoEstoque.Tipo tipo,
            LocalDate dataInicial,
            LocalDate dataFinal);

    interface HistoricoEstoqueRepositoryPort {

        List<MovimentacaoEstoque> listar(
                Long medicamentoId,
                Long loteId,
                MovimentacaoEstoque.Tipo tipo,
                LocalDate dataInicial,
                LocalDate dataFinal);

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);
    }
}
