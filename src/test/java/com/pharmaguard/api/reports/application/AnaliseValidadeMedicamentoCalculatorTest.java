package com.pharmaguard.api.reports.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AnaliseValidadeMedicamentoCalculatorTest {

    @Test
    void deveClassificarRiscoCriticoQuandoHaAltaQuantidadeVencida() {
        AnaliseValidadeMedicamento analise = AnaliseValidadeMedicamentoCalculator.calcular(40, 20, 40, 10);

        assertEquals(0.6d, analise.percentualEstoqueEmRisco(), 0.0001d);
        assertEquals(AnaliseValidadeMedicamento.RiscoValidade.CRITICO, analise.riscoValidade());
        assertTrue(analise.priorizarFefo());
        assertTrue(analise.fatorImpactoConsumoReal() < 1.0d);
    }

    @Test
    void deveClassificarRiscoMedioComItensProximosDoVencimento() {
        AnaliseValidadeMedicamento analise = AnaliseValidadeMedicamentoCalculator.calcular(90, 15, 0, 20);

        assertEquals(AnaliseValidadeMedicamento.RiscoValidade.MEDIO, analise.riscoValidade());
        assertEquals(20, analise.diasParaProximoVencimento());
        assertTrue(analise.priorizarFefo());
    }

    @Test
    void deveRetornarSemRiscoQuandoNaoHaEstoque() {
        AnaliseValidadeMedicamento analise = AnaliseValidadeMedicamentoCalculator.calcular(0, 0, 0, Integer.MAX_VALUE);

        assertEquals(AnaliseValidadeMedicamento.RiscoValidade.BAIXO, analise.riscoValidade());
        assertEquals(1.0d, analise.fatorImpactoConsumoReal());
        assertFalse(analise.priorizarFefo());
    }
}