package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import java.util.List;
import java.util.Optional;

public interface LoteUseCase {

    Lote cadastrar(Long medicamentoId, Lote lote);

    void remover(Long medicamentoId, Long loteId);

    Lote buscarPorId(Long medicamentoId, Long loteId);

    List<Lote> listarPorMedicamento(Long medicamentoId);

    interface LoteRepositoryPort {

        Lote salvar(Lote lote);

        void remover(Long id);

        Optional<Lote> buscarPorMedicamentoIdEId(Long medicamentoId, Long loteId);

        List<Lote> listarPorMedicamento(Long medicamentoId);

        boolean existePorNumeroLoteEMedicamento(String numeroLote, Long medicamentoId);

        Optional<Medicamento> buscarMedicamentoPorId(Long id);
    }
}
