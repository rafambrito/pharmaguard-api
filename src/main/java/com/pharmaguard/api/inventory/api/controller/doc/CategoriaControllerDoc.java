package com.pharmaguard.api.inventory.api.controller.doc;

import com.pharmaguard.api.inventory.api.dto.request.AtualizarCategoriaRequest;
import com.pharmaguard.api.inventory.api.dto.request.CriarCategoriaRequest;
import com.pharmaguard.api.inventory.api.dto.response.CategoriaResponse;
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

@Tag(name = "Categorias", description = "API para operacoes CRUD de categorias")
public interface CategoriaControllerDoc {

    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "409", description = "Categoria ja cadastrada")
    })
    ResponseEntity<CategoriaResponse> criar(
            @Valid @RequestBody(description = "Dados da categoria", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriarCategoriaRequest.class))) CriarCategoriaRequest request);

    @Operation(summary = "Listar categorias", description = "Lista todas as categorias")
    @ApiResponse(responseCode = "200", description = "Lista de categorias", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class)))
    ResponseEntity<List<CategoriaResponse>> listar();

    @Operation(summary = "Buscar categoria por id", description = "Retorna uma categoria pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoria nao encontrada")
    })
    ResponseEntity<CategoriaResponse> buscarPorId(@Parameter(description = "Id da categoria", required = true) Long id);

    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Categoria nao encontrada"),
            @ApiResponse(responseCode = "409", description = "Categoria ja cadastrada")
    })
    ResponseEntity<CategoriaResponse> atualizar(
            @Parameter(description = "Id da categoria", required = true) Long id,
            @Valid @RequestBody(description = "Dados para atualizacao", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarCategoriaRequest.class))) AtualizarCategoriaRequest request);

    @Operation(summary = "Remover categoria", description = "Remove uma categoria por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria nao encontrada")
    })
    ResponseEntity<Void> remover(@Parameter(description = "Id da categoria", required = true) Long id);
}
