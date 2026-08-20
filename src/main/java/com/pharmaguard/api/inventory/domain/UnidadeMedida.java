package com.pharmaguard.api.inventory.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class UnidadeMedida {

    public enum Status {
        ATIVA,
        INATIVA
    }

    private Long id;
    private String nome;
    private String sigla;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public UnidadeMedida() {
    }

    public UnidadeMedida(Long id, String nome, String sigla, Status status,
            LocalDateTime dataCriacao, LocalDateTime dataUltimaAlteracao) {
        this.id = id;
        setNome(nome);
        setSigla(sigla);
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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = validarObrigatorio(sigla, "sigla").toUpperCase();
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

    public void validarIdentidadeUnica(UnidadeMedidaIdentidadeUnicaPort unicidadePort) {
        new UnidadeMedidaRegraIdentidadeUnica().validarParaCriacao(this, unicidadePort);
    }

    private String validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.trim();
    }
}
