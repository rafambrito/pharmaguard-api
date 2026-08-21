package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.LoteUseCase.LoteRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(LoteJpaRepository.class)
public class LoteJpaAdapter implements LoteRepositoryPort {

    private final LoteJpaRepository loteJpa;
    private final MedicamentoJpaRepository medicamentoJpa;

    public LoteJpaAdapter(LoteJpaRepository loteJpa, MedicamentoJpaRepository medicamentoJpa) {
        this.loteJpa = loteJpa;
        this.medicamentoJpa = medicamentoJpa;
    }

    @Override
    public Lote salvar(Lote lote) {
        return toDomain(loteJpa.save(toEntity(lote)));
    }

    @Override
    public void remover(Long id) {
        loteJpa.deleteById(id);
    }

    @Override
    public Optional<Lote> buscarPorMedicamentoIdEId(Long medicamentoId, Long loteId) {
        return loteJpa.findByIdAndMedicamento_Id(loteId, medicamentoId).map(this::toDomain);
    }

    @Override
    public List<Lote> listarPorMedicamento(Long medicamentoId) {
        return loteJpa.findAllByMedicamento_Id(medicamentoId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorNumeroLoteEMedicamento(String numeroLote, Long medicamentoId) {
        return loteJpa.existsByNumeroLoteIgnoreCaseAndMedicamento_Id(numeroLote, medicamentoId);
    }

    @Override
    public Optional<Medicamento> buscarMedicamentoPorId(Long id) {
        return medicamentoJpa.findById(id).map(this::medicamentoToDomain);
    }

    private LoteEntity toEntity(Lote lote) {
        LoteEntity e = new LoteEntity();
        e.setId(lote.getId());
        e.setNumeroLote(lote.getNumeroLote());
        e.setDataValidade(lote.getDataValidade());
        e.setQuantidadeInicial(lote.getQuantidadeInicial());
        e.setMedicamento(medicamentoJpa.getReferenceById(lote.getMedicamento().getId()));
        return e;
    }

    private Lote toDomain(LoteEntity e) {
        Medicamento medicamento = medicamentoToDomain(e.getMedicamento());
        Lote lote = new Lote();
        lote.setId(e.getId());
        lote.setNumeroLote(e.getNumeroLote());
        lote.setDataValidade(e.getDataValidade());
        lote.setQuantidadeInicial(e.getQuantidadeInicial());
        lote.setMedicamento(medicamento);
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
