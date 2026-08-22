package com.pharmaguard.api.reports.application;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_FILTRO_OBRIGATORIO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_INVALIDO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_VALIDACAO_PERIODO_OBRIGATORIO;
import org.springframework.stereotype.Service;

@Service
public class RelatorioEstoqueMinimoUseCaseImpl implements RelatorioEstoqueMinimoUseCase {

    private final RelatorioEstoqueMinimoRepositoryPort repository;

    public RelatorioEstoqueMinimoUseCaseImpl(RelatorioEstoqueMinimoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public RelatorioEstoqueMinimoResponse gerar(FiltroEstoqueMinimo filtro) {
        validarFiltro(filtro);

        List<RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo> itens = repository.listarItensAbaixoMinimo(filtro);
        if (itens == null) {
            itens = List.of();
        }

        itens = itens.stream()
                .sorted(Comparator
                        .comparingInt((RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo item) -> prioridadeStatus(item.status()))
                        .thenComparingInt(RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo::saldoAtual)
                        .reversed())
                .toList();

        return new RelatorioEstoqueMinimoResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                itens.size(),
                itens);
    }

    private void validarFiltro(FiltroEstoqueMinimo filtro) {
        Objects.requireNonNull(filtro, MSG_VALIDACAO_FILTRO_OBRIGATORIO);
        if (filtro.periodoInicio() == null || filtro.periodoFim() == null) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_OBRIGATORIO);
        }
        if (filtro.periodoInicio().isAfter(filtro.periodoFim())) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_INVALIDO);
        }
    }

    private int prioridadeStatus(RelatorioEstoqueMinimoResponse.StatusEstoque status) {
        return switch (status) {
            case RUPTURA -> 3;
            case BAIXO -> 2;
            case NORMAL -> 1;
        };
    }
}
