package com.pharmaguard.api.intelligence.application;

import com.pharmaguard.api.intelligence.domain.TipoPainel;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;

public record ExplicarPainelCommand(TipoPainel tipoPainel, FiltroMetricasMotorEstatistico filtro) {
}