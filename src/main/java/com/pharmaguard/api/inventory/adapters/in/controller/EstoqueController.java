package com.pharmaguard.api.inventory.adapters.in.controller;

import com.pharmaguard.api.inventory.adapters.in.controller.doc.EstoqueControllerDoc;
import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarEntradaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarSaidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.EntradaEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.LoteVencimentoResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.MovimentacaoEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaidaEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaldoEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaldoLoteResponse;
import com.pharmaguard.api.inventory.adapters.in.mapper.InventoryAdapterInMapper;
import com.pharmaguard.api.inventory.application.EntradaEstoqueUseCase;
import com.pharmaguard.api.inventory.application.HistoricoEstoqueUseCase;
import com.pharmaguard.api.inventory.application.SaidaEstoqueUseCase;
import com.pharmaguard.api.inventory.application.SaldoEstoqueUseCase;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.StatusValidade;
import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("/api/v1/estoque")
public class EstoqueController implements EstoqueControllerDoc {

    private final EntradaEstoqueUseCase entradaUseCase;
    private final SaidaEstoqueUseCase saidaUseCase;
    private final SaldoEstoqueUseCase saldoUseCase;
    private final HistoricoEstoqueUseCase historicoUseCase;
    private final InventoryAdapterInMapper mapper;

    public EstoqueController(EntradaEstoqueUseCase entradaUseCase,
            SaidaEstoqueUseCase saidaUseCase,
            SaldoEstoqueUseCase saldoUseCase,
            HistoricoEstoqueUseCase historicoUseCase,
            InventoryAdapterInMapper mapper) {
        this.entradaUseCase = entradaUseCase;
        this.saidaUseCase = saidaUseCase;
        this.saldoUseCase = saldoUseCase;
        this.historicoUseCase = historicoUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/entradas")
    @Override
    public ResponseEntity<EntradaEstoqueResponse> registrarEntrada(
            @Valid @RequestBody RegistrarEntradaRequest request) {
        var entrada = entradaUseCase.registrar(
                request.medicamentoId(),
                request.loteId(),
                mapper.toDomain(request));
        var response = mapper.toResponse(entrada);
        return ResponseEntity.created(URI.create("/api/v1/estoque/entradas/" + response.id())).body(response);
    }

    @GetMapping("/entradas")
    @Override
    public ResponseEntity<List<EntradaEstoqueResponse>> listarEntradas(
            @RequestParam(required = false) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @RequestParam(required = false) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long loteId) {
        return ResponseEntity.ok(entradaUseCase.listar(medicamentoId, loteId).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/entradas/{id}")
    @Override
    public ResponseEntity<EntradaEstoqueResponse> buscarEntrada(
            @PathVariable @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long id) {
        return ResponseEntity.ok(mapper.toResponse(entradaUseCase.buscarPorId(id)));
    }

    @PostMapping("/saidas")
    @Override
    public ResponseEntity<SaidaEstoqueResponse> registrarSaida(
            @Valid @RequestBody RegistrarSaidaRequest request) {
        var saida = saidaUseCase.registrar(request.medicamentoId(), mapper.toDomain(request));
        var response = mapper.toResponse(saida);
        return ResponseEntity.created(URI.create("/api/v1/estoque/saidas/" + response.id())).body(response);
    }

    @GetMapping("/saidas")
    @Override
    public ResponseEntity<List<SaidaEstoqueResponse>> listarSaidas(
            @RequestParam(required = false) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId) {
        return ResponseEntity.ok(saidaUseCase.listar(medicamentoId).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/saidas/{id}")
    @Override
    public ResponseEntity<SaidaEstoqueResponse> buscarSaida(
            @PathVariable @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long id) {
        return ResponseEntity.ok(mapper.toResponse(saidaUseCase.buscarPorId(id)));
    }

    @GetMapping("/movimentacoes")
    @Override
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> listarMovimentacoes(
            @RequestParam(required = false) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId,
            @RequestParam(required = false) @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long loteId,
            @RequestParam(required = false) MovimentacaoEstoque.Tipo tipo,
            @RequestParam(required = false) LocalDate dataInicial,
            @RequestParam(required = false) LocalDate dataFinal) {
        return ResponseEntity.ok(historicoUseCase.listar(medicamentoId, loteId, tipo, dataInicial, dataFinal).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/saldos/{medicamentoId}")
    @Override
    public ResponseEntity<SaldoEstoqueResponse> consultarSaldo(
            @PathVariable @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId) {
        return ResponseEntity.ok(mapper.toResponse(saldoUseCase.consultarPorMedicamento(medicamentoId)));
    }

    @GetMapping("/saldos/{medicamentoId}/lotes")
    @Override
    public ResponseEntity<List<SaldoLoteResponse>> consultarSaldoPorLote(
            @PathVariable @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO) Long medicamentoId) {
        return ResponseEntity.ok(saldoUseCase.consultarLotesPorMedicamento(medicamentoId).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/vencimentos")
    @Override
    public ResponseEntity<List<LoteVencimentoResponse>> listarVencimentos(
            @RequestParam(defaultValue = "30")
            @Positive(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_MINIMA) int diasParaVencer) {
        if (diasParaVencer < 1) {
            throw new IllegalArgumentException("diasParaVencer deve ser maior que zero");
        }

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(diasParaVencer);
        var response = saldoUseCase.consultarTodosOsLotes().stream()
                .filter(saldo -> saldo.getStatusValidade() != StatusValidade.VALIDO)
                .filter(saldo -> saldo.getStatusValidade() == StatusValidade.VENCIDO
                        || !saldo.getDataValidade().isAfter(limite))
                .map(saldo -> new LoteVencimentoResponse(
                        saldo.getMedicamentoId(),
                        saldo.getLoteId(),
                        saldo.getNumeroLote(),
                        saldo.getDataValidade(),
                        ChronoUnit.DAYS.between(hoje, saldo.getDataValidade()),
                        saldo.getStatusValidade(),
                        saldo.getQuantidadeDisponivel()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
