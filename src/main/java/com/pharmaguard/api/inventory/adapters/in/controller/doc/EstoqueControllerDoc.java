package com.pharmaguard.api.inventory.adapters.in.controller.doc;

import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarEntradaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarSaidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.EntradaEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.LoteVencimentoResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.MovimentacaoEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaidaEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaldoEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaldoLoteResponse;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.shared.config.MessageKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Estoque", description = "API para operacoes de estoque farmacautico")
public interface EstoqueControllerDoc {

    @Operation(summary = "Registrar entrada de estoque", description = "Registra uma entrada vinculada a medicamento e lote")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entrada registrada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Medicamento ou lote nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Regra de negocio violada")
    })
    ResponseEntity<EntradaEstoqueResponse> registrarEntrada(
            @Valid @RequestBody(description = "Dados da entrada", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegistrarEntradaRequest.class))) RegistrarEntradaRequest request);

    @Operation(summary = "Listar entradas de estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de entradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class)))
    })
    ResponseEntity<List<EntradaEstoqueResponse>> listarEntradas(
            @Parameter(description = "Id do medicamento") @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @Parameter(description = "Id do lote") @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long loteId,
            @Parameter(description = "Id da unidade de saude", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long unidadeId);

    @Operation(summary = "Buscar entrada por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrada encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntradaEstoqueResponse.class))),
            @ApiResponse(responseCode = "404", description = "Entrada nao encontrada")
    })
    ResponseEntity<EntradaEstoqueResponse> buscarEntrada(
            @Parameter(description = "Id da entrada", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long id);

    @Operation(summary = "Registrar saida de estoque", description = "Registra uma saida aplicando a regra FEFO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saida registrada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaidaEstoqueResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Medicamento nao encontrado"),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    })
    ResponseEntity<SaidaEstoqueResponse> registrarSaida(
            @Valid @RequestBody(description = "Dados da saida", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegistrarSaidaRequest.class))) RegistrarSaidaRequest request);

    @Operation(summary = "Listar saidas de estoque")
    ResponseEntity<List<SaidaEstoqueResponse>> listarSaidas(
            @Parameter(description = "Id do medicamento") @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @Parameter(description = "Id da unidade de saude", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long unidadeId);

    @Operation(summary = "Buscar saida por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saida encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaidaEstoqueResponse.class))),
            @ApiResponse(responseCode = "404", description = "Saida nao encontrada")
    })
    ResponseEntity<SaidaEstoqueResponse> buscarSaida(
            @Parameter(description = "Id da saida", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long id);

    @Operation(summary = "Listar movimentacoes de estoque")
    ResponseEntity<List<MovimentacaoEstoqueResponse>> listarMovimentacoes(
            @Parameter(description = "Id do medicamento") @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @Parameter(description = "Id do lote") @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long loteId,
            @Parameter(description = "Tipo da movimentacao") MovimentacaoEstoque.Tipo tipo,
            @Parameter(description = "Data inicial") LocalDate dataInicial,
            @Parameter(description = "Data final") LocalDate dataFinal,
            @Parameter(description = "Id da unidade de saude", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long unidadeId);

    @Operation(summary = "Consultar saldo por medicamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SaldoEstoqueResponse.class))),
            @ApiResponse(responseCode = "404", description = "Medicamento nao encontrado")
    })
    ResponseEntity<SaldoEstoqueResponse> consultarSaldo(
            @Parameter(description = "Id do medicamento", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @Parameter(description = "Id da unidade de saude", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long unidadeId);

    @Operation(summary = "Consultar saldo por lote do medicamento")
    ResponseEntity<List<SaldoLoteResponse>> consultarSaldoPorLote(
            @Parameter(description = "Id do medicamento", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @Parameter(description = "Id da unidade de saude", required = true) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long unidadeId);

    @Operation(summary = "Listar lotes vencidos e proximos do vencimento")
    ResponseEntity<List<LoteVencimentoResponse>> listarVencimentos(
            @Parameter(description = "Quantidade de dias para considerar proximo do vencimento") @Positive(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_MINIMA) int diasParaVencer);
}
