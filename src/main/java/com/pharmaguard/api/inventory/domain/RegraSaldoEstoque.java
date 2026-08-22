package com.pharmaguard.api.inventory.domain;

import java.util.List;
import java.util.Objects;

public class RegraSaldoEstoque {

    public int calcularQuantidadeDisponivel(List<SaldoLoteEstoque> lotes) {
        Objects.requireNonNull(lotes, "lotes e obrigatorio");
        return lotes.stream()
                .filter(Objects::nonNull)
                .mapToInt(SaldoLoteEstoque::getQuantidadeDisponivel)
                .sum();
    }

    public void validarSaidaComSaldo(int quantidadeSolicitada, List<SaldoLoteEstoque> lotes) {
        new RegraQuantidadeEstoque().validarQuantidadePositiva(quantidadeSolicitada, "quantidadeSolicitada");
        int saldoDisponivel = calcularQuantidadeDisponivel(lotes);
        if (saldoDisponivel < quantidadeSolicitada) {
            throw new IllegalArgumentException("quantidade solicitada maior que o saldo disponivel");
        }
    }
}
