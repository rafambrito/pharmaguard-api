package com.pharmaguard.api.supplier.adapters.in.mapper;

import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarContatoFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.AtualizarLeadTimeFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.CriarContatoFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.request.CriarFornecedorRequest;
import com.pharmaguard.api.supplier.adapters.in.dto.response.ContatoFornecedorResponse;
import com.pharmaguard.api.supplier.adapters.in.dto.response.FornecedorResponse;
import com.pharmaguard.api.supplier.adapters.in.dto.response.LeadTimeFornecedorResponse;
import com.pharmaguard.api.supplier.domain.ContatoFornecedor;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import org.springframework.stereotype.Component;

@Component
public class SupplierAdapterInMapper {

    public Fornecedor toDomain(CriarFornecedorRequest request) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(request.nome());
        fornecedor.setCodigo(request.codigo());
        fornecedor.setDocumento(request.documento());
        fornecedor.setObservacao(request.observacao());
        fornecedor.setLeadTimeDias(request.leadTimeDias());
        return fornecedor;
    }

    public void applyToDomain(AtualizarFornecedorRequest request, Fornecedor fornecedor) {
        fornecedor.setNome(request.nome());
        fornecedor.setCodigo(request.codigo());
        fornecedor.setDocumento(request.documento());
        fornecedor.setObservacao(request.observacao());
        fornecedor.setLeadTimeDias(request.leadTimeDias());
        if (request.ativo() != null) {
            fornecedor.setStatus(request.ativo() ? Fornecedor.Status.ATIVO : Fornecedor.Status.INATIVO);
        }
        fornecedor.marcarAtualizacao();
    }

    public FornecedorResponse toResponse(Fornecedor fornecedor) {
        return new FornecedorResponse(
                fornecedor.getId(),
                fornecedor.getNome(),
                fornecedor.getCodigo(),
                fornecedor.getDocumento(),
                fornecedor.getObservacao(),
                fornecedor.getLeadTimeDias(),
                fornecedor.getStatusLeadTime() == null ? null : fornecedor.getStatusLeadTime().name(),
                fornecedor.getStatus() == Fornecedor.Status.ATIVO,
                fornecedor.getDataCriacao(),
                fornecedor.getDataUltimaAlteracao());
    }

    public ContatoFornecedor toDomain(CriarContatoFornecedorRequest request) {
        ContatoFornecedor contato = new ContatoFornecedor();
        contato.setNome(request.nome());
        contato.setCargo(request.cargo());
        contato.setTelefone(request.telefone());
        contato.setEmail(request.email());
        contato.setCanalPrincipal(ContatoFornecedor.CanalPrincipal.valueOf(request.canalPrincipal().trim().toUpperCase()));
        return contato;
    }

    public void applyToDomain(AtualizarContatoFornecedorRequest request, ContatoFornecedor contato) {
        contato.setNome(request.nome());
        contato.setCargo(request.cargo());
        contato.setTelefone(request.telefone());
        contato.setEmail(request.email());
        contato.setCanalPrincipal(ContatoFornecedor.CanalPrincipal.valueOf(request.canalPrincipal().trim().toUpperCase()));
        if (request.ativo() != null) {
            contato.setAtivo(request.ativo());
        }
        contato.marcarAtualizacao();
    }

    public ContatoFornecedorResponse toResponse(ContatoFornecedor contato) {
        return new ContatoFornecedorResponse(
                contato.getId(),
                contato.getNome(),
                contato.getCargo(),
                contato.getTelefone(),
                contato.getEmail(),
                contato.getCanalPrincipal().name(),
                contato.isAtivo(),
                contato.getFornecedor() == null ? null : contato.getFornecedor().getId(),
                contato.getDataCriacao(),
                contato.getDataUltimaAlteracao());
    }

    public LeadTimeFornecedorResponse toResponse(Long fornecedorId, Fornecedor fornecedor) {
        return new LeadTimeFornecedorResponse(
                fornecedorId,
                fornecedor.getLeadTimeDias(),
                fornecedor.getStatusLeadTime() == null ? null : fornecedor.getStatusLeadTime().name(),
                fornecedor.getDataUltimaAlteracao());
    }

    public void applyToDomain(AtualizarLeadTimeFornecedorRequest request, Fornecedor fornecedor) {
        fornecedor.setLeadTimeDias(request.leadTimeDias());
        fornecedor.marcarAtualizacao();
    }
}