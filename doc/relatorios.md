# Modulo Relatorios - (Consumo / Produtos Criticos / Estoque Minimo / Vencimentos / Reposicao)

## 1. Contexto
- O modulo de relatorios faz parte do monolito modular PharmaGuard.
- A estrutura segue Clean Architecture e Arquitetura Hexagonal (ports and adapters).
- Objetivo da etapa: entregar APIs de analise e apoio decisional para consumo, criticidade de medicamentos, estoque minimo, vencimentos e recomendacao de reposicao.
- Escopo desta etapa:
  - API First com contrato OpenAPI
  - Relatorio de consumo historico
  - Relatorio de produtos criticos
  - Relatorio de estoque minimo
  - Relatorio de vencimentos
  - Relatorio de reposicao
  - Testes do modulo
- Requisitos transversais previstos:
  - Reutilizar `message.properties` para mensagens de validacao e erro.
  - Definir o contrato OpenAPI do modulo antes da implementacao dos endpoints.
  - Respeitar as regras de dominio ja estabelecidas nos modulos de medicamentos e estoque.
  - Manter as consultas de relatorio separadas da logica operacional de movimentacao.

## 2. Arquitetura do modulo
### 2.1 Camadas
- domain
  - Entidades, agregados e regras puras de analise e agregacao (indicadores, filtros, criticas, prazos e recomendacoes).
- application
  - Casos de uso para consolidar dados e gerar retornos de relatorios.
  - Ports de entrada e saida para consultas e agregacoes.
- adapters.out
  - Adapters de persistencia JPA, queries SQL e servicos de consulta agregada.
- adapters.in
  - Endpoints REST, DTOs, validacoes de entrada e mapeamento request/response.

### 2.2 Fluxo principal (resumo)
- API recebe requisicao de filtro/periodo.
- Application orquestra a consulta e calculos de agregacao.
- Domain aplica regras de validacao e transformacao dos indicadores.
- adapters.out consulta os dados de estoque, movimentacoes e medicamentos.
- API retorna resposta padronizada com dados do relatorio.

## 3. Regras
- Cada relatorio deve receber filtros minimos validos (periodo, medicamento, categoria, unidade ou fornecedor quando aplicavel).
- Relatorios devem operar sobre dados consolidados e nao sobre registros de transacao sem agregacao.
- Produtos criticos devem considerar disponibilidade, consumo e status do medicamento/estoque.
- Estoque minimo deve obedecer a politica definida pela aplicacao e pelo modulo de estoque.
- Vencimentos devem priorizar itens proximos do prazo e lotes com risco de perda.
- Reposicao deve ser orientada por demanda historica, lead time e risco de ruptura.
- Consultas devem ser eficientes para periodos de 30, 90 e 180 dias, sem comprometer a API.
- Mensagens de validacao e erro devem ser externalizadas em `message.properties`.
- O modulo deve manter foco em leitura e analise, sem alterar regras operacionais de cadastro.

## 4. Tarefas

### T0 - API First (OpenAPI)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.0 | Definir estrategia API First para relatorios | contrato OpenAPI como fonte de verdade da API | X - Pendente |
| T5.0.1 | Criar arquivo de contrato do modulo relatorios | arquivo yaml com paths e schemas dos relatorios | X - Pendente |
| T5.0.2 | Validar padrao de respostas e erros | contratos de sucesso e erro alinhados ao projeto | X - Pendente |

### T1 - Relatorio de Consumo

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.1 | Definir modelo de consumo historico | estrutura de dados para consumo por periodo | X - Pendente |
| T5.1.1 | Definir filtros de consulta | periodo, medicamento, categoria e origem | X - Pendente |
| T5.1.2 | Definir agregacoes basicas | total consumido, media diaria e tendencia | X - Pendente |

### T2 - Relatorio de Produtos Criticos

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.2 | Definir criticidade funcional | regra para apontar itens em risco | X - Pendente |
| T5.2.1 | Definir indicadores de criticidade | saldo, consumo, status e disponib. minima | X - Pendente |
| T5.2.2 | Definir agrupamento | lista priorizada por risco e urgencia | X - Pendente |

