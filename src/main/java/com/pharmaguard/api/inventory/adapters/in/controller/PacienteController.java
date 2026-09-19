package com.pharmaguard.api.inventory.adapters.in.controller;

import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarPacienteRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.DispensacaoResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.PacienteResponse;
import com.pharmaguard.api.inventory.application.DispensacaoUseCase;
import com.pharmaguard.api.inventory.application.PacienteUseCase;
import com.pharmaguard.api.inventory.domain.Paciente;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pacientes")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
public class PacienteController {
    private final PacienteUseCase pacienteUseCase;
    private final DispensacaoUseCase dispensacaoUseCase;
    public PacienteController(PacienteUseCase pacienteUseCase, DispensacaoUseCase dispensacaoUseCase) {
        this.pacienteUseCase = pacienteUseCase; this.dispensacaoUseCase = dispensacaoUseCase;
    }
    @PostMapping public ResponseEntity<PacienteResponse> criar(@Valid @RequestBody CriarPacienteRequest request) {
        PacienteResponse response = toResponse(pacienteUseCase.criar(toDomain(request)));
        return ResponseEntity.created(URI.create("/api/v1/pacientes/" + response.id())).body(response);
    }
    @GetMapping public ResponseEntity<List<PacienteResponse>> listar(@RequestParam(required = false) String cpf, @RequestParam(required = false) String nome) {
        return ResponseEntity.ok(pacienteUseCase.listar(cpf, nome).stream().map(this::toResponse).toList());
    }
    @GetMapping("/{id}") public ResponseEntity<PacienteResponse> buscar(@PathVariable Long id) { return ResponseEntity.ok(toResponse(pacienteUseCase.buscarPorId(id))); }
    @PutMapping("/{id}") public ResponseEntity<PacienteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CriarPacienteRequest request) {
        Paciente paciente = toDomain(request); paciente.setId(id); return ResponseEntity.ok(toResponse(pacienteUseCase.atualizar(paciente)));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> inativar(@PathVariable Long id) { pacienteUseCase.inativar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/{id}/dispensacoes") public ResponseEntity<List<DispensacaoResponse>> historico(@PathVariable Long id) {
        pacienteUseCase.buscarPorId(id);
        return ResponseEntity.ok(dispensacaoUseCase.listar(null, id, null, null, null).stream().map(DispensacaoController::toResponse).toList());
    }
    private Paciente toDomain(CriarPacienteRequest request) {
        Paciente paciente = new Paciente(); paciente.setNome(request.nome()); paciente.setCpf(request.cpf()); paciente.setDataNascimento(request.dataNascimento());
        paciente.setCartaoSus(request.cartaoSus()); paciente.setTelefone(request.telefone()); paciente.setEmail(request.email()); paciente.setCidade(request.cidade()); paciente.setUf(request.uf()); return paciente;
    }
    private PacienteResponse toResponse(Paciente paciente) { return new PacienteResponse(paciente.getId(), paciente.getNome(), paciente.getCpf(), paciente.getDataNascimento(), paciente.getCartaoSus(), paciente.getTelefone(), paciente.getEmail(), paciente.getCidade(), paciente.getUf(), paciente.getStatus(), paciente.getDataCriacao(), paciente.getDataUltimaAlteracao()); }
}