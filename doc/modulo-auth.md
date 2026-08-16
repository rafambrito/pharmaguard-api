# Modulo Auth - (Auth / Usuarios)

## 1. Contexto
- O modulo auth faz parte do monolito modular PharmaGuard.
- A estrutura segue Clean Architecture e Arquitetura Hexagonal (ports and adapters).
- Objetivo da etapa: entregar CRUD de usuarios e autenticacao JWT com regras de acesso por perfil/role.
- Escopo desta etapa:
  - API First com contrato OpenAPI
  - CRUD Usuario
  - CRUD Perfil
  - Seguranca com Spring Security
  - JWT
  - Refresh Token (opcional)
  - Roles
  - Auditoria
- Requisito transversal previsto:
  - Criar arquivo global `message.properties` para centralizar mensagens da aplicacao (nao apenas do modulo auth).
  - Definir contrato OpenAPI do modulo auth antes da implementacao dos endpoints.

## 2. Arquitetura do modulo
### 2.1 Camadas
- domain
  - Entidades e regras de negocio puras (Usuario, Perfil, politicas de senha, regras de status e papeis).
- application
  - Casos de uso (criar, atualizar, consultar, autenticar, renovar token, atribuir/remover perfil).
  - Ports de entrada e saida.
- infrastructure
  - Adapters de persistencia (JPA), seguranca (JWT provider, password encoder), auditoria e configuracoes.
- api
  - Endpoints REST, DTOs, validacoes de entrada, mapeamento request/response.

### 2.2 Fluxo principal (resumo)
- API recebe requisicao.
- Application executa caso de uso.
- Domain aplica regras.
- Infrastructure persiste/consulta e integra com mecanismos de seguranca.
- API retorna resposta padronizada.

## 3. Regras
- Usuario deve ter identificacao unica (email/login unico).
- Senha nunca deve ser persistida em texto puro.
- Usuario deve possuir pelo menos um perfil/role ativo para acesso autenticado.
- JWT deve conter subject e claims minimas de autorizacao (roles/perfis).
- Token expirado nao autoriza acesso.
- Refresh token (quando habilitado) deve ter expiracao e mecanismo de revogacao/invalidez.
- Endpoints protegidos devem respeitar autorizacao por role.
- Auditoria deve registrar eventos relevantes de autenticacao/autorizacao e alteracoes de usuario.
- Mensagens de validacao/erro devem ser externalizadas em `message.properties`.

## 4. Tarefas

### T0 - API First (OpenAPI)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.0 | Definir estrategia API First para auth | contrato OpenAPI como fonte de verdade da API | X - Pendente |
| T1.0.1 | Criar arquivo de contrato de usuario | arquivo usuario.yaml com paths e schemas de usuario e auth | X - Pendente |
| T1.0.2 | Validar padrao de respostas e erros | contratos de sucesso e erro alinhados ao modulo | X - Pendente |

### T1 - Criar Entidade Usuario

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.1 | Criar entidade Usuario no dominio | classe Usuario na camada domain de auth | ✅ Concluído|
| T1.1.1 | Definir atributos obrigatorios (nome, email/login, senha hash, status) | modelo de dominio com invariantes basicas | ✅ Concluído |
| T1.1.2 | Definir regras de identidade unica | regra de unicidade aplicada via porta/repositorio | ✅ Concluído |

### T2 - Criar Entidade Perfil

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.2 | Criar entidade Perfil no dominio | classe Perfil na camada domain de auth | ✅ Concluído  |
| T1.2.1 | Definir papeis iniciais | enum/objeto de papeis com regras basicas | ✅ Concluído |
| T1.2.2 | Relacionar Usuario x Perfil | regra de atribuicao e remocao de perfil | ✅ Concluído |

### T3 - Casos de Uso (Application)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.3 | Criar casos de uso de Usuario | servicos de aplicacao para CRUD de usuario | ✅ Concluído |
| T1.3.1 | Criar caso de uso de autenticacao | caso de uso autenticar usuario (login/senha) | ✅ Concluído |
| T1.3.2 | Criar caso de uso de refresh token (opcional) | caso de uso para renovar sessao | ✅ Concluído |

### T4 - Repository / Persistencia

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.4 | Definir portas de repositorio | interfaces de saida no application |  ✅ Concluído  |
| T1.4.1 | Implementar adapters JPA Usuario/Perfil | repositorios JPA na camada infrastructure | ✅ Concluído |
| T1.4.2 | Mapear entidades persistencia | entidades JPA e mapeamentos consistentes | ✅ Concluído |

### T5 - Endpoints

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.5 | Expor endpoints CRUD Usuario | controller/resource com operacoes CRUD | ✅ Concluído  |
| T1.5.1 | Expor endpoint de login | endpoint de autenticacao com retorno JWT | ✅ Concluído|
| T1.5.2 | Expor endpoint de refresh token (opcional) | endpoint de renovacao de token | ✅ Concluído|

