package com.pharmaguard.api.reports.adapters.in.controller.doc;

import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;

@Tag(name = "Relatorios", description = "APIs para identificacao de produtos criticos")
public interface RelatorioProdutosCriticosControllerDoc {

    @Operation(summary = "Gerar relatorio de produtos criticos", description = "Lista medicamentos em risco com base em saldo, consumo e urgencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatorio gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos ou periodo invalido")
    })
    ResponseEntity<RelatorioProdutosCriticosResponse> gerar(
            @Parameter(description = "Data inicial do periodo da consulta", required = true) LocalDate periodoInicio,
            @Parameter(description = "Data final do periodo da consulta", required = true) LocalDate periodoFim,
            @Parameter(description = "Id do medicamento para filtrar") Long medicamentoId,
            @Parameter(description = "Id da categoria para filtrar") Long categoriaId,
            @Parameter(description = "Id da unidade de medida para filtrar") Long unidadeMedidaId,
            @Parameter(description = "Id da unidade de saude para filtrar") Long unidadeSaudeId);
}
