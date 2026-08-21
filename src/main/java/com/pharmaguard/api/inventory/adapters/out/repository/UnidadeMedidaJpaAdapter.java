package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase.UnidadeMedidaRepositoryPort;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(UnidadeMedidaJpaRepository.class)
public class UnidadeMedidaJpaAdapter implements UnidadeMedidaRepositoryPort {

    private final UnidadeMedidaJpaRepository jpa;

    public UnidadeMedidaJpaAdapter(UnidadeMedidaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public UnidadeMedida salvar(UnidadeMedida u) {
        return toDomain(jpa.save(toEntity(u)));
    }

    @Override
    public UnidadeMedida atualizar(UnidadeMedida u) {
        return toDomain(jpa.save(toEntity(u)));
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public Optional<UnidadeMedida> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<UnidadeMedida> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorSigla(String sigla) {
        return jpa.existsBySiglaIgnoreCase(sigla);
    }

    UnidadeMedidaEntity toEntity(UnidadeMedida u) {
        UnidadeMedidaEntity e = new UnidadeMedidaEntity();
        e.setId(u.getId());
        e.setNome(u.getNome());
        e.setSigla(u.getSigla());
        e.setStatus(u.getStatus());
        e.setDataCriacao(u.getDataCriacao());
        e.setDataUltimaAlteracao(u.getDataUltimaAlteracao());
        return e;
    }

    UnidadeMedida toDomain(UnidadeMedidaEntity e) {
        return new UnidadeMedida(e.getId(), e.getNome(), e.getSigla(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }
}
