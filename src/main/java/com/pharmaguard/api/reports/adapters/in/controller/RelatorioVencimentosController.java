package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.RelatorioVencimentosControllerDoc;
import com.pharmaguard.api.reports.application.FiltroVencimentos;
import com.pharmaguard.api.reports.application.RelatorioVencimentosResponse;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCase;
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
public class RelatorioVencimentosController implements RelatorioVencimentosControllerDoc {

    private final RelatorioVencimentosUseCase useCase;

    public RelatorioVencimentosController(RelatorioVencimentosUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/vencimentos")
    public ResponseEntity<RelatorioVencimentosResponse> gerar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId,
            @RequestParam(required = false) Long unidadeSaudeId) {

        return ResponseEntity.ok(useCase.gerar(new FiltroVencimentos(
                periodoInicio,
                periodoFim,
                medicamentoId,
                categoriaId,
                unidadeMedidaId, unidadeSaudeId)));
    }
}
