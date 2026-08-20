package com.pharmaguard.api.inventory.api.controller;

import com.pharmaguard.api.inventory.api.controller.doc.UnidadeMedidaControllerDoc;
import com.pharmaguard.api.inventory.api.dto.request.AtualizarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.api.dto.request.CriarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.api.dto.response.UnidadeMedidaResponse;
import com.pharmaguard.api.inventory.api.mapper.InventoryApiMapper;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase;
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
@RequestMapping("/api/v1/unidades-medida")
public class UnidadeMedidaController implements UnidadeMedidaControllerDoc {

    private final UnidadeMedidaUseCase unidadeMedidaUseCase;
    private final InventoryApiMapper mapper;

    public UnidadeMedidaController(UnidadeMedidaUseCase unidadeMedidaUseCase, InventoryApiMapper mapper) {
        this.unidadeMedidaUseCase = unidadeMedidaUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<UnidadeMedidaResponse> criar(@Valid @RequestBody CriarUnidadeMedidaRequest request) {
        var unidadeMedida = unidadeMedidaUseCase.criar(mapper.toDomain(request));
        var response = mapper.toResponse(unidadeMedida);
        return ResponseEntity.created(URI.create("/api/v1/unidades-medida/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UnidadeMedidaResponse>> listar() {
        var response = unidadeMedidaUseCase.listarTodos().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeMedidaResponse> buscarPorId(@PathVariable Long id) {
        var unidadeMedida = unidadeMedidaUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(unidadeMedida));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeMedidaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarUnidadeMedidaRequest request) {
        var unidadeMedida = unidadeMedidaUseCase.buscarPorId(id);
        mapper.applyToDomain(request, unidadeMedida);
        unidadeMedida.setId(id);
        var atualizada = unidadeMedidaUseCase.atualizar(unidadeMedida);
        return ResponseEntity.ok(mapper.toResponse(atualizada));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        unidadeMedidaUseCase.buscarPorId(id);
        unidadeMedidaUseCase.remover(id);
        return ResponseEntity.noContent().build();
    }
}
