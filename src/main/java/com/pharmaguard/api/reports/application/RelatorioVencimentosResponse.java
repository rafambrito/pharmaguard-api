package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record RelatorioVencimentosResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        int totalItensVencendo,
        List<ItemVencimento> itens) {

    public enum StatusValidadeRelatorio {
        VALIDO,
        PROXIMO_VENCIMENTO,
        VENCIDO
    }

    public enum SeveridadeVencimento {
        BAIXA,
        MEDIA,
        ALTA,
        CRITICA
    }

    public record ItemVencimento(
            Long medicamentoId,
            String nomeMedicamento,
            String numeroLote,
            LocalDate dataValidade,
            int quantidade,
            StatusValidadeRelatorio statusValidade,
            SeveridadeVencimento severidade) {
    }
}
