package com.pharmaguard.api.intelligence.application;

import com.pharmaguard.api.intelligence.domain.TipoPainel;
import java.time.Instant;

public record ExplicacaoPainelResponse(
        TipoPainel tipoPainel,
        String explicacao,
        OrigemExplicacao origem,
        Instant geradoEm) {

    public enum OrigemExplicacao {
        OLLAMA,
        FALLBACK
    }
}