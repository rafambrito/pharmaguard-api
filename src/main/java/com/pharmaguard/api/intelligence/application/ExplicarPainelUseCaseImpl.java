package com.pharmaguard.api.intelligence.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmaguard.api.intelligence.domain.GeradorInsightPort;
import com.pharmaguard.api.intelligence.domain.InsightPrompt;
import com.pharmaguard.api.reports.application.DashboardOverviewResponse;
import com.pharmaguard.api.reports.application.DashboardOverviewUseCase;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplicarPainelUseCaseImpl implements ExplicarPainelUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExplicarPainelUseCaseImpl.class);
    private static final Pattern NUMERO = Pattern.compile("\\d+(?:[.,]\\d+)?");
    private static final Pattern CONTEUDO_CLINICO = Pattern.compile("(?i)\\b(dose|dosagem|diagnostico|prescri[çc][ãa]o)\\b");
    private static final String SISTEMA = """
            Voce e um redator de informacoes operacionais para estoque farmaceutico.
            Use exclusivamente os dados autorizados recebidos no contexto.
            Nao invente numeros, datas, medicamentos, unidades ou fatos.
            Nao faca diagnosticos clinicos, indicacoes ou recomendacoes de dosagem.
            Explique somente a situacao logistica de estoque em portugues claro, em no maximo tres frases.
            Se os dados forem insuficientes, declare essa limitacao sem inferir fatos.
            Retorne apenas texto simples, sem Markdown.
            """;

    private final DashboardOverviewUseCase dashboardOverviewUseCase;
    private final GeradorInsightPort geradorInsightPort;
    private final ObjectMapper objectMapper;

    public ExplicarPainelUseCaseImpl(
            DashboardOverviewUseCase dashboardOverviewUseCase,
            GeradorInsightPort geradorInsightPort,
            ObjectMapper objectMapper) {
        this.dashboardOverviewUseCase = dashboardOverviewUseCase;
        this.geradorInsightPort = geradorInsightPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public ExplicacaoPainelResponse explicar(ExplicarPainelCommand command) {
        DashboardOverviewResponse overview = dashboardOverviewUseCase.consultar(command.filtro());
        Object painel = selecionarPainel(command, overview);
        String contexto = serializarContexto(command, painel);

        try {
            String explicacao = sanitizar(geradorInsightPort.gerar(new InsightPrompt(SISTEMA, contexto)), contexto);
            return new ExplicacaoPainelResponse(
                    command.tipoPainel(), explicacao, ExplicacaoPainelResponse.OrigemExplicacao.OLLAMA, Instant.now());
        } catch (RuntimeException exception) {
            log.warn("Geracao de explicacao indisponivel para o painel {}", command.tipoPainel(), exception);
            return new ExplicacaoPainelResponse(
                    command.tipoPainel(), fallback(command, overview), ExplicacaoPainelResponse.OrigemExplicacao.FALLBACK, Instant.now());
        }
    }

    private Object selecionarPainel(ExplicarPainelCommand command, DashboardOverviewResponse overview) {
        return switch (command.tipoPainel()) {
            case ALERTAS -> new PainelAlertas(overview.resumoAlertas(), overview.alertas());
            case CONSUMO -> overview.consumo();
            case METRICAS -> overview.metricas();
            case TRANSFERENCIAS -> overview.reposicoes();
            case ESTOQUE_POR_UNIDADE -> overview.unidades();
        };
    }

    private String serializarContexto(ExplicarPainelCommand command, Object painel) {
        try {
            return """
                    TIPO_DO_PAINEL: %s
                    DADOS_AUTORIZADOS:
                    %s
                    """.formatted(command.tipoPainel(), objectMapper.writeValueAsString(painel));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel preparar o contexto do painel", exception);
        }
    }

    private String sanitizar(String resposta, String contexto) {
        if (resposta == null || resposta.isBlank()) {
            throw new IllegalStateException("O gerador retornou uma explicacao vazia");
        }
        String texto = resposta.replaceAll("[`#*_]", "").replaceAll("\\s+", " ").trim();
        if (texto.length() > 1_200) {
            throw new IllegalStateException("O gerador retornou uma explicacao maior que o limite permitido");
        }
        if (CONTEUDO_CLINICO.matcher(texto).find()) {
            throw new IllegalStateException("O gerador retornou conteudo clinico nao permitido");
        }
        validarNumeros(texto, contexto);
        return texto;
    }

    private void validarNumeros(String texto, String contexto) {
        Set<String> numerosAutorizados = extrairNumeros(contexto);
        for (String numero : extrairNumeros(texto)) {
            if (!numerosAutorizados.contains(numero)) {
                throw new IllegalStateException("O gerador retornou um numero ausente do contexto autorizado");
            }
        }
    }

    private Set<String> extrairNumeros(String texto) {
        Set<String> numeros = new HashSet<>();
        Matcher matcher = NUMERO.matcher(texto);
        while (matcher.find()) {
            numeros.add(matcher.group().replace(',', '.'));
        }
        return numeros;
    }

    private String fallback(ExplicarPainelCommand command, DashboardOverviewResponse overview) {
        return "A explicacao com IA esta indisponivel. Consulte os dados calculados do painel %s para o periodo de %s a %s."
                .formatted(command.tipoPainel().name().toLowerCase().replace('_', ' '), overview.periodoInicio(), overview.periodoFim());
    }

    private record PainelAlertas(Object resumo, Object itens) {
    }
}