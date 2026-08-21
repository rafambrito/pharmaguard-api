# Etapa 9 - Resiliência

## 1. Contexto do módulo

Esta etapa faz parte do MVP do PharmaGuard e está alinhada ao roadmap arquitetural descrito no README do projeto e no roadmap detalhado da solução.

A Etapa 9 é uma preocupação transversal da plataforma, e não um módulo de negócio isolado. Ela reforça a capacidade do sistema de continuar estável e previsível quando há falhas transitórias, lentidão de dependências, pressão de carga ou indisponibilidade parcial de integrações.

No contexto do projeto, a resiliência deve ser tratada na camada adapters.out e compartilhamento do sistema, especialmente:

- `shared` (padrões de infraestrutura, erros, segurança e observabilidade)
- `inventory` (movimentações, validação de estoque e processos intensivos)
- `auth` (fluxos de autenticação e sessões)
- `supplier` (integrações e cadastros externos/internos)
- `scheduler` e `analytics` (execução periódica e processamento de dados)

A ideia central é garantir que a API continue funcionando em condições adversas, sem cair em falhas amplas ou comportamento instável para o usuário e para os operadores do sistema.

## 2. Visão de negócio

A resiliência do PharmaGuard deve apoiar a operação contínua do sistema em ambientes com:

- dependências lentas ou instáveis;
- picos de demanda em consultas e movimentações;
- falhas temporárias em integrações internas ou externas;
- processos recorrentes que exigem tolerância a erros;
- necessidade de observabilidade operacional para diagnóstico rápido.

O objetivo não é criar um novo domínio de negócio, mas garantir que o sistema seja robusto, monitorável e tolerante a falhas, preservando o funcionamento essencial do MVP.

## 3. Escopo da etapa

### 3.1 Incluído

- retry de operações transitórias;
- circuit breaker para dependências instáveis;
- timeouts para chamadas lentas;
- bulkhead quando houver isolamento de recursos ou pools de execução;
- health checks do sistema e dependências;
- métricas com Micrometer;
- observabilidade de falhas, latência e status operacional;
- testes de robustez e comportamento em condições de falha.

### 3.2 Excluído

Para manter o foco do MVP e evitar expansão de escopo, esta etapa não inclui:

- novas regras de negócio para módulos de domínio;
- refatorações extensivas de funcionalidade já implementada;
- múltiplos canais de notificação externos;
- integrações não previstas no escopo do projeto;
- desenvolvimento de frontend;
- mudanças de contratos de API fora do necessário para resiliência;
- execução de tarefas fora do objetivo desta etapa.

> Recomendação: não implementar, nem executar qualquer tarefa que não seja estritamente do módulo de resiliência e observabilidade.

## 4. Arquitetura da resiliência

A camada de resiliência deve ser tratada como infraestrutura transversal, mantendo a separação de responsabilidades do projeto.

### Estrutura sugerida

```text
shared/
├── adapters.out/
│   ├── resilience/
│   │   ├── config/
│   │   ├── retry/
│   │   ├── circuitbreaker/
│   │   ├── timeout/
│   │   ├── bulkhead/
│   │   ├── health/
│   │   └── metrics/
│   ├── observability/
│   └── logging/
```

### Princípios esperados

- `adapters.in` não deve lidar diretamente com lógica de tolerância a falhas;
- `application` deve continuar focada em casos de uso do negócio;
- `shared/adapters.out` deve encapsular comportamento resiliente;
- padrões de resiliência devem ser configuráveis e observáveis;
- falhas temporárias devem ser tratadas sem quebrar a experiência do usuário;
- a aplicação deve expor indicadores claros para operação e manutenção.

## 5. Dependências esperadas

A Etapa 9 depende diretamente da estrutura já estabelecida do sistema, especialmente:

- `shared` para infraestrutura comum e observabilidade;
- `inventory` para cenários de alta carga e processamento repetitivo;
- `scheduler` para tarefas periódicas e execução automática;
- `analytics` para fluxos que podem sofrer lentidão ou falha em processamento;
- `auth` para garantir serviço estável e segura autenticação/autorização;
- `database`/persistência para medir impactos de latência e indisponibilidade parcial;
- `spring-boot-starter-actuator` e Micrometer como base de health check e métricas.

## 6. Comportamento esperado

A aplicação deve operar com comportamento resiliente, ou seja:

```text
Requisição / processo
   │
   ▼
Validação de dependência
   │
   ├── retry se falha transitória
   ├── timeout se operação estiver lenta
   ├── circuit breaker se dependência falhar repetidamente
   ├── bulkhead se houver pressão excessiva
   └── métricas + logs + health status
```

A execução deve:

