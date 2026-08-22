package com.pharmaguard.api.inventory.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class MovimentacaoEstoque {

    public enum Tipo {
        ENTRADA,
        SAIDA,
        AJUSTE
    }

    private Long id;
    private Tipo tipo;
    private Medicamento medicamento;
    private Lote lote;
    private int quantidade;
    private int saldoAposMovimentacao;
    private LocalDateTime dataMovimentacao;
    private String motivo;
    private Long usuarioResponsavelId;

    public MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(Long id, Tipo tipo, Medicamento medicamento, Lote lote, int quantidade,
            int saldoAposMovimentacao, LocalDateTime dataMovimentacao, String motivo, Long usuarioResponsavelId) {
        this.id = id;
        setTipo(tipo);
        setMedicamento(medicamento);
        setLote(lote);
        setQuantidade(quantidade);
        setSaldoAposMovimentacao(saldoAposMovimentacao);
        this.dataMovimentacao = dataMovimentacao;
        setMotivo(motivo);
        this.usuarioResponsavelId = usuarioResponsavelId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = Objects.requireNonNull(tipo, "tipo e obrigatorio");
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = Objects.requireNonNull(medicamento, "medicamento e obrigatorio");
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        new RegraQuantidadeEstoque().validarQuantidadePositiva(quantidade, "quantidade");
        this.quantidade = quantidade;
    }

    public int getSaldoAposMovimentacao() {
        return saldoAposMovimentacao;
    }

    public void setSaldoAposMovimentacao(int saldoAposMovimentacao) {
        new RegraQuantidadeEstoque().validarSaldoNaoNegativo(saldoAposMovimentacao, "saldoAposMovimentacao");
        this.saldoAposMovimentacao = saldoAposMovimentacao;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("motivo e obrigatorio");
        }
        this.motivo = motivo.trim();
    }

    public Long getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public void setUsuarioResponsavelId(Long usuarioResponsavelId) {
        this.usuarioResponsavelId = usuarioResponsavelId;
    }

    public void registrarMomentoMovimentacao() {
        this.dataMovimentacao = LocalDateTime.now();
    }
}
