package com.pharmaguard.api.inventory.infrastructure.repository.entity;

import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "unidades_medida", uniqueConstraints = {
        @UniqueConstraint(name = "uk_unidades_medida_sigla", columnNames = "sigla")
})
public class UnidadeMedidaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "sigla", nullable = false, length = 20)
    private String sigla;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UnidadeMedida.Status status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime dataUltimaAlteracao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public UnidadeMedida.Status getStatus() { return status; }
    public void setStatus(UnidadeMedida.Status status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaAlteracao() { return dataUltimaAlteracao; }
    public void setDataUltimaAlteracao(LocalDateTime v) { this.dataUltimaAlteracao = v; }
}
