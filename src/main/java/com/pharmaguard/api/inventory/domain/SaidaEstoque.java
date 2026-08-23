package com.pharmaguard.api.inventory.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaidaEstoque {

    public enum Motivo {
        DISPENSACAO,
        PERDA,
        AJUSTE_INVENTARIO,
        TRANSFERENCIA_SAIDA
    }

    private Long id;
    private Medicamento medicamento;
    private UnidadeSaude unidadeSaude;
    private int quantidadeTotal;
    private LocalDateTime dataSaida;
    private Motivo motivo;
    private String observacao;
    private Long usuarioResponsavelId;
    private final List<LoteUtilizado> lotesUtilizados = new ArrayList<>();

    public SaidaEstoque() {
    }

    public SaidaEstoque(Long id, Medicamento medicamento, int quantidadeTotal, LocalDateTime dataSaida,
            Motivo motivo, String observacao, Long usuarioResponsavelId) {
        this.id = id;
        setMedicamento(medicamento);
        setQuantidadeTotal(quantidadeTotal);
        this.dataSaida = dataSaida;
        setMotivo(motivo);
        setObservacao(observacao);
        this.usuarioResponsavelId = usuarioResponsavelId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = Objects.requireNonNull(medicamento, "medicamento e obrigatorio");
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = Objects.requireNonNull(unidadeSaude, "unidade de saude e obrigatoria");
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        new RegraQuantidadeEstoque().validarQuantidadePositiva(quantidadeTotal, "quantidadeTotal");
        this.quantidadeTotal = quantidadeTotal;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    public void setMotivo(Motivo motivo) {
        this.motivo = Objects.requireNonNull(motivo, "motivo e obrigatorio");
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao == null ? null : observacao.trim();
    }

    public Long getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public void setUsuarioResponsavelId(Long usuarioResponsavelId) {
        this.usuarioResponsavelId = usuarioResponsavelId;
    }

    public List<LoteUtilizado> getLotesUtilizados() {
        return List.copyOf(lotesUtilizados);
    }

    public void definirLotesUtilizados(List<LoteUtilizado> lotes) {
        Objects.requireNonNull(lotes, "lotes e obrigatorio");
        if (lotes.isEmpty()) {
            throw new IllegalArgumentException("lotes utilizados e obrigatorio");
        }
        this.lotesUtilizados.clear();
        this.lotesUtilizados.addAll(lotes);
    }

    public void registrarMomentoSaida() {
        this.dataSaida = LocalDateTime.now();
    }

    public static class LoteUtilizado {

        private Long loteId;
        private String numeroLote;
        private int quantidadeConsumida;

        public LoteUtilizado() {
        }

        public LoteUtilizado(Long loteId, String numeroLote, int quantidadeConsumida) {
            this.loteId = loteId;
            setNumeroLote(numeroLote);
            setQuantidadeConsumida(quantidadeConsumida);
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

        public int getQuantidadeConsumida() {
            return quantidadeConsumida;
        }

        public void setQuantidadeConsumida(int quantidadeConsumida) {
            new RegraQuantidadeEstoque().validarQuantidadePositiva(quantidadeConsumida, "quantidadeConsumida");
            this.quantidadeConsumida = quantidadeConsumida;
        }
    }
}
