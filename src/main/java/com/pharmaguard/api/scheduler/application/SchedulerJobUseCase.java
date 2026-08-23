package com.pharmaguard.api.scheduler.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SchedulerJobUseCase {

    SchedulerExecutionSummary executarProcessamentoDiario();

    record SchedulerExecutionSummary(
            LocalDate periodoInicio,
            LocalDate periodoFim,
            int totalMedicamentosAnalisados,
            int totalAlertas,
            boolean executadoComSucesso,
            String mensagem,
            LocalDateTime executadoEm) {
    }
}
