package com.pharmaguard.api.supplier.domain;

import java.util.Objects;

public class RegraLeadTime {

    private static final int LIMIAR_LEAD_TIME_ELEVADO = 30;

    public void validarDias(Integer leadTimeDias) {
        Objects.requireNonNull(leadTimeDias, "leadTimeDias e obrigatorio");
        if (leadTimeDias < 0) {
            throw new IllegalArgumentException("leadTimeDias nao pode ser negativo");
        }
    }

    public StatusLeadTime classificar(Integer leadTimeDias) {
        validarDias(leadTimeDias);
        if (leadTimeDias > LIMIAR_LEAD_TIME_ELEVADO) {
            return StatusLeadTime.ELEVADO;
        }
        return StatusLeadTime.USUAL;
    }
}