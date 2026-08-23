package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import com.pharmaguard.api.inventory.application.SaldoEstoqueUseCase.SaldoEstoqueRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnBean({SaldoLoteEstoqueJpaRepository.class, UnidadeSaudeJpaRepository.class})
public class SaldoEstoqueJpaAdapter implements SaldoEstoqueRepositoryPort {

    private final MedicamentoJpaRepository medicamentoJpa;
    private final LoteJpaRepository loteJpa;
    private final SaldoLoteEstoqueJpaRepository saldoLoteJpa;
    private final UnidadeSaudeJpaRepository unidadeSaudeJpa;

    public SaldoEstoqueJpaAdapter(MedicamentoJpaRepository medicamentoJpa,
            LoteJpaRepository loteJpa,
            SaldoLoteEstoqueJpaRepository saldoLoteJpa,
            UnidadeSaudeJpaRepository unidadeSaudeJpa) {
        this.medicamentoJpa = medicamentoJpa;
        this.loteJpa = loteJpa;
        this.saldoLoteJpa = saldoLoteJpa;
        this.unidadeSaudeJpa = unidadeSaudeJpa;
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return medicamentoJpa.findById(medicamentoId).map(this::medicamentoToDomain);
    }

    @Override
    public boolean unidadeAtiva(Long unidadeId) {
        return unidadeSaudeJpa.existsByIdAndStatus(unidadeId,
                com.pharmaguard.api.inventory.domain.UnidadeSaude.Status.ATIVA);
    }

    @Override
    public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId) {
        return listarSaldosPorMedicamento(0L, medicamentoId);
    }

    @Override
    public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long unidadeId, Long medicamentoId) {
        return loteJpa.findAllByMedicamento_Id(medicamentoId).stream()
                .map(lote -> saldoLoteToDomain(unidadeId, lote))
                .toList();
    }

    @Override
    public List<SaldoLoteEstoque> listarTodosOsSaldos() {
        return loteJpa.findAll().stream()
                .map(lote -> saldoLoteToDomain(0L, lote))
                .toList();
    }

    @Override
    public int consultarQuantidadeReservada(Long medicamentoId) {
        return 0;
    }

    private SaldoLoteEstoque saldoLoteToDomain(LoteEntity lote) {
        return saldoLoteToDomain(0L, lote);
    }

    private SaldoLoteEstoque saldoLoteToDomain(Long unidadeId, LoteEntity lote) {
        int quantidadeDisponivel = saldoLoteJpa.findById(new com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueId(lote.getId(), unidadeId))
                .map(SaldoLoteEstoqueEntity::getQuantidadeDisponivel)
                .orElse(0);
        SaldoLoteEstoque saldo = new SaldoLoteEstoque(lote.getMedicamento().getId(), lote.getId(), lote.getNumeroLote(),
            lote.getDataValidade(), quantidadeDisponivel);
        saldo.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(unidadeId));
        return saldo;
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
