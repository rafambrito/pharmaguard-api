package com.pharmaguard.api.reports.adapters.in.controller.doc;

import com.pharmaguard.api.reports.application.RelatorioReposicaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Relatórios", description = "Endpoints de consulta e apoio decisional")
public interface RelatorioReposicaoControllerDoc {

    @Operation(
            summary = "Relatório de reposição",
            description = "Retorna itens que devem receber reposição com base em consumo histórico, lead time e risco de ruptura.")
    @ApiResponse(
            responseCode = "200",
            description = "Relatório gerado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioReposicaoResponse.class)))
    @GetMapping("/reposicao")
    ResponseEntity<RelatorioReposicaoResponse> consultar(
            @Parameter(description = "Data inicial do período")
            @RequestParam LocalDate periodoInicio,
            @Parameter(description = "Data final do período")
            @RequestParam LocalDate periodoFim,
            @Parameter(description = "Filtro por medicamento")
            @RequestParam(required = false) Long medicamentoId,
            @Parameter(description = "Filtro por categoria")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Filtro por unidade de medida")
            @RequestParam(required = false) Long unidadeMedidaId,
            @Parameter(description = "Filtro por fornecedor")
            @RequestParam(required = false) Long fornecedorId,
            @Parameter(description = "Filtro por unidade de saude")
            @RequestParam(required = false) Long unidadeSaudeId);
}
