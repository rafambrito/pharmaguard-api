package com.pharmaguard.api.supplier.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Fornecedor {

    public enum Status {
        ATIVO,
        INATIVO
    }

    private Long id;
    private String nome;
    private String codigo;
    private String documento;
    private String observacao;
    private Integer leadTimeDias;
    private Status status;
    private final List<ContatoFornecedor> contatos;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    public Fornecedor() {
        this.contatos = new ArrayList<>();
    }

    public Fornecedor(Long id, String nome, String codigo, String documento, String observacao,
            Integer leadTimeDias, Status status, LocalDateTime dataCriacao,
            LocalDateTime dataUltimaAlteracao) {
        this();
        this.id = id;
        setNome(nome);
        setCodigo(codigo);
        setDocumento(documento);
        setObservacao(observacao);
        setLeadTimeDias(leadTimeDias);
        setStatus(status);
        validarIdentificacao();
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = normalizarOpcional(codigo);
        validarIdentificacao();
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = normalizarOpcional(documento);
        validarIdentificacao();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = normalizarOpcional(observacao);
    }

    public Integer getLeadTimeDias() {
        return leadTimeDias;
    }

    public void setLeadTimeDias(Integer leadTimeDias) {
        new RegraLeadTime().validarDias(leadTimeDias);
        this.leadTimeDias = leadTimeDias;
    }

    public StatusLeadTime getStatusLeadTime() {
        if (leadTimeDias == null) {
            return null;
        }
        return new RegraLeadTime().classificar(leadTimeDias);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "status e obrigatorio");
    }

    public List<ContatoFornecedor> getContatos() {
        return Collections.unmodifiableList(contatos);
    }

    public void adicionarContato(ContatoFornecedor contato) {
        ContatoFornecedor contatoValidado = Objects.requireNonNull(contato, "contato e obrigatorio");
        contatoValidado.setFornecedor(this);
        if (!contatos.contains(contatoValidado)) {
            contatos.add(contatoValidado);
        }
    }

    public void removerContato(ContatoFornecedor contato) {
        if (contato == null) {
            return;
        }
        contatos.removeIf(contatoExistente -> mesmoContato(contatoExistente, contato));
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

    public void validarIdentidadeUnica(FornecedorIdentidadeUnicaPort unicidadePort) {
        new FornecedorRegraIdentidadeUnica().validarParaCriacao(this, unicidadePort);
    }

    private void validarIdentificacao() {
        if ((codigo == null || codigo.isBlank()) && (documento == null || documento.isBlank())) {
            throw new IllegalArgumentException("codigo ou documento e obrigatorio");
        }
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

    private boolean mesmoContato(ContatoFornecedor primeiro, ContatoFornecedor segundo) {
        if (primeiro.getId() != null && segundo.getId() != null) {
            return Objects.equals(primeiro.getId(), segundo.getId());
        }
        return primeiro == segundo;
    }
}