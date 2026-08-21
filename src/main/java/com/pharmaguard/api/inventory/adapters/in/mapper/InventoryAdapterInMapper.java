package com.pharmaguard.api.inventory.adapters.in.mapper;

import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarCategoriaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarMedicamentoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CadastrarLoteRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarCategoriaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarMedicamentoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.CategoriaResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.LoteResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.MedicamentoResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.UnidadeMedidaResponse;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.shared.config.MessageKeys;
import org.springframework.stereotype.Component;

@Component
public class InventoryAdapterInMapper {

    public Categoria toDomain(CriarCategoriaRequest request) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        return categoria;
    }

    public void applyToDomain(AtualizarCategoriaRequest request, Categoria categoria) {
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        if (request.ativo() != null) {
            categoria.setStatus(request.ativo() ? Categoria.Status.ATIVA : Categoria.Status.INATIVA);
        }
        categoria.marcarAtualizacao();
    }

    public CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getStatus() == Categoria.Status.ATIVA,
                categoria.getDataCriacao(),
                categoria.getDataUltimaAlteracao());
    }

    public UnidadeMedida toDomain(CriarUnidadeMedidaRequest request) {
        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setNome(request.nome());
        unidadeMedida.setSigla(request.sigla());
        return unidadeMedida;
    }

    public void applyToDomain(AtualizarUnidadeMedidaRequest request, UnidadeMedida unidadeMedida) {
        unidadeMedida.setNome(request.nome());
        unidadeMedida.setSigla(request.sigla());
        if (request.ativo() != null) {
            unidadeMedida.setStatus(request.ativo() ? UnidadeMedida.Status.ATIVA : UnidadeMedida.Status.INATIVA);
        }
        unidadeMedida.marcarAtualizacao();
    }

    public UnidadeMedidaResponse toResponse(UnidadeMedida unidadeMedida) {
        return new UnidadeMedidaResponse(
                unidadeMedida.getId(),
                unidadeMedida.getNome(),
                unidadeMedida.getSigla(),
                unidadeMedida.getStatus() == UnidadeMedida.Status.ATIVA,
                unidadeMedida.getDataCriacao(),
                unidadeMedida.getDataUltimaAlteracao());
    }

    public Medicamento toDomain(CriarMedicamentoRequest request) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNome(request.nome());
        medicamento.setApresentacao(request.apresentacao());
        medicamento.setDescricao(request.descricao());
        medicamento.setCriticidade(parseCriticidade(request.criticidade()));
        return medicamento;
    }

    public void applyToDomain(AtualizarMedicamentoRequest request, Medicamento medicamento) {
        medicamento.setNome(request.nome());
        medicamento.setApresentacao(request.apresentacao());
        medicamento.setDescricao(request.descricao());
        medicamento.setCriticidade(parseCriticidade(request.criticidade()));
        if (request.ativo() != null) {
            medicamento.setStatus(request.ativo() ? Medicamento.Status.ATIVO : Medicamento.Status.INATIVO);
        }
        medicamento.marcarAtualizacao();
    }

    public MedicamentoResponse toResponse(Medicamento medicamento) {
        return new MedicamentoResponse(
                medicamento.getId(),
                medicamento.getNome(),
                medicamento.getApresentacao(),
                medicamento.getDescricao(),
                toResponse(medicamento.getCategoria()),
                toResponse(medicamento.getUnidadeMedida()),
                medicamento.getCriticidade().name(),
                medicamento.getStatus() == Medicamento.Status.ATIVO,
                medicamento.getDataCriacao(),
                medicamento.getDataUltimaAlteracao());
    }

    public Lote toDomain(CadastrarLoteRequest request) {
        Lote lote = new Lote();
        lote.setNumeroLote(request.numeroLote());
        lote.setDataValidade(request.dataValidade());
        lote.setQuantidadeInicial(request.quantidadeInicial());
        return lote;
    }

    public LoteResponse toResponse(Lote lote) {
        return new LoteResponse(
                lote.getId(),
                lote.getNumeroLote(),
                lote.getDataValidade(),
                lote.getQuantidadeInicial(),
                lote.getStatusValidade().name(),
                lote.getMedicamento().getId(),
                null);
    }

    private Medicamento.Criticidade parseCriticidade(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(MessageKeys.MSG_VALIDACAO_CRITICIDADE_OBRIGATORIA);
        }

        try {
            return Medicamento.Criticidade.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(MessageKeys.MSG_VALIDACAO_CRITICIDADE_INVALIDA);
        }
    }
}
