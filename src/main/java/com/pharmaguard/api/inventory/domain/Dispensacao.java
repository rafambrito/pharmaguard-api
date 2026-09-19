package com.pharmaguard.api.inventory.domain;

import java.util.Objects;

public class Dispensacao {

    private Long id;
    private Paciente paciente;
    private SaidaEstoque saidaEstoque;
    private String numeroReceita;
    private String crmPrescritor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = Objects.requireNonNull(paciente, "paciente e obrigatorio"); }
    public SaidaEstoque getSaidaEstoque() { return saidaEstoque; }
    public void setSaidaEstoque(SaidaEstoque saidaEstoque) {
        this.saidaEstoque = Objects.requireNonNull(saidaEstoque, "saidaEstoque e obrigatoria");
    }
    public String getNumeroReceita() { return numeroReceita; }
    public void setNumeroReceita(String numeroReceita) { this.numeroReceita = opcional(numeroReceita); }
    public String getCrmPrescritor() { return crmPrescritor; }
    public void setCrmPrescritor(String crmPrescritor) { this.crmPrescritor = opcional(crmPrescritor); }

    private String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}