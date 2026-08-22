package com.pharmaguard.api.inventory.adapters.out.repository.entity;

import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private MovimentacaoEstoque.Tipo tipo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private MedicamentoEntity medicamento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id")
    private LoteEntity lote;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @Column(name = "saldo_apos_movimentacao", nullable = false)
    private int saldoAposMovimentacao;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao;

    @Column(name = "motivo", nullable = false, length = 120)
    private String motivo;

    @Column(name = "usuario_responsavel_id")
    private Long usuarioResponsavelId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MovimentacaoEstoque.Tipo getTipo() { return tipo; }
    public void setTipo(MovimentacaoEstoque.Tipo tipo) { this.tipo = tipo; }

    public MedicamentoEntity getMedicamento() { return medicamento; }
    public void setMedicamento(MedicamentoEntity medicamento) { this.medicamento = medicamento; }

    public LoteEntity getLote() { return lote; }
    public void setLote(LoteEntity lote) { this.lote = lote; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int getSaldoAposMovimentacao() { return saldoAposMovimentacao; }
    public void setSaldoAposMovimentacao(int saldoAposMovimentacao) { this.saldoAposMovimentacao = saldoAposMovimentacao; }

    public LocalDateTime getDataMovimentacao() { return dataMovimentacao; }
    public void setDataMovimentacao(LocalDateTime dataMovimentacao) { this.dataMovimentacao = dataMovimentacao; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Long getUsuarioResponsavelId() { return usuarioResponsavelId; }
    public void setUsuarioResponsavelId(Long usuarioResponsavelId) { this.usuarioResponsavelId = usuarioResponsavelId; }
}
