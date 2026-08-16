package com.pharmaguard.api.auth.domain;

public interface UsuarioIdentidadeUnicaPort {

    boolean existePorEmail(String email);

    boolean existePorLogin(String login);
}
