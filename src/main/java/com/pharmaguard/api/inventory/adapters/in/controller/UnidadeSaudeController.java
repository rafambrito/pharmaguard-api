package com.pharmaguard.api.inventory.adapters.in.controller;

import com.pharmaguard.api.inventory.adapters.in.controller.doc.UnidadeSaudeControllerDoc;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarUnidadeSaudeRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.UnidadeSaudeResponse;
import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCase;
import com.pharmaguard.api.inventory.domain.UnidadeSaude;
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
@RequestMapping("/api/v1/unidades-saude")
public class UnidadeSaudeController implements UnidadeSaudeControllerDoc {
    private final UnidadeSaudeUseCase useCase;

    public UnidadeSaudeController(UnidadeSaudeUseCase useCase) { this.useCase = useCase; }

    @Override @PostMapping
    public ResponseEntity<UnidadeSaudeResponse> criar(@Valid @RequestBody CriarUnidadeSaudeRequest request) {
        UnidadeSaudeResponse response = toResponse(useCase.criar(toDomain(request)));
        return ResponseEntity.created(URI.create("/api/v1/unidades-saude/" + response.id())).body(response);
    }

    @Override @GetMapping
    public ResponseEntity<List<UnidadeSaudeResponse>> listar() {
        return ResponseEntity.ok(useCase.listarTodos().stream().map(this::toResponse).toList());
    }

    @Override @GetMapping("/{id}")
    public ResponseEntity<UnidadeSaudeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(useCase.buscarPorId(id)));
    }

    @Override @PutMapping("/{id}")
    public ResponseEntity<UnidadeSaudeResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody CriarUnidadeSaudeRequest request) {
        UnidadeSaude unidade = toDomain(request);
        unidade.setId(id);
        return ResponseEntity.ok(toResponse(useCase.atualizar(unidade)));
    }

    @Override @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        useCase.inativar(id);
        return ResponseEntity.noContent().build();
    }

    private UnidadeSaude toDomain(CriarUnidadeSaudeRequest request) {
        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setIdentificacao(request.identificacao());
        unidade.setNome(request.nome());
        unidade.setTipo(request.tipo());
        unidade.setEndereco(request.endereco());
        return unidade;
    }

    private UnidadeSaudeResponse toResponse(UnidadeSaude unidade) {
        return new UnidadeSaudeResponse(unidade.getId(), unidade.getIdentificacao(), unidade.getNome(),
                unidade.getTipo(), unidade.getEndereco(), unidade.getStatus(), unidade.getDataCadastro(),
                unidade.getDataAtualizacao());
    }
}