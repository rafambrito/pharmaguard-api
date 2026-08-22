package com.pharmaguard.api.reports.application;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_FILTRO_OBRIGATORIO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_INVALIDO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_OBRIGATORIO;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class RelatorioAlertasUseCaseImpl implements RelatorioAlertasUseCase {

    private final RelatorioAlertasRepositoryPort repositoryPort;

    public RelatorioAlertasUseCaseImpl(RelatorioAlertasRepositoryPort repositoryPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "repositoryPort e obrigatorio");
    }

    @Override
    public RelatorioAlertasResponse gerar(FiltroAlertas filtro) {
        validarFiltro(filtro);

        SnapshotAlertas snapshot = repositoryPort.consultar(filtro);
        List<RelatorioAlertasResponse.ItemAlerta> alertas = snapshot.alertas();
        if (alertas == null) {
            alertas = List.of();
        }

        alertas = alertas.stream()
                .sorted(Comparator
                        .comparingInt((RelatorioAlertasResponse.ItemAlerta alerta) -> prioridadeSeveridade(alerta.severidade()))
                        .thenComparingInt(RelatorioAlertasResponse.ItemAlerta::quantidadeImpactada)
                        .reversed())
                .toList();

        RelatorioAlertasResponse.ResumoAlertas resumo = snapshot.resumo();
        if (resumo == null) {
            resumo = new RelatorioAlertasResponse.ResumoAlertas(0, 0, 0, 0d, 0);
        }

        return new RelatorioAlertasResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                alertas.size(),
                resumo,
                alertas);
    }

    private void validarFiltro(FiltroAlertas filtro) {
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

    private int prioridadeSeveridade(RelatorioAlertasResponse.SeveridadeAlerta severidade) {
        return switch (severidade) {
            case CRITICA -> 4;
            case ALTA -> 3;
            case MEDIA -> 2;
            case BAIXA -> 1;
        };
    }
}