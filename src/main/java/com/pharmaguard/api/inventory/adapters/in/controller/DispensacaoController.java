package com.pharmaguard.api.inventory.adapters.in.controller;

import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarDispensacaoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.DispensacaoResponse;
import com.pharmaguard.api.inventory.application.DispensacaoUseCase;
import com.pharmaguard.api.inventory.domain.Dispensacao;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dispensacoes")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
public class DispensacaoController {
    private final DispensacaoUseCase useCase;
    public DispensacaoController(DispensacaoUseCase useCase) { this.useCase = useCase; }
    @PostMapping public ResponseEntity<DispensacaoResponse> registrar(@Valid @RequestBody RegistrarDispensacaoRequest request) {
        DispensacaoResponse response = toResponse(useCase.registrar(request.unidadeId(), request.pacienteId(), request.medicamentoId(), request.quantidade(), request.numeroReceita(), request.crmPrescritor(), request.observacao()));
        return ResponseEntity.created(URI.create("/api/v1/dispensacoes/" + response.id())).body(response);
    }
    @GetMapping public ResponseEntity<List<DispensacaoResponse>> listar(@RequestParam(required = false) Long unidadeId,
            @RequestParam(required = false) Long pacienteId, @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) LocalDate dataInicial, @RequestParam(required = false) LocalDate dataFinal) {
        return ResponseEntity.ok(useCase.listar(unidadeId, pacienteId, medicamentoId, dataInicial, dataFinal).stream().map(DispensacaoController::toResponse).toList());
    }
    @GetMapping("/{id}") public ResponseEntity<DispensacaoResponse> buscar(@PathVariable Long id) { return ResponseEntity.ok(toResponse(useCase.buscarPorId(id))); }
    static DispensacaoResponse toResponse(Dispensacao dispensacao) {
        var saida = dispensacao.getSaidaEstoque();
        var lotes = saida.getLotesUtilizados().stream().map(lote -> new DispensacaoResponse.LoteDispensadoResponse(lote.getLoteId(), lote.getNumeroLote(), lote.getQuantidadeConsumida())).toList();
        return new DispensacaoResponse(dispensacao.getId(), dispensacao.getPaciente().getId(), saida.getId(), saida.getUnidadeSaude().getId(), saida.getMedicamento().getId(), saida.getQuantidadeTotal(), dispensacao.getNumeroReceita(), dispensacao.getCrmPrescritor(), saida.getObservacao(), saida.getDataSaida(), lotes);
    }
}