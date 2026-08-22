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
public class RelatorioProdutosCriticosUseCaseImpl implements RelatorioProdutosCriticosUseCase {

    private final RelatorioProdutosCriticosRepositoryPort repository;

    public RelatorioProdutosCriticosUseCaseImpl(RelatorioProdutosCriticosRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public RelatorioProdutosCriticosResponse gerar(FiltroProdutosCriticos filtro) {
        validarFiltro(filtro);

        List<RelatorioProdutosCriticosResponse.ItemCritico> itens = repository.listarProdutosCriticos(filtro);
        if (itens == null) {
            itens = List.of();
        }

        itens = itens.stream()
                .sorted(Comparator
                        .comparingInt((RelatorioProdutosCriticosResponse.ItemCritico item) -> prioridadeRisco(item.risco()))
                        .thenComparingInt(item -> prioridadeUrgencia(item.urgencia()))
                        .reversed())
                .toList();

        return new RelatorioProdutosCriticosResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                itens.size(),
                itens);
    }

    private void validarFiltro(FiltroProdutosCriticos filtro) {
        Objects.requireNonNull(filtro, MSG_VALIDACAO_FILTRO_OBRIGATORIO);
        if (filtro.periodoInicio() == null || filtro.periodoFim() == null) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_OBRIGATORIO);
        }
        if (filtro.periodoInicio().isAfter(filtro.periodoFim())) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_INVALIDO);
        }
    }

    private int prioridadeRisco(RelatorioProdutosCriticosResponse.Risco risco) {
        return switch (risco) {
            case CRITICO -> 4;
            case ALTO -> 3;
            case MEDIO -> 2;
            case BAIXO -> 1;
        };
    }

    private int prioridadeUrgencia(RelatorioProdutosCriticosResponse.Urgencia urgencia) {
        return switch (urgencia) {
            case ALTA -> 3;
            case MEDIA -> 2;
            case BAIXA -> 1;
        };
    }
}
