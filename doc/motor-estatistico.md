# Etapa 6 - Motor Estatístico

## 1. Contexto do módulo

O motor estatístico faz parte do módulo de inteligência do PharmaGuard e é o componente responsável por transformar o histórico de movimentações em previsões e recomendações operacionais.

De acordo com o visionamento do projeto, o diferencial da solução não está apenas no controle do estoque, mas na capacidade de responder:

- quanto há em estoque;
- quanto está sendo consumido;
- o que está próximo de vencer;
- o que corre risco de faltar;
- quando repor;
- quanto comprar.

Esse conjunto de decisões está diretamente alinhado ao bloco "Analytics / Intelligence" da arquitetura do MVP, que integra consumo, validade, disponibilidade e reposição em um motor analítico.

## 2. Objetivo da etapa

A etapa de Motor Estatístico tem como objetivo estruturar a base analítica para a tomada de decisão de reposição e gestão de risco, com foco em:

- processamento de histórico de movimentações;
- cálculo de consumo médio e variação;
- avaliação de risco de ruptura e vencimento;
- suporte à recomendação de compra e reposição;
- geração de alertas e indicadores em apoio ao módulo de estoque.

## 3. Escopo da etapa

O escopo desta etapa é a definição da base da inteligência do estoque, não a implementação funcional completa do módulo de analytics.

### Escopo previsto

- análise do histórico de consumo por medicamento;
- cálculo de indicadores estatísticos baseados em movimentações;
- identificação de tendência de consumo por período;
- cálculo de estoque de segurança, mínimo e máximo;
- avaliação de risco de ruptura;
- suporte à recomendação de reposição;
- integração com o contexto do módulo de medicamentos e estoque.

### Fora do escopo desta etapa

- implementação de regras de negócio do módulo de compras;
- criação de telas ou interfaces de usuário;
- automatização de pedidos de compra fora da lógica de recomendação;
- migração de infraestrutura ou deploy;
- alterações fora do objetivo de inteligência do estoque.

## 4. Relação com o módulo de medicamentos

O módulo de medicamentos fornece a base catalogada para a análise. Conforme o documento do módulo, ele entrega:

- categoria;
- unidade de medida;
- medicamento;
- validade;
- lote;
- relacionamento entre produto e lote.

O motor estatístico utiliza esse contexto para analisar o comportamento real do consumo por medicamento e lote, preservando as regras de negócio já definidas na camada de domínio.

## 5. Contexto arquitetural

A arquitetura do PharmaGuard organiza o sistema em módulos: auth, inventory, supplier, analytics, scheduler, reports e shared.

O motor estatístico deve estar alinhado ao módulo analytics e seguir a mesma lógica arquitetural:

- adapters.in: entrada de dados/consultas;
- application: casos de uso e serviços de análise;
- domain: regras estatísticas e métricas;
- adapters.out: persistência e cálculos estruturados;
- integração com estoque e relatórios.

## 6. Divisão de tarefas proposta

### T6.0 - Definição da base analítica

- levantar o histórico de movimentações do estoque;
- identificar a granularidade da análise: por medicamento, lote, categoria ou período;
- definir fontes de dados e regras de agregação.

### T6.1 - Definição de indicadores estatísticos

- consumo médio diário;
- desvio padrão do consumo;
- tendência de consumo;
- média ajustada;
- intervalo de previsão por período.

### T6.2 - Cálculo de nível de estoque

- estoque mínimo;
- estoque máximo;
- estoque de segurança;
- cobertura de demanda por período;
- risco de ruptura.

### T6.3 - Análise de validade e vencimento

- risco de vencimento por lote;
- avaliação de itens próximos do vencimento;
- impacto da validade no consumo real;
- suporte para política FEFO.

### T6.4 - Recomendação de reposição

- identificar itens com demanda crescente;
- combinar consumo, prazo e validade;
- sugerir quantidade recomendada de compra;
- sinalizar itens críticos ou em risco.

### T6.5 - Alertas e relatórios

- alertas de ruptura;
- alertas de vencimento;
- alertas de excesso de estoque;
- relatórios de consumo e criticidade.

### T6.6 - Integração com o restante do sistema

- expor métricas para API ou relatórios;
- integrar motor a módulos de estoque e inventário;
- preparar integração com scheduler e dashboards.

### T6.7 - Testes e validação

- validar cálculos estatísticos com dados reais e sintéticos;
- testar cenários de consumo estável, alto e irregular;
- validar regras de alerta e reposição;
- garantir consistência com o módulo de medicamentos e estoque.

## 7. Critérios de aceite da etapa

A etapa deve ser considerada concluída quando:

- a base de cálculo do consumo estiver formalizada;
- os indicadores estatísticos estiverem definidos;
- os cenários de risco e reposição estiverem documentados;
- a integração com o módulo de estoque estiver coerente;
- a lógica de alerta estiver alinhada ao contexto clínico e operacional do PharmaGuard.

## 8. Observação de execução

Esta etapa deve ser tratada como documento de planejamento e arquitetura de inteligência de estoque, sem implementação de código ou execução de tarefas fora do escopo definido.

## 9. Resumo executivo

O Motor Estatístico é a camada analítica do PharmaGuard, responsável por transformar dados de movimentação e validade em decisões de reposição, alerta e gestão de risco. Ele depende diretamente do contexto do módulo de medicamentos e do histórico de estoque para produzir previsões com segurança e consistência operacional.