### T3 - Relatorio de Estoque Minimo

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.3 | Definir regra de estoque minimo | politicas e calculos de referencia | X - Pendente |
| T5.3.1 | Mapear status do estoque | indicadores de baixo estoque e ruptura | X - Pendente |
| T5.3.2 | Definir saidas do relatorio | lista de itens abaixo do minimo com contexto | X - Pendente |

### T4 - Relatorio de Vencimentos

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.4 | Definir regra de vencimento | criterios para itens proximos ou vencidos | X - Pendente |
| T5.4.1 | Definir agrupamento por lote | lotes por medicamento, validade e quantidade | X - Pendente |
| T5.4.2 | Definir severidade | alerta por prazo critico e risco de perda | X - Pendente |

### T5 - Relatorio de Reposicao

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.5 | Definir regra de reposicao | recomendacao com base em consumo e lead time | X - Pendente |
| T5.5.1 | Definir parametros de recomendacao | quantidade sugerida, urgencia e prioridade | X - Pendente |
| T5.5.2 | Definir integracao com fornecedores | suporte ao lead time e perfil do fornecedor | X - Pendente |

### T6 - Casos de Uso (Application)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.6 | Criar casos de uso de relatorios | servicos de aplicacao para cada relatorio | X - Pendente |
| T5.6.1 | Criar casos de uso de agregacao e filtros | composicao de consultas por periodo e criterio | X - Pendente |
| T5.6.2 | Criar casos de uso de recomendacao | servicos para gerar ordem de reposicao e alerta | X - Pendente |

### T7 - Repository / Persistencia

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.7 | Definir portas de consulta | interfaces para leitura agregada e relatorios | X - Pendente |
| T5.7.1 | Implementar adapters de consulta | queries JPA/SQL para consumos e estoque | X - Pendente |
| T5.7.2 | Mapear projecoes e agregacoes | DTOs de apoio para relatorios e indicadores | X - Pendente |

### T8 - Endpoints

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.8 | Expor endpoints de relatorios | controller/resource com endpoints do modulo | X - Pendente |
| T5.8.1 | Expor endpoint de consumo | consulta de consumo e media por periodo | X - Pendente |
| T5.8.2 | Expor endpoints de criticidade, vencimento e reposicao | endpoints de consulta analitica e priorizacao | X - Pendente |

### T9 - Validacoes e Mensagens

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.9 | Aplicar validacoes de entrada | DTOs validados e respostas padronizadas | X - Pendente |
| T5.9.1 | Reutilizar mensagens no arquivo global | chaves do modulo adicionadas em `message.properties` | X - Pendente |
| T5.9.2 | Integrar mensagens em validacoes e excecoes | erros centralizados e consistentes | X - Pendente |

### T10 - Testes

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T5.10 | Criar testes unitarios do modulo | casos de dominio, agregacao e validacoes | X - Pendente |
| T5.10.1 | Criar testes de integracao da API | cenarios de relatorio com dados reais e filtros | X - Pendente |
| T5.10.2 | Gerar cobertura minima do modulo | relatorio de cobertura da etapa de relatorios | X - Pendente |

## 5. Criterios de Aceite

| Status | Criterio |
|--------|----------|
| X - Pendente | Modulo compilando |
| X - Pendente | Contrato OpenAPI do modulo relatorios definido |
| X - Pendente | Relatorio de consumo funcionando |
| X - Pendente | Relatorio de produtos criticos funcionando |
| X - Pendente | Relatorio de estoque minimo funcionando |
| X - Pendente | Relatorio de vencimentos funcionando |
| X - Pendente | Relatorio de reposicao funcionando |
| X - Pendente | Mensagens centralizadas em `message.properties` |
| X - Pendente | Testes unitarios e de integracao executando |
| X - Pendente | Cobertura minima do modulo gerada |

## 6. Observacoes de escopo
- Esta etapa deve focar em leitura, agregacao e apoio decisional.
- Nao inclui implementacao de regras de negocio operacionais de estoque, cadastro de medicamentos ou alteracoes em cadastros de fornecedores.
- A etapa 5 depende de dados consolidados gerados pelos modulos de estoque, medicamentos e fornecedores ja previstos no roadmap.
- A implementacao do motor estatistico da Etapa 6 deve ser tratada como etapa separada, sem conflitar com o modulo de relatorios.
