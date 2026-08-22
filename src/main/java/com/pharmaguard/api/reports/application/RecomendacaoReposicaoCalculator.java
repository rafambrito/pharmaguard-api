package com.pharmaguard.api.reports.application;

public final class RecomendacaoReposicaoCalculator {

    private RecomendacaoReposicaoCalculator() {
    }

    public static RecomendacaoReposicao calcular(BaseAnaliticaMedicamento item, int saldoAproveitavel) {
        boolean demandaCrescente = item.indicadoresEstatisticos().tendenciaConsumo()
                == IndicadoresEstatisticosConsumo.TendenciaConsumo.CRESCIMENTO;

        int estoqueAlvo = item.nivelEstoque().estoqueMaximo();
        int ajusteDemanda = demandaCrescente
                ? (int) Math.ceil(item.indicadoresEstatisticos().mediaAjustada() * item.leadTimeDias() * 0.30d)
                : 0;
        int ajusteValidade = ajusteValidade(item);
        int estoqueAlvoAjustado = estoqueAlvo + ajusteDemanda + ajusteValidade;

        int quantidadeSugerida = Math.max(0, estoqueAlvoAjustado - saldoAproveitavel);

        RelatorioReposicaoResponse.Urgencia urgencia = urgenciaCombinada(item, demandaCrescente);
        RelatorioReposicaoResponse.Prioridade prioridade = prioridadePorUrgencia(urgencia);
        boolean itemCriticoOuRisco = urgencia == RelatorioReposicaoResponse.Urgencia.CRITICA
                || urgencia == RelatorioReposicaoResponse.Urgencia.ALTA
                || demandaCrescente;

        String justificativa = "Reposicao recomendada por consumo+prazo+validade: "
                + "demanda_crescente=" + demandaCrescente
                + ", risco_ruptura=" + item.nivelEstoque().riscoRuptura()
                + ", risco_validade=" + item.analiseValidade().riscoValidade()
                + ", estoque_alvo=" + estoqueAlvo
                + ", ajuste_demanda=" + ajusteDemanda
                + ", ajuste_validade=" + ajusteValidade
                + ", saldo_aproveitavel=" + saldoAproveitavel
                + ", item_critico_ou_risco=" + itemCriticoOuRisco;

        return new RecomendacaoReposicao(
                quantidadeSugerida,
                urgencia,
                prioridade,
                demandaCrescente,
                itemCriticoOuRisco,
                justificativa);
    }

    private static int ajusteValidade(BaseAnaliticaMedicamento item) {
        double risco = item.analiseValidade().percentualEstoqueEmRisco();
        return (int) Math.ceil(item.nivelEstoque().estoqueMinimo() * risco * 0.5d);
    }

    private static RelatorioReposicaoResponse.Urgencia urgenciaCombinada(
            BaseAnaliticaMedicamento item,
            boolean demandaCrescente) {
        RelatorioReposicaoResponse.Urgencia urgencia = maiorUrgencia(
                urgenciaPorRiscoRuptura(item.nivelEstoque().riscoRuptura()),
                urgenciaPorRiscoValidade(item.analiseValidade().riscoValidade()));

        double coberturaDias = item.nivelEstoque().coberturaDemandaDias();
        if (demandaCrescente && coberturaDias < item.leadTimeDias()) {
            urgencia = elevarUrgencia(urgencia);
        }
        return urgencia;
    }

    private static RelatorioReposicaoResponse.Urgencia urgenciaPorRiscoRuptura(
            NivelEstoqueCalculado.RiscoRuptura riscoRuptura) {
        return switch (riscoRuptura) {
            case CRITICO -> RelatorioReposicaoResponse.Urgencia.CRITICA;
            case ALTO -> RelatorioReposicaoResponse.Urgencia.ALTA;
            case MEDIO -> RelatorioReposicaoResponse.Urgencia.MEDIA;
            case BAIXO -> RelatorioReposicaoResponse.Urgencia.BAIXA;
        };
    }

    private static RelatorioReposicaoResponse.Urgencia urgenciaPorRiscoValidade(
            AnaliseValidadeMedicamento.RiscoValidade riscoValidade) {
        return switch (riscoValidade) {
            case CRITICO -> RelatorioReposicaoResponse.Urgencia.CRITICA;
            case ALTO -> RelatorioReposicaoResponse.Urgencia.ALTA;
            case MEDIO -> RelatorioReposicaoResponse.Urgencia.MEDIA;
            case BAIXO -> RelatorioReposicaoResponse.Urgencia.BAIXA;
        };
    }

    private static RelatorioReposicaoResponse.Urgencia maiorUrgencia(
            RelatorioReposicaoResponse.Urgencia primeira,
            RelatorioReposicaoResponse.Urgencia segunda) {
        return primeira.ordinal() >= segunda.ordinal() ? primeira : segunda;
    }

    private static RelatorioReposicaoResponse.Urgencia elevarUrgencia(RelatorioReposicaoResponse.Urgencia urgencia) {
        return switch (urgencia) {
            case BAIXA -> RelatorioReposicaoResponse.Urgencia.MEDIA;
            case MEDIA -> RelatorioReposicaoResponse.Urgencia.ALTA;
            case ALTA, CRITICA -> RelatorioReposicaoResponse.Urgencia.CRITICA;
        };
    }

    private static RelatorioReposicaoResponse.Prioridade prioridadePorUrgencia(
            RelatorioReposicaoResponse.Urgencia urgencia) {
        return switch (urgencia) {
            case CRITICA, ALTA -> RelatorioReposicaoResponse.Prioridade.ALTA;
            case MEDIA -> RelatorioReposicaoResponse.Prioridade.MEDIA;
            case BAIXA -> RelatorioReposicaoResponse.Prioridade.BAIXA;
        };
    }
}