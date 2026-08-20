package com.pharmaguard.api.inventory.api.controller.doc;

import com.pharmaguard.api.inventory.api.dto.request.AtualizarMedicamentoRequest;
import com.pharmaguard.api.inventory.api.dto.request.CriarMedicamentoRequest;
import com.pharmaguard.api.inventory.api.dto.response.MedicamentoResponse;
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

@Tag(name = "Medicamentos", description = "API para operacoes CRUD de medicamentos")
public interface MedicamentoControllerDoc {

    @Operation(summary = "Criar medicamento", description = "Cria um novo medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medicamento criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicamentoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Categoria ou unidade de medida nao encontrada"),
            @ApiResponse(responseCode = "409", description = "Medicamento ja cadastrado")
    })
    ResponseEntity<MedicamentoResponse> criar(
            @Valid @RequestBody(description = "Dados do medicamento", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriarMedicamentoRequest.class))) CriarMedicamentoRequest request);

    @Operation(summary = "Listar medicamentos", description = "Lista todos os medicamentos")
    @ApiResponse(responseCode = "200", description = "Lista de medicamentos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicamentoResponse.class)))
    ResponseEntity<List<MedicamentoResponse>> listar();

    @Operation(summary = "Buscar medicamento por id", description = "Retorna um medicamento pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicamentoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento nao encontrado")
    })
    ResponseEntity<MedicamentoResponse> buscarPorId(@Parameter(description = "Id do medicamento", required = true) Long id);

    @Operation(summary = "Atualizar medicamento", description = "Atualiza os dados de um medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicamento atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicamentoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Medicamento, categoria ou unidade de medida nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Medicamento ja cadastrado")
    })
    ResponseEntity<MedicamentoResponse> atualizar(
            @Parameter(description = "Id do medicamento", required = true) Long id,
            @Valid @RequestBody(description = "Dados para atualizacao", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarMedicamentoRequest.class))) AtualizarMedicamentoRequest request);

    @Operation(summary = "Remover medicamento", description = "Remove um medicamento por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medicamento removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Medicamento nao encontrado")
    })
    ResponseEntity<Void> remover(@Parameter(description = "Id do medicamento", required = true) Long id);
}
