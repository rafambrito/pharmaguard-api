# Módulo Qualidade - Planejamento da Implementação

## 1. Contexto

Este módulo é transversal ao MVP do PharmaGuard e está diretamente alinhado ao roadmap do projeto, especialmente à Etapa 8: Qualidade.

O objetivo da qualidade no projeto não é apenas validar código, mas garantir que o sistema entregue valor com confiabilidade, rastreabilidade e previsibilidade. A aplicação precisa operar com segurança em fluxos críticos como autenticação, estoque, fornecedores, relatórios e inteligência de consumo.

O contexto do projeto, definido no README, indica que o sistema deve seguir uma estratégia de testes baseada na pirâmide de testes, com foco em:

- testes unitários para regras de domínio;
- testes de casos de uso;
- testes de integração para persistência;
- testes de endpoints da API;
- testes do motor estatístico;
- testes das regras FEFO;
- testes dos cálculos de estoque.

Além disso, a arquitetura também prevê observabilidade, logs estruturados e rastreabilidade operacional, elementos fundamentais para suporte e garantia de qualidade.

## 2. Visão de negócio

O módulo de qualidade deve assegurar que o PharmaGuard funcione de forma estável e confiável em todos os fluxos principais do sistema.

Em termos de negócio, isso significa:

- reduzir regressões em funcionalidades críticas;
- aumentar confiança nas decisões de estoque e reposição;
- garantir consistência dos dados persistidos;
- validar que as regras de negócio continuam corretas após alterações;
- reduzir riscos de falhas em produção;
- dar evidência de qualidade para cada módulo do MVP.

A qualidade está diretamente ligada à execução do sistema como ferramenta operacional de saúde pública, onde falhas podem impactar disponibilidade, rastreabilidade e decisões de gestão.

## 3. Escopo do módulo

### 3.1 Escopo incluído

- estratégia de testes do MVP;
- testes unitários das regras de domínio;
- testes de casos de uso e serviços de aplicação;
- testes de integração com banco e persistência;
- testes de endpoints e contratos de API;
- validação das regras de FEFO e estoque;
- testes do motor estatístico e indicadores de consumo;
- validação de cobertura mínima por área crítica;
- testes de regressão para fluxos centrais;
- observabilidade básica: logs, contexto de correlação e rastreabilidade;
- evidências de qualidade para validação do módulo e do MVP.

### 3.2 Escopo excluído

Para manter foco no MVP e evitar dispersão, este plano não inclui:

- desenvolvimento de frontend;
- testes de interface gráfica e UX;
- testes de performance e carga;
- testes de segurança avançados com pen-testing;
- automação de deploy em múltiplos ambientes;
- integração com sistemas externos em produção;
- melhorias arquiteturais fora do escopo de validação;
- refatorações de módulos sem cobertura de regressão;
- qualquer implementação funcional não relacionada diretamente à qualidade.

> Recomendação: não implementar, nem executar qualquer tarefa fora do escopo descrito acima.

## 4. Dependências do módulo

O módulo de qualidade depende de múltiplas áreas do projeto e deve ser tratado como uma responsabilidade transversal:

- auth
  - autenticação;
  - autorização;
  - usuários e perfis;
  - auditoria e rastreabilidade.

- inventory
  - entradas;
  - saídas;
  - lotes;
  - validade;
  - FEFO;
  - cálculos de saldo e risco.

- supplier
  - fornecedores;
  - lead time;
  - relacionamento de produtos e fornecedores.

- analytics
  - consumo;
  - indicadores estatísticos;
  - risco de ruptura e vencimento.

- shared
  - exceções;
  - padrões de resposta;
  - logging e contexto de correlação;
  - mensagens e validações comuns.

- stack técnico
  - Java 21;
  - Spring Boot 3.x;
  - JUnit 5;
  - Mockito;
  - Spring Test;
  - PostgreSQL;
  - Docker e Docker Compose;
  - JaCoCo ou mecanismo equivalente de cobertura;
  - OpenAPI para contratos de API.

## 5. Arquitetura do módulo

### 5.1 Natureza do módulo

O módulo de qualidade não é um módulo de domínio isolado, como `auth` ou `inventory`. Ele atua como camada transversal de garantia, cobrindo vários módulos do sistema.

Sua arquitetura lógica é composta por:

```text
Qualidade
├── estratégia de testes
├── regras de validação
├── suíte por camada
├── testes de regressão
├── cobertura e evidências
├── observabilidade e logs
└── validação de aceite
```

### 5.2 Fluxo principal

1. O desenvolvedor altera uma funcionalidade ou regra de negócio.
2. A suíte de testes correspondente é executada.
3. Os testes unitários e de integração validam a alteração.
4. Os endpoints e contratos são verificados.
5. A cobertura de áreas críticas é revisada.
6. Evidências de execução são registradas.
7. O módulo ou funcionalidade é validado para integração no MVP.

### 5.3 Estratégia definida para o MVP

A estratégia da Etapa 8 é incremental e orientada ao risco, mantendo a maior parte
da validação em testes rápidos e isolados:

