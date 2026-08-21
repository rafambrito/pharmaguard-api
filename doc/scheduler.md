# Etapa 7 - Scheduler

## 1. Contexto do módulo

Esta etapa faz parte do MVP do PharmaGuard e está alinhada ao roadmap arquitetural descrito no README do projeto.

Conforme a visão do sistema, o módulo de `scheduler` é um componente transversal responsável por disparar processos periódicos e rotineiros, enquanto as regras de negócio continuam centralizadas nos módulos de aplicação e domínio, especialmente em:

- `inventory` (estoque, lotes, movimentações e validade)
- `analytics` (motor de inteligência e recomendações)
- `reports` (relatórios e consolidação de dados)
- `shared` (padronização de respostas, mensagens e infraestrutura comum)

O README identifica o Scheduler como uma camada de orquestração da automação, posicionada entre a inteligência de estoque e os alertas/relatórios. A ideia principal é manter o processamento periódico separado da API, evitando acoplamento de regras de agendamento com regras de negócio.

## 2. Visão de negócio

O Scheduler do PharmaGuard deve atuar como executor de tarefas recorrentes do sistema, sem implementar lógica de domínio por si só. Sua responsabilidade é disparar, coordenar e monitorar processos automáticos que apoiam a operação clínica e a gestão de estoque.

O módulo deve apoiar cenários como:

- atualização periódica de indicadores de consumo;
- verificação de produtos próximos do vencimento;
- identificação de risco de ruptura;
- geração de recomendações de reposição;
- disparo de alertas operacionais;
- consolidação de dados para relatórios e análises.

## 3. Escopo da etapa

### 3.1 Incluído

- definição da arquitetura do módulo scheduler;
- identificação dos jobs periódicos do MVP;
- integração com módulos de negócio já existentes;
- orquestração de processamento automatizado;
- geração de alertas e recomendações baseadas em regras existentes;
- testes focados em agendamento e execução programada;
- observabilidade básica do scheduler (logs, execução e falhas).

### 3.2 Excluído

Para manter o foco do MVP e evitar extensão de escopo, esta etapa não inclui:

- implementação de regras de negócio novas fora do contexto do scheduler;
- refatoração de módulos de domínio;
- criação de múltiplos canais de alerta externos;
- integrações com sistemas de terceiros fora do padrão do projeto;
- desenvolvimento do frontend;
- alterações de contrato de API não diretamente relacionadas ao scheduler;
- execução de tarefas fora do objetivo desta etapa.

> Recomendação: não implementar, nem executar qualquer tarefa que não seja estritamente do módulo Scheduler.

## 4. Arquitetura do módulo

### 4.1 Estrutura sugerida

```text
scheduler/
├── application/
│   ├── job/
│   ├── usecase/
│   ├── port/input/
│   └── port/output/
├── adapters.out/
│   ├── scheduling/
│   ├── config/
│   ├── runner/
│   └── logging/
└── domain/
    └── model/
```

### 4.2 Princípio de responsabilidade

O Scheduler deve seguir o mesmo padrão da arquitetura do projeto:

- `API` não deve conter lógica de agendamento;
- `Application` deve coordenar casos de uso do agendamento;
- `Domain` deve manter regras de negócio e critérios de alerta;
- `adapters.out` deve tratar cron, execução, persistência e integração externa;
- o `scheduler` deve consumir serviços e portas já definidos pelos módulos responsáveis.

## 5. Dependências esperadas

O módulo Scheduler depende diretamente de:

- `inventory` para dados de estoque, validade e movimentação;
- `analytics` para consumo, estatísticas e recomendações;
- `reports` para consolidação de indicadores e relatórios;
- `shared` para mensagens, erros e padrões de infraestrutura;
- `auth` apenas como referência para auditoria e rastreabilidade, quando necessário.

## 6. Comportamento esperado do Scheduler

O processamento automático deve ocorrer como execução periódica, e não como fluxo de requisição do usuário. A orquestração deve seguir este modelo:

```text
Scheduler
   │
   ▼
Execução periódica
   │
   ▼
Atualização diária / processamento agendado
   │
   ├── Consumo
   ├── Validade
   ├── Estoque
   └── Recomendações / Alertas
```

