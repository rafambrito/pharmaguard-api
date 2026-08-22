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
@ConditionalOnBean(SaldoLoteEstoqueJpaRepository.class)
public class SaldoEstoqueJpaAdapter implements SaldoEstoqueRepositoryPort {

    private final MedicamentoJpaRepository medicamentoJpa;
    private final LoteJpaRepository loteJpa;
    private final SaldoLoteEstoqueJpaRepository saldoLoteJpa;

    public SaldoEstoqueJpaAdapter(MedicamentoJpaRepository medicamentoJpa,
            LoteJpaRepository loteJpa,
            SaldoLoteEstoqueJpaRepository saldoLoteJpa) {
        this.medicamentoJpa = medicamentoJpa;
        this.loteJpa = loteJpa;
        this.saldoLoteJpa = saldoLoteJpa;
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long medicamentoId) {
        return medicamentoJpa.findById(medicamentoId).map(this::medicamentoToDomain);
    }

    @Override
    public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId) {
        return loteJpa.findAllByMedicamento_Id(medicamentoId).stream()
                .map(this::saldoLoteToDomain)
                .toList();
    }

    @Override
    public List<SaldoLoteEstoque> listarTodosOsSaldos() {
        return loteJpa.findAll().stream()
                .map(this::saldoLoteToDomain)
                .toList();
    }

    @Override
    public int consultarQuantidadeReservada(Long medicamentoId) {
        return 0;
    }

    private SaldoLoteEstoque saldoLoteToDomain(LoteEntity lote) {
        int quantidadeDisponivel = saldoLoteJpa.findById(lote.getId())
                .map(SaldoLoteEstoqueEntity::getQuantidadeDisponivel)
                .orElse(lote.getQuantidadeInicial());
        return new SaldoLoteEstoque(lote.getMedicamento().getId(), lote.getId(), lote.getNumeroLote(),
            lote.getDataValidade(), quantidadeDisponivel);
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
