package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaidaEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaidaLoteUtilizadoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import com.pharmaguard.api.inventory.application.SaidaEstoqueUseCase.SaidaEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class SaidaEstoqueJpaAdapter implements SaidaEstoqueRepositoryPort {

    private final SaidaEstoqueJpaRepository saidaJpa;
    private final MedicamentoJpaRepository medicamentoJpa;
    private final LoteJpaRepository loteJpa;
    private final SaldoLoteEstoqueJpaRepository saldoLoteJpa;
    private final MovimentacaoEstoqueJpaRepository movimentacaoJpa;
    private final UnidadeSaudeJpaRepository unidadeSaudeJpa;

    public SaidaEstoqueJpaAdapter(SaidaEstoqueJpaRepository saidaJpa,
            MedicamentoJpaRepository medicamentoJpa,
            LoteJpaRepository loteJpa,
            SaldoLoteEstoqueJpaRepository saldoLoteJpa,
            MovimentacaoEstoqueJpaRepository movimentacaoJpa,
            UnidadeSaudeJpaRepository unidadeSaudeJpa) {
        this.saidaJpa = saidaJpa;
        this.medicamentoJpa = medicamentoJpa;
        this.loteJpa = loteJpa;
        this.saldoLoteJpa = saldoLoteJpa;
        this.movimentacaoJpa = movimentacaoJpa;
        this.unidadeSaudeJpa = unidadeSaudeJpa;
    }

    @Override
    public SaidaEstoque salvar(SaidaEstoque saida) {
        return toDomain(saidaJpa.save(toEntity(saida)));
    }

    @Override
    public boolean unidadeAtiva(Long unidadeId) {
        return unidadeSaudeJpa.existsByIdAndStatus(unidadeId,
                com.pharmaguard.api.inventory.domain.UnidadeSaude.Status.ATIVA);
    }

    @Override
    public Optional<SaidaEstoque> buscarPorId(Long id) {
        return saidaJpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<SaidaEstoque> listar(Long medicamentoId) {
        if (medicamentoId == null) {
            return saidaJpa.findAll().stream().map(this::toDomain).toList();
        }
        return saidaJpa.findAllByMedicamento_IdOrderByDataSaidaDesc(medicamentoId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<SaidaEstoque> listar(Long unidadeId, Long medicamentoId) {
        return saidaJpa.findAllByMedicamento_IdAndUnidadeSaude_IdOrderByDataSaidaDesc(medicamentoId, unidadeId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return medicamentoJpa.findById(medicamentoId).map(this::medicamentoToDomain);
    }

    @Override
    public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId) {
        return listarSaldosPorMedicamento(0L, medicamentoId);
        }

        @Override
        public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long unidadeId, Long medicamentoId) {
        return loteJpa.findAllByMedicamento_Id(medicamentoId).stream()
                .map(lote -> new SaldoLoteEstoque(
                        lote.getId(),
                        lote.getNumeroLote(),
                        lote.getDataValidade(),
                        saldoAtualDoLote(unidadeId, lote)))
                .toList();
    }

    @Override
    public int baixarSaldoLote(Long loteId, int quantidade) {
        return baixarSaldoLote(0L, loteId, quantidade);
    }

    @Override
    public int baixarSaldoLote(Long unidadeId, Long loteId, int quantidade) {
        LoteEntity lote = loteJpa.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("lote nao encontrado para atualizar saldo"));

        SaldoLoteEstoqueEntity saldo = saldoLoteJpa.findById(new com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueId(loteId, unidadeId))
            .orElseGet(() -> criarSaldoInicial(lote, unidadeId));

        int novoSaldo = saldo.getQuantidadeDisponivel() - quantidade;
        if (novoSaldo < 0) {
            throw new IllegalArgumentException("saldo insuficiente no lote informado");
        }

        saldo.setQuantidadeDisponivel(novoSaldo);
        saldo.setDataUltimaMovimentacao(LocalDateTime.now());
        return saldoLoteJpa.save(saldo).getQuantidadeDisponivel();
    }

    @Override
    public MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao) {
        return movimentacaoToDomain(movimentacaoJpa.save(movimentacaoToEntity(movimentacao)));
    }

    private SaldoLoteEstoqueEntity criarSaldoInicial(LoteEntity lote, Long unidadeId) {
        SaldoLoteEstoqueEntity saldo = new SaldoLoteEstoqueEntity();
        saldo.setLoteId(lote.getId());
        saldo.setLote(lote);
        saldo.setUnidadeSaudeId(unidadeId);
        saldo.setQuantidadeDisponivel(0);
        saldo.setDataUltimaMovimentacao(LocalDateTime.now());
        return saldo;
    }

    private int saldoAtualDoLote(Long unidadeId, LoteEntity lote) {
        return saldoLoteJpa.findById(new com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueId(lote.getId(), unidadeId))
                .map(SaldoLoteEstoqueEntity::getQuantidadeDisponivel)
                .orElse(0);
    }

    private SaidaEstoqueEntity toEntity(SaidaEstoque saida) {
        SaidaEstoqueEntity entity = new SaidaEstoqueEntity();
        entity.setId(saida.getId());
        entity.setMedicamento(medicamentoJpa.getReferenceById(saida.getMedicamento().getId()));
        entity.setUnidadeSaude(unidadeSaudeJpa.getReferenceById(saida.getUnidadeSaude().getId()));
        entity.setQuantidadeTotal(saida.getQuantidadeTotal());
        entity.setDataSaida(Objects.requireNonNullElseGet(saida.getDataSaida(), LocalDateTime::now));
        entity.setMotivo(saida.getMotivo());
        entity.setObservacao(saida.getObservacao());
        entity.setUsuarioResponsavelId(saida.getUsuarioResponsavelId());

        List<SaidaLoteUtilizadoEntity> lotes = new ArrayList<>();
        for (SaidaEstoque.LoteUtilizado loteUtilizado : saida.getLotesUtilizados()) {
            SaidaLoteUtilizadoEntity item = new SaidaLoteUtilizadoEntity();
            item.setSaida(entity);
            item.setLote(loteJpa.getReferenceById(loteUtilizado.getLoteId()));
            item.setNumeroLote(loteUtilizado.getNumeroLote());
            item.setQuantidadeConsumida(loteUtilizado.getQuantidadeConsumida());
            lotes.add(item);
        }
        entity.setLotesUtilizados(lotes);
        return entity;
    }

    private SaidaEstoque toDomain(SaidaEstoqueEntity entity) {
        SaidaEstoque saida = new SaidaEstoque();
        saida.setId(entity.getId());
        saida.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        saida.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(entity.getUnidadeSaude().getId()));
        saida.setQuantidadeTotal(entity.getQuantidadeTotal());
        saida.setDataSaida(entity.getDataSaida());
        saida.setMotivo(entity.getMotivo());
        saida.setObservacao(entity.getObservacao());
        saida.setUsuarioResponsavelId(entity.getUsuarioResponsavelId());

        List<SaidaEstoque.LoteUtilizado> lotes = entity.getLotesUtilizados().stream()
                .map(item -> new SaidaEstoque.LoteUtilizado(
                        item.getLote().getId(),
                        item.getNumeroLote(),
                        item.getQuantidadeConsumida()))
                .toList();
        if (!lotes.isEmpty()) {
            saida.definirLotesUtilizados(lotes);
        }
        return saida;
    }

    private MovimentacaoEstoqueEntity movimentacaoToEntity(MovimentacaoEstoque movimentacao) {
        MovimentacaoEstoqueEntity entity = new MovimentacaoEstoqueEntity();
        entity.setId(movimentacao.getId());
        entity.setTipo(movimentacao.getTipo());
        entity.setMedicamento(medicamentoJpa.getReferenceById(movimentacao.getMedicamento().getId()));
        if (movimentacao.getLote() != null && movimentacao.getLote().getId() != null) {
            entity.setLote(loteJpa.getReferenceById(movimentacao.getLote().getId()));
        }
        entity.setQuantidade(movimentacao.getQuantidade());
        entity.setSaldoAposMovimentacao(movimentacao.getSaldoAposMovimentacao());
        entity.setDataMovimentacao(Objects.requireNonNullElseGet(movimentacao.getDataMovimentacao(), LocalDateTime::now));
        entity.setMotivo(movimentacao.getMotivo());
        entity.setUnidadeSaude(unidadeSaudeJpa.getReferenceById(movimentacao.getUnidadeSaude().getId()));
        entity.setUsuarioResponsavelId(movimentacao.getUsuarioResponsavelId());
        return entity;
    }

    private MovimentacaoEstoque movimentacaoToDomain(MovimentacaoEstoqueEntity entity) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(entity.getId());
        movimentacao.setTipo(entity.getTipo());
        movimentacao.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        if (entity.getLote() != null) {
            movimentacao.setLote(loteToDomain(entity.getLote()));
        }
        movimentacao.setQuantidade(entity.getQuantidade());
        movimentacao.setSaldoAposMovimentacao(entity.getSaldoAposMovimentacao());
        movimentacao.setDataMovimentacao(entity.getDataMovimentacao());
        movimentacao.setMotivo(entity.getMotivo());
        movimentacao.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(entity.getUnidadeSaude().getId()));
        movimentacao.setUsuarioResponsavelId(entity.getUsuarioResponsavelId());
        return movimentacao;
    }

    private com.pharmaguard.api.inventory.domain.Lote loteToDomain(LoteEntity entity) {
        com.pharmaguard.api.inventory.domain.Lote lote = new com.pharmaguard.api.inventory.domain.Lote();
        lote.setId(entity.getId());
        lote.setNumeroLote(entity.getNumeroLote());
        lote.setDataValidade(entity.getDataValidade());
        lote.setQuantidadeInicial(entity.getQuantidadeInicial());
        lote.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        return lote;
    }

    private Medicamento medicamentoToDomain(MedicamentoEntity e) {
        return new Medicamento(e.getId(), e.getNome(), e.getApresentacao(), e.getDescricao(),
                categoriaToDomain(e.getCategoria()), unidadeMedidaToDomain(e.getUnidadeMedida()),
                e.getCriticidade(), e.getStatus(), e.getDataCriacao(), e.getDataUltimaAlteracao());
    }

    private Categoria categoriaToDomain(CategoriaEntity e) {
        return new Categoria(e.getId(), e.getNome(), e.getDescricao(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }

    private UnidadeMedida unidadeMedidaToDomain(UnidadeMedidaEntity e) {
        return new UnidadeMedida(e.getId(), e.getNome(), e.getSigla(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }
}
