package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.DashboardOverviewControllerDoc;
import com.pharmaguard.api.reports.application.DashboardOverviewResponse;
import com.pharmaguard.api.reports.application.DashboardOverviewUseCase;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
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
@RequestMapping("/api/v1/dashboard")
public class DashboardOverviewController implements DashboardOverviewControllerDoc {

    private final DashboardOverviewUseCase useCase;

    public DashboardOverviewController(DashboardOverviewUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewResponse> consultar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) Long unidadeSaudeId) {

        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                periodoInicio,
                periodoFim,
                medicamentoId,
                categoriaId,
                unidadeMedidaId,
                fornecedorId,
                unidadeSaudeId);

        return ResponseEntity.ok(useCase.consultar(filtro));
    }
}