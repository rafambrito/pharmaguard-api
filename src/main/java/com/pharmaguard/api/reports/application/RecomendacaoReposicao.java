package com.pharmaguard.api.reports.application;

public record RecomendacaoReposicao(
        int quantidadeSugerida,
        RelatorioReposicaoResponse.Urgencia urgencia,
        RelatorioReposicaoResponse.Prioridade prioridade,
        boolean demandaCrescente,
        boolean itemCriticoOuRisco,
        String justificativa) {
}