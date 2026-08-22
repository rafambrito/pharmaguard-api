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
import com.pharmaguard.api.reports.application.FiltroEstoqueMinimo;
import com.pharmaguard.api.reports.application.FiltroProdutosCriticos;
import com.pharmaguard.api.reports.application.FiltroReposicao;
import com.pharmaguard.api.reports.application.FiltroVencimentos;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoResponse;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCase;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosResponse;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCase;
import com.pharmaguard.api.reports.application.RelatorioReposicaoResponse;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCase;
import com.pharmaguard.api.reports.application.RelatorioVencimentosResponse;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean({LoteJpaRepository.class, SaldoLoteEstoqueJpaRepository.class,
        MovimentacaoEstoqueJpaRepository.class, MedicamentoJpaRepository.class})
public class RelatorioEstoqueJpaAdapter implements
        RelatorioProdutosCriticosUseCase.RelatorioProdutosCriticosRepositoryPort,
        RelatorioEstoqueMinimoUseCase.RelatorioEstoqueMinimoRepositoryPort,
        RelatorioVencimentosUseCase.RelatorioVencimentosRepositoryPort,
        RelatorioReposicaoUseCase.RelatorioReposicaoRepositoryPort {

    private final MedicamentoJpaRepository medicamentoJpa;
    private final LoteJpaRepository loteJpa;
    private final SaldoLoteEstoqueJpaRepository saldoJpa;
    private final MovimentacaoEstoqueJpaRepository movimentacaoJpa;

    public RelatorioEstoqueJpaAdapter(MedicamentoJpaRepository medicamentoJpa,
            LoteJpaRepository loteJpa, SaldoLoteEstoqueJpaRepository saldoJpa,
            MovimentacaoEstoqueJpaRepository movimentacaoJpa) {
        this.medicamentoJpa = Objects.requireNonNull(medicamentoJpa, "medicamentoJpa e obrigatorio");
        this.loteJpa = Objects.requireNonNull(loteJpa, "loteJpa e obrigatorio");
        this.saldoJpa = Objects.requireNonNull(saldoJpa, "saldoJpa e obrigatorio");
        this.movimentacaoJpa = Objects.requireNonNull(movimentacaoJpa, "movimentacaoJpa e obrigatorio");
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
        Map<Long, Integer> saldos = saldosPorMedicamento();
        Map<Long, Double> consumos = consumosPorMedicamento(filtro.periodoInicio(), filtro.periodoFim());
        int dias = (int) diasNoPeriodo(filtro.periodoInicio(), filtro.periodoFim());
        List<RelatorioReposicaoResponse.ItemReposicao> itens = new ArrayList<>();
        for (MedicamentoEntity medicamento : medicamentosFiltrados(filtro.medicamentoId(), filtro.categoriaId(), filtro.unidadeMedidaId())) {
            int saldo = saldos.getOrDefault(medicamento.getId(), 0);
            int leadTime = 7;
            int quantidadeSugerida = Math.max(0, (int) Math.ceil(consumos.getOrDefault(medicamento.getId(), 0d) / dias * leadTime * 2) - saldo);
            if (quantidadeSugerida == 0) {
                continue;
            }
            RelatorioReposicaoResponse.Urgencia urgencia = saldo == 0
                    ? RelatorioReposicaoResponse.Urgencia.CRITICA
                    : saldo <= leadTime ? RelatorioReposicaoResponse.Urgencia.ALTA : RelatorioReposicaoResponse.Urgencia.MEDIA;
            itens.add(new RelatorioReposicaoResponse.ItemReposicao(
                    medicamento.getId(), medicamento.getNome(), quantidadeSugerida, urgencia,
                    urgencia == RelatorioReposicaoResponse.Urgencia.MEDIA
                            ? RelatorioReposicaoResponse.Prioridade.MEDIA : RelatorioReposicaoResponse.Prioridade.ALTA,
                    null, leadTime, "Reposição calculada pelo consumo médio e lead time padrão"));
        }
        return itens;
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
