package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record RelatorioProdutosCriticosResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        int totalProdutosCriticos,
        List<ItemCritico> itens) {

    public enum Risco {
        BAIXO,
        MEDIO,
        ALTO,
        CRITICO
    }

    public enum Urgencia {
        BAIXA,
        MEDIA,
        ALTA
    }

    public record ItemCritico(
            Long medicamentoId,
            String nomeMedicamento,
            String categoriaNome,
            int saldoAtual,
            double consumoMedioDiario,
            Risco risco,
            Urgencia urgencia,
            String descricaoRisco) {
    }
}
