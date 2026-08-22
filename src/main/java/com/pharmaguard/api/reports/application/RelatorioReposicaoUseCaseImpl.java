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
public class RelatorioReposicaoUseCaseImpl implements RelatorioReposicaoUseCase {

    private final RelatorioReposicaoRepositoryPort repositoryPort;

    public RelatorioReposicaoUseCaseImpl(RelatorioReposicaoRepositoryPort repositoryPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "repositoryPort e obrigatorio");
    }

    @Override
    public RelatorioReposicaoResponse gerar(FiltroReposicao filtro) {
        validarFiltro(filtro);

        List<RelatorioReposicaoResponse.ItemReposicao> itens = repositoryPort.listarItensParaReposicao(filtro);
        if (itens == null) {
            itens = List.of();
        }

        itens = itens
                .stream()
                .sorted(Comparator.comparing((RelatorioReposicaoResponse.ItemReposicao item) -> item.prioridade())
                        .thenComparing((RelatorioReposicaoResponse.ItemReposicao item) -> item.urgencia())
                        .reversed())
                .toList();

        return new RelatorioReposicaoResponse(
            filtro.periodoInicio(),
            filtro.periodoFim(),
            itens.size(),
            itens);
    }

    private void validarFiltro(FiltroReposicao filtro) {
        Objects.requireNonNull(filtro, MSG_VALIDACAO_FILTRO_OBRIGATORIO);

        LocalDate inicio = filtro.periodoInicio();
        LocalDate fim = filtro.periodoFim();

        if (inicio == null || fim == null) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_OBRIGATORIO);
        }

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(MSG_VALIDACAO_PERIODO_INVALIDO);
        }
    }
}
