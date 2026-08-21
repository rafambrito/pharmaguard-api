package com.pharmaguard.api.supplier.adapters.in.controller;

import com.pharmaguard.api.shared.config.MessageKeys;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import com.pharmaguard.api.supplier.adapters.in.controller.doc.FornecedorControllerDoc;
import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.CriarFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.FornecedorResponse;
import com.pharmaguard.api.supplier.adapters.in.mapper.SupplierAdapterInMapper;
import com.pharmaguard.api.supplier.application.FornecedorUseCase;
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
@RequestMapping("/api/v1/fornecedores")
public class FornecedorController implements FornecedorControllerDoc {

    private final FornecedorUseCase fornecedorUseCase;
    private final SupplierAdapterInMapper mapper;

    public FornecedorController(FornecedorUseCase fornecedorUseCase, SupplierAdapterInMapper mapper) {
        this.fornecedorUseCase = fornecedorUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody CriarFornecedorRequest request) {
        var fornecedor = fornecedorUseCase.salvar(mapper.toDomain(request));
        var response = mapper.toResponse(fornecedor);
        return ResponseEntity.created(URI.create("/api/v1/fornecedores/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listar() {
        var response = fornecedorUseCase.buscarTodos().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Long id) {
        var fornecedor = fornecedorUseCase.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_FORNECEDOR_NAO_ENCONTRADO));
        return ResponseEntity.ok(mapper.toResponse(fornecedor));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarFornecedorRequest request) {
        var fornecedor = fornecedorUseCase.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_FORNECEDOR_NAO_ENCONTRADO));

        mapper.applyToDomain(request, fornecedor);
        fornecedor.setId(id);
        var atualizado = fornecedorUseCase.atualizar(fornecedor);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        fornecedorUseCase.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_FORNECEDOR_NAO_ENCONTRADO));
        fornecedorUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}