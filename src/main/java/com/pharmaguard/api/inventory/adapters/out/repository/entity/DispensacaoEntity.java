package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dispensacoes")
public class DispensacaoEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "paciente_id", nullable = false) private PacienteEntity paciente;
    @OneToOne @JoinColumn(name = "saida_estoque_id", nullable = false, unique = true) private SaidaEstoqueEntity saidaEstoque;
    @Column(name = "numero_receita", length = 100) private String numeroReceita;
    @Column(name = "crm_prescritor", length = 50) private String crmPrescritor;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public PacienteEntity getPaciente() { return paciente; } public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }
    public SaidaEstoqueEntity getSaidaEstoque() { return saidaEstoque; } public void setSaidaEstoque(SaidaEstoqueEntity saidaEstoque) { this.saidaEstoque = saidaEstoque; }
    public String getNumeroReceita() { return numeroReceita; } public void setNumeroReceita(String numeroReceita) { this.numeroReceita = numeroReceita; }
    public String getCrmPrescritor() { return crmPrescritor; } public void setCrmPrescritor(String crmPrescritor) { this.crmPrescritor = crmPrescritor; }
}