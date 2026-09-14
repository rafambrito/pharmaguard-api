package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import java.io.Serializable;
import java.util.Objects;

public class SaldoLoteEstoqueId implements Serializable {

    private Long loteId;
    private Long unidadeSaudeId;

    public SaldoLoteEstoqueId() {
    }

    public SaldoLoteEstoqueId(Long loteId, Long unidadeSaudeId) {
        this.loteId = loteId;
        this.unidadeSaudeId = unidadeSaudeId;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public Long getUnidadeSaudeId() {
        return unidadeSaudeId;
    }

    public void setUnidadeSaudeId(Long unidadeSaudeId) {
        this.unidadeSaudeId = unidadeSaudeId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SaldoLoteEstoqueId that)) return false;
        return Objects.equals(loteId, that.loteId) && Objects.equals(unidadeSaudeId, that.unidadeSaudeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loteId, unidadeSaudeId);
    }
}