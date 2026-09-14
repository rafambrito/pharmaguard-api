package com.pharmaguard.api.intelligence.adapters.in.controller.doc;

import com.pharmaguard.api.intelligence.adapters.in.controller.ExplicarPainelRequest;
import com.pharmaguard.api.intelligence.application.ExplicacaoPainelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Inteligência", description = "APIs de IA e análise preditiva de estoque")
public interface IntelligenceControllerDoc {

    @Operation(
            summary = "Explicar dados e gerar insights com IA",
            description = "Gera uma análise em linguagem natural para o painel selecionado com base nos dados consolidados do estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Explicação gerada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou período incorreto")
    })
    ResponseEntity<ExplicacaoPainelResponse> explicar(
            @Parameter(description = "Dados para solicitação de explicação do painel") @Valid ExplicarPainelRequest request);
}
