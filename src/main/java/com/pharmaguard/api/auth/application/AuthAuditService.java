package com.pharmaguard.api.auth.application;

public interface AuthAuditService {

    void registrarLoginSucesso(String login, String email);

    void registrarLoginFalha(String login, String motivo);

    void registrarUsuarioCriado(String login, Long usuarioId);

    void registrarUsuarioAtualizado(String login, Long usuarioId, String detalhe);

    void registrarUsuarioRemovido(String login, Long usuarioId);

    void registrarPerfilAtualizado(String login, Long usuarioId, String perfil);

    static AuthAuditService noop() {
        return new NoopAuthAuditService();
    }

    final class NoopAuthAuditService implements AuthAuditService {
        @Override
        public void registrarLoginSucesso(String login, String email) {
        }

        @Override
        public void registrarLoginFalha(String login, String motivo) {
        }

        @Override
        public void registrarUsuarioCriado(String login, Long usuarioId) {
        }

        @Override
        public void registrarUsuarioAtualizado(String login, Long usuarioId, String detalhe) {
        }

        @Override
        public void registrarUsuarioRemovido(String login, Long usuarioId) {
        }

        @Override
        public void registrarPerfilAtualizado(String login, Long usuarioId, String perfil) {
        }
    }
}
