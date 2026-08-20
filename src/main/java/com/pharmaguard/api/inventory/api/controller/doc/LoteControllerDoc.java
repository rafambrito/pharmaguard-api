package com.pharmaguard.api.inventory.api.controller.doc;

import com.pharmaguard.api.inventory.api.dto.request.CadastrarLoteRequest;
import com.pharmaguard.api.inventory.api.dto.response.LoteResponse;
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

@Tag(name = "Lotes", description = "API para operacoes de lotes de medicamentos")
public interface LoteControllerDoc {

    @Operation(summary = "Cadastrar lote", description = "Cadastra um lote para um medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lote cadastrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoteResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou validade vencida"),
            @ApiResponse(responseCode = "404", description = "Medicamento nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Lote ja cadastrado para este medicamento")
    })
    ResponseEntity<LoteResponse> cadastrar(
            @Parameter(description = "Id do medicamento", required = true) Long medicamentoId,
            @Valid @RequestBody(description = "Dados do lote", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CadastrarLoteRequest.class))) CadastrarLoteRequest request);

    @Operation(summary = "Listar lotes do medicamento", description = "Lista os lotes de um medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de lotes do medicamento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoteResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento nao encontrado")
    })
    ResponseEntity<List<LoteResponse>> listarPorMedicamento(@Parameter(description = "Id do medicamento", required = true) Long medicamentoId);

    @Operation(summary = "Buscar lote por id", description = "Retorna um lote pelo id dentro do medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lote encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoteResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lote nao encontrado")
    })
    ResponseEntity<LoteResponse> buscarPorId(
            @Parameter(description = "Id do medicamento", required = true) Long medicamentoId,
            @Parameter(description = "Id do lote", required = true) Long loteId);

    @Operation(summary = "Remover lote", description = "Remove um lote de um medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lote removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Lote nao encontrado")
    })
    ResponseEntity<Void> remover(
            @Parameter(description = "Id do medicamento", required = true) Long medicamentoId,
            @Parameter(description = "Id do lote", required = true) Long loteId);
}
