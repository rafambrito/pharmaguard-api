package com.pharmaguard.api.supplier.adapters.in.controller.doc;

import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarLeadTimeFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.LeadTimeFornecedorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Lead Time", description = "API para consulta e atualizacao de lead time de fornecedores")
public interface LeadTimeFornecedorControllerDoc {

    @Operation(summary = "Consultar lead time do fornecedor", description = "Retorna o lead time de um fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead time do fornecedor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadTimeFornecedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado")
    })
    ResponseEntity<LeadTimeFornecedorResponse> consultar(@Parameter(description = "Id do fornecedor", required = true) Long fornecedorId);

    @Operation(summary = "Atualizar lead time do fornecedor", description = "Atualiza o lead time de um fornecedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead time atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeadTimeFornecedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Fornecedor nao encontrado")
    })
    ResponseEntity<LeadTimeFornecedorResponse> atualizar(
            @Parameter(description = "Id do fornecedor", required = true) Long fornecedorId,
            @Valid @RequestBody(description = "Dados para atualizacao do lead time", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AtualizarLeadTimeFornecedorRequest.class))) AtualizarLeadTimeFornecedorRequest request);
}