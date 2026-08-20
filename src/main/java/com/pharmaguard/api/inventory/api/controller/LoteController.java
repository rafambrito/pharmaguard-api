package com.pharmaguard.api.inventory.api.controller;

import com.pharmaguard.api.inventory.api.controller.doc.LoteControllerDoc;
import com.pharmaguard.api.inventory.api.dto.request.CadastrarLoteRequest;
import com.pharmaguard.api.inventory.api.dto.response.LoteResponse;
import com.pharmaguard.api.inventory.api.mapper.InventoryApiMapper;
import com.pharmaguard.api.inventory.application.LoteUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medicamentos/{medicamentoId}/lotes")
public class LoteController implements LoteControllerDoc {

    private final LoteUseCase loteUseCase;
    private final InventoryApiMapper mapper;

    public LoteController(LoteUseCase loteUseCase, InventoryApiMapper mapper) {
        this.loteUseCase = loteUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<LoteResponse> cadastrar(@PathVariable Long medicamentoId,
            @Valid @RequestBody CadastrarLoteRequest request) {
        var lote = loteUseCase.cadastrar(medicamentoId, mapper.toDomain(request));
        var response = mapper.toResponse(lote);
        return ResponseEntity.created(URI.create("/api/v1/medicamentos/" + medicamentoId + "/lotes/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<LoteResponse>> listarPorMedicamento(@PathVariable Long medicamentoId) {
        var response = loteUseCase.listarPorMedicamento(medicamentoId).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{loteId}")
    public ResponseEntity<LoteResponse> buscarPorId(@PathVariable Long medicamentoId, @PathVariable Long loteId) {
        var lote = loteUseCase.buscarPorId(medicamentoId, loteId);
        return ResponseEntity.ok(mapper.toResponse(lote));
    }

    @Override
    @DeleteMapping("/{loteId}")
    public ResponseEntity<Void> remover(@PathVariable Long medicamentoId, @PathVariable Long loteId) {
        loteUseCase.remover(medicamentoId, loteId);
        return ResponseEntity.noContent().build();
    }
}
