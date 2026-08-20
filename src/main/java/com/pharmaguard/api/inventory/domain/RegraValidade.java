package com.pharmaguard.api.inventory.domain;

import java.time.LocalDate;
import java.util.Objects;

public class RegraValidade {

    // dias restantes para considerar o lote proximo do vencimento
    private static final int DIAS_PROXIMO_VENCIMENTO = 90;

    public void validarDataFutura(LocalDate dataValidade) {
        Objects.requireNonNull(dataValidade, "dataValidade e obrigatoria");
        if (!dataValidade.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("dataValidade nao pode ser uma data vencida ou igual a hoje");
        }
    }

    public StatusValidade classificar(LocalDate dataValidade) {
        Objects.requireNonNull(dataValidade, "dataValidade e obrigatoria");
        LocalDate hoje = LocalDate.now();
        if (!dataValidade.isAfter(hoje)) {
            return StatusValidade.VENCIDO;
        }
        if (!dataValidade.isAfter(hoje.plusDays(DIAS_PROXIMO_VENCIMENTO))) {
            return StatusValidade.PROXIMO_VENCIMENTO;
        }
        return StatusValidade.VALIDO;
    }
}
