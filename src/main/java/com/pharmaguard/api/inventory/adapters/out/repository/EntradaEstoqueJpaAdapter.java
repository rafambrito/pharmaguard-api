package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.EntradaEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import com.pharmaguard.api.inventory.application.EntradaEstoqueUseCase.EntradaEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@ConditionalOnBean(EntradaEstoqueJpaRepository.class)
public class EntradaEstoqueJpaAdapter implements EntradaEstoqueRepositoryPort {

    private final EntradaEstoqueJpaRepository entradaJpa;
    private final MedicamentoJpaRepository medicamentoJpa;
    private final LoteJpaRepository loteJpa;
    private final SaldoLoteEstoqueJpaRepository saldoLoteJpa;
    private final MovimentacaoEstoqueJpaRepository movimentacaoJpa;

    public EntradaEstoqueJpaAdapter(EntradaEstoqueJpaRepository entradaJpa,
            MedicamentoJpaRepository medicamentoJpa,
            LoteJpaRepository loteJpa,
            SaldoLoteEstoqueJpaRepository saldoLoteJpa,
            MovimentacaoEstoqueJpaRepository movimentacaoJpa) {
        this.entradaJpa = entradaJpa;
        this.medicamentoJpa = medicamentoJpa;
        this.loteJpa = loteJpa;
        this.saldoLoteJpa = saldoLoteJpa;
        this.movimentacaoJpa = movimentacaoJpa;
    }

    @Override
    public EntradaEstoque salvar(EntradaEstoque entrada) {
        return toDomain(entradaJpa.save(toEntity(entrada)));
    }

    @Override
    public Optional<EntradaEstoque> buscarPorId(Long id) {
        return entradaJpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<EntradaEstoque> listar(Long medicamentoId, Long loteId) {
        return entradaJpa.findByFiltros(medicamentoId, loteId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return medicamentoJpa.findById(medicamentoId).map(this::medicamentoToDomain);
    }

    @Override
    public Optional<Lote> buscarLotePorMedicamentoIdEId(Long medicamentoId, Long loteId) {
        return loteJpa.findByIdAndMedicamento_Id(loteId, medicamentoId).map(this::loteToDomain);
    }

    @Override
    public int creditarSaldoLote(Long loteId, int quantidade) {
        LoteEntity lote = loteJpa.findById(loteId)
                .orElseThrow(() -> new IllegalArgumentException("lote nao encontrado para atualizar saldo"));

        SaldoLoteEstoqueEntity saldo = saldoLoteJpa.findById(loteId)
                .orElseGet(() -> criarSaldoInicial(lote));

        saldo.setQuantidadeDisponivel(saldo.getQuantidadeDisponivel() + quantidade);
        saldo.setDataUltimaMovimentacao(LocalDateTime.now());
        return saldoLoteJpa.save(saldo).getQuantidadeDisponivel();
    }

    @Override
    public MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao) {
        return movimentacaoToDomain(movimentacaoJpa.save(movimentacaoToEntity(movimentacao)));
    }

    private SaldoLoteEstoqueEntity criarSaldoInicial(LoteEntity lote) {
        SaldoLoteEstoqueEntity saldo = new SaldoLoteEstoqueEntity();
        saldo.setLote(lote);
        saldo.setQuantidadeDisponivel(lote.getQuantidadeInicial());
        saldo.setDataUltimaMovimentacao(LocalDateTime.now());
        return saldo;
    }

    private EntradaEstoqueEntity toEntity(EntradaEstoque entrada) {
        EntradaEstoqueEntity entity = new EntradaEstoqueEntity();
        entity.setId(entrada.getId());
        entity.setMedicamento(medicamentoJpa.getReferenceById(entrada.getMedicamento().getId()));
        entity.setLote(loteJpa.getReferenceById(entrada.getLote().getId()));
        entity.setQuantidade(entrada.getQuantidade());
        entity.setDataEntrada(Objects.requireNonNullElseGet(entrada.getDataEntrada(), LocalDateTime::now));
        entity.setOrigem(entrada.getOrigem());
        entity.setDocumento(entrada.getDocumento());
        entity.setObservacao(entrada.getObservacao());
        entity.setUsuarioResponsavelId(entrada.getUsuarioResponsavelId());
        return entity;
    }

    private EntradaEstoque toDomain(EntradaEstoqueEntity entity) {
        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setId(entity.getId());
        entrada.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        entrada.setLote(loteToDomain(entity.getLote()));
        entrada.setQuantidade(entity.getQuantidade());
        entrada.setDataEntrada(entity.getDataEntrada());
        entrada.setOrigem(entity.getOrigem());
        entrada.setDocumento(entity.getDocumento());
        entrada.setObservacao(entity.getObservacao());
        entrada.setUsuarioResponsavelId(entity.getUsuarioResponsavelId());
        return entrada;
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
        movimentacao.setUsuarioResponsavelId(entity.getUsuarioResponsavelId());
        return movimentacao;
    }

    private Lote loteToDomain(LoteEntity entity) {
        Lote lote = new Lote();
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
