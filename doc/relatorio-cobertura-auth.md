# Relatorio de Cobertura - Modulo Auth

Data: 2026-08-15

Escopo de testes executados:
- src/test/java/com/pharmaguard/api/auth/application/AutenticarUsuarioUseCaseImplTest.java
- src/test/java/com/pharmaguard/api/auth/application/RenovarSessaoUseCaseImplTest.java
- src/test/java/com/pharmaguard/api/auth/application/UsuarioUseCaseImplTest.java
- src/test/java/com/pharmaguard/api/auth/domain/UsuarioTest.java
- src/test/java/com/pharmaguard/api/auth/api/integration/UsuarioControllerIntegrationTest.java
- src/test/java/com/pharmaguard/api/auth/api/integration/AuthControllerIntegrationTest.java

Resultado dos testes:
- 46 passed
- 0 failed

Cobertura agregada do modulo auth (statements):
- 213/283
- 75.3%

Cobertura por arquivo:

| Arquivo | Cobertura |
|---|---:|
| src/main/java/com/pharmaguard/api/auth/application/UsuarioUseCaseImpl.java | 92.7% |
| src/main/java/com/pharmaguard/api/auth/application/RenovarSessaoUseCaseImpl.java | 92.9% |
| src/main/java/com/pharmaguard/api/auth/application/AutenticarUsuarioUseCaseImpl.java | 92.3% |
| src/main/java/com/pharmaguard/api/auth/infrastructure/repository/InMemoryAuthRepositoryAdapter.java | 76.4% |
| src/main/java/com/pharmaguard/api/auth/infrastructure/config/UseCaseConfig.java | 0.0% |
| src/main/java/com/pharmaguard/api/auth/domain/UsuarioRegraIdentidadeUnica.java | 100.0% |
| src/main/java/com/pharmaguard/api/auth/domain/Perfil.java | 32.4% |
| src/main/java/com/pharmaguard/api/auth/domain/Usuario.java | 80.2% |
| src/main/java/com/pharmaguard/api/auth/api/mapper/UsuarioApiMapper.java | 85.7% |
| src/main/java/com/pharmaguard/api/auth/api/controller/UsuarioController.java | 93.5% |
| src/main/java/com/pharmaguard/api/auth/api/controller/AuthController.java | 100.0% |
| src/main/java/com/pharmaguard/api/auth/api/dto/response/PerfilResponse.java | 0.0% |
| src/main/java/com/pharmaguard/api/auth/api/dto/response/TokenResponse.java | 100.0% |
| src/main/java/com/pharmaguard/api/auth/api/dto/response/UsuarioResponse.java | 100.0% |
| src/main/java/com/pharmaguard/api/auth/api/dto/request/CriarUsuarioRequest.java | 100.0% |
| src/main/java/com/pharmaguard/api/auth/api/dto/request/AtualizarUsuarioRequest.java | 100.0% |
| src/main/java/com/pharmaguard/api/auth/api/dto/request/RenovarSessaoRequest.java | 100.0% |
