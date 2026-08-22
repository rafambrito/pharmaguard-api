package com.pharmaguard.api.reports.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class RecomendacaoReposicaoCalculatorTest {

    @Test
    void deveSinalizarDemandaCrescenteEAumentarUrgencia() {
        BaseAnaliticaMedicamento item = baseCom(
                IndicadoresEstatisticosConsumo.TendenciaConsumo.CRESCIMENTO,
                NivelEstoqueCalculado.RiscoRuptura.MEDIO,
                AnaliseValidadeMedicamento.RiscoValidade.BAIXO,
                4d,
                10,
                12,
                40,
                70);

        RecomendacaoReposicao recomendacao = RecomendacaoReposicaoCalculator.calcular(item, 8);

        assertTrue(recomendacao.demandaCrescente());
        assertEquals(RelatorioReposicaoResponse.Urgencia.ALTA, recomendacao.urgencia());
        assertEquals(RelatorioReposicaoResponse.Prioridade.ALTA, recomendacao.prioridade());
        assertTrue(recomendacao.quantidadeSugerida() > 0);
    }

    @Test
    void deveSinalizarItemCriticoComRiscoDeValidadeCritico() {
        BaseAnaliticaMedicamento item = baseCom(
                IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL,
                NivelEstoqueCalculado.RiscoRuptura.BAIXO,
                AnaliseValidadeMedicamento.RiscoValidade.CRITICO,
                6d,
                15,
                20,
                80,
                120);

        RecomendacaoReposicao recomendacao = RecomendacaoReposicaoCalculator.calcular(item, 30);

        assertEquals(RelatorioReposicaoResponse.Urgencia.CRITICA, recomendacao.urgencia());
        assertEquals(RelatorioReposicaoResponse.Prioridade.ALTA, recomendacao.prioridade());
        assertTrue(recomendacao.itemCriticoOuRisco());
        assertTrue(recomendacao.justificativa().contains("consumo+prazo+validade"));
    }

        @Test
        void deveManterBaixaPrioridadeQuandoSemRiscoEComSaldoElevado() {
                BaseAnaliticaMedicamento item = baseCom(
                                IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL,
                                NivelEstoqueCalculado.RiscoRuptura.BAIXO,
                                AnaliseValidadeMedicamento.RiscoValidade.BAIXO,
                                20d,
                                10,
                                5,
                                40,
                                120);

                RecomendacaoReposicao recomendacao = RecomendacaoReposicaoCalculator.calcular(item, 120);

                assertEquals(0, recomendacao.quantidadeSugerida());
                assertEquals(RelatorioReposicaoResponse.Urgencia.BAIXA, recomendacao.urgencia());
                assertEquals(RelatorioReposicaoResponse.Prioridade.BAIXA, recomendacao.prioridade());
                assertEquals(false, recomendacao.itemCriticoOuRisco());
        }

    private BaseAnaliticaMedicamento baseCom(
            IndicadoresEstatisticosConsumo.TendenciaConsumo tendencia,
            NivelEstoqueCalculado.RiscoRuptura riscoRuptura,
            AnaliseValidadeMedicamento.RiscoValidade riscoValidade,
            double coberturaDias,
            int estoqueMinimo,
            int estoqueSeguranca,
            int estoqueMaximo,
            int saldoAtual) {
        IndicadoresEstatisticosConsumo indicadores = new IndicadoresEstatisticosConsumo(
                5d,
                2d,
                tendencia,
                6d,
                140d,
                200d);

        NivelEstoqueCalculado nivel = new NivelEstoqueCalculado(
                estoqueMinimo,
                estoqueMaximo,
                estoqueSeguranca,
                coberturaDias,
                riscoRuptura);

        AnaliseValidadeMedicamento validade = new AnaliseValidadeMedicamento(
                60,
                20,
                10,
                12,
                0.30d,
                0.80d,
                riscoValidade,
                true);

        return new BaseAnaliticaMedicamento(
                1L,
                "Amoxicilina",
                10L,
                20L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                31,
                saldoAtual,
                150d,
                5d,
                7,
                indicadores,
                nivel,
                validade);
    }
}