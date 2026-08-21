package com.pharmaguard.api.auth.adapters.in.controller;

import com.pharmaguard.api.auth.adapters.in.controller.doc.UsuarioControllerDoc;
import com.pharmaguard.api.auth.adapters.in.dto.request.AtualizarUsuarioRequest;
import com.pharmaguard.api.auth.adapters.in.dto.request.CriarUsuarioRequest;
import com.pharmaguard.api.auth.adapters.in.dto.response.UsuarioResponse;
import com.pharmaguard.api.auth.adapters.in.mapper.UsuarioAdapterInMapper;
import com.pharmaguard.api.auth.application.UsuarioUseCase;
import com.pharmaguard.api.shared.config.MessageKeys;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController implements UsuarioControllerDoc {

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioAdapterInMapper mapper;

    public UsuarioController(UsuarioUseCase usuarioUseCase, UsuarioAdapterInMapper mapper) {
        this.usuarioUseCase = usuarioUseCase;
        this.mapper = mapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody CriarUsuarioRequest request) {
        var usuario = usuarioUseCase.salvar(mapper.toDomain(request));
        var response = mapper.toResponse(usuario);
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + response.id())).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        var response = usuarioUseCase.buscarTodos().stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        var usuario = usuarioUseCase.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_USUARIO_NAO_ENCONTRADO));
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        var usuarioExistente = usuarioUseCase.buscarPorId(id)
            .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_USUARIO_NAO_ENCONTRADO));

        mapper.applyToDomain(request, usuarioExistente);
        usuarioExistente.setId(id);

        var usuarioAtualizado = usuarioUseCase.atualizar(usuarioExistente);
        return ResponseEntity.ok(mapper.toResponse(usuarioAtualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        usuarioUseCase.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_USUARIO_NAO_ENCONTRADO));

        usuarioUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
