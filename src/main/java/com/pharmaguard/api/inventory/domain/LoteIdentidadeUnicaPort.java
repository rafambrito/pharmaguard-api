package com.pharmaguard.api.inventory.domain;

public interface LoteIdentidadeUnicaPort {

    boolean existePorNumeroLoteEMedicamento(String numeroLote, Long medicamentoId);
}
