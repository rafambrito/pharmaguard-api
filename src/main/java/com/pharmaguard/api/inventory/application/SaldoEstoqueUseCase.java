package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.EstoqueAtual;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;

import java.util.List;
import java.util.Optional;

public interface SaldoEstoqueUseCase {

    EstoqueAtual consultarPorMedicamento(Long unidadeId, Long medicamentoId);

    default EstoqueAtual consultarPorMedicamento(Long medicamentoId) {
        return consultarPorMedicamento(1L, medicamentoId);
    }

    List<SaldoLoteEstoque> consultarLotesPorMedicamento(Long medicamentoId);

    default List<SaldoLoteEstoque> consultarLotesPorMedicamento(Long unidadeId, Long medicamentoId) {
        return consultarLotesPorMedicamento(medicamentoId);
    }

    List<SaldoLoteEstoque> consultarTodosOsLotes();

    interface SaldoEstoqueRepositoryPort {

        Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId);

        default boolean unidadeAtiva(Long unidadeId) {
            return true;
        }

        List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId);

        default List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long unidadeId, Long medicamentoId) {
            return listarSaldosPorMedicamento(medicamentoId);
        }

        List<SaldoLoteEstoque> listarTodosOsSaldos();

        int consultarQuantidadeReservada(Long medicamentoId);
    }
}
