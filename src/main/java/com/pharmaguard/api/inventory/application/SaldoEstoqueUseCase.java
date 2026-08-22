package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.EstoqueAtual;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;

import java.util.List;
import java.util.Optional;

public interface SaldoEstoqueUseCase {

    EstoqueAtual consultarPorMedicamento(Long medicamentoId);

    List<SaldoLoteEstoque> consultarLotesPorMedicamento(Long medicamentoId);

    List<SaldoLoteEstoque> consultarTodosOsLotes();

    interface SaldoEstoqueRepositoryPort {

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);

        List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId);

        List<SaldoLoteEstoque> listarTodosOsSaldos();

        int consultarQuantidadeReservada(Long medicamentoId);
    }
}
