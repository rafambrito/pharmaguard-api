package com.pharmaguard.api.intelligence.adapters.in.controller;

import com.pharmaguard.api.intelligence.adapters.in.controller.doc.IntelligenceControllerDoc;
import com.pharmaguard.api.intelligence.application.ExplicarPainelCommand;
import com.pharmaguard.api.intelligence.application.ExplicarPainelUseCase;
import com.pharmaguard.api.intelligence.application.ExplicacaoPainelResponse;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/intelligence")
public class IntelligenceController implements IntelligenceControllerDoc {

    private final ExplicarPainelUseCase useCase;

    public IntelligenceController(ExplicarPainelUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @PostMapping("/explicar")
    public ResponseEntity<ExplicacaoPainelResponse> explicar(@Valid @RequestBody ExplicarPainelRequest request) {
        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                request.periodoInicio(), request.periodoFim(), request.medicamentoId(), request.categoriaId(),
                request.unidadeMedidaId(), request.fornecedorId(), request.unidadeSaudeId());
        return ResponseEntity.ok(useCase.explicar(new ExplicarPainelCommand(request.tipoPainel(), filtro)));
    }
}