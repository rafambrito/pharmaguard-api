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

### Decisão arquitetural do T0

Para o MVP, a resiliência será implementada de forma incremental e transversal,
sem criar um novo módulo de negócio. O ponto de entrada será a infraestrutura
compartilhada, e cada proteção será aplicada somente ao adapter ou processo que
possuir a dependência correspondente.

Decisões definidas:

- `shared` será o proprietário das configurações, métricas, health checks e
   convenções comuns de resiliência;
- adapters de persistência e integrações serão os pontos preferenciais para
   retry, timeout e circuit breaker;
- o scheduler terá isolamento entre execuções e registro de falhas, sem mover
   regras de negócio para a camada de agendamento;
- bulkhead será adotado somente se a implementação revelar um pool, fila ou
   executor compartilhado sujeito a saturação;
- o contrato dos endpoints permanecerá estável, usando o tratamento global de
   exceções já existente para respostas de falha;
- toda proteção deverá possuir configuração externa, limite explícito e
   métrica ou log que permita verificar seu efeito.

Esta decisão evita aplicar retry indiscriminadamente a operações de escrita,
que podem duplicar movimentações de estoque. Operações idempotentes de leitura
e consultas de infraestrutura serão avaliadas primeiro; escritas só poderão
ser repetidas quando houver garantia de idempotência ou chave de deduplicação.

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
- configuração de agendamento do Spring para o job diário já definido no módulo
   `scheduler`.

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

### 6.1 Mapa de dependências críticas validado

| Área | Evidência atual | Risco considerado | Diretriz da Etapa 9 |
|---|---|---|---|
| Persistência | adapters JPA e repositórios de estoque, usuários, fornecedores e relatórios | lentidão ou indisponibilidade do banco | health check do banco, timeout de acesso e métricas de latência/erro |
| Movimentação de estoque | entradas, saídas, saldo e histórico | repetição indevida pode duplicar movimentação | não aplicar retry automático a escrita sem idempotência explícita |
| Relatórios e estatísticas | consultas de consumo, estoque, alertas e métricas | consultas pesadas podem consumir conexões | timeout, métricas de duração e avaliação de isolamento quando necessário |
| Scheduler | job diário às 2h que coordena métricas e alertas | falha silenciosa ou execução concorrente | captura de falha, logs estruturados, métricas de execução e proteção contra sobreposição |
| Autenticação | Spring Security, JWT e refresh token | indisponibilidade ou latência em operações de sessão | preservar respostas de segurança e medir falhas sem repetir autenticação cegamente |
| Integrações externas | nenhuma chamada externa identificada no código atual | escopo futuro, sem alvo concreto no MVP | não criar circuit breaker ou retry para integração inexistente |

O mapa será revisado quando uma nova integração externa ou executor
concorrente for introduzido. Até lá, a Etapa 9 deve priorizar o banco, o
scheduler e as consultas críticas do domínio.

## 7. Divisão de tarefas

### T0 - Definição do escopo e arquitetura da resiliência

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.0 | Definir escopo da Etapa 9 no MVP | documentação e limites de responsabilidade | ✅ Concluído |
| T9.0.1 | Alinhar a resiliência ao roadmap do projeto | referência clara ao posicionamento entre qualidade e MVP | ✅ Concluído |
| T9.0.2 | Validar dependências críticas do sistema | mapa de integração e pontos de risco | ✅ Concluído |

### T1 - Retry e tolerância a falhas transitórias

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.1 | Definir política de retry | mecanismo controlado para falhas temporárias | ✅ Concluído |
| T9.1.1 | Definir critérios de retry por tipo de operação | regras para chamadas críticas e não críticas | ✅ Concluído |
| T9.1.2 | Definir limitação de tentativas e backoff | estratégia de repetição segura | ✅ Concluído |

### T2 - Circuit Breaker

Política aplicada ao scheduler:

- circuito aberto após três falhas transitórias consecutivas, considerando a
   falha final depois das tentativas de retry;
- novas execuções são rejeitadas durante 30 segundos;
- após a janela, uma execução de teste é permitida para verificar recuperação;
- falhas não transitórias não abrem o circuito;
- circuito aberto é tratado como falha do job e registrado pelo log existente,
   sem alterar o contrato dos endpoints.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.2 | Definir proteção por circuit breaker | quebra de cascata em dependências falhando | ✅ Concluído |
| T9.2.1 | Definir thresholds de falha | critérios para abrir e fechar o circuito | ✅ Concluído |
| T9.2.2 | Definir estado de fallback seguro | comportamento em degradação | ✅ Concluído |

### T3 - Timeouts e controle de latência

Política aplicada ao scheduler:

- limite padrão de 30 segundos para o processamento completo, incluindo retry e
   circuit breaker;
- a operação é executada em executor dedicado de threads virtuais;
- ao exceder o prazo, a tarefa é cancelada por interrupção e o erro é
   registrado pelo scheduler;
- o valor pode ser alterado por `resilience.timeout.scheduler`.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.3 | Definir timeouts globais da aplicação | limites para chamadas e processamento | ✅ Concluído |
| T9.3.1 | Classificar operações por prioridade | chamadas rápidas, médias e pesadas | ✅ Concluído |
| T9.3.2 | Definir resposta em timeout | estratégia de fallback e observabilidade | ✅ Concluído |

