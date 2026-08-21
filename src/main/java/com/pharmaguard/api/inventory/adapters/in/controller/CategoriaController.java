package com.pharmaguard.api.inventory.adapters.in.controller;

import com.pharmaguard.api.inventory.adapters.in.controller.doc.CategoriaControllerDoc;
import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarCategoriaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarCategoriaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.CategoriaResponse;
import com.pharmaguard.api.inventory.adapters.in.mapper.InventoryAdapterInMapper;
import com.pharmaguard.api.inventory.application.CategoriaUseCase;
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
@RequestMapping("/api/v1/categorias")
public class CategoriaController implements CategoriaControllerDoc {

    private final CategoriaUseCase categoriaUseCase;
    private final InventoryAdapterInMapper mapper;

    public CategoriaController(CategoriaUseCase categoriaUseCase, InventoryAdapterInMapper mapper) {
        this.categoriaUseCase = categoriaUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CriarCategoriaRequest request) {
        var categoria = categoriaUseCase.criar(mapper.toDomain(request));
        var response = mapper.toResponse(categoria);
        return ResponseEntity.created(URI.create("/api/v1/categorias/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        var response = categoriaUseCase.listarTodos().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        var categoria = categoriaUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(categoria));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarCategoriaRequest request) {
        var categoria = categoriaUseCase.buscarPorId(id);
        mapper.applyToDomain(request, categoria);
        categoria.setId(id);
        var atualizada = categoriaUseCase.atualizar(categoria);
        return ResponseEntity.ok(mapper.toResponse(atualizada));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        categoriaUseCase.buscarPorId(id);
        categoriaUseCase.remover(id);
        return ResponseEntity.noContent().build();
    }
}
