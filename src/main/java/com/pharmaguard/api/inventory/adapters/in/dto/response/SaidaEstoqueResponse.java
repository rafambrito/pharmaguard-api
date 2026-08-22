package com.pharmaguard.api.inventory.adapters.in.dto.response;

import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SaidaEstoqueResponse(
        Long id,
        Long medicamentoId,
        Integer quantidadeTotal,
        SaidaEstoque.Motivo motivo,
        String observacao,
        List<LoteConsumidoResponse> lotesConsumidos,
        LocalDateTime dataSaida,
        Long usuarioResponsavelId) {

    public record LoteConsumidoResponse(
            Long loteId,
            String numeroLote,
            LocalDate dataValidade,
            Integer quantidadeConsumida) {
    }
}
