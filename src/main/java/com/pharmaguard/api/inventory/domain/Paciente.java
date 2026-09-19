package com.pharmaguard.api.inventory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Paciente {

    public enum Status {
        ATIVO,
        INATIVO
    }

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String cartaoSus;
    private String telefone;
    private String email;
    private String cidade;
    private String uf;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = obrigatorio(nome, "nome"); }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = normalizarCpf(cpf); }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = Objects.requireNonNull(dataNascimento, "dataNascimento e obrigatoria");
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("dataNascimento nao pode estar no futuro");
        }
    }
    public String getCartaoSus() { return cartaoSus; }
    public void setCartaoSus(String cartaoSus) { this.cartaoSus = opcional(cartaoSus); }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = opcional(telefone); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = opcional(email); }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = opcional(cidade); }
    public String getUf() { return uf; }
    public void setUf(String uf) {
        String valor = opcional(uf);
        if (valor != null && valor.length() != 2) {
            throw new IllegalArgumentException("uf deve ter 2 caracteres");
        }
        this.uf = valor == null ? null : valor.toUpperCase();
    }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = Objects.requireNonNull(status, "status e obrigatorio"); }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataUltimaAlteracao() { return dataUltimaAlteracao; }
    public void setDataUltimaAlteracao(LocalDateTime dataUltimaAlteracao) { this.dataUltimaAlteracao = dataUltimaAlteracao; }
    public void inativar() { status = Status.INATIVO; dataUltimaAlteracao = LocalDateTime.now(); }
    public boolean estaAtivo() { return status == Status.ATIVO; }

    private String normalizarCpf(String cpf) {
        String valor = obrigatorio(cpf, "cpf").replaceAll("\\D", "");
        if (valor.length() != 11 || valor.chars().distinct().count() == 1 || !digitosValidos(valor)) {
            throw new IllegalArgumentException("cpf invalido");
        }
        return valor;
    }

    private boolean digitosValidos(String cpf) {
        return digito(cpf, 9) == cpf.charAt(9) - '0' && digito(cpf, 10) == cpf.charAt(10) - '0';
    }

    private int digito(String cpf, int tamanho) {
        int soma = 0;
        for (int indice = 0; indice < tamanho; indice++) {
            soma += (cpf.charAt(indice) - '0') * (tamanho + 1 - indice);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private String obrigatorio(String valor, String campo) {
        String normalizado = opcional(valor);
        if (normalizado == null) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return normalizado;
    }

    private String opcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}