package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Optional;

public interface RenovarSessaoUseCase {

    Usuario renovarSessao(String login);

    interface RefreshTokenRepositoryPort {
        Optional<Usuario> buscarPorLogin(String login);
    }
}
