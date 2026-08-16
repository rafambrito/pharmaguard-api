package com.pharmaguard.api.auth.infrastructure.config;

import com.pharmaguard.api.auth.application.AuthAuditService;
import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCase;
import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCaseImpl;
import com.pharmaguard.api.auth.application.DefinirSenhaUseCase;
import com.pharmaguard.api.auth.application.DefinirSenhaUseCaseImpl;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCase;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCaseImpl;
import com.pharmaguard.api.auth.application.UsuarioUseCase;
import com.pharmaguard.api.auth.application.UsuarioUseCaseImpl;
import com.pharmaguard.api.auth.infrastructure.audit.Slf4jAuthAuditService;
import com.pharmaguard.api.auth.infrastructure.repository.InMemoryAuthRepositoryAdapter;
import com.pharmaguard.api.auth.infrastructure.repository.UsuarioJpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class UseCaseConfig {

    @Bean
    @ConditionalOnMissingBean(UsuarioJpaRepository.class)
    public InMemoryAuthRepositoryAdapter inMemoryAuthRepositoryAdapter() {
        return new InMemoryAuthRepositoryAdapter();
    }

    @Bean
    public AuthAuditService authAuditService() {
        return new Slf4jAuthAuditService();
    }

    @Bean
    public AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoderPort() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new AutenticarUsuarioUseCase.PasswordEncoderPort() {
            @Override
            public String encode(CharSequence rawPassword) {
                return encoder.encode(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword != null && encoder.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Bean
    public UsuarioUseCase usuarioUseCase(
            UsuarioUseCase.UsuarioRepositoryPort repository,
            AuthAuditService authAuditService,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder) {
        return new UsuarioUseCaseImpl(repository, authAuditService, passwordEncoder);
    }

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(
            UsuarioUseCase.UsuarioRepositoryPort usuarioRepository,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder,
            AuthAuditService authAuditService) {
        return new AutenticarUsuarioUseCaseImpl(usuarioRepository::buscarPorLogin, passwordEncoder, authAuditService);
    }

    @Bean
    public DefinirSenhaUseCase definirSenhaUseCase(
            UsuarioUseCase.UsuarioRepositoryPort usuarioRepository,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder) {
        return new DefinirSenhaUseCaseImpl(usuarioRepository, passwordEncoder);
    }

    @Bean
    public RenovarSessaoUseCase renovarSessaoUseCase(
            UsuarioUseCase.UsuarioRepositoryPort usuarioRepository) {
        return new RenovarSessaoUseCaseImpl(usuarioRepository::buscarPorLogin);
    }
}
