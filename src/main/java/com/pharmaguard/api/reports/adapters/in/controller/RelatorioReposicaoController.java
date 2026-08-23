package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.RelatorioReposicaoControllerDoc;
import com.pharmaguard.api.reports.application.FiltroReposicao;
import com.pharmaguard.api.reports.application.RelatorioReposicaoResponse;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCase;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/relatorios")
public class RelatorioReposicaoController implements RelatorioReposicaoControllerDoc {

    private final RelatorioReposicaoUseCase useCase;

    public RelatorioReposicaoController(RelatorioReposicaoUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/reposicao")
    public ResponseEntity<RelatorioReposicaoResponse> consultar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Long unidadeSaudeId) {

        FiltroReposicao filtro = new FiltroReposicao(periodoInicio, periodoFim, medicamentoId, categoriaId, unidadeMedidaId, fornecedorId, unidadeSaudeId);
        return ResponseEntity.ok(useCase.gerar(filtro));
    }
}
