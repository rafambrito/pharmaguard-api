package com.pharmaguard.api.reports.application;

public final class AnaliseValidadeMedicamentoCalculator {

    private AnaliseValidadeMedicamentoCalculator() {
    }

    public static AnaliseValidadeMedicamento calcular(
            int quantidadeValida,
            int quantidadeProximaVencimento,
            int quantidadeVencida,
            int diasParaProximoVencimento) {
        int valido = Math.max(0, quantidadeValida);
        int proximo = Math.max(0, quantidadeProximaVencimento);
        int vencido = Math.max(0, quantidadeVencida);
        int total = valido + proximo + vencido;

        if (total == 0) {
            return new AnaliseValidadeMedicamento(
                    0,
                    0,
                    0,
                    Integer.MAX_VALUE,
                    0d,
                    1d,
                    AnaliseValidadeMedicamento.RiscoValidade.BAIXO,
                    false);
        }

        double percentualRisco = (double) (proximo + vencido) / total;
        double percentualVencido = (double) vencido / total;
        double percentualProximo = (double) proximo / total;

        AnaliseValidadeMedicamento.RiscoValidade risco = classificarRisco(
                percentualRisco,
                percentualVencido,
                diasParaProximoVencimento,
                vencido,
                proximo);

        double perdaPotencial = (percentualVencido * 1.0d) + (percentualProximo * 0.5d);
        double fatorImpacto = Math.max(0.5d, 1.0d - Math.min(0.5d, perdaPotencial));

        return new AnaliseValidadeMedicamento(
                valido,
                proximo,
                vencido,
                Math.max(0, diasParaProximoVencimento),
                percentualRisco,
                fatorImpacto,
                risco,
                proximo > 0 || vencido > 0);
    }

    private static AnaliseValidadeMedicamento.RiscoValidade classificarRisco(
            double percentualRisco,
            double percentualVencido,
            int diasParaProximoVencimento,
            int vencido,
            int proximo) {
        if (vencido > 0 && percentualVencido >= 0.20d) {
            return AnaliseValidadeMedicamento.RiscoValidade.CRITICO;
        }
        if (vencido > 0) {
            return AnaliseValidadeMedicamento.RiscoValidade.ALTO;
        }
        if (proximo > 0 && diasParaProximoVencimento <= 15) {
            return AnaliseValidadeMedicamento.RiscoValidade.ALTO;
        }
        if (percentualRisco >= 0.30d) {
            return AnaliseValidadeMedicamento.RiscoValidade.ALTO;
        }
        if (percentualRisco >= 0.10d) {
            return AnaliseValidadeMedicamento.RiscoValidade.MEDIO;
        }
        return AnaliseValidadeMedicamento.RiscoValidade.BAIXO;
    }
}