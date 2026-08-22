package com.pharmaguard.api.reports.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.LoteJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.MedicamentoJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.MovimentacaoEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.SaldoLoteEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaldoLoteEstoqueEntity;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.StatusValidade;
import com.pharmaguard.api.reports.application.AnaliseValidadeMedicamento;
import com.pharmaguard.api.reports.application.AnaliseValidadeMedicamentoCalculator;
import com.pharmaguard.api.reports.application.BaseAnaliticaMedicamento;
import com.pharmaguard.api.reports.application.FiltroAlertas;
import com.pharmaguard.api.reports.application.FiltroEstoqueMinimo;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.FiltroProdutosCriticos;
import com.pharmaguard.api.reports.application.FiltroReposicao;
import com.pharmaguard.api.reports.application.FiltroVencimentos;
import com.pharmaguard.api.reports.application.IndicadoresEstatisticosConsumo;
import com.pharmaguard.api.reports.application.IndicadoresEstatisticosConsumoCalculator;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
import com.pharmaguard.api.reports.application.NivelEstoqueCalculado;
import com.pharmaguard.api.reports.application.NivelEstoqueCalculator;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoResponse;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCase;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosResponse;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCase;
import com.pharmaguard.api.reports.application.RelatorioReposicaoResponse;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCase;
import com.pharmaguard.api.reports.application.RelatorioVencimentosResponse;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCase;
import com.pharmaguard.api.reports.application.RecomendacaoReposicao;
import com.pharmaguard.api.reports.application.RecomendacaoReposicaoCalculator;
import com.pharmaguard.api.supplier.adapters.out.repository.FornecedorJpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean({LoteJpaRepository.class, SaldoLoteEstoqueJpaRepository.class,
    MovimentacaoEstoqueJpaRepository.class, MedicamentoJpaRepository.class, FornecedorJpaRepository.class})
