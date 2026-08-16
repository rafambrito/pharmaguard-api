package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Optional;

public interface AutenticarUsuarioUseCase {

    Usuario autenticar(String loginOuEmail, String senha);

    interface UsuarioRepositoryPort {
        Optional<Usuario> buscarPorLogin(String login);
    }

    interface PasswordEncoderPort {
        String encode(CharSequence rawPassword);

        boolean matches(CharSequence rawPassword, String encodedPassword);
    }
}
