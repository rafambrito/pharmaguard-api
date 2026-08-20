package com.pharmaguard.api.inventory.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Lote {

    private Long id;
    private String numeroLote;
    private LocalDate dataValidade;
    private int quantidadeInicial;
    private Medicamento medicamento;

    public Lote() {
    }

    public Lote(Long id, String numeroLote, LocalDate dataValidade, int quantidadeInicial, Medicamento medicamento) {
        this.id = id;
        setNumeroLote(numeroLote);
        setDataValidade(dataValidade);
        setQuantidadeInicial(quantidadeInicial);
        setMedicamento(medicamento);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        if (numeroLote == null || numeroLote.isBlank()) {
            throw new IllegalArgumentException("numeroLote e obrigatorio");
        }
        this.numeroLote = numeroLote.trim();
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        new RegraValidade().validarDataFutura(dataValidade);
        this.dataValidade = dataValidade;
    }

    public int getQuantidadeInicial() {
        return quantidadeInicial;
    }

    public void setQuantidadeInicial(int quantidadeInicial) {
        if (quantidadeInicial < 1) {
            throw new IllegalArgumentException("quantidadeInicial deve ser maior que zero");
        }
        this.quantidadeInicial = quantidadeInicial;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = Objects.requireNonNull(medicamento, "medicamento e obrigatorio");
    }

    public StatusValidade getStatusValidade() {
        return new RegraValidade().classificar(dataValidade);
    }

    public void validarIdentidadeUnica(LoteIdentidadeUnicaPort unicidadePort) {
        new LoteRegraIdentidadeUnica().validarParaCadastro(this, unicidadePort);
    }
}
