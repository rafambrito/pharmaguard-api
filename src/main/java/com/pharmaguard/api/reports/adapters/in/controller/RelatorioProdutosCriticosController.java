package com.pharmaguard.api.reports.adapters.in.controller;

import com.pharmaguard.api.reports.adapters.in.controller.doc.RelatorioProdutosCriticosControllerDoc;
import com.pharmaguard.api.reports.application.FiltroProdutosCriticos;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosResponse;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCase;
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
public class RelatorioProdutosCriticosController implements RelatorioProdutosCriticosControllerDoc {

    private final RelatorioProdutosCriticosUseCase useCase;

    public RelatorioProdutosCriticosController(RelatorioProdutosCriticosUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    @GetMapping("/produtos-criticos")
    public ResponseEntity<RelatorioProdutosCriticosResponse> gerar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodoFim,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long unidadeMedidaId) {

        return ResponseEntity.ok(useCase.gerar(new FiltroProdutosCriticos(
                periodoInicio,
                periodoFim,
                medicamentoId,
                categoriaId,
                unidadeMedidaId)));
    }
}
