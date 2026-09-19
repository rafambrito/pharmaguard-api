package com.pharmaguard.api.inventory.adapters.in.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record DispensacaoResponse(Long id, Long pacienteId, Long saidaEstoqueId, Long unidadeId, Long medicamentoId,
        Integer quantidade, String numeroReceita, String crmPrescritor, String observacao, LocalDateTime dataDispensacao,
        List<LoteDispensadoResponse> lotes) {
    public record LoteDispensadoResponse(Long loteId, String numeroLote, Integer quantidade) { }
}