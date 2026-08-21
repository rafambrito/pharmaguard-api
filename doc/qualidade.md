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
| T8.0 | Definir a estratégia de testes do MVP e pirâmide de qualidade | documento de estratégia de testes | Planejado |
| T8.1 | Mapear módulos e fluxos críticos para priorização de testes | matriz de riscos e cobertura | Planejado |
| T8.2 | Definir critérios mínimos de qualidade e evidência | checklist de validação | Planejado |

### T8.1 - Testes unitários e de domínio

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.1.1 | Validar regras de domínio do módulo auth | suíte de testes unitários | Planejado |
| T8.1.2 | Validar regras de domínio do módulo inventory | suíte de testes unitários | Planejado |
| T8.1.3 | Validar regras de domínio do módulo supplier | suíte de testes unitários | Planejado |
| T8.1.4 | Validar regras de cálculo e lógica estatística do analytics | suíte de testes unitários | Planejado |

### T8.2 - Casos de uso e integração

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.2.1 | Testar casos de uso de autenticação e usuários | testes de aplicação | Planejado |
| T8.2.2 | Testar casos de uso de estoque, entradas e saídas | testes de aplicação | Planejado |
| T8.2.3 | Testar persistência e integridade de dados | testes de integração | Planejado |
| T8.2.4 | Validar regras FEFO, vencimento e saldo | testes de regra de negócio | Planejado |

### T8.3 - API e contrato

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.3.1 | Definir e validar contratos de endpoints do MVP | suíte de testes de API | Planejado |
| T8.3.2 | Verificar respostas e erros padronizados | validação de contrato e payload | Planejado |
| T8.3.3 | Validar integração entre API e módulos de negócio | testes de endpoint | Planejado |

### T8.4 - Cobertura e regressão

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.4.1 | Definir metas de cobertura por módulo crítico | relatório de cobertura alvo | Planejado |
| T8.4.2 | Executar testes de regressão em fluxos centrais | evidência de regressão | Planejado |
| T8.4.3 | Validar cenários de risco e exceções | suíte de prevenção de regressão | Planejado |

### T8.5 - Observabilidade e diagnósticos

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T8.5.1 | Verificar padrões de logging e correlação | evidência de observabilidade | Planejado |
| T8.5.2 | Validar rastreabilidade de operações críticas | checklist de auditoria | Planejado |
| T8.5.3 | Garantir diagnósticos de falhas na API e no domínio | guia de troubleshooting | Planejado |

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
