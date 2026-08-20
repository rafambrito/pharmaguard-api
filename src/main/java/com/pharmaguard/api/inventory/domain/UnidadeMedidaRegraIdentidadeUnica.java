package com.pharmaguard.api.inventory.domain;

import java.util.Objects;

public class UnidadeMedidaRegraIdentidadeUnica {

    public void validarParaCriacao(UnidadeMedida unidadeMedida, UnidadeMedidaIdentidadeUnicaPort unicidadePort) {
        Objects.requireNonNull(unidadeMedida, "unidadeMedida e obrigatorio");
        Objects.requireNonNull(unicidadePort, "unicidadePort e obrigatorio");

        if (unicidadePort.existePorSigla(unidadeMedida.getSigla())) {
            throw new IllegalArgumentException("sigla de unidade de medida ja cadastrada");
        }
    }
}
