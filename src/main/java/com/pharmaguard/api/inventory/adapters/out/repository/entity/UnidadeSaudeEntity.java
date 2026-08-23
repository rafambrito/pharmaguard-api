package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import com.pharmaguard.api.inventory.domain.UnidadeSaude;

@Entity
@Table(name = "unidades_saude")
public class UnidadeSaudeEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String identificacao;
    @Column(nullable = false, length = 150)
    private String nome;
    @Column(nullable = false, length = 50)
    private String tipo;
    @Column(nullable = false, length = 300)
    private String endereco;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeSaude.Status status;
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public UnidadeSaude.Status getStatus() { return status; }
    public void setStatus(UnidadeSaude.Status status) { this.status = status; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}