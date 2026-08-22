package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record RelatorioReposicaoResponse(
    LocalDate periodoInicio,
    LocalDate periodoFim,
        int totalItensParaReposicao,
        List<ItemReposicao> itens) {

    public record ItemReposicao(
            Long medicamentoId,
            String nomeMedicamento,
            int quantidadeSugerida,
            Urgencia urgencia,
            Prioridade prioridade,
            Long fornecedorId,
            Integer leadTimeDias,
            String justificativa) {
    }

    public enum Urgencia {
        BAIXA,
        MEDIA,
        ALTA,
        CRITICA
    }

    public enum Prioridade {
        BAIXA,
        MEDIA,
        ALTA
    }
}
