package com.pharmaguard.api.inventory.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Medicamento {

    public enum Status {
        ATIVO,
        INATIVO
    }

    public enum Criticidade {
        BAIXA,
        MEDIA,
        ALTA,
        CRITICA
    }

    private Long id;
    private String nome;
    private String apresentacao;
    private String descricao;
    private Categoria categoria;
    private UnidadeMedida unidadeMedida;
    private Criticidade criticidade;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public Medicamento() {
    }

    public Medicamento(Long id, String nome, String apresentacao, String descricao,
            Categoria categoria, UnidadeMedida unidadeMedida, Criticidade criticidade,
            Status status, LocalDateTime dataCriacao, LocalDateTime dataUltimaAlteracao) {
        this.id = id;
        setNome(nome);
        setApresentacao(apresentacao);
        this.descricao = descricao;
        setCategoria(categoria);
        setUnidadeMedida(unidadeMedida);
        setCriticidade(criticidade);
        setStatus(status);
        this.dataCriacao = dataCriacao;
        this.dataUltimaAlteracao = dataUltimaAlteracao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = validarObrigatorio(nome, "nome");
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = validarObrigatorio(apresentacao, "apresentacao");
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "categoria e obrigatoria");
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = Objects.requireNonNull(unidadeMedida, "unidadeMedida e obrigatoria");
    }

    public Criticidade getCriticidade() {
        return criticidade;
    }

    public void setCriticidade(Criticidade criticidade) {
        this.criticidade = Objects.requireNonNull(criticidade, "criticidade e obrigatoria");
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "status e obrigatorio");
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataUltimaAlteracao() {
        return dataUltimaAlteracao;
    }

    public void setDataUltimaAlteracao(LocalDateTime dataUltimaAlteracao) {
        this.dataUltimaAlteracao = dataUltimaAlteracao;
    }

    public void marcarCriacao() {
        this.dataCriacao = LocalDateTime.now();
    }

    public void marcarAtualizacao() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }

    public void validarIdentidadeUnica(MedicamentoIdentidadeUnicaPort unicidadePort) {
        new MedicamentoRegraIdentidadeUnica().validarParaCriacao(this, unicidadePort);
    }

    private String validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.trim();
    }
}