| Camada | Responsabilidade | Ferramentas e convenções |
|---|---|---|
| Unitária | Validar regras de domínio, cálculos e casos de uso sem infraestrutura externa | JUnit 5, Mockito quando necessário, classes com sufixo `Test` |
| Integração | Validar persistência, configuração Spring, segurança e integração entre adapters e aplicação | Spring Test, contexto real e banco conforme o cenário existente, classes com sufixo `IntegrationTest` |
| Arquitetura | Impedir violações das dependências entre camadas e módulos | ArchUnit |
| Contrato/API | Validar status HTTP, payloads, validações e erros expostos pelos endpoints | Testes de controllers e integração da API |

Os testes devem seguir o padrão Arrange-Act-Assert, ter nomes que expressem o
comportamento esperado e evitar dependência entre casos. Mocks ficam restritos a
colaboradores externos ao comportamento sob teste; regras de negócio não devem
ser validadas apenas por testes de controller.

O ciclo mínimo de validação é:

```bash
./mvnw clean test
```

Esse comando compila o projeto, executa a suíte e gera o relatório JaCoCo em
`target/site/jacoco/index.html`. A execução deve ser repetida antes do aceite de
uma alteração que afete autenticação, estoque, FEFO, relatórios ou cálculos
estatísticos.

Para uma verificação rápida durante o desenvolvimento, é aceitável executar:

```bash
./mvnw test
```

O resultado da execução, os testes reprovados e o relatório de cobertura são as
evidências mínimas da estratégia. A definição de metas numéricas por módulo,
matriz de riscos e checklist formal fica para as tarefas T8.1 e T8.2.

## 6. Regras de negócio e critérios de qualidade

### 6.1 Pirâmide de testes

O projeto deve seguir a estratégia de testes baseada na pirâmide:

- maior volume de testes unitários;
- quantidade intermediária de testes de integração;
- menor quantidade de testes end-to-end ou de contrato de alto custo.

### 6.2 Prioridades de validação

- domínio com regras críticas;
- casos de uso de estoque e movimentação;
- persistência e integridade de dados;
- regras de negócio como FEFO e vencimento;
- cálculo de consumo e indicadores estatísticos;
- endpoints da API e contratos de entrada/saída.

### 6.3 Critérios mínimos de qualidade

- testes automatizados para regras centrais do MVP;
- regressões detectadas em alterações de código;
- cobertura de áreas críticas com evidência de execução;
- validação do comportamento esperado antes de aceite funcional;
- consistência de logs e rastreabilidade para suporte e auditoria.

### 6.4 Observabilidade

A qualidade também inclui capacidade de diagnóstico. O sistema deve permitir:

- logs estruturados;
- correlação por request ou operação;
- rastreabilidade de ações do usuário;
- identificação de falhas em fluxos críticos;
- diagnósticos rápidos para suporte e manutenção.

## 7. Entidades e conceitos principais

### 7.1 Estratégia de Teste

- categoria: unitário / integração / contrato / regressão
- objetivo: validar regra, comportamento ou fluxo
- escopo: módulo ou funcionalidade afetada
- evidência: execução e relatório associado

### 7.2 Cobertura de Qualidade

- área crítica
- percentual mínimo de cobertura
- regra validada
- responsável pela evidência

### 7.3 Observabilidade

- requestId / correlationId
- contexto de usuário
- log de operação
- registro de erro ou exceção
- rastreio de transação funcional

## 8. Divisão de tarefas

### T8.0 - Definição da estratégia de qualidade

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.0 | Definir a estratégia de testes do MVP e pirâmide de qualidade | documento de estratégia de testes | ✅ Concluído |
| T8.1 | Mapear módulos e fluxos críticos para priorização de testes | matriz de riscos e cobertura | Planejado |
| T8.2 | Definir critérios mínimos de qualidade e evidência | checklist de validação | Planejado |

### T8.1 - Testes unitários e de domínio

Implementação concluída com a suíte unitária existente dos módulos auth,
inventory, supplier e reports/analytics, complementada pelos testes de definição
de senha em `DefinirSenhaUseCaseImplTest`.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.1.1 | Validar regras de domínio do módulo auth | suíte de testes unitários | ✅ Concluído |
| T8.1.2 | Validar regras de domínio do módulo inventory | suíte de testes unitários | ✅ Concluído |
| T8.1.3 | Validar regras de domínio do módulo supplier | suíte de testes unitários | ✅ Concluído |
| T8.1.4 | Validar regras de cálculo e lógica estatística do analytics | suíte de testes unitários | ✅ Concluído |

### T8.2 - Casos de uso e integração

