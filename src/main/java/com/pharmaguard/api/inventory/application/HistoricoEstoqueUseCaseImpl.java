package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class HistoricoEstoqueUseCaseImpl implements HistoricoEstoqueUseCase {

    private final HistoricoEstoqueRepositoryPort repository;

    public HistoricoEstoqueUseCaseImpl(HistoricoEstoqueRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public List<MovimentacaoEstoque> listar(
            Long medicamentoId,
            Long loteId,
            MovimentacaoEstoque.Tipo tipo,
            LocalDate dataInicial,
            LocalDate dataFinal) {
        validarIntervaloDatas(dataInicial, dataFinal);

        if (medicamentoId != null) {
            repository.buscarMedicamentoPorId(medicamentoId)
                    .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));
        }

        return repository.listar(medicamentoId, loteId, tipo, dataInicial, dataFinal);
    }

    private void validarIntervaloDatas(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("dataInicial nao pode ser maior que dataFinal");
        }
    }
}