- repetir operações transientes de forma controlada;
- evitar cascata de falhas em dependências críticas;
- interromper chamadas repetitivas quando o alvo está indisponível;
- preservar recursos e evitar sobrecarga do sistema;
- permitir diagnóstico por logs, health checks e métricas;
- manter a aplicação em estado operacional ainda que com degradação parcial.

## 7. Divisão de tarefas

### T0 - Definição do escopo e arquitetura da resiliência

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.0 | Definir escopo da Etapa 9 no MVP | documentação e limites de responsabilidade | Planejado |
| T9.0.1 | Alinhar a resiliência ao roadmap do projeto | referência clara ao posicionamento entre qualidade e MVP | Planejado |
| T9.0.2 | Validar dependências críticas do sistema | mapa de integração e pontos de risco | Planejado |

### T1 - Retry e tolerância a falhas transitórias

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.1 | Definir política de retry | mecanismo controlado para falhas temporárias | Planejado |
| T9.1.1 | Definir critérios de retry por tipo de operação | regras para chamadas críticas e não críticas | Planejado |
| T9.1.2 | Definir limitação de tentativas e backoff | estratégia de repetição segura | Planejado |

### T2 - Circuit Breaker

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.2 | Definir proteção por circuit breaker | quebra de cascata em dependências falhando | Planejado |
| T9.2.1 | Definir thresholds de falha | critérios para abrir e fechar o circuito | Planejado |
| T9.2.2 | Definir estado de fallback seguro | comportamento em degradação | Planejado |

### T3 - Timeouts e controle de latência

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.3 | Definir timeouts globais da aplicação | limites para chamadas e processamento | Planejado |
| T9.3.1 | Classificar operações por prioridade | chamadas rápidas, médias e pesadas | Planejado |
| T9.3.2 | Definir resposta em timeout | estratégia de fallback e observabilidade | Planejado |

### T4 - Bulkhead e isolamento de recursos

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.4 | Definir isolamento de recursos quando aplicável | separação de filas, pools ou execuções | Planejado |
| T9.4.1 | Identificar pontos de saturação | gargalos por processamento ou concorrência | Planejado |
| T9.4.2 | Definir política de proteção por capacidade | evita efeito de dominó em carga alta | Planejado |

### T5 - Health Check e monitoramento operacional

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.5 | Definir health checks da aplicação | endpoints de status e dependências | Planejado |
| T9.5.1 | Definir health checks de infraestrutura | banco, integrações críticas e serviços relevantes | Planejado |
| T9.5.2 | Definir política de disponibilidade | estado operacional da aplicação | Planejado |

### T6 - Métricas e Micrometer

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.6 | Definir métricas fundamentais | latência, falhas, throughput e status | Planejado |
| T9.6.1 | Integrar Micrometer | exposição de métricas para observabilidade | Planejado |
| T9.6.2 | Definir dashboards mínimos | indicadores para operação e troubleshooting | Planejado |

### T7 - Observabilidade e diagnóstico

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.7 | Definir logs estruturados de falha e retry | rastreabilidade operacional | Planejado |
| T9.7.1 | Definir indicadores para circuit breaker e timeout | sinais claros de degradação | Planejado |
| T9.7.2 | Definir critérios de alarme e investigação | base para operação do sistema | Planejado |

### T8 - Validação e testes de resiliência

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.8 | Definir testes de comportamento em falha | cenários de recuperação e degradação | Planejado |
| T9.8.1 | Validar retry e circuit breaker | testes automatizados de resiliência | Planejado |
| T9.8.2 | Validar health check e métricas | evidência de estabilidade e observabilidade | Planejado |

## 8. Resultado esperado da etapa

Ao final da Etapa 9, a aplicação deve apresentar:

- tolerância a falhas transitórias;
- proteção contra dependências indisponíveis;
- limites de latência e sobrecarga;
- observabilidade adequada;
- health checks claros para operação;
- sinais de degradação e recuperação monitoráveis.

Esse conjunto torna a plataforma mais confiável para o MVP e prepara o sistema para a etapa final de entrega, documentação e apresentação do produto.

## 9. Regras de execução

- manter o foco na infraestrutura de resiliência;
- não implementar regras de negócio novas fora do escopo;
- priorizar medidas de proteção e observabilidade;
- validar sempre a relação com a arquitetura modular existente;
- evitar expansão de escopo para integrações externas ou frontend.

## 10. Observação final

A Etapa 9 não é um módulo de negócio, mas um mecanismo estrutural que garante estabilidade operacional. No contexto do PharmaGuard, ela reforça a qualidade de execução do MVP, reduzindo riscos de indisponibilidade e facilitando a manutenção e operação do sistema.
