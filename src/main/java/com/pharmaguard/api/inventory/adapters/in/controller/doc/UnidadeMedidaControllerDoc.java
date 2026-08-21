package com.pharmaguard.api.inventory.adapters.in.controller.doc;

import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.UnidadeMedidaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Unidades de Medida", description = "API para operacoes CRUD de unidades de medida")
public interface UnidadeMedidaControllerDoc {

    @Operation(summary = "Criar unidade de medida", description = "Cria uma nova unidade de medida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unidade de medida criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "409", description = "Unidade de medida ja cadastrada")
    })
    ResponseEntity<UnidadeMedidaResponse> criar(
            @Valid @RequestBody(description = "Dados da unidade de medida", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriarUnidadeMedidaRequest.class))) CriarUnidadeMedidaRequest request);

    @Operation(summary = "Listar unidades de medida", description = "Lista todas as unidades de medida")
    @ApiResponse(responseCode = "200", description = "Lista de unidades de medida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class)))
    ResponseEntity<List<UnidadeMedidaResponse>> listar();

    @Operation(summary = "Buscar unidade de medida por id", description = "Retorna uma unidade de medida pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade de medida encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unidade de medida nao encontrada")
    })
    ResponseEntity<UnidadeMedidaResponse> buscarPorId(@Parameter(description = "Id da unidade de medida", required = true) Long id);

    @Operation(summary = "Atualizar unidade de medida", description = "Atualiza os dados de uma unidade de medida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidade de medida atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnidadeMedidaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Unidade de medida nao encontrada"),
            @ApiResponse(responseCode = "409", description = "Unidade de medida ja cadastrada")
    })
    ResponseEntity<UnidadeMedidaResponse> atualizar(
            @Parameter(description = "Id da unidade de medida", required = true) Long id,
            @Valid @RequestBody(description = "Dados para atualizacao", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarUnidadeMedidaRequest.class))) AtualizarUnidadeMedidaRequest request);

    @Operation(summary = "Remover unidade de medida", description = "Remove uma unidade de medida por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unidade de medida removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Unidade de medida nao encontrada")
    })
    ResponseEntity<Void> remover(@Parameter(description = "Id da unidade de medida", required = true) Long id);
}
