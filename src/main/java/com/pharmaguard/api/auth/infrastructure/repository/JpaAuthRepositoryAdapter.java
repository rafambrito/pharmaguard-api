package com.pharmaguard.api.auth.infrastructure.repository;

import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCase;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCase;
import com.pharmaguard.api.auth.application.UsuarioUseCase;
import com.pharmaguard.api.auth.domain.Perfil;
import com.pharmaguard.api.auth.domain.Usuario;
import com.pharmaguard.api.auth.infrastructure.repository.entity.UsuarioEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(UsuarioJpaRepository.class)
public class JpaAuthRepositoryAdapter implements UsuarioUseCase.UsuarioRepositoryPort,
        AutenticarUsuarioUseCase.UsuarioRepositoryPort,
        RenovarSessaoUseCase.RefreshTokenRepositoryPort {

    private final UsuarioJpaRepository repository;

    public JpaAuthRepositoryAdapter(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Usuario> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return repository.findByLogin(login).map(this::toDomain);
    }

    private UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNome(usuario.getNome());
        entity.setEmail(usuario.getEmail());
        entity.setLogin(usuario.getLogin());
        entity.setTipo(usuario.getTipo());
        entity.setPerfil(usuario.getPerfis().isEmpty() ? usuario.getTipo() : usuario.getPerfis().get(0).getNome());
        entity.setSenhaHash(usuario.getSenhaHash());
        entity.setStatus(usuario.getStatus());
        entity.setDataCriacao(usuario.getDataCriacao());
        entity.setDataUltimaAlteracao(usuario.getDataUltimaAlteracao());
        return entity;
    }

    private Usuario toDomain(UsuarioEntity entity) {
        Usuario usuario = new Usuario(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getLogin(),
                entity.getTipo(),
                entity.getSenhaHash(),
                entity.getStatus(),
                entity.getDataCriacao(),
                entity.getDataUltimaAlteracao());

        String role = entity.getPerfil() == null || entity.getPerfil().isBlank()
            ? entity.getTipo()
            : entity.getPerfil();
        Perfil perfil = new Perfil();
        perfil.setNome(role);
        perfil.setDescricao("role " + role);
        perfil.setAtivo(true);
        usuario.adicionarPerfil(perfil);
        return usuario;
    }
}