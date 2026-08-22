package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "saida_lotes_utilizados")
public class SaidaLoteUtilizadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saida_id", nullable = false)
    private SaidaEstoqueEntity saida;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteEntity lote;

    @Column(name = "numero_lote", nullable = false, length = 100)
    private String numeroLote;

    @Column(name = "quantidade_consumida", nullable = false)
    private int quantidadeConsumida;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SaidaEstoqueEntity getSaida() { return saida; }
    public void setSaida(SaidaEstoqueEntity saida) { this.saida = saida; }

    public LoteEntity getLote() { return lote; }
    public void setLote(LoteEntity lote) { this.lote = lote; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public int getQuantidadeConsumida() { return quantidadeConsumida; }
    public void setQuantidadeConsumida(int quantidadeConsumida) { this.quantidadeConsumida = quantidadeConsumida; }
}
