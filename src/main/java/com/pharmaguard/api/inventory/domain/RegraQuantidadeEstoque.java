package com.pharmaguard.api.inventory.domain;

public class RegraQuantidadeEstoque {

    public void validarQuantidadePositiva(int quantidade, String campo) {
        if (quantidade < 1) {
            throw new IllegalArgumentException(campo + " deve ser maior que zero");
        }
    }

    public void validarSaldoNaoNegativo(int saldo, String campo) {
        if (saldo < 0) {
            throw new IllegalArgumentException(campo + " nao pode ser negativo");
        }
    }
}
