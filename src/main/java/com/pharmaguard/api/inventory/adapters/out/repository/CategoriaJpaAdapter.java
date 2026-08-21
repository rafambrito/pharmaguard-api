package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.CategoriaUseCase.CategoriaRepositoryPort;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(CategoriaJpaRepository.class)
public class CategoriaJpaAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository jpa;

    public CategoriaJpaAdapter(CategoriaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        return toDomain(jpa.save(toEntity(categoria)));
    }

    @Override
    public Categoria atualizar(Categoria categoria) {
        return toDomain(jpa.save(toEntity(categoria)));
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Categoria> listarTodos() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpa.existsByNomeIgnoreCase(nome);
    }

    CategoriaEntity toEntity(Categoria c) {
        CategoriaEntity e = new CategoriaEntity();
        e.setId(c.getId());
        e.setNome(c.getNome());
        e.setDescricao(c.getDescricao());
        e.setStatus(c.getStatus());
        e.setDataCriacao(c.getDataCriacao());
        e.setDataUltimaAlteracao(c.getDataUltimaAlteracao());
        return e;
    }

    Categoria toDomain(CategoriaEntity e) {
        return new Categoria(e.getId(), e.getNome(), e.getDescricao(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }
}
