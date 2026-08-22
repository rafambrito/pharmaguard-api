package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.EstoqueAtual;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

public class SaldoEstoqueUseCaseImpl implements SaldoEstoqueUseCase {

    private final SaldoEstoqueRepositoryPort repository;

    public SaldoEstoqueUseCaseImpl(SaldoEstoqueRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public EstoqueAtual consultarPorMedicamento(Long medicamentoId) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");

        Medicamento medicamento = repository.buscarMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));

        List<SaldoLoteEstoque> saldosPorLote = repository.listarSaldosPorMedicamento(medicamentoId);

        EstoqueAtual estoqueAtual = new EstoqueAtual();
        estoqueAtual.setMedicamento(medicamento);
        estoqueAtual.setQuantidadeReservada(repository.consultarQuantidadeReservada(medicamentoId));
        estoqueAtual.definirLotesAtivos(saldosPorLote);
        estoqueAtual.recalcularComBaseNosLotes();
        return estoqueAtual;
    }

    @Override
    public List<SaldoLoteEstoque> consultarLotesPorMedicamento(Long medicamentoId) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        repository.buscarMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));
        return repository.listarSaldosPorMedicamento(medicamentoId);
    }

    @Override
    public List<SaldoLoteEstoque> consultarTodosOsLotes() {
        return repository.listarTodosOsSaldos();
    }
}
