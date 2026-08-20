package com.pharmaguard.api.inventory.domain;

import java.util.Objects;

public class LoteRegraIdentidadeUnica {

    public void validarParaCadastro(Lote lote, LoteIdentidadeUnicaPort unicidadePort) {
        Objects.requireNonNull(lote, "lote e obrigatorio");
        Objects.requireNonNull(unicidadePort, "unicidadePort e obrigatorio");

        if (unicidadePort.existePorNumeroLoteEMedicamento(lote.getNumeroLote(), lote.getMedicamento().getId())) {
            throw new IllegalArgumentException("lote com mesmo numero ja cadastrado para este medicamento");
        }
    }
}
