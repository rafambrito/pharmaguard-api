package com.pharmaguard.api.inventory.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/** Reference to the health unit owning an inventory context. */
public class UnidadeSaude {

    private Long id;
    private String identificacao;
    private String nome;
    private String tipo;
    private String endereco;
    private Status status;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public enum Status {
        ATIVA,
        INATIVA
    }

    public UnidadeSaude() {
    }

    public UnidadeSaude(Long id) {
        setId(id);
    }

    public UnidadeSaude(Long id, String identificacao, String nome, String tipo, String endereco,
            Status status, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        this.id = id;
        setIdentificacao(identificacao);
        setNome(nome);
        setTipo(tipo);
        setEndereco(endereco);
        setStatus(status);
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = obrigatorio(identificacao, "identificacao"); }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = obrigatorio(nome, "nome"); }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = obrigatorio(tipo, "tipo"); }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = obrigatorio(endereco, "endereco"); }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = Objects.requireNonNull(status, "status e obrigatorio"); }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public void marcarCadastro() { dataCadastro = LocalDateTime.now(); }
    public void marcarAtualizacao() { dataAtualizacao = LocalDateTime.now(); }

    private String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.trim();
    }
}