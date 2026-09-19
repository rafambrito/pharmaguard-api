package com.pharmaguard.api.inventory.adapters.in.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistrarDispensacaoRequest(
        @NotNull @Positive Long unidadeId,
        @NotNull @Positive Long pacienteId,
        @NotNull @Positive Long medicamentoId,
        @NotNull @Positive Integer quantidade,
        @Size(max = 100) String numeroReceita,
        @Size(max = 50) String crmPrescritor,
        @Size(max = 500) String observacao) {
}