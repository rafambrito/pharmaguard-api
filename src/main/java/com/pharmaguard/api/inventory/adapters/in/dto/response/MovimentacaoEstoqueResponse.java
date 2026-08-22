package com.pharmaguard.api.inventory.adapters.in.dto.response;

import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponse(
        Long id,
        MovimentacaoEstoque.Tipo tipo,
        Long medicamentoId,
        Long loteId,
        Integer quantidade,
        Integer saldoAposMovimentacao,
        String motivo,
        LocalDateTime dataMovimentacao,
        Long usuarioResponsavelId) {
}
