package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Dispensacao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DispensacaoUseCase {
    Dispensacao registrar(Long unidadeId, Long pacienteId, Long medicamentoId, int quantidade,
            String numeroReceita, String crmPrescritor, String observacao);
    Dispensacao buscarPorId(Long id);
    List<Dispensacao> listar(Long unidadeId, Long pacienteId, Long medicamentoId,
            LocalDate dataInicial, LocalDate dataFinal);

    interface DispensacaoRepositoryPort {
        Dispensacao salvar(Dispensacao dispensacao);
        Optional<Dispensacao> buscarPorId(Long id);
        List<Dispensacao> listar(Long unidadeId, Long pacienteId, Long medicamentoId,
                LocalDate dataInicial, LocalDate dataFinal);
    }
}