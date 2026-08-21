package com.pharmaguard.api.supplier.adapters.in.controller;

import com.pharmaguard.api.shared.config.MessageKeys;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import com.pharmaguard.api.supplier.adapters.in.controller.doc.LeadTimeFornecedorControllerDoc;
import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarLeadTimeFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.LeadTimeFornecedorResponse;
import com.pharmaguard.api.supplier.adapters.in.mapper.SupplierAdapterInMapper;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fornecedores/{fornecedorId}/lead-time")
public class LeadTimeFornecedorController implements LeadTimeFornecedorControllerDoc {

    private final LeadTimeFornecedorUseCase leadTimeFornecedorUseCase;
    private final SupplierAdapterInMapper mapper;

    public LeadTimeFornecedorController(LeadTimeFornecedorUseCase leadTimeFornecedorUseCase, SupplierAdapterInMapper mapper) {
        this.leadTimeFornecedorUseCase = leadTimeFornecedorUseCase;
        this.mapper = mapper;
    }

    @Override
    @GetMapping
    public ResponseEntity<LeadTimeFornecedorResponse> consultar(@PathVariable Long fornecedorId) {
        var fornecedor = leadTimeFornecedorUseCase.consultar(fornecedorId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageKeys.MSG_RECURSO_FORNECEDOR_NAO_ENCONTRADO));
        return ResponseEntity.ok(mapper.toResponse(fornecedorId, fornecedor));
    }

    @Override
    @PutMapping
    public ResponseEntity<LeadTimeFornecedorResponse> atualizar(@PathVariable Long fornecedorId,
            @Valid @RequestBody AtualizarLeadTimeFornecedorRequest request) {
        var fornecedor = leadTimeFornecedorUseCase.atualizar(fornecedorId, request.leadTimeDias());
        return ResponseEntity.ok(mapper.toResponse(fornecedorId, fornecedor));
    }
}