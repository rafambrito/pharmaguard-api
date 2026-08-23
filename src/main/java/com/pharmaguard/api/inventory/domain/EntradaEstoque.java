package com.pharmaguard.api.inventory.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class EntradaEstoque {

    public enum Origem {
        FORNECEDOR,
        AJUSTE_INVENTARIO,
        DEVOLUCAO,
        TRANSFERENCIA_ENTRADA
    }

    private Long id;
    private Medicamento medicamento;
    private Lote lote;
    private UnidadeSaude unidadeSaude;
    private int quantidade;
    private LocalDateTime dataEntrada;
    private Origem origem;
    private String documento;
    private String observacao;
    private Long usuarioResponsavelId;

    public EntradaEstoque() {
    }

    public EntradaEstoque(Long id, Medicamento medicamento, Lote lote, int quantidade, LocalDateTime dataEntrada,
            Origem origem, String documento, String observacao, Long usuarioResponsavelId) {
        this.id = id;
        setMedicamento(medicamento);
        setLote(lote);
        setQuantidade(quantidade);
        this.dataEntrada = dataEntrada;
        setOrigem(origem);
        setDocumento(documento);
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

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = Objects.requireNonNull(lote, "lote e obrigatorio");
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = Objects.requireNonNull(unidadeSaude, "unidade de saude e obrigatoria");
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        new RegraQuantidadeEstoque().validarQuantidadePositiva(quantidade, "quantidade");
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = Objects.requireNonNull(origem, "origem e obrigatoria");
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento == null ? null : documento.trim();
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

    public void registrarMomentoEntrada() {
        this.dataEntrada = LocalDateTime.now();
    }
}
