package com.pharmaguard.api.auth.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Usuario {

    public enum Status {
        ATIVO,
        INATIVO,
        BLOQUEADO
    }

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String tipo;
    private String senhaHash;
    private Status status;
    private final List<Perfil> perfis;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public Usuario() {
        this.perfis = new ArrayList<>();
    }

    public Usuario(Long id, String nome, String email, String login, String tipo, String senhaHash,
            Status status, LocalDateTime dataCriacao, LocalDateTime dataUltimaAlteracao) {
        this();
        this.id = id;
        setNome(nome);
        setEmail(email);
        setLogin(login);
        setTipo(tipo);
        setSenhaHash(senhaHash);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String emailNormalizado = validarObrigatorio(email, "email");
        if (!emailNormalizado.contains("@")) {
            throw new IllegalArgumentException("email deve ter formato valido");
        }
        this.email = emailNormalizado;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = validarObrigatorio(login, "login");
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        String tipoNormalizado = validarObrigatorio(tipo, "tipo").toUpperCase();
        this.tipo = tipoNormalizado;

        if (!perfis.isEmpty()) {
            Perfil perfilAtual = perfis.get(0);
            perfilAtual.setNome(tipoNormalizado);
        }
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = validarObrigatorio(senhaHash, "senhaHash");
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "status e obrigatorio");
    }

    public List<Perfil> getPerfis() {
        return Collections.unmodifiableList(perfis);
    }

    public void adicionarPerfil(Perfil perfil) {
        Perfil perfilValidado = Objects.requireNonNull(perfil, "perfil e obrigatorio");
        perfis.clear();
        perfis.add(perfilValidado);
        this.tipo = perfilValidado.getNome();
    }

    public void removerPerfil(Perfil perfil) {
        if (perfil == null) {
            return;
        }
        perfis.removeIf(perfilExistente -> mesmoPerfil(perfilExistente, perfil));
        if (perfis.isEmpty()) {
            this.tipo = null;
        }
    }

    public boolean possuiPerfil(Perfil perfil) {
        if (perfil == null) {
            return false;
        }
        return contemPerfil(perfil);
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

    public void validarIdentidadeUnica(UsuarioIdentidadeUnicaPort unicidadePort) {
        new UsuarioRegraIdentidadeUnica().validarParaCriacao(this, unicidadePort);
    }

    private boolean contemPerfil(Perfil perfil) {
        return perfis.stream().anyMatch(perfilExistente -> mesmoPerfil(perfilExistente, perfil));
    }

    private boolean mesmoPerfil(Perfil primeiro, Perfil segundo) {
        if (primeiro.getId() != null && segundo.getId() != null) {
            return Objects.equals(primeiro.getId(), segundo.getId());
        }
        return Objects.equals(primeiro.getNome(), segundo.getNome());
    }

    private String validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " e obrigatorio");
        }
        return valor.trim();
    }
}
