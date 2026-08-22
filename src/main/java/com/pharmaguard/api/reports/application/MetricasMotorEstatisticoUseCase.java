package com.pharmaguard.api.reports.application;

public interface MetricasMotorEstatisticoUseCase {

    MetricasMotorEstatisticoResponse consultar(FiltroMetricasMotorEstatistico filtro);

    interface MetricasMotorEstatisticoRepositoryPort {
        MetricasMotorEstatisticoResponse consultarMetricas(FiltroMetricasMotorEstatistico filtro);
    }
}