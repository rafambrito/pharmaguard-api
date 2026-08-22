package com.pharmaguard.api.reports.application;

public record AnaliseValidadeMedicamento(
        int quantidadeValida,
        int quantidadeProximaVencimento,
        int quantidadeVencida,
        int diasParaProximoVencimento,
        double percentualEstoqueEmRisco,
        double fatorImpactoConsumoReal,
        RiscoValidade riscoValidade,
        boolean priorizarFefo) {

    public enum RiscoValidade {
        BAIXO,
        MEDIO,
        ALTO,
        CRITICO
    }
}