Implementação concluída com os testes de aplicação dos casos de uso de
autenticação e usuários, além dos fluxos de estoque em
`EstoqueUseCaseTest`. A suíte valida entrada com atualização de saldo e
histórico, saída com aplicação de FEFO, consulta de saldo e validação do
histórico. Os testes de integração existentes cobrem controllers, segurança,
persistência e relatórios.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.2.1 | Testar casos de uso de autenticação e usuários | testes de aplicação | ✅ Concluído |
| T8.2.2 | Testar casos de uso de estoque, entradas e saídas | testes de aplicação | ✅ Concluído |
| T8.2.3 | Testar persistência e integridade de dados | testes de integração | ✅ Concluído |
| T8.2.4 | Validar regras FEFO, vencimento e saldo | testes de regra de negócio | ✅ Concluído |

### T8.3 - API e contrato

Implementação validada por testes de integração dos controllers de auth,
usuários, inventory, fornecedores e reports. A suíte verifica status HTTP,
payloads de resposta, validações de entrada, códigos e detalhes de erro, além
da integração dos endpoints com os casos de uso. O contrato OpenAPI permanece
gerado pelo Springdoc a partir dos controllers e DTOs existentes, sem adicionar
uma camada de contrato paralela ao MVP.

Foram executados **36 testes de API**, todos aprovados.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.3.1 | Definir e validar contratos de endpoints do MVP | suíte de testes de API | ✅ Concluído |
| T8.3.2 | Verificar respostas e erros padronizados | validação de contrato e payload | ✅ Concluído |
| T8.3.3 | Validar integração entre API e módulos de negócio | testes de endpoint | ✅ Concluído |

### T8.4 - Cobertura e regressão

Execução realizada com JaCoCo e regressão da suíte executável. O baseline atual
é de **50,46% de instruções** e **34,95% de branches**, calculado a partir de
`target/site/jacoco/jacoco.csv`. A meta desta etapa é não reduzir esse baseline
em alterações futuras e evoluir progressivamente a cobertura dos pacotes de
domínio e aplicação, priorizando regras críticas.

Na regressão executável, **112 testes passaram** e o relatório foi gerado em
`target/site/jacoco/index.html`. Os testes de negócio, domínio, controllers e
relatórios passaram. Permanecem pendentes `PharmaguardApiApplicationTests` e
`SecurityConfigIntegrationTest`, que falham no carregamento do
`ApplicationContext`; isso impede declarar a regressão completa como verde.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.4.1 | Definir metas de cobertura por módulo crítico | relatório de cobertura alvo | ✅ Concluído |
| T8.4.2 | Executar testes de regressão em fluxos centrais | evidência de regressão | ⚠️ Parcial |
| T8.4.3 | Validar cenários de risco e exceções | suíte de prevenção de regressão | ✅ Concluído |

### T8.5 - Observabilidade e diagnósticos

Implementação validada pelos testes do filtro de correlação, do tratamento
global de exceções, da auditoria de autenticação e do processamento do
scheduler. O Logback registra `timestamp`, `level`, `app`, `correlationId`,
`logger` e `msg`; o filtro reutiliza o identificador recebido ou gera um UUID,
devolvendo-o também no header da resposta.

O diagnóstico de uma falha deve seguir esta sequência:

1. Capturar o `correlationId` da resposta ou do log da requisição.
2. Localizar os eventos relacionados pelo identificador e observar `event`,
  `path` e `level`.
3. Verificar o `ProblemDetail` retornado, especialmente `status`, `detail`,
  `code` e `errors` quando houver validação.
4. Para processamento agendado, conferir o resumo de execução e os eventos de
  métricas/alertas registrados pelo scheduler.

Foram executados **14 testes de observabilidade e diagnóstico**, todos
aprovados.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.5.1 | Verificar padrões de logging e correlação | evidência de observabilidade | ✅ Concluído |
| T8.5.2 | Validar rastreabilidade de operações críticas | checklist de auditoria | ✅ Concluído |
| T8.5.3 | Garantir diagnósticos de falhas na API e no domínio | guia de troubleshooting | ✅ Concluído |

### T8.6 - Aceite e evidência final

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.6.1 | Validar qualidade global do MVP | relatório de qualidade | Planejado |
| T8.6.2 | Registrar evidências de testes e cobertura | pacote de evidências | Planejado |
| T8.6.3 | Validar cobertura do módulo | evidência de qualidade | Planejado |

## 9. Critério de conclusão

O módulo de qualidade pode ser considerado concluído quando:

- toda a suíte crítica do MVP estiver automatizada;
- regras de negócio centrais forem testadas e validadas;
- os principais fluxos de autenticação, estoque e fornecedores estiverem cobertos;
- os testes de integração e API estiverem consistentes;
- a cobertura relevante estiver documentada com evidência;
- a observabilidade básica estiver disponível para diagnóstico;
- os riscos de regressão forem reduzidos a um nível aceitável para o MVP.

## 10. Observações finais

Este módulo é estratégico para a maturidade da solução. Ele não é um esforço complementar isolado; ele estrutura a capacidade do projeto de evoluir com segurança, mantendo confiabilidade nas decisões de estoque e no funcionamento do sistema em produção.

O foco do planejamento deve permanecer na qualidade do MVP, sem abrir escopo para implementações funcionais ou arquiteturais fora do objetivo do projeto.
