package com.pharmaguard.api.supplier.adapters.out.repository;

import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCase;
import com.pharmaguard.api.supplier.application.FornecedorUseCase;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCase;
import com.pharmaguard.api.supplier.domain.ContatoFornecedor;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import com.pharmaguard.api.supplier.adapters.out.repository.entity.ContatoFornecedorEntity;
import com.pharmaguard.api.supplier.adapters.out.repository.entity.FornecedorEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(FornecedorJpaRepository.class)
public class SupplierJpaAdapter implements
        FornecedorUseCase.FornecedorRepositoryPort,
        ContatoFornecedorUseCase.ContatoFornecedorRepositoryPort,
        LeadTimeFornecedorUseCase.LeadTimeFornecedorRepositoryPort {

    private final FornecedorJpaRepository fornecedorJpa;
    private final ContatoFornecedorJpaRepository contatoJpa;

    public SupplierJpaAdapter(FornecedorJpaRepository fornecedorJpa, ContatoFornecedorJpaRepository contatoJpa) {
        this.fornecedorJpa = fornecedorJpa;
        this.contatoJpa = contatoJpa;
    }

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        return toDomain(fornecedorJpa.save(toEntity(fornecedor)));
    }

    @Override
    public Fornecedor atualizar(Fornecedor fornecedor) {
        return toDomain(fornecedorJpa.save(toEntity(fornecedor)));
    }

    @Override
    public void deletar(Long id) {
        fornecedorJpa.deleteById(id);
    }

    @Override
    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorJpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Fornecedor> buscarTodos() {
        return fornecedorJpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Fornecedor> buscarPorCodigo(String codigo) {
        return fornecedorJpa.findByCodigoIgnoreCase(codigo).map(this::toDomain);
    }

    @Override
    public Optional<Fornecedor> buscarPorDocumento(String documento) {
        return fornecedorJpa.findByDocumento(documento).map(this::toDomain);
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return fornecedorJpa.existsByCodigoIgnoreCase(codigo);
    }

    @Override
    public boolean existePorDocumento(String documento) {
        return fornecedorJpa.existsByDocumento(documento);
    }

    @Override
    public Optional<Fornecedor> buscarFornecedorPorId(Long fornecedorId) {
        return fornecedorJpa.findById(fornecedorId).map(this::toDomain);
    }

    @Override
    public Optional<ContatoFornecedor> buscarPorId(Long fornecedorId, Long contatoId) {
        return contatoJpa.findByIdAndFornecedor_Id(contatoId, fornecedorId).map(this::contatoToDomain);
    }

    @Override
    public List<ContatoFornecedor> listarPorFornecedor(Long fornecedorId) {
        return contatoJpa.findAllByFornecedor_Id(fornecedorId).stream().map(this::contatoToDomain).toList();
    }

    @Override
    public ContatoFornecedor salvar(Long fornecedorId, ContatoFornecedor contato) {
        ContatoFornecedorEntity entity = contatoToEntity(contato);
        entity.setFornecedor(fornecedorJpa.getReferenceById(fornecedorId));
        return contatoToDomain(contatoJpa.save(entity));
    }

    @Override
    public ContatoFornecedor atualizar(Long fornecedorId, ContatoFornecedor contato) {
        ContatoFornecedorEntity entity = contatoToEntity(contato);
        entity.setFornecedor(fornecedorJpa.getReferenceById(fornecedorId));
        return contatoToDomain(contatoJpa.save(entity));
    }

    @Override
    public void deletar(Long fornecedorId, Long contatoId) {
        contatoJpa.deleteByIdAndFornecedor_Id(contatoId, fornecedorId);
    }

    private FornecedorEntity toEntity(Fornecedor fornecedor) {
        FornecedorEntity entity = new FornecedorEntity();
        entity.setId(fornecedor.getId());
        entity.setNome(fornecedor.getNome());
        entity.setCodigo(fornecedor.getCodigo());
        entity.setDocumento(fornecedor.getDocumento());
        entity.setObservacao(fornecedor.getObservacao());
        entity.setLeadTimeDias(fornecedor.getLeadTimeDias());
        entity.setStatus(fornecedor.getStatus());
        entity.setDataCriacao(fornecedor.getDataCriacao());
        entity.setDataUltimaAlteracao(fornecedor.getDataUltimaAlteracao());
        return entity;
    }

    private Fornecedor toDomain(FornecedorEntity entity) {
        return new Fornecedor(
                entity.getId(),
                entity.getNome(),
                entity.getCodigo(),
                entity.getDocumento(),
                entity.getObservacao(),
                entity.getLeadTimeDias(),
                entity.getStatus(),
                entity.getDataCriacao(),
                entity.getDataUltimaAlteracao());
    }

    private ContatoFornecedorEntity contatoToEntity(ContatoFornecedor contato) {
        ContatoFornecedorEntity entity = new ContatoFornecedorEntity();
        entity.setId(contato.getId());
        entity.setNome(contato.getNome());
        entity.setCargo(contato.getCargo());
        entity.setTelefone(contato.getTelefone());
        entity.setEmail(contato.getEmail());
        entity.setCanalPrincipal(contato.getCanalPrincipal());
        entity.setAtivo(contato.isAtivo());
        entity.setDataCriacao(contato.getDataCriacao());
        entity.setDataUltimaAlteracao(contato.getDataUltimaAlteracao());
        return entity;
    }

    private ContatoFornecedor contatoToDomain(ContatoFornecedorEntity entity) {
        Fornecedor fornecedor = toDomain(entity.getFornecedor());
        return new ContatoFornecedor(
                entity.getId(),
                entity.getNome(),
                entity.getCargo(),
                entity.getTelefone(),
                entity.getEmail(),
                entity.getCanalPrincipal(),
                entity.isAtivo(),
                fornecedor,
                entity.getDataCriacao(),
                entity.getDataUltimaAlteracao());
    }
}