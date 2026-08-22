package com.pharmaguard.api.reports.application;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_FILTRO_OBRIGATORIO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_INVALIDO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_OBRIGATORIO;

import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class MetricasMotorEstatisticoUseCaseImpl implements MetricasMotorEstatisticoUseCase {

    private final MetricasMotorEstatisticoRepositoryPort repositoryPort;

    public MetricasMotorEstatisticoUseCaseImpl(MetricasMotorEstatisticoRepositoryPort repositoryPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "repositoryPort e obrigatorio");
    }

    @Override
    public MetricasMotorEstatisticoResponse consultar(FiltroMetricasMotorEstatistico filtro) {
        validarFiltro(filtro);
        return repositoryPort.consultarMetricas(filtro);
    }

    private void validarFiltro(FiltroMetricasMotorEstatistico filtro) {
        Objects.requireNonNull(filtro, MSG_VALIDACAO_FILTRO_OBRIGATORIO);

        LocalDate inicio = filtro.periodoInicio();
        LocalDate fim = filtro.periodoFim();
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_OBRIGATORIO);
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_INVALIDO);
        }
    }
}