### T6 - Validacoes e Mensagens

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.6 | Aplicar validacoes de entrada | DTOs validados e respostas de erro padrao | ✅ Concluído|
| T1.6.1 | Externalizar mensagens no arquivo global | arquivo `message.properties` com mensagens da aplicacao | ✅ Concluído |
| T1.6.2 | Integrar mensagens em validacoes/excecoes | erros com codigos e mensagens centralizadas | ✅ Concluído |

### T7 - Testes

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.7 | Testes unitarios de dominio e application | suite de testes unitarios/mockados | ✅ Concluído |
| T1.7.1 | Testes de integracao da API auth | cenarios CRUD e autenticacao validados | ✅ Concluído  |
| T1.7.2 | Cobertura minima do modulo | relatorio de cobertura do modulo auth | ✅ Concluído |

### T8 - Spring Security

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.8 | Configurar Spring Security | cadeia de seguranca configurada para API | ✅ Concluído |
| T1.8.1 | Definir rotas publicas e protegidas | matriz de acesso por endpoint | ✅ Concluído |

#### Matriz de acesso por endpoint

| Endpoint | Metodo | Acesso |
|----------|--------|--------|
| /swagger-ui.html | GET | Publico |
| /swagger-ui/** | GET | Publico |
| /v3/api-docs/** | GET | Publico |
| /actuator/health | GET | Publico |
| /actuator/info | GET | Publico |
| /api/v1/auth/refresh-token | POST | Publico |
| /api/v1/usuarios | POST | Protegido |
| /api/v1/usuarios | GET | Protegido |
| /api/v1/usuarios/{id} | GET | Protegido |
| /api/v1/usuarios/{id} | PUT | Protegido |
| /api/v1/usuarios/{id} | DELETE | Protegido |

### T9 - JWT

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.9 | Implementar emissao e validacao de JWT | provider/servico JWT funcionando | ✅ Concluído |
| T1.9.1 | Incluir claims de autorizacao | tokens com roles/perfis necessarios |✅ Concluído |

### T10 - Refresh Token (opcional)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.10 | Implementar fluxo de refresh token | renovacao segura de acesso | ✅ Concluído |
| T1.10.1 | Definir politica de expiracao/revogacao | regras de ciclo de vida do refresh token | ✅ Concluído |

#### Politica de ciclo de vida do refresh token

- Expiracao por tempo configuravel via `security.jwt.refresh-token-validity-seconds`.
- Rotacao obrigatoria: ao renovar sessao, novo refresh token e emitido.
- Revogacao imediata do refresh token utilizado no endpoint de renovacao.
- Bloqueio de reuso: token revogado nao pode renovar sessao novamente.
- Token ativo unico por usuario (apenas o ultimo refresh token emitido permanece valido).

### T11 - Roles

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.11 | Aplicar autorizacao por roles | controle de acesso por perfil em endpoints | ✅ Concluído |
| T1.11.1 | Cobrir cenarios de permissao | testes de autorizacao por role | ✅ Concluído |

### T12 - Auditoria

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.12 | Implementar auditoria de auth/usuarios | trilha de auditoria para eventos criticos | ✅ Concluído  |
| T1.12.1 | Definir eventos auditaveis | log estruturado de login, falha, alteracao de perfil/usuario | ✅ Concluído  |

### T13 - Governanca Arquitetural (ArchUnit)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T1.13 | Criar suite ArchUnit para regras modulares | testes ArchUnit cobrindo fronteiras entre modulos | ✅ Concluído  |
| T1.13.1 | Definir regras de dependencia entre camadas | regras impedindo violacao de Clean Architecture (api -> application -> domain) | ✅ Concluído |
| T1.13.2 | Definir regras de acesso entre modulos | regras impedindo acesso direto indevido entre modulos (usar contratos/ports) | ✅ Concluído |
| T1.13.3 | Definir regras para adapters e frameworks | dominio sem dependencia de frameworks e adapters restritos a infrastructure | ✅ Concluído  |
| T1.13.4 | Publicar guia de boas praticas arquiteturais | documento com convencoes para futuras implementacoes respeitarem a arquitetura | ✅ Concluído  |
| T1.13.5 | Integrar validacao arquitetural no pipeline | execucao automatica de ArchUnit em build/test do projeto | ✅ Concluído  |

## 5. Criterios de Aceite

| Status | Criterio |
|--------|----------|
| ✅ Concluído | Modulo compilando |
| X - Pendente | Documentacao Swagger |
| ✅ Concluído | Contrato OpenAPI do modulo auth definido em usuario.yaml |
| X - Pendente | Testes unitarios/mockados |
| X - Pendente | Testes funcionais (CRUD funcionando) |
| X - Pendente | Autenticacao JWT funcionando |
| ✅ Concluído | Mensagens centralizadas em `message.properties` |
| X - Pendente | Autorizacao por roles aplicada em endpoints protegidos |
| X - Pendente | Auditoria de eventos de autenticacao e alteracao de usuarios |
| X - Pendente | Regras ArchUnit validadas para dependencias entre camadas e entre modulos |
| X - Pendente | Guia de boas praticas arquiteturais publicado para futuras implementacoes |
