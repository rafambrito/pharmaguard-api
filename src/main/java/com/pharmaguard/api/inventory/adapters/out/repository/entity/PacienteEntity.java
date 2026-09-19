package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import com.pharmaguard.api.inventory.domain.Paciente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pacientes")
public class PacienteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false, unique = true, length = 11) private String cpf;
    @Column(name = "data_nascimento", nullable = false) private LocalDate dataNascimento;
    @Column(name = "cartao_sus", length = 30) private String cartaoSus;
    @Column(length = 30) private String telefone;
    @Column(length = 150) private String email;
    @Column(length = 100) private String cidade;
    @Column(length = 2) private String uf;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Paciente.Status status;
    @Column(name = "data_criacao", nullable = false) private LocalDateTime dataCriacao;
    @Column(name = "data_ultima_alteracao") private LocalDateTime dataUltimaAlteracao;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; } public void setCpf(String cpf) { this.cpf = cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; } public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCartaoSus() { return cartaoSus; } public void setCartaoSus(String cartaoSus) { this.cartaoSus = cartaoSus; }
    public String getTelefone() { return telefone; } public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getCidade() { return cidade; } public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; } public void setUf(String uf) { this.uf = uf; }
    public Paciente.Status getStatus() { return status; } public void setStatus(Paciente.Status status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; } public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataUltimaAlteracao() { return dataUltimaAlteracao; } public void setDataUltimaAlteracao(LocalDateTime dataUltimaAlteracao) { this.dataUltimaAlteracao = dataUltimaAlteracao; }
}