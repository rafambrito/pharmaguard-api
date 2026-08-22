package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record RelatorioEstoqueMinimoResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        int totalItensAbaixoMinimo,
        List<ItemEstoqueMinimo> itens) {

    public enum StatusEstoque {
        NORMAL,
        BAIXO,
        RUPTURA
    }

    public record ItemEstoqueMinimo(
            Long medicamentoId,
            String nomeMedicamento,
            String categoriaNome,
            int saldoAtual,
            int estoqueMinimo,
            StatusEstoque status,
            int necessidadeReposicao) {
    }
}
