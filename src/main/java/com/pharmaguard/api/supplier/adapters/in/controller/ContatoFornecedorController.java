package com.pharmaguard.api.supplier.adapters.in.controller;

import com.pharmaguard.api.shared.config.MessageKeys;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import com.pharmaguard.api.supplier.adapters.in.controller.doc.ContatoFornecedorControllerDoc;
import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarContatoFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.CriarContatoFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.ContatoFornecedorResponse;
import com.pharmaguard.api.supplier.adapters.in.mapper.SupplierAdapterInMapper;
import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCase;
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
@RequestMapping("/api/v1/fornecedores/{fornecedorId}/contatos")
public class ContatoFornecedorController implements ContatoFornecedorControllerDoc {

    private final ContatoFornecedorUseCase contatoFornecedorUseCase;
    private final SupplierAdapterInMapper mapper;

    public ContatoFornecedorController(ContatoFornecedorUseCase contatoFornecedorUseCase, SupplierAdapterInMapper mapper) {
        this.contatoFornecedorUseCase = contatoFornecedorUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<ContatoFornecedorResponse> criar(@PathVariable Long fornecedorId,
            @Valid @RequestBody CriarContatoFornecedorRequest request) {
        var contato = contatoFornecedorUseCase.salvar(fornecedorId, mapper.toDomain(request));
        var response = mapper.toResponse(contato);
        return ResponseEntity.created(URI.create("/api/v1/fornecedores/" + fornecedorId + "/contatos/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ContatoFornecedorResponse>> listar(@PathVariable Long fornecedorId) {
        var response = contatoFornecedorUseCase.listarPorFornecedor(fornecedorId).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{contatoId}")
    public ResponseEntity<ContatoFornecedorResponse> buscarPorId(@PathVariable Long fornecedorId,
            @PathVariable Long contatoId) {
        var contato = contatoFornecedorUseCase.buscarPorId(fornecedorId, contatoId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_CONTATO_FORNECEDOR_NAO_ENCONTRADO));
        return ResponseEntity.ok(mapper.toResponse(contato));
    }

    @Override
    @PutMapping("/{contatoId}")
    public ResponseEntity<ContatoFornecedorResponse> atualizar(@PathVariable Long fornecedorId,
            @PathVariable Long contatoId,
            @Valid @RequestBody AtualizarContatoFornecedorRequest request) {
        var contato = contatoFornecedorUseCase.buscarPorId(fornecedorId, contatoId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_CONTATO_FORNECEDOR_NAO_ENCONTRADO));

        mapper.applyToDomain(request, contato);
        contato.setId(contatoId);
        var atualizado = contatoFornecedorUseCase.atualizar(fornecedorId, contato);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    @DeleteMapping("/{contatoId}")
    public ResponseEntity<Void> remover(@PathVariable Long fornecedorId, @PathVariable Long contatoId) {
        contatoFornecedorUseCase.buscarPorId(fornecedorId, contatoId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_CONTATO_FORNECEDOR_NAO_ENCONTRADO));
        contatoFornecedorUseCase.deletar(fornecedorId, contatoId);
        return ResponseEntity.noContent().build();
    }
}