### T4 - Bulkhead e isolamento de recursos

Política aplicada ao scheduler:

- capacidade padrão de uma execução concorrente;
- uma nova execução não aguarda em fila: é rejeitada imediatamente quando o
   limite é atingido;
- a permissão é liberada após sucesso, falha ou cancelamento da operação;
- a capacidade pode ser alterada por
   `resilience.bulkhead.scheduler.max-concurrent-calls`;
- o bulkhead protege o executor criado para o timeout sem limitar requisições
   HTTP ou alterar regras de negócio.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.4 | Definir isolamento de recursos quando aplicável | separação de filas, pools ou execuções | ✅ Concluído |
| T9.4.1 | Identificar pontos de saturação | gargalos por processamento ou concorrência | ✅ Concluído |
| T9.4.2 | Definir política de proteção por capacidade | evita efeito de dominó em carga alta | ✅ Concluído |

### T5 - Health Check e monitoramento operacional

Política aplicada ao Actuator:

- exposição externa restrita a `health` e `info`;
- detalhes dos componentes não são exibidos publicamente;
- `liveness` representa a capacidade do processo de continuar executando;
- `readiness` inclui o estado da aplicação e a disponibilidade do banco;
- os endpoints `/actuator/health/**` e `/actuator/info` são públicos para uso
   de orquestradores e monitoramento, sem expor dados sensíveis.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.5 | Definir health checks da aplicação | endpoints de status e dependências | ✅ Concluído |
| T9.5.1 | Definir health checks de infraestrutura | banco, integrações críticas e serviços relevantes | ✅ Concluído |
| T9.5.2 | Definir política de disponibilidade | estado operacional da aplicação | ✅ Concluído |

### T6 - Métricas e Micrometer

Métricas mínimas aplicadas ao scheduler:

- `pharmaguard.scheduler.executions{outcome="success|failure"}` para volume e
   resultado das execuções;
- `pharmaguard.scheduler.execution.duration` para distribuição de latência;
- exposição pelo endpoint autenticado `/actuator/metrics`;
- sem tags dinâmicas de exceção, evitando cardinalidade excessiva;
- dashboard mínimo: execuções por resultado, duração e falhas do scheduler.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.6 | Definir métricas fundamentais | latência, falhas, throughput e status | ✅ Concluído |
| T9.6.1 | Integrar Micrometer | exposição de métricas para observabilidade | ✅ Concluído |
| T9.6.2 | Definir dashboards mínimos | indicadores para operação e troubleshooting | ✅ Concluído |

### T7 - Observabilidade e diagnóstico

Eventos estruturados implementados:

- `event=retry_scheduled` com tentativa seguinte, limite, backoff e tipo da
   falha;
- `event=circuit_breaker_open`, `event=circuit_breaker_rejected`,
   `event=circuit_breaker_half_open` e `event=circuit_breaker_closed`;
- `event=operation_timeout` e `event=operation_interrupted`, indicando prazo e
   cancelamento;
- `event=bulkhead_rejected` para execuções recusadas por capacidade;
- alarmes iniciais: aumento de `circuit_breaker_open`, `operation_timeout` ou
   `bulkhead_rejected`; investigação deve correlacionar esses eventos com as
   métricas do scheduler e a saúde do banco.

Os logs usam o padrão existente do projeto, com nível, aplicação,
`correlationId` quando disponível e logger. Nenhum payload de negócio ou
mensagem potencialmente sensível de exceção é registrado pelos mecanismos.

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.7 | Definir logs estruturados de falha e retry | rastreabilidade operacional | ✅ Concluído |
| T9.7.1 | Definir indicadores para circuit breaker e timeout | sinais claros de degradação | ✅ Concluído |
| T9.7.2 | Definir critérios de alarme e investigação | base para operação do sistema | ✅ Concluído |

### T8 - Validação e testes de resiliência

Validações automatizadas implementadas:

- retry: sucesso após falhas transitórias, rejeição de falha não elegível e
   respeito ao número máximo de tentativas;
- circuit breaker: abertura por limite de falhas, rejeição enquanto aberto,
   meia-abertura após a janela e recuperação com sucesso;
- timeout: cancelamento de operação lenta, propagação de falha e retorno dentro
   do prazo;
- bulkhead: rejeição imediata por capacidade e liberação após falha;
- métricas: contadores de sucesso/falha e timer de duração com
   `SimpleMeterRegistry`;
- health check: configuração validada por compilação, com grupos liveness e
   readiness e indicador de banco no Actuator.

Execução focada:

```bash
./mvnw -q -Dtest=RetryExecutorTest,CircuitBreakerTest,TimeoutExecutorTest,BulkheadExecutorTest,SchedulerMetricsTest test
```

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T9.8 | Definir testes de comportamento em falha | cenários de recuperação e degradação | ✅ Concluído |
| T9.8.1 | Validar retry e circuit breaker | testes automatizados de resiliência | ✅ Concluído |
| T9.8.2 | Validar health check e métricas | evidência de estabilidade e observabilidade | ✅ Concluído |

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