A execução deve:

- disparar em intervalos definidos;
- recuperar dados dos módulos de negócio;
- aplicar regras e critérios do domínio;
- registrar eventos e logs de execução;
- gerar respostas/alertas para uso em relatórios ou operação administrativa.

## 7. Divisão de tarefas

### T0 - Definição do escopo e arquitetura

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.0 | Definir escopo do módulo Scheduler no MVP | documentação do módulo e limites de responsabilidade | Planejado |
| T7.0.1 | Alinhar o scheduler ao roadmap do projeto | referência clara ao posicionamento entre inteligência e relatórios | Planejado |
| T7.0.2 | Validar dependências com inventory e analytics | mapa de integração do módulo | Planejado |

### T1 - Estrutura do módulo

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.1 | Criar estrutura do módulo scheduler | packages application/adapters/out/domain | Planejado |
| T7.1.1 | Definir portas de entrada/saída | interfaces de integração com outros módulos | Planejado |
| T7.1.2 | Definir configuração de agendamento | beans e cron de execução | Planejado |

### T2 - Jobs periódicos

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.2 | Definir job de atualização diária | processo periódico de atualização de indicadores | Planejado |
| T7.2.1 | Definir job de verificação de validade | análise de vencimento e lotes críticos | Planejado |
| T7.2.2 | Definir job de análise de consumo/estoque | processamento de risco de ruptura e saldo | Planejado |

### T3 - Integração com motor de inteligência

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.3 | Conectar scheduler ao Inventory Intelligence Engine | integração descrita e organizada por portas | Planejado |
| T7.3.1 | Orquestrar atualização de recomendações | gatilho de geração de sugestões de reposição | Planejado |
| T7.3.2 | Definir regras de disparo de alertas | critérios para avisos operacionais | Planejado |

### T4 - Alertas e notificações

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.4 | Definir tipos de alerta do módulo | lista de alertas do MVP | Planejado |
| T7.4.1 | Definir critérios de severidade | classificação de risco e criticidade | Planejado |
| T7.4.2 | Definir canal de saída dos alertas | integração com logs, painel ou service de notificação | Planejado |

### T5 - Relatórios e dados consolidados

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.5 | Preparar dados para relatórios periódicos | estrutura de agregação para relatórios | Planejado |
| T7.5.1 | Definir materialização de indicadores | métricas prontas para relatório | Planejado |
| T7.5.2 | Garantir compatibilidade com modulo de reports | interface mínima de consumo | Planejado |

### T6 - Observabilidade e execução

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.6 | Definir logs de execução do scheduler | registros claros de início, falha e sucesso | Planejado |
| T7.6.1 | Definir mecanismo de retry/falha | política de recuperação de execução | Planejado |
| T7.6.2 | Definir auditoria para jobs agendados | rastreabilidade das execuções | Planejado |

### T7 - Testes do módulo

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T7.7 | Criar testes unitários do scheduler | validação de regras de disparo e critérios | Planejado |
| T7.7.1 | Criar testes de integração do agendamento | execução de jobs e integração com dependências | Planejado |
| T7.7.2 | Validar comportamento em cenários de falha | retry, log e persistência de erro | Planejado |

## 8. Critérios de aceite da etapa

| Status | Critério |
|---|---|
| Planejado | Módulo Scheduler claramente definido no monólito modular |
| Planejado | Regras de negócio permanecem em módulos de domínio e aplicação |
| Planejado | Jobs periódicos definidos e alinhados ao MVP |
| Planejado | Integração com inventory e analytics documentada |
| Planejado | Alertas e indicadores do scheduler definidos |
| Planejado | Testes de execução e falha definidos |
| Planejado | Escopo do Scheduler mantido dentro do MVP sem dispersão |

## 9. Observação final

Esta etapa deve ser tratada como um módulo de automação e coordenação, e não como um módulo de negócio completo. O objetivo principal é garantir que o sistema execute tarefas periódicas com consistência, rastreabilidade e integração com os demais módulos, sem diluir a responsabilidade da lógica de negócio em pontos de agendamento.

O Scheduler deve atuar como executor do processo, não como repositório de regras de negócio.
