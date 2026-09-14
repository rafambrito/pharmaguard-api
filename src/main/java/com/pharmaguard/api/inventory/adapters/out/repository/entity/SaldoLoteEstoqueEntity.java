package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "saldo_lotes_estoque")
@IdClass(SaldoLoteEstoqueId.class)
public class SaldoLoteEstoqueEntity {

    @Id
    @Column(name = "lote_id")
    private Long loteId;

    @Id
    @Column(name = "unidade_saude_id")
    private Long unidadeSaudeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id", insertable = false, updatable = false)
    private LoteEntity lote;

    @Column(name = "quantidade_disponivel", nullable = false)
    private int quantidadeDisponivel;

    @Column(name = "data_ultima_movimentacao")
    private LocalDateTime dataUltimaMovimentacao;

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public Long getUnidadeSaudeId() { return unidadeSaudeId; }
    public void setUnidadeSaudeId(Long unidadeSaudeId) { this.unidadeSaudeId = unidadeSaudeId; }

    public LoteEntity getLote() { return lote; }
    public void setLote(LoteEntity lote) { this.lote = lote; }

    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public LocalDateTime getDataUltimaMovimentacao() { return dataUltimaMovimentacao; }
    public void setDataUltimaMovimentacao(LocalDateTime dataUltimaMovimentacao) { this.dataUltimaMovimentacao = dataUltimaMovimentacao; }
}
