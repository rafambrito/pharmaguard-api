package com.pharmaguard.api.reports.adapters.in.controller.doc;

import com.pharmaguard.api.reports.application.DashboardOverviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;

@Tag(name = "Dashboard", description = "APIs de consulta consolidada do dashboard")
public interface DashboardOverviewControllerDoc {

    @Operation(
            summary = "Consultar visao geral do dashboard",
            description = "Consolida metricas, alertas, consumo e sugestoes de reposicao para a tela inicial")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard retornado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos ou periodo invalido")
    })
    ResponseEntity<DashboardOverviewResponse> consultar(
            @Parameter(description = "Data inicial do periodo") LocalDate periodoInicio,
            @Parameter(description = "Data final do periodo") LocalDate periodoFim,
            @Parameter(description = "Filtro por medicamento") Long medicamentoId,
            @Parameter(description = "Filtro por categoria") Long categoriaId,
            @Parameter(description = "Filtro por unidade de medida") Long unidadeMedidaId,
            @Parameter(description = "Filtro por fornecedor") Long fornecedorId,
            @Parameter(description = "Filtro por unidade de saude") Long unidadeSaudeId);
}