package com.pharmaguard.api.auth.adapters.in.mapper;

import com.pharmaguard.api.auth.adapters.in.dto.request.AtualizarUsuarioRequest;
import com.pharmaguard.api.auth.adapters.in.dto.request.CriarUsuarioRequest;
import com.pharmaguard.api.auth.adapters.in.dto.response.PerfilResponse;
import com.pharmaguard.api.auth.adapters.in.dto.response.UsuarioResponse;
import com.pharmaguard.api.auth.domain.Perfil;
import com.pharmaguard.api.auth.domain.Usuario;
import com.pharmaguard.api.shared.config.MessageKeys;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAdapterInMapper {

    public Usuario toDomain(CriarUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setLogin(request.login());
        usuario.setTipo(request.tipo());
        usuario.adicionarPerfil(criarPerfilPrincipal(usuario.getTipo()));
        usuario.setSenhaHash(request.senha());
        usuario.setStatus(parseStatus(request.status(), Usuario.Status.ATIVO));
        usuario.marcarCriacao();
        return usuario;
    }

    public void applyToDomain(AtualizarUsuarioRequest request, Usuario usuario) {
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setLogin(request.login());
        usuario.setStatus(parseStatus(request.status(), usuario.getStatus()));
        usuario.marcarAtualizacao();
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        List<PerfilResponse> perfis = usuario.getPerfis().stream()
                .map(perfil -> new PerfilResponse(perfil.getId(), perfil.getNome(), perfil.getDescricao(), perfil.isAtivo()))
                .toList();

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin(),
                usuario.getTipo(),
                usuario.getStatus().name(),
                perfis,
                usuario.getDataCriacao(),
                usuario.getDataUltimaAlteracao());
    }

    private Usuario.Status parseStatus(String status, Usuario.Status defaultStatus) {
        if (status == null || status.isBlank()) {
            return defaultStatus;
        }

        try {
            return Usuario.Status.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(MessageKeys.MSG_VALIDACAO_STATUS_INVALIDO);
        }
    }

    private Perfil criarPerfilPrincipal(String tipo) {
        String role = tipo.trim().toUpperCase();
        Perfil perfil = new Perfil();
        perfil.setNome(role);
        perfil.setDescricao("role " + role);
        perfil.setAtivo(true);
        return perfil;
    }
}
