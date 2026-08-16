package com.pharmaguard.api.auth.application;

public interface DefinirSenhaUseCase {

    void definirSenha(String login, String senha, String confirmarSenha);
}