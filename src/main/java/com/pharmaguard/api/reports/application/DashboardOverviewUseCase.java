package com.pharmaguard.api.reports.application;

public interface DashboardOverviewUseCase {

    DashboardOverviewResponse consultar(FiltroMetricasMotorEstatistico filtro);
}