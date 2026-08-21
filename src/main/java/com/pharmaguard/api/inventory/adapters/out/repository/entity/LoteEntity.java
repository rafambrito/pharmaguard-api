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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "lotes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_lotes_numero_medicamento", columnNames = {"numero_lote", "medicamento_id"})
})
public class LoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lote", nullable = false, length = 100)
    private String numeroLote;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "quantidade_inicial", nullable = false)
    private int quantidadeInicial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private MedicamentoEntity medicamento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public int getQuantidadeInicial() { return quantidadeInicial; }
    public void setQuantidadeInicial(int quantidadeInicial) { this.quantidadeInicial = quantidadeInicial; }

    public MedicamentoEntity getMedicamento() { return medicamento; }
    public void setMedicamento(MedicamentoEntity medicamento) { this.medicamento = medicamento; }
}
