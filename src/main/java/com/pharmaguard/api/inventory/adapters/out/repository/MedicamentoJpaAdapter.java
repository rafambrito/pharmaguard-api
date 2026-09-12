package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.MedicamentoUseCase.MedicamentoRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class MedicamentoJpaAdapter implements MedicamentoRepositoryPort {

    private final MedicamentoJpaRepository medicamentoJpa;
    private final CategoriaJpaRepository categoriaJpa;
    private final UnidadeMedidaJpaRepository unidadeMedidaJpa;

    public MedicamentoJpaAdapter(MedicamentoJpaRepository medicamentoJpa,
            CategoriaJpaRepository categoriaJpa,
            UnidadeMedidaJpaRepository unidadeMedidaJpa) {
        this.medicamentoJpa = medicamentoJpa;
        this.categoriaJpa = categoriaJpa;
        this.unidadeMedidaJpa = unidadeMedidaJpa;
    }

    @Override
    public Medicamento salvar(Medicamento m) {
        return toDomain(medicamentoJpa.save(toEntity(m)));
    }

    @Override
    public Medicamento atualizar(Medicamento m) {
        return toDomain(medicamentoJpa.save(toEntity(m)));
    }

    @Override
    public void remover(Long id) {
        medicamentoJpa.deleteById(id);
    }

    @Override
    public Optional<Medicamento> buscarPorId(Long id) {
        return medicamentoJpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Medicamento> listarTodos() {
        return medicamentoJpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorNomeEApresentacao(String nome, String apresentacao) {
        return medicamentoJpa.existsByNomeIgnoreCaseAndApresentacaoIgnoreCase(nome, apresentacao);
    }

    @Override
    public Optional<Categoria> buscarCategoriaPorNome(String nome) {
        return categoriaJpa.findByNomeIgnoreCase(nome).map(this::categoriaToDomain);
    }

    @Override
    public Categoria salvarCategoria(Categoria categoria) {
        return categoriaToDomain(categoriaJpa.save(categoriaToEntity(categoria)));
    }

    @Override
    public Optional<UnidadeMedida> buscarUnidadeMedidaPorId(Long id) {
        return unidadeMedidaJpa.findById(id).map(this::unidadeMedidaToDomain);
    }

    private MedicamentoEntity toEntity(Medicamento m) {
        MedicamentoEntity e = new MedicamentoEntity();
        e.setId(m.getId());
        e.setNome(m.getNome());
        e.setApresentacao(m.getApresentacao());
        e.setDescricao(m.getDescricao());
        e.setCategoria(categoriaJpa.getReferenceById(m.getCategoria().getId()));
        e.setUnidadeMedida(unidadeMedidaJpa.getReferenceById(m.getUnidadeMedida().getId()));
        e.setCriticidade(m.getCriticidade());
        e.setStatus(m.getStatus());
        e.setDataCriacao(m.getDataCriacao());
        e.setDataUltimaAlteracao(m.getDataUltimaAlteracao());
        return e;
    }

    private Medicamento toDomain(MedicamentoEntity e) {
        return new Medicamento(e.getId(), e.getNome(), e.getApresentacao(), e.getDescricao(),
                categoriaToDomain(e.getCategoria()), unidadeMedidaToDomain(e.getUnidadeMedida()),
                e.getCriticidade(), e.getStatus(), e.getDataCriacao(), e.getDataUltimaAlteracao());
    }

    private Categoria categoriaToDomain(CategoriaEntity e) {
        return new Categoria(e.getId(), e.getNome(), e.getDescricao(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }

    private CategoriaEntity categoriaToEntity(Categoria c) {
        CategoriaEntity e = new CategoriaEntity();
        e.setId(c.getId());
        e.setNome(c.getNome());
        e.setDescricao(c.getDescricao());
        e.setStatus(c.getStatus());
        e.setDataCriacao(c.getDataCriacao());
        e.setDataUltimaAlteracao(c.getDataUltimaAlteracao());
        return e;
    }

    private UnidadeMedida unidadeMedidaToDomain(UnidadeMedidaEntity e) {
        return new UnidadeMedida(e.getId(), e.getNome(), e.getSigla(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }
}
