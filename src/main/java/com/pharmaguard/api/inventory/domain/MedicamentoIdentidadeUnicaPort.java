package com.pharmaguard.api.inventory.domain;

public interface MedicamentoIdentidadeUnicaPort {

    boolean existePorNomeEApresentacao(String nome, String apresentacao);
}
