package com.pharmaguard.api.inventory.adapters.in.controller;

import com.pharmaguard.api.inventory.adapters.in.controller.doc.MedicamentoControllerDoc;
import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarMedicamentoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarMedicamentoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.MedicamentoResponse;
import com.pharmaguard.api.inventory.adapters.in.mapper.InventoryAdapterInMapper;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medicamentos")
public class MedicamentoController implements MedicamentoControllerDoc {

    private final MedicamentoUseCase medicamentoUseCase;
    private final InventoryAdapterInMapper mapper;

    public MedicamentoController(MedicamentoUseCase medicamentoUseCase, InventoryAdapterInMapper mapper) {
        this.medicamentoUseCase = medicamentoUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<MedicamentoResponse> criar(@Valid @RequestBody CriarMedicamentoRequest request) {
        var medicamento = medicamentoUseCase.criar(
                mapper.toDomain(request),
                request.categoriaId(),
                request.unidadeMedidaId());
        var response = mapper.toResponse(medicamento);
        return ResponseEntity.created(URI.create("/api/v1/medicamentos/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MedicamentoResponse>> listar() {
        var response = medicamentoUseCase.listarTodos().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponse> buscarPorId(@PathVariable Long id) {
        var medicamento = medicamentoUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(medicamento));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarMedicamentoRequest request) {
        var medicamento = medicamentoUseCase.buscarPorId(id);
        mapper.applyToDomain(request, medicamento);
        medicamento.setId(id);
        var atualizado = medicamentoUseCase.atualizar(medicamento, request.categoriaId(), request.unidadeMedidaId());
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        medicamentoUseCase.remover(id);
        return ResponseEntity.noContent().build();
    }
}
