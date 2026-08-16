package com.pharmaguard.api.auth.domain;

import java.util.Objects;

public class UsuarioRegraIdentidadeUnica {

    public void validarParaCriacao(Usuario usuario, UsuarioIdentidadeUnicaPort unicidadePort) {
        Objects.requireNonNull(usuario, "usuario e obrigatorio");
        Objects.requireNonNull(unicidadePort, "unicidadePort e obrigatorio");

        if (unicidadePort.existePorEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("email ja cadastrado");
        }

        if (unicidadePort.existePorLogin(usuario.getLogin())) {
            throw new IllegalArgumentException("login ja cadastrado");
        }
    }
}
