package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_FILTRO_OBRIGATORIO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_INVALIDO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_OBRIGATORIO;
import org.springframework.stereotype.Service;

@Service
public class RelatorioVencimentosUseCaseImpl implements RelatorioVencimentosUseCase {

    private final RelatorioVencimentosRepositoryPort repository;

    public RelatorioVencimentosUseCaseImpl(RelatorioVencimentosRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public RelatorioVencimentosResponse gerar(FiltroVencimentos filtro) {
        validarFiltro(filtro);

        List<RelatorioVencimentosResponse.ItemVencimento> itens = repository.listarItensVencendo(filtro);
        if (itens == null) {
            itens = List.of();
        }

        itens = itens.stream()
                .sorted(Comparator
                        .comparingInt((RelatorioVencimentosResponse.ItemVencimento item) -> prioridadeSeveridade(item.severidade()))
                        .thenComparing(RelatorioVencimentosResponse.ItemVencimento::dataValidade)
                        .reversed())
                .toList();

        return new RelatorioVencimentosResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                itens.size(),
                itens);
    }

    private void validarFiltro(FiltroVencimentos filtro) {
        Objects.requireNonNull(filtro, MSG_VALIDACAO_FILTRO_OBRIGATORIO);
        if (filtro.periodoInicio() == null || filtro.periodoFim() == null) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_OBRIGATORIO);
        }
        if (filtro.periodoInicio().isAfter(filtro.periodoFim())) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_INVALIDO);
        }
    }

    private int prioridadeSeveridade(RelatorioVencimentosResponse.SeveridadeVencimento severidade) {
        return switch (severidade) {
            case CRITICA -> 4;
            case ALTA -> 3;
            case MEDIA -> 2;
            case BAIXA -> 1;
        };
    }
}
