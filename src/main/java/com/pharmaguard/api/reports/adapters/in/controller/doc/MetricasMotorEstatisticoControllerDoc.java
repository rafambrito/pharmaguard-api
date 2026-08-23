package com.pharmaguard.api.reports.adapters.in.controller.doc;

import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;

@Tag(name = "Relatorios", description = "APIs de metricas integradas do motor estatistico")
public interface MetricasMotorEstatisticoControllerDoc {

    @Operation(
            summary = "Consultar metricas do motor estatistico",
            description = "Exposicao consolidada para integracao com estoque, scheduler e dashboards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metricas retornadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos ou periodo invalido")
    })
    ResponseEntity<MetricasMotorEstatisticoResponse> consultar(
            @Parameter(description = "Data inicial do periodo") LocalDate periodoInicio,
            @Parameter(description = "Data final do periodo") LocalDate periodoFim,
            @Parameter(description = "Filtro por medicamento") Long medicamentoId,
            @Parameter(description = "Filtro por categoria") Long categoriaId,
            @Parameter(description = "Filtro por unidade de medida") Long unidadeMedidaId,
            @Parameter(description = "Filtro por fornecedor") Long fornecedorId,
            @Parameter(description = "Filtro por unidade de saude") Long unidadeSaudeId);
}