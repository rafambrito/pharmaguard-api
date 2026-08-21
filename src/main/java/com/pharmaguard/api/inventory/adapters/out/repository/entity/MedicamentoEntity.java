package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import com.pharmaguard.api.inventory.domain.Medicamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicamentos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_medicamentos_nome_apresentacao", columnNames = {"nome", "apresentacao"})
})
public class MedicamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "apresentacao", nullable = false, length = 200)
    private String apresentacao;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unidade_medida_id", nullable = false)
    private UnidadeMedidaEntity unidadeMedida;

    @Enumerated(EnumType.STRING)
    @Column(name = "criticidade", nullable = false, length = 20)
    private Medicamento.Criticidade criticidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Medicamento.Status status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime dataUltimaAlteracao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getApresentacao() { return apresentacao; }
    public void setApresentacao(String apresentacao) { this.apresentacao = apresentacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public CategoriaEntity getCategoria() { return categoria; }
    public void setCategoria(CategoriaEntity categoria) { this.categoria = categoria; }

    public UnidadeMedidaEntity getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(UnidadeMedidaEntity unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public Medicamento.Criticidade getCriticidade() { return criticidade; }
    public void setCriticidade(Medicamento.Criticidade criticidade) { this.criticidade = criticidade; }

    public Medicamento.Status getStatus() { return status; }
    public void setStatus(Medicamento.Status status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaAlteracao() { return dataUltimaAlteracao; }
    public void setDataUltimaAlteracao(LocalDateTime v) { this.dataUltimaAlteracao = v; }
}
