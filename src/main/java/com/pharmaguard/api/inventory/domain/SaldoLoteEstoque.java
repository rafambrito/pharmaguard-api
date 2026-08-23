package com.pharmaguard.api.inventory.domain;

import java.time.LocalDate;

public class SaldoLoteEstoque {

    private Long loteId;
    private Long medicamentoId;
    private UnidadeSaude unidadeSaude;
    private String numeroLote;
    private LocalDate dataValidade;
    private int quantidadeDisponivel;

    public SaldoLoteEstoque() {
    }

    public SaldoLoteEstoque(Long loteId, String numeroLote, LocalDate dataValidade, int quantidadeDisponivel) {
        this.loteId = loteId;
        setNumeroLote(numeroLote);
        setDataValidade(dataValidade);
        setQuantidadeDisponivel(quantidadeDisponivel);
    }

    public SaldoLoteEstoque(Long medicamentoId, Long loteId, String numeroLote,
            LocalDate dataValidade, int quantidadeDisponivel) {
        this(loteId, numeroLote, dataValidade, quantidadeDisponivel);
        this.medicamentoId = medicamentoId;
    }

    public Long getMedicamentoId() {
        return medicamentoId;
    }

    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
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
        this.dataValidade = dataValidade;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        new RegraQuantidadeEstoque().validarSaldoNaoNegativo(quantidadeDisponivel, "quantidadeDisponivel");
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public StatusValidade getStatusValidade() {
        return new RegraValidade().classificar(dataValidade);
    }

    public boolean estaValidoParaSaida() {
        return quantidadeDisponivel > 0 && getStatusValidade() != StatusValidade.VENCIDO;
    }
}
