package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record RelatorioAlertasResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        int totalAlertas,
        ResumoAlertas resumo,
        List<ItemAlerta> alertas) {

    public enum TipoAlerta {
        RUPTURA,
        VENCIMENTO,
        EXCESSO_ESTOQUE
    }

    public enum SeveridadeAlerta {
        BAIXA,
        MEDIA,
        ALTA,
        CRITICA
    }

    public record ResumoAlertas(
            int totalRuptura,
            int totalVencimento,
            int totalExcessoEstoque,
            double totalConsumoPeriodo,
            int totalItensCriticos) {
    }

    public record ItemAlerta(
            Long medicamentoId,
            String nomeMedicamento,
            TipoAlerta tipo,
            SeveridadeAlerta severidade,
            int quantidadeImpactada,
            String descricao) {
    }
}