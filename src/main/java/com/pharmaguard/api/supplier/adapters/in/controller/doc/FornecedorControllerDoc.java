package com.pharmaguard.api.supplier.adapters.in.controller.doc;

import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.CriarFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.FornecedorResponse;
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

@Tag(name = "Fornecedores", description = "API para operacoes CRUD de fornecedores")
public interface FornecedorControllerDoc {

    @Operation(summary = "Criar fornecedor", description = "Cria um novo fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornecedor criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "409", description = "Fornecedor ja cadastrado")
    })
    ResponseEntity<FornecedorResponse> criar(
            @Valid @RequestBody(description = "Dados do fornecedor", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriarFornecedorRequest.class))) CriarFornecedorRequest request);

    @Operation(summary = "Listar fornecedores", description = "Lista todos os fornecedores")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class)))
    ResponseEntity<List<FornecedorResponse>> listar();

    @Operation(summary = "Buscar fornecedor por id", description = "Retorna um fornecedor pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado")
    })
    ResponseEntity<FornecedorResponse> buscarPorId(@Parameter(description = "Id do fornecedor", required = true) Long id);

    @Operation(summary = "Atualizar fornecedor", description = "Atualiza os dados de um fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Fornecedor ja cadastrado")
    })
    ResponseEntity<FornecedorResponse> atualizar(
            @Parameter(description = "Id do fornecedor", required = true) Long id,
            @Valid @RequestBody(description = "Dados para atualizacao", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarFornecedorRequest.class))) AtualizarFornecedorRequest request);

    @Operation(summary = "Remover fornecedor", description = "Remove um fornecedor por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fornecedor removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado")
    })
    ResponseEntity<Void> remover(@Parameter(description = "Id do fornecedor", required = true) Long id);
}