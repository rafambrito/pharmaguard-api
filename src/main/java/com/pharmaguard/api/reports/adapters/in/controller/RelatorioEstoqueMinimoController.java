package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.RelatorioEstoqueMinimoControllerDoc;
import com.pharmaguard.api.reports.application.FiltroEstoqueMinimo;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoResponse;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCase;
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
public class RelatorioEstoqueMinimoController implements RelatorioEstoqueMinimoControllerDoc {

    private final RelatorioEstoqueMinimoUseCase useCase;

    public RelatorioEstoqueMinimoController(RelatorioEstoqueMinimoUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/estoque-minimo")
    public ResponseEntity<RelatorioEstoqueMinimoResponse> gerar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId,
            @RequestParam(required = false) Long unidadeSaudeId) {

        return ResponseEntity.ok(useCase.gerar(new FiltroEstoqueMinimo(
                periodoInicio,
                periodoFim,
                medicamentoId,
                categoriaId,
                unidadeMedidaId, unidadeSaudeId)));
    }
}
