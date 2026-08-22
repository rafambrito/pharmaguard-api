package com.pharmaguard.api.reports.application;

import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_FILTRO_OBRIGATORIO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_INVALIDO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_OBRIGATORIO;
import org.springframework.stereotype.Service;

@Service
public class RelatorioConsumoUseCaseImpl implements RelatorioConsumoUseCase {

    private final RelatorioConsumoRepositoryPort repository;

    public RelatorioConsumoUseCaseImpl(RelatorioConsumoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public RelatorioConsumoResponse gerar(FiltroConsumo filtro) {
        validarFiltro(filtro);

        List<MovimentacaoEstoque> saidas = repository.listarSaidas(filtro);
        Map<Long, List<MovimentacaoEstoque>> grupos = new HashMap<>();

        for (MovimentacaoEstoque movimentacao : saidas) {
            if (movimentacao.getMedicamento() == null) {
                continue;
            }
            grupos.computeIfAbsent(movimentacao.getMedicamento().getId(), ignored -> new ArrayList<>())
                    .add(movimentacao);
        }

        List<RelatorioConsumoResponse.ItemConsumo> itens = new ArrayList<>();
        double totalConsumido = 0d;

        for (Map.Entry<Long, List<MovimentacaoEstoque>> entry : grupos.entrySet()) {
            List<MovimentacaoEstoque> movimentacoes = entry.getValue();
            double quantidade = movimentacoes.stream()
                    .mapToDouble(MovimentacaoEstoque::getQuantidade)
                    .sum();
            totalConsumido += quantidade;

            Medicamento medicamento = movimentacoes.getFirst().getMedicamento();
            itens.add(new RelatorioConsumoResponse.ItemConsumo(
                    medicamento.getId(),
                    medicamento.getNome(),
                    medicamento.getCategoria() != null ? medicamento.getCategoria().getNome() : null,
                    medicamento.getUnidadeMedida() != null ? medicamento.getUnidadeMedida().getSigla() : null,
                    quantidade,
                    quantidade / Math.max(1d, numeroDiasNoPeriodo(filtro.periodoInicio(), filtro.periodoFim()))));
        }

        itens.sort(Comparator.comparingDouble(RelatorioConsumoResponse.ItemConsumo::quantidadeConsumida).reversed());

        double mediaDiaria = totalConsumido / Math.max(1d, numeroDiasNoPeriodo(filtro.periodoInicio(), filtro.periodoFim()));
        RelatorioConsumoResponse.TendenciaConsumo tendencia = determinarTendencia(saidas, filtro.periodoInicio(), filtro.periodoFim());

        return new RelatorioConsumoResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                filtro,
                totalConsumido,
                mediaDiaria,
                tendencia,
                itens);
    }

    private void validarFiltro(FiltroConsumo filtro) {
        Objects.requireNonNull(filtro, MSG_VALIDACAO_FILTRO_OBRIGATORIO);
        if (filtro.periodoInicio() == null || filtro.periodoFim() == null) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_OBRIGATORIO);
        }
        if (filtro.periodoInicio().isAfter(filtro.periodoFim())) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_INVALIDO);
        }
    }

    private long numeroDiasNoPeriodo(LocalDate inicio, LocalDate fim) {
        return ChronoUnit.DAYS.between(inicio, fim) + 1;
    }

    private RelatorioConsumoResponse.TendenciaConsumo determinarTendencia(
            List<MovimentacaoEstoque> saidas,
            LocalDate periodoInicio,
            LocalDate periodoFim) {

        if (saidas.isEmpty()) {
            return RelatorioConsumoResponse.TendenciaConsumo.ESTAVEL;
        }

        long totalDias = numeroDiasNoPeriodo(periodoInicio, periodoFim);
        if (totalDias <= 1) {
            return RelatorioConsumoResponse.TendenciaConsumo.ESTAVEL;
        }

        long pontoMedio = totalDias / 2;
        LocalDate meta = periodoInicio.plusDays(pontoMedio);

        double consumoPrimeiraMetade = saidas.stream()
                .filter(saida -> !saida.getDataMovimentacao().toLocalDate().isAfter(meta))
                .mapToDouble(MovimentacaoEstoque::getQuantidade)
                .sum();

        double consumoSegundaMetade = saidas.stream()
                .filter(saida -> saida.getDataMovimentacao().toLocalDate().isAfter(meta))
                .mapToDouble(MovimentacaoEstoque::getQuantidade)
                .sum();

        if (consumoSegundaMetade > consumoPrimeiraMetade * 1.1d) {
            return RelatorioConsumoResponse.TendenciaConsumo.CRESCENTE;
        }
        if (consumoSegundaMetade < consumoPrimeiraMetade * 0.9d) {
            return RelatorioConsumoResponse.TendenciaConsumo.DECRESCENTE;
        }
        return RelatorioConsumoResponse.TendenciaConsumo.ESTAVEL;
    }
}
