package com.pharmaguard.api.inventory.adapters.in.dto.response;

import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import java.time.LocalDateTime;

public record EntradaEstoqueResponse(
        Long id,
        Long medicamentoId,
        Long loteId,
        Integer quantidade,
        EntradaEstoque.Origem origem,
        String documento,
        String observacao,
        LocalDateTime dataEntrada,
        Long usuarioResponsavelId) {
}
