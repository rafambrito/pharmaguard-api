package com.pharmaguard.api.supplier.adapters.in.controller.doc;

import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarContatoFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.CriarContatoFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.ContatoFornecedorResponse;
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

@Tag(name = "Contatos", description = "API para gestao de contatos de fornecedores")
public interface ContatoFornecedorControllerDoc {

    @Operation(summary = "Cadastrar contato do fornecedor", description = "Cadastra um contato para um fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato cadastrado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContatoFornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Contato ja cadastrado para o fornecedor")
    })
    ResponseEntity<ContatoFornecedorResponse> criar(
            @Parameter(description = "Id do fornecedor", required = true) Long fornecedorId,
            @Valid @RequestBody(description = "Dados do contato", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CriarContatoFornecedorRequest.class))) CriarContatoFornecedorRequest request);

    @Operation(summary = "Listar contatos do fornecedor", description = "Lista os contatos de um fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contatos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContatoFornecedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado")
    })
    ResponseEntity<List<ContatoFornecedorResponse>> listar(@Parameter(description = "Id do fornecedor", required = true) Long fornecedorId);

    @Operation(summary = "Buscar contato do fornecedor por id", description = "Retorna um contato pelo id dentro do fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContatoFornecedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fornecedor ou contato nao encontrado")
    })
    ResponseEntity<ContatoFornecedorResponse> buscarPorId(
            @Parameter(description = "Id do fornecedor", required = true) Long fornecedorId,
            @Parameter(description = "Id do contato", required = true) Long contatoId);

    @Operation(summary = "Atualizar contato do fornecedor", description = "Atualiza um contato de fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContatoFornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Fornecedor ou contato nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Contato ja cadastrado para o fornecedor")
    })
    ResponseEntity<ContatoFornecedorResponse> atualizar(
            @Parameter(description = "Id do fornecedor", required = true) Long fornecedorId,
            @Parameter(description = "Id do contato", required = true) Long contatoId,
            @Valid @RequestBody(description = "Dados para atualizacao", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarContatoFornecedorRequest.class))) AtualizarContatoFornecedorRequest request);

    @Operation(summary = "Remover contato do fornecedor", description = "Remove um contato de fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contato removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor ou contato nao encontrado")
    })
    ResponseEntity<Void> remover(
            @Parameter(description = "Id do fornecedor", required = true) Long fornecedorId,
            @Parameter(description = "Id do contato", required = true) Long contatoId);
}