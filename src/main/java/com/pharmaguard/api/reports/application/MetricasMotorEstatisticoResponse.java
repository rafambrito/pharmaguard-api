package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MetricasMotorEstatisticoResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        int totalMedicamentosAnalisados,
        int totalItensComReposicaoSugerida,
        int totalItensComRiscoRuptura,
        int totalItensComRiscoValidade,
        double totalConsumoPeriodo,
        double coberturaMediaDias,
        boolean integracaoSchedulerPreparada,
        boolean integracaoDashboardPreparada,
        LocalDateTime atualizadoEm) {
}