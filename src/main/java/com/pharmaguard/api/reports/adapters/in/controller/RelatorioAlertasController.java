package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.RelatorioAlertasControllerDoc;
import com.pharmaguard.api.reports.application.FiltroAlertas;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
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
public class RelatorioAlertasController implements RelatorioAlertasControllerDoc {

    private final RelatorioAlertasUseCase useCase;

    public RelatorioAlertasController(RelatorioAlertasUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/alertas")
    public ResponseEntity<RelatorioAlertasResponse> gerar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId,
            @RequestParam(required = false) Long fornecedorId) {

        FiltroAlertas filtro = new FiltroAlertas(
                periodoInicio,
                periodoFim,
                medicamentoId,
                categoriaId,
                unidadeMedidaId,
                fornecedorId);

        return ResponseEntity.ok(useCase.gerar(filtro));
    }
}