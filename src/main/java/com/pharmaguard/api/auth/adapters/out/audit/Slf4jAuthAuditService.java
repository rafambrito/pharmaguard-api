package com.pharmaguard.api.auth.adapters.out.audit;

import com.pharmaguard.api.auth.application.AuthAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jAuthAuditService implements AuthAuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jAuthAuditService.class);

    @Override
    public void registrarLoginSucesso(String login, String email) {
        LOGGER.info("event=auth_login_success login={} email={} status=success", login, email);
    }

    @Override
    public void registrarLoginFalha(String login, String motivo) {
        LOGGER.warn("event=auth_login_failure login={} motivo={}", login, motivo);
    }

    @Override
    public void registrarUsuarioCriado(String login, Long usuarioId) {
        LOGGER.info("event=usuario_criado login={} usuarioId={}", login, usuarioId);
    }

    @Override
    public void registrarUsuarioAtualizado(String login, Long usuarioId, String detalhe) {
        LOGGER.info("event=usuario_atualizado login={} usuarioId={} detalhe={}", login, usuarioId, detalhe);
    }

    @Override
    public void registrarUsuarioRemovido(String login, Long usuarioId) {
        LOGGER.warn("event=usuario_removido login={} usuarioId={}", login, usuarioId);
    }

    @Override
    public void registrarPerfilAtualizado(String login, Long usuarioId, String perfil) {
        LOGGER.info("event=perfil_atualizado login={} usuarioId={} perfil={}", login, usuarioId, perfil);
    }
}
