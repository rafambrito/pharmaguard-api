package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.MetricasMotorEstatisticoControllerDoc;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
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
public class MetricasMotorEstatisticoController implements MetricasMotorEstatisticoControllerDoc {

    private final MetricasMotorEstatisticoUseCase useCase;

    public MetricasMotorEstatisticoController(MetricasMotorEstatisticoUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/metricas-motor")
    public ResponseEntity<MetricasMotorEstatisticoResponse> consultar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId,
            @RequestParam(required = false) Long fornecedorId) {

        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                periodoInicio,
                periodoFim,
                medicamentoId,
                categoriaId,
                unidadeMedidaId,
                fornecedorId);

        return ResponseEntity.ok(useCase.consultar(filtro));
    }
}