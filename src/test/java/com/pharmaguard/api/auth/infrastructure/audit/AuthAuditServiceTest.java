package com.pharmaguard.api.auth.infrastructure.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pharmaguard.api.auth.application.AuthAuditService;
import org.junit.jupiter.api.Test;

class AuthAuditServiceTest {

    @Test
    void shouldExposeProfileChangeEvent() {
        RecordingAuditService auditService = new RecordingAuditService();

        auditService.registrarPerfilAtualizado("ana", 7L, "perfil_admin");

        assertEquals("perfil_atualizado", auditService.lastEvent);
        assertEquals("ana", auditService.lastLogin);
    }

    private static final class RecordingAuditService implements AuthAuditService {
        private String lastEvent;
        private String lastLogin;

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
            this.lastEvent = "perfil_atualizado";
            this.lastLogin = login;
        }
    }
}
