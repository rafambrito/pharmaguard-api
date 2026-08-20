package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_NEGOCIO_LOTE_DUPLICADO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_LOTE_NAO_ENCONTRADO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO;

public class LoteUseCaseImpl implements LoteUseCase {

    private final LoteRepositoryPort repository;

    public LoteUseCaseImpl(LoteRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public Lote cadastrar(Long medicamentoId, Lote lote) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        Objects.requireNonNull(lote, "lote e obrigatorio");

        Medicamento medicamento = repository.buscarMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO));

        if (repository.existePorNumeroLoteEMedicamento(lote.getNumeroLote(), medicamentoId)) {
            throw new BusinessException(MSG_NEGOCIO_LOTE_DUPLICADO);
        }

        lote.setMedicamento(medicamento);
        return repository.salvar(lote);
    }

    @Override
    public void remover(Long medicamentoId, Long loteId) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        Objects.requireNonNull(loteId, "loteId e obrigatorio");
        repository.buscarPorMedicamentoIdEId(medicamentoId, loteId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_LOTE_NAO_ENCONTRADO));
        repository.remover(loteId);
    }

    @Override
    public Lote buscarPorId(Long medicamentoId, Long loteId) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        Objects.requireNonNull(loteId, "loteId e obrigatorio");
        return repository.buscarPorMedicamentoIdEId(medicamentoId, loteId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_LOTE_NAO_ENCONTRADO));
    }

    @Override
    public List<Lote> listarPorMedicamento(Long medicamentoId) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        repository.buscarMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RECURSO_MEDICAMENTO_NAO_ENCONTRADO));
        return repository.listarPorMedicamento(medicamentoId);
    }
}
