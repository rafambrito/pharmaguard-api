package com.pharmaguard.api.scheduler.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.pharmaguard.api.reports.application.FiltroAlertas;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SchedulerJobUseCaseImplTest {

    @Test
    void deveExecutarProcessamentoDiarioComMetricasEAlertas() {
        MetricasMotorEstatisticoUseCase metricasUseCase = filtro -> new MetricasMotorEstatisticoResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                12,
                4,
                2,
                1,
                184.5d,
                18.0d,
                true,
                true,
                LocalDateTime.now());

        RelatorioAlertasUseCase alertasUseCase = filtro -> new RelatorioAlertasResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                1,
                new RelatorioAlertasResponse.ResumoAlertas(1, 0, 0, 184.5d, 1),
                List.of(new RelatorioAlertasResponse.ItemAlerta(
                        1L,
                        "Amoxicilina",
                        RelatorioAlertasResponse.TipoAlerta.RUPTURA,
                        RelatorioAlertasResponse.SeveridadeAlerta.ALTA,
                        5,
                        "Risco de ruptura em estoque")));

        SchedulerJobUseCase useCase = new SchedulerJobUseCaseImpl(metricasUseCase, alertasUseCase);

        SchedulerJobUseCase.SchedulerExecutionSummary result = useCase.executarProcessamentoDiario();

        assertThat(result.executadoComSucesso()).isTrue();
        assertThat(result.totalMedicamentosAnalisados()).isEqualTo(12);
        assertThat(result.totalAlertas()).isEqualTo(1);
        assertThat(result.periodoInicio()).isNotNull();
        assertThat(result.periodoFim()).isNotNull();
    }
}
