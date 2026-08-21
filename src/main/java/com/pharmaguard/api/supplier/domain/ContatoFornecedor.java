package com.pharmaguard.api.supplier.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class ContatoFornecedor {

    public enum CanalPrincipal {
        TELEFONE,
        EMAIL,
        WHATSAPP,
        OUTRO
    }

    private Long id;
    private String nome;
    private String cargo;
    private String telefone;
    private String email;
    private CanalPrincipal canalPrincipal;
    private boolean ativo;
    private Fornecedor fornecedor;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public ContatoFornecedor() {
    }

    public ContatoFornecedor(Long id, String nome, String cargo, String telefone, String email,
            CanalPrincipal canalPrincipal, boolean ativo, Fornecedor fornecedor,
            LocalDateTime dataCriacao, LocalDateTime dataUltimaAlteracao) {
        this.id = id;
        setNome(nome);
        setCargo(cargo);
        setTelefone(telefone);
        setEmail(email);
        setCanalPrincipal(canalPrincipal);
        setAtivo(ativo);
        setFornecedor(fornecedor);
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = normalizarOpcional(cargo);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = normalizarOpcional(telefone);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String emailNormalizado = normalizarOpcional(email);
        if (emailNormalizado != null && !emailNormalizado.contains("@")) {
            throw new IllegalArgumentException("email deve ter formato valido");
        }
        this.email = emailNormalizado;
    }

    public CanalPrincipal getCanalPrincipal() {
        return canalPrincipal;
    }

    public void setCanalPrincipal(CanalPrincipal canalPrincipal) {
        this.canalPrincipal = Objects.requireNonNull(canalPrincipal, "canalPrincipal e obrigatorio");
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
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

    private String validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.trim();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}