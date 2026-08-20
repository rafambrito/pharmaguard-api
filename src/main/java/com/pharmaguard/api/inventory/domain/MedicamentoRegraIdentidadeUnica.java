package com.pharmaguard.api.inventory.domain;

import java.util.Objects;

public class MedicamentoRegraIdentidadeUnica {

    public void validarParaCriacao(Medicamento medicamento, MedicamentoIdentidadeUnicaPort unicidadePort) {
        Objects.requireNonNull(medicamento, "medicamento e obrigatorio");
        Objects.requireNonNull(unicidadePort, "unicidadePort e obrigatorio");

        if (unicidadePort.existePorNomeEApresentacao(medicamento.getNome(), medicamento.getApresentacao())) {
            throw new IllegalArgumentException("medicamento com mesmo nome e apresentacao ja cadastrado");
        }
    }
}
