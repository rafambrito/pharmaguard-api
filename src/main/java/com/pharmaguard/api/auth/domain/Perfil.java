package com.pharmaguard.api.auth.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Perfil {

    public enum Papel {
        ADMIN("Administração do sistema, usuários, perfis e configurações"),
        FARMACEUTICO("Gestão de medicamentos, estoque, lotes, validade, entradas/saídas e fornecedores"),
        PROFISSIONAL_SAUDE("Consulta de estoque e informações necessárias para utilização dos insumos"),
        GESTOR("Visualização de estoque, alertas e relatórios gerenciais");

        private final String responsabilidade;

        Papel(String responsabilidade) {
            this.responsabilidade = responsabilidade;
        }

        public String getResponsabilidade() {
            return responsabilidade;
        }
    }

    private Long id;
    private String nome;
    private String descricao;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public Perfil() {
    }

    public Perfil(Long id, String nome, String descricao, boolean ativo,
            LocalDateTime dataCriacao, LocalDateTime dataUltimaAlteracao) {
        this.id = id;
        setNome(nome);
        setDescricao(descricao);
        this.ativo = ativo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = validarObrigatorio(descricao, "descricao");
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    private String validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.trim();
    }
}