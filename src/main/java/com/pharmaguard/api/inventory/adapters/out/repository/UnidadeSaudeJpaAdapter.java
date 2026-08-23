package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeSaudeEntity;
import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCase.UnidadeSaudeRepositoryPort;
import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(UnidadeSaudeJpaRepository.class)
public class UnidadeSaudeJpaAdapter implements UnidadeSaudeRepositoryPort {
    private final UnidadeSaudeJpaRepository jpa;
    public UnidadeSaudeJpaAdapter(UnidadeSaudeJpaRepository jpa) { this.jpa = jpa; }
    @Override public UnidadeSaude salvar(UnidadeSaude unidade) { return toDomain(jpa.save(toEntity(unidade))); }
    @Override public UnidadeSaude atualizar(UnidadeSaude unidade) { return toDomain(jpa.save(toEntity(unidade))); }
    @Override public Optional<UnidadeSaude> buscarPorId(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<UnidadeSaude> listarTodos() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public boolean existePorIdentificacao(String identificacao) { return jpa.existsByIdentificacaoIgnoreCase(identificacao); }
    private UnidadeSaudeEntity toEntity(UnidadeSaude u) {
        UnidadeSaudeEntity e = new UnidadeSaudeEntity(); e.setId(u.getId()); e.setIdentificacao(u.getIdentificacao());
        e.setNome(u.getNome()); e.setTipo(u.getTipo()); e.setEndereco(u.getEndereco()); e.setStatus(u.getStatus());
        e.setDataCadastro(u.getDataCadastro()); e.setDataAtualizacao(u.getDataAtualizacao()); return e;
    }
    private UnidadeSaude toDomain(UnidadeSaudeEntity e) {
        return new UnidadeSaude(e.getId(), e.getIdentificacao(), e.getNome(), e.getTipo(), e.getEndereco(),
                e.getStatus(), e.getDataCadastro(), e.getDataAtualizacao());
    }
}