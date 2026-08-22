package com.pharmaguard.api.reports.application;

public record NivelEstoqueCalculado(
        int estoqueMinimo,
        int estoqueMaximo,
        int estoqueSeguranca,
        double coberturaDemandaDias,
        RiscoRuptura riscoRuptura) {

    public enum RiscoRuptura {
        BAIXO,
        MEDIO,
        ALTO,
        CRITICO
    }
}