public class RelatorioEstoqueJpaAdapter implements
    MetricasMotorEstatisticoUseCase.MetricasMotorEstatisticoRepositoryPort,
    RelatorioAlertasUseCase.RelatorioAlertasRepositoryPort,
        RelatorioProdutosCriticosUseCase.RelatorioProdutosCriticosRepositoryPort,
        RelatorioEstoqueMinimoUseCase.RelatorioEstoqueMinimoRepositoryPort,
        RelatorioVencimentosUseCase.RelatorioVencimentosRepositoryPort,
        RelatorioReposicaoUseCase.RelatorioReposicaoRepositoryPort {

    private final MedicamentoJpaRepository medicamentoJpa;
    private final LoteJpaRepository loteJpa;
    private final SaldoLoteEstoqueJpaRepository saldoJpa;
    private final MovimentacaoEstoqueJpaRepository movimentacaoJpa;
    private final FornecedorJpaRepository fornecedorJpa;

    public RelatorioEstoqueJpaAdapter(MedicamentoJpaRepository medicamentoJpa,
            LoteJpaRepository loteJpa, SaldoLoteEstoqueJpaRepository saldoJpa,
            MovimentacaoEstoqueJpaRepository movimentacaoJpa,
            FornecedorJpaRepository fornecedorJpa) {
        this.medicamentoJpa = Objects.requireNonNull(medicamentoJpa, "medicamentoJpa e obrigatorio");
        this.loteJpa = Objects.requireNonNull(loteJpa, "loteJpa e obrigatorio");
        this.saldoJpa = Objects.requireNonNull(saldoJpa, "saldoJpa e obrigatorio");
        this.movimentacaoJpa = Objects.requireNonNull(movimentacaoJpa, "movimentacaoJpa e obrigatorio");
        this.fornecedorJpa = Objects.requireNonNull(fornecedorJpa, "fornecedorJpa e obrigatorio");
    }

    @Override
    public List<RelatorioProdutosCriticosResponse.ItemCritico> listarProdutosCriticos(FiltroProdutosCriticos filtro) {
        Map<Long, Integer> saldos = saldosPorMedicamento();
        Map<Long, Double> consumos = consumosPorMedicamento(filtro.periodoInicio(), filtro.periodoFim());
        double dias = diasNoPeriodo(filtro.periodoInicio(), filtro.periodoFim());
        List<RelatorioProdutosCriticosResponse.ItemCritico> itens = new ArrayList<>();

        for (MedicamentoEntity medicamento : medicamentosFiltrados(filtro.medicamentoId(), filtro.categoriaId(), filtro.unidadeMedidaId())) {
            int saldo = saldos.getOrDefault(medicamento.getId(), 0);
            double media = consumos.getOrDefault(medicamento.getId(), 0d) / dias;
            RelatorioProdutosCriticosResponse.Risco risco = risco(medicamento.getCriticidade(), saldo, media);
            if (risco == RelatorioProdutosCriticosResponse.Risco.BAIXO) {
                continue;
            }
            itens.add(new RelatorioProdutosCriticosResponse.ItemCritico(
                    medicamento.getId(), medicamento.getNome(), medicamento.getCategoria().getNome(), saldo,
                    media, risco, urgencia(saldo, media), "Saldo ou consumo indica risco de ruptura"));
        }
        return itens;
    }

    @Override
    public List<RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo> listarItensAbaixoMinimo(FiltroEstoqueMinimo filtro) {
        Map<Long, Integer> saldos = saldosPorMedicamento();
        List<RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo> itens = new ArrayList<>();
        for (MedicamentoEntity medicamento : medicamentosFiltrados(filtro.medicamentoId(), filtro.categoriaId(), filtro.unidadeMedidaId())) {
            int saldo = saldos.getOrDefault(medicamento.getId(), 0);
            int minimo = estoqueMinimo(medicamento.getId());
            if (saldo >= minimo) {
                continue;
            }
            RelatorioEstoqueMinimoResponse.StatusEstoque status = saldo == 0
                    ? RelatorioEstoqueMinimoResponse.StatusEstoque.RUPTURA
                    : RelatorioEstoqueMinimoResponse.StatusEstoque.BAIXO;
            itens.add(new RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo(
                    medicamento.getId(), medicamento.getNome(), medicamento.getCategoria().getNome(),
                    saldo, minimo, status, minimo - saldo));
        }
        return itens;
    }

    @Override
    public List<RelatorioVencimentosResponse.ItemVencimento> listarItensVencendo(FiltroVencimentos filtro) {
        List<RelatorioVencimentosResponse.ItemVencimento> itens = new ArrayList<>();
        for (SaldoLoteEstoqueEntity saldo : saldoJpa.findAll()) {
            LoteEntity lote = saldo.getLote();
            MedicamentoEntity medicamento = lote.getMedicamento();
            if (!matches(medicamento, filtro.medicamentoId(), filtro.categoriaId(), filtro.unidadeMedidaId())
                    || lote.getDataValidade().isBefore(filtro.periodoInicio())
                    || lote.getDataValidade().isAfter(filtro.periodoFim())) {
                continue;
            }
            StatusValidade status = statusValidade(lote.getDataValidade());
            itens.add(new RelatorioVencimentosResponse.ItemVencimento(
                    medicamento.getId(), medicamento.getNome(), lote.getNumeroLote(), lote.getDataValidade(),
                    saldo.getQuantidadeDisponivel(), RelatorioVencimentosResponse.StatusValidadeRelatorio.valueOf(status.name()),
                    severidade(status)));
        }
        return itens;
    }

    @Override
    public List<RelatorioReposicaoResponse.ItemReposicao> listarItensParaReposicao(FiltroReposicao filtro) {
        List<BaseAnaliticaMedicamento> baseAnalitica = baseAnaliticaPorMedicamento(filtro);
        List<RelatorioReposicaoResponse.ItemReposicao> itens = new ArrayList<>();
        for (BaseAnaliticaMedicamento item : baseAnalitica) {
            int saldoAproveitavel = saldoAproveitavel(item);
            RecomendacaoReposicao recomendacao = RecomendacaoReposicaoCalculator.calcular(item, saldoAproveitavel);
            if (recomendacao.quantidadeSugerida() == 0) {
                continue;
            }
            itens.add(new RelatorioReposicaoResponse.ItemReposicao(
                    item.medicamentoId(), item.nomeMedicamento(), recomendacao.quantidadeSugerida(),
                    recomendacao.urgencia(), recomendacao.prioridade(),
                    filtro.fornecedorId(), item.leadTimeDias(),
                    recomendacao.justificativa()));
        }
        return itens;
    }

    @Override
    public MetricasMotorEstatisticoResponse consultarMetricas(FiltroMetricasMotorEstatistico filtro) {
        List<BaseAnaliticaMedicamento> baseAnalitica = baseAnaliticaPorParametros(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro.medicamentoId(),
                filtro.categoriaId(),
                filtro.unidadeMedidaId(),
                filtro.fornecedorId());

        int totalComReposicao = 0;
        int totalRiscoRuptura = 0;
        int totalRiscoValidade = 0;
        double totalConsumo = 0d;
        double somaCobertura = 0d;
        int totalCoberturasFinitas = 0;

        for (BaseAnaliticaMedicamento item : baseAnalitica) {
            totalConsumo += item.consumoTotalPeriodo();

            int saldoAproveitavel = saldoAproveitavel(item);
            RecomendacaoReposicao recomendacao = RecomendacaoReposicaoCalculator.calcular(item, saldoAproveitavel);
            if (recomendacao.quantidadeSugerida() > 0) {
                totalComReposicao++;
            }

            if (item.nivelEstoque().riscoRuptura() == NivelEstoqueCalculado.RiscoRuptura.ALTO
                    || item.nivelEstoque().riscoRuptura() == NivelEstoqueCalculado.RiscoRuptura.CRITICO) {
                totalRiscoRuptura++;
            }

            if (item.analiseValidade().riscoValidade() == AnaliseValidadeMedicamento.RiscoValidade.ALTO
                    || item.analiseValidade().riscoValidade() == AnaliseValidadeMedicamento.RiscoValidade.CRITICO) {
                totalRiscoValidade++;
            }

            double cobertura = item.nivelEstoque().coberturaDemandaDias();
            if (!Double.isInfinite(cobertura) && !Double.isNaN(cobertura)) {
                somaCobertura += cobertura;
                totalCoberturasFinitas++;
            }
        }

        return new MetricasMotorEstatisticoResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                baseAnalitica.size(),
                totalComReposicao,
                totalRiscoRuptura,
                totalRiscoValidade,
                totalConsumo,
                totalCoberturasFinitas == 0 ? 0d : somaCobertura / totalCoberturasFinitas,
                true,
                true,
                LocalDateTime.now());
    }

    @Override
    public RelatorioAlertasUseCase.SnapshotAlertas consultar(FiltroAlertas filtro) {
        List<BaseAnaliticaMedicamento> baseAnalitica = baseAnaliticaPorParametros(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro.medicamentoId(),
                filtro.categoriaId(),
                filtro.unidadeMedidaId(),
                filtro.fornecedorId());

        List<RelatorioAlertasResponse.ItemAlerta> alertas = new ArrayList<>();
        int totalRuptura = 0;
        int totalVencimento = 0;
        int totalExcesso = 0;
        int totalItensCriticos = 0;
        double totalConsumoPeriodo = 0d;

        for (BaseAnaliticaMedicamento item : baseAnalitica) {
            totalConsumoPeriodo += item.consumoTotalPeriodo();

            boolean itemCritico = item.nivelEstoque().riscoRuptura() == NivelEstoqueCalculado.RiscoRuptura.CRITICO
                    || item.nivelEstoque().riscoRuptura() == NivelEstoqueCalculado.RiscoRuptura.ALTO
                    || item.analiseValidade().riscoValidade() == AnaliseValidadeMedicamento.RiscoValidade.CRITICO
                    || item.analiseValidade().riscoValidade() == AnaliseValidadeMedicamento.RiscoValidade.ALTO;
            if (itemCritico) {
                totalItensCriticos++;
            }

            int saldoAproveitavel = saldoAproveitavel(item);
            if (item.nivelEstoque().riscoRuptura() == NivelEstoqueCalculado.RiscoRuptura.CRITICO
                    || item.nivelEstoque().riscoRuptura() == NivelEstoqueCalculado.RiscoRuptura.ALTO) {
                totalRuptura++;
                alertas.add(new RelatorioAlertasResponse.ItemAlerta(
                        item.medicamentoId(),
                        item.nomeMedicamento(),
                        RelatorioAlertasResponse.TipoAlerta.RUPTURA,
                        severidadeRuptura(item.nivelEstoque().riscoRuptura()),
                        Math.max(0, item.nivelEstoque().estoqueMinimo() - saldoAproveitavel),
                        "Risco de ruptura baseado em cobertura, consumo e lead time"));
            }

            if (item.analiseValidade().riscoValidade() != AnaliseValidadeMedicamento.RiscoValidade.BAIXO) {
                totalVencimento++;
                alertas.add(new RelatorioAlertasResponse.ItemAlerta(
                        item.medicamentoId(),
                        item.nomeMedicamento(),
                        RelatorioAlertasResponse.TipoAlerta.VENCIMENTO,
                        severidadeValidade(item.analiseValidade().riscoValidade()),
                        item.analiseValidade().quantidadeProximaVencimento() + item.analiseValidade().quantidadeVencida(),
                        "Risco de vencimento identificado por lote e impacto no consumo real"));
            }

            if (saldoAproveitavel > item.nivelEstoque().estoqueMaximo()) {
                totalExcesso++;
                alertas.add(new RelatorioAlertasResponse.ItemAlerta(
                        item.medicamentoId(),
                        item.nomeMedicamento(),
                        RelatorioAlertasResponse.TipoAlerta.EXCESSO_ESTOQUE,
                        RelatorioAlertasResponse.SeveridadeAlerta.MEDIA,
                        saldoAproveitavel - item.nivelEstoque().estoqueMaximo(),
                        "Excesso de estoque em relacao ao nivel maximo calculado"));
            }
        }

        RelatorioAlertasResponse.ResumoAlertas resumo = new RelatorioAlertasResponse.ResumoAlertas(
                totalRuptura,
                totalVencimento,
                totalExcesso,
                totalConsumoPeriodo,
                totalItensCriticos);

        return new RelatorioAlertasUseCase.SnapshotAlertas(alertas, resumo);
    }

    private List<BaseAnaliticaMedicamento> baseAnaliticaPorMedicamento(FiltroReposicao filtro) {
        return baseAnaliticaPorParametros(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro.medicamentoId(),
                filtro.categoriaId(),
                filtro.unidadeMedidaId(),
                filtro.fornecedorId());
    }

    private List<BaseAnaliticaMedicamento> baseAnaliticaPorParametros(
            LocalDate periodoInicio,
            LocalDate periodoFim,
            Long medicamentoId,
            Long categoriaId,
            Long unidadeMedidaId,
            Long fornecedorId) {
        Map<Long, Integer> saldos = saldosPorMedicamento();
        Map<Long, Double> consumos = consumosPorMedicamento(periodoInicio, periodoFim);
        Map<Long, List<Double>> serieConsumoDiario = serieConsumoDiarioPorMedicamento(periodoInicio, periodoFim);
        Map<Long, AnaliseValidadeMedicamento> analiseValidade = analiseValidadePorMedicamento();
        int diasNoPeriodo = (int) diasNoPeriodo(periodoInicio, periodoFim);
        int leadTimeDias = leadTimePorFornecedor(fornecedorId);

        List<BaseAnaliticaMedicamento> base = new ArrayList<>();
        for (MedicamentoEntity medicamento : medicamentosFiltrados(medicamentoId, categoriaId, unidadeMedidaId)) {
            double consumoTotal = consumos.getOrDefault(medicamento.getId(), 0d);
            IndicadoresEstatisticosConsumo indicadores = IndicadoresEstatisticosConsumoCalculator.calcular(
                    serieConsumoDiario.getOrDefault(medicamento.getId(), Collections.emptyList()),
                    diasNoPeriodo);
                NivelEstoqueCalculado nivelEstoque = NivelEstoqueCalculator.calcular(
                    indicadores,
                    saldos.getOrDefault(medicamento.getId(), 0),
                    leadTimeDias);
                AnaliseValidadeMedicamento analise = analiseValidade.getOrDefault(
                        medicamento.getId(),
                        AnaliseValidadeMedicamentoCalculator.calcular(0, 0, 0, Integer.MAX_VALUE));
            base.add(new BaseAnaliticaMedicamento(
                        medicamento.getId(),
                        medicamento.getNome(),
                        medicamento.getCategoria().getId(),
                        medicamento.getUnidadeMedida().getId(),
                        periodoInicio,
                        periodoFim,
                        diasNoPeriodo,
                        saldos.getOrDefault(medicamento.getId(), 0),
                        consumoTotal,
                        consumoTotal / diasNoPeriodo,
                    leadTimeDias,
                    indicadores,
                        nivelEstoque,
                        analise));
        }
        return base;
    }

    private Map<Long, AnaliseValidadeMedicamento> analiseValidadePorMedicamento() {
        Map<Long, Integer> quantidadeValida = new HashMap<>();
        Map<Long, Integer> quantidadeProxima = new HashMap<>();
        Map<Long, Integer> quantidadeVencida = new HashMap<>();
        Map<Long, Integer> diasMinimosProximo = new HashMap<>();
        Set<Long> medicamentosComSaldo = new HashSet<>();

        LocalDate hoje = LocalDate.now();
        for (SaldoLoteEstoqueEntity saldo : saldoJpa.findAll()) {
            Long medicamentoId = saldo.getLote().getMedicamento().getId();
            int quantidade = Math.max(0, saldo.getQuantidadeDisponivel());
            if (quantidade == 0) {
                continue;
            }
            medicamentosComSaldo.add(medicamentoId);

            StatusValidade status = statusValidade(saldo.getLote().getDataValidade());
            if (status == StatusValidade.VENCIDO) {
                quantidadeVencida.merge(medicamentoId, quantidade, Integer::sum);
                continue;
            }
            if (status == StatusValidade.PROXIMO_VENCIMENTO) {
                quantidadeProxima.merge(medicamentoId, quantidade, Integer::sum);
                int diasParaVencer = (int) ChronoUnit.DAYS.between(hoje, saldo.getLote().getDataValidade());
                diasMinimosProximo.merge(medicamentoId, Math.max(0, diasParaVencer), Math::min);
                continue;
            }
            quantidadeValida.merge(medicamentoId, quantidade, Integer::sum);
        }

        Map<Long, AnaliseValidadeMedicamento> analises = new HashMap<>();
        for (Long medicamentoId : medicamentosComSaldo) {
            analises.put(medicamentoId, AnaliseValidadeMedicamentoCalculator.calcular(
                    quantidadeValida.getOrDefault(medicamentoId, 0),
                    quantidadeProxima.getOrDefault(medicamentoId, 0),
                    quantidadeVencida.getOrDefault(medicamentoId, 0),
                    diasMinimosProximo.getOrDefault(medicamentoId, Integer.MAX_VALUE)));
        }
        return analises;
    }

    private Map<Long, List<Double>> serieConsumoDiarioPorMedicamento(LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.plusDays(1).atStartOfDay().minusNanos(1);

        Map<Long, Map<LocalDate, Double>> consumoPorMedicamentoPorDia = new HashMap<>();
        for (MovimentacaoEstoqueEntity movimentacao : movimentacaoJpa.findByFiltros(
                null, null, MovimentacaoEstoque.Tipo.SAIDA, dataInicio, dataFim)) {
            Long medicamentoId = movimentacao.getMedicamento().getId();
            LocalDate dataConsumo = movimentacao.getDataMovimentacao().toLocalDate();
            consumoPorMedicamentoPorDia
                    .computeIfAbsent(medicamentoId, id -> new HashMap<>())
                    .merge(dataConsumo, (double) movimentacao.getQuantidade(), Double::sum);
        }

        Map<Long, List<Double>> serieCompleta = new HashMap<>();
        for (Map.Entry<Long, Map<LocalDate, Double>> entrada : consumoPorMedicamentoPorDia.entrySet()) {
            List<Double> serieDiaria = new ArrayList<>();
            LocalDate dia = inicio;
            while (!dia.isAfter(fim)) {
                serieDiaria.add(entrada.getValue().getOrDefault(dia, 0d));
                dia = dia.plusDays(1);
            }
            serieCompleta.put(entrada.getKey(), serieDiaria);
        }
        return serieCompleta;
    }

    private int leadTimePorFornecedor(Long fornecedorId) {
        if (fornecedorId == null) {
            return 7;
        }
        return fornecedorJpa.findById(fornecedorId)
            .map(fornecedor -> Math.max(1, fornecedor.getLeadTimeDias()))
            .orElse(7);
    }

    private int saldoAproveitavel(BaseAnaliticaMedicamento item) {
        AnaliseValidadeMedicamento validade = item.analiseValidade();
        int impactoProximoVencimento = (int) Math.ceil(
                validade.quantidadeProximaVencimento() * (1d - validade.fatorImpactoConsumoReal()));
        return Math.max(0, item.saldoAtual() - validade.quantidadeVencida() - impactoProximoVencimento);
    }

    private RelatorioAlertasResponse.SeveridadeAlerta severidadeRuptura(NivelEstoqueCalculado.RiscoRuptura risco) {
        return switch (risco) {
            case CRITICO -> RelatorioAlertasResponse.SeveridadeAlerta.CRITICA;
            case ALTO -> RelatorioAlertasResponse.SeveridadeAlerta.ALTA;
            case MEDIO -> RelatorioAlertasResponse.SeveridadeAlerta.MEDIA;
            case BAIXO -> RelatorioAlertasResponse.SeveridadeAlerta.BAIXA;
        };
    }

    private RelatorioAlertasResponse.SeveridadeAlerta severidadeValidade(
            AnaliseValidadeMedicamento.RiscoValidade riscoValidade) {
        return switch (riscoValidade) {
            case CRITICO -> RelatorioAlertasResponse.SeveridadeAlerta.CRITICA;
            case ALTO -> RelatorioAlertasResponse.SeveridadeAlerta.ALTA;
            case MEDIO -> RelatorioAlertasResponse.SeveridadeAlerta.MEDIA;
            case BAIXO -> RelatorioAlertasResponse.SeveridadeAlerta.BAIXA;
        };
    }

    private List<MedicamentoEntity> medicamentosFiltrados(Long medicamentoId, Long categoriaId, Long unidadeMedidaId) {
        return medicamentoJpa.findAll().stream()
                .filter(medicamento -> matches(medicamento, medicamentoId, categoriaId, unidadeMedidaId))
                .toList();
    }

    private boolean matches(MedicamentoEntity medicamento, Long medicamentoId, Long categoriaId, Long unidadeMedidaId) {
        return (medicamentoId == null || medicamentoId.equals(medicamento.getId()))
                && (categoriaId == null || categoriaId.equals(medicamento.getCategoria().getId()))
                && (unidadeMedidaId == null || unidadeMedidaId.equals(medicamento.getUnidadeMedida().getId()));
    }

    private Map<Long, Integer> saldosPorMedicamento() {
        Map<Long, Integer> saldos = new HashMap<>();
        for (SaldoLoteEstoqueEntity saldo : saldoJpa.findAll()) {
            saldos.merge(saldo.getLote().getMedicamento().getId(), saldo.getQuantidadeDisponivel(), Integer::sum);
        }
        return saldos;
    }

    private Map<Long, Double> consumosPorMedicamento(LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.plusDays(1).atStartOfDay().minusNanos(1);
        Map<Long, Double> consumos = new HashMap<>();
        for (MovimentacaoEstoqueEntity movimentacao : movimentacaoJpa.findByFiltros(
                null, null, MovimentacaoEstoque.Tipo.SAIDA, dataInicio, dataFim)) {
            consumos.merge(movimentacao.getMedicamento().getId(), (double) movimentacao.getQuantidade(), Double::sum);
        }
        return consumos;
    }

    private int estoqueMinimo(Long medicamentoId) {
        return loteJpa.findAllByMedicamento_Id(medicamentoId).stream()
                .mapToInt(lote -> Math.max(1, lote.getQuantidadeInicial() / 10))
                .max().orElse(1);
    }

    private double diasNoPeriodo(LocalDate inicio, LocalDate fim) {
        return Math.max(1, ChronoUnit.DAYS.between(inicio, fim) + 1);
    }

    private StatusValidade statusValidade(LocalDate validade) {
        LocalDate hoje = LocalDate.now();
        if (!validade.isAfter(hoje)) return StatusValidade.VENCIDO;
        if (!validade.isAfter(hoje.plusDays(90))) return StatusValidade.PROXIMO_VENCIMENTO;
        return StatusValidade.VALIDO;
    }

    private RelatorioVencimentosResponse.SeveridadeVencimento severidade(StatusValidade status) {
        return switch (status) {
            case VENCIDO -> RelatorioVencimentosResponse.SeveridadeVencimento.CRITICA;
            case PROXIMO_VENCIMENTO -> RelatorioVencimentosResponse.SeveridadeVencimento.ALTA;
            case VALIDO -> RelatorioVencimentosResponse.SeveridadeVencimento.BAIXA;
        };
    }

    private RelatorioProdutosCriticosResponse.Risco risco(Medicamento.Criticidade criticidade, int saldo, double consumoMedio) {
        if (saldo == 0) return RelatorioProdutosCriticosResponse.Risco.CRITICO;
        if (criticidade == Medicamento.Criticidade.CRITICA || saldo < consumoMedio * 7) {
            return RelatorioProdutosCriticosResponse.Risco.ALTO;
        }
        return RelatorioProdutosCriticosResponse.Risco.BAIXO;
    }

    private RelatorioProdutosCriticosResponse.Urgencia urgencia(int saldo, double consumoMedio) {
        if (saldo == 0 || saldo < consumoMedio * 3) return RelatorioProdutosCriticosResponse.Urgencia.ALTA;
        return RelatorioProdutosCriticosResponse.Urgencia.MEDIA;
    }
}
