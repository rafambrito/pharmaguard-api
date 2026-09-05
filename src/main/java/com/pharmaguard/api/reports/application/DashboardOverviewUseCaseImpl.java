package com.pharmaguard.api.reports.application;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_FILTRO_OBRIGATORIO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_INVALIDO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_OBRIGATORIO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class DashboardOverviewUseCaseImpl implements DashboardOverviewUseCase {

    private static final int TOTAL_PONTOS_CONSUMO = 6;
    private static final DateTimeFormatter LABEL_PERIODO = DateTimeFormatter.ofPattern("dd/MM");

    private final MetricasMotorEstatisticoUseCase metricasUseCase;
    private final RelatorioAlertasUseCase alertasUseCase;
    private final RelatorioConsumoUseCase consumoUseCase;
    private final RelatorioReposicaoUseCase reposicaoUseCase;

    public DashboardOverviewUseCaseImpl(
            MetricasMotorEstatisticoUseCase metricasUseCase,
            RelatorioAlertasUseCase alertasUseCase,
            RelatorioConsumoUseCase consumoUseCase,
            RelatorioReposicaoUseCase reposicaoUseCase) {
        this.metricasUseCase = Objects.requireNonNull(metricasUseCase, "metricasUseCase e obrigatorio");
        this.alertasUseCase = Objects.requireNonNull(alertasUseCase, "alertasUseCase e obrigatorio");
        this.consumoUseCase = Objects.requireNonNull(consumoUseCase, "consumoUseCase e obrigatorio");
        this.reposicaoUseCase = Objects.requireNonNull(reposicaoUseCase, "reposicaoUseCase e obrigatorio");
    }

    @Override
    public DashboardOverviewResponse consultar(FiltroMetricasMotorEstatistico filtro) {
        validarFiltro(filtro);

        MetricasMotorEstatisticoResponse metricas = metricasUseCase.consultar(filtro);
        RelatorioAlertasResponse alertas = alertasUseCase.gerar(new FiltroAlertas(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro.medicamentoId(),
                filtro.categoriaId(),
                filtro.unidadeMedidaId(),
                filtro.fornecedorId(),
                filtro.unidadeSaudeId()));
        RelatorioConsumoResponse consumo = consumoUseCase.gerar(new FiltroConsumo(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro.medicamentoId(),
                filtro.categoriaId(),
                filtro.unidadeMedidaId(),
                filtro.fornecedorId(),
                filtro.unidadeSaudeId()));
        RelatorioReposicaoResponse reposicao = reposicaoUseCase.gerar(new FiltroReposicao(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro.medicamentoId(),
                filtro.categoriaId(),
                filtro.unidadeMedidaId(),
                filtro.fornecedorId(),
                filtro.unidadeSaudeId()));

        RelatorioAlertasResponse.ResumoAlertas resumoAlertas = alertas.resumo();
        List<DashboardOverviewResponse.ResumoUnidade> unidades = List.of(new DashboardOverviewResponse.ResumoUnidade(
                filtro.unidadeSaudeId(),
                null,
                resumoAlertas.totalItensCriticos(),
                resumoAlertas.totalVencimento()));

        return new DashboardOverviewResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                metricas,
                resumoAlertas,
                alertas.alertas(),
                unidades,
                reposicao.itens(),
                new DashboardOverviewResponse.ConsumoResumo(
                        consumo.totalConsumido(),
                        consumo.mediaDiaria(),
                        consumo.tendencia(),
                        montarSerieConsumo(filtro)));
    }

    private List<DashboardOverviewResponse.PontoConsumo> montarSerieConsumo(FiltroMetricasMotorEstatistico filtro) {
        long totalDias = ChronoUnit.DAYS.between(filtro.periodoInicio(), filtro.periodoFim()) + 1;
        int totalPontos = (int) Math.min(TOTAL_PONTOS_CONSUMO, totalDias);
        long diasPorPonto = (long) Math.ceil((double) totalDias / totalPontos);

        List<DashboardOverviewResponse.PontoConsumo> pontos = new ArrayList<>();
        LocalDate inicioPonto = filtro.periodoInicio();
        while (!inicioPonto.isAfter(filtro.periodoFim())) {
            LocalDate fimPonto = inicioPonto.plusDays(diasPorPonto - 1);
            if (fimPonto.isAfter(filtro.periodoFim())) {
                fimPonto = filtro.periodoFim();
            }

            RelatorioConsumoResponse consumo = consumoUseCase.gerar(new FiltroConsumo(
                    inicioPonto,
                    fimPonto,
                    filtro.medicamentoId(),
                    filtro.categoriaId(),
                    filtro.unidadeMedidaId(),
                    filtro.fornecedorId(),
                    filtro.unidadeSaudeId()));
            pontos.add(new DashboardOverviewResponse.PontoConsumo(
                    inicioPonto,
                    fimPonto,
                    formatarLabel(inicioPonto, fimPonto),
                    consumo.totalConsumido()));

            inicioPonto = fimPonto.plusDays(1);
        }
        return pontos;
    }

    private String formatarLabel(LocalDate inicio, LocalDate fim) {
        if (inicio.equals(fim)) {
            return inicio.format(LABEL_PERIODO);
        }
        return inicio.format(LABEL_PERIODO) + "-" + fim.format(LABEL_PERIODO);
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