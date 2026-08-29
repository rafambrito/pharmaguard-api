package com.pharmaguard.api.inventory.adapters.in.mapper;

import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarCategoriaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarMedicamentoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.AtualizarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CadastrarLoteRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarCategoriaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarMedicamentoRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarUnidadeMedidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarEntradaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.request.RegistrarSaidaRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.CategoriaResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.CategoriaMedicamentoResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.EntradaEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.LoteResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.MedicamentoResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.MovimentacaoEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaidaEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaldoEstoqueResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.SaldoLoteResponse;
import com.pharmaguard.api.inventory.adapters.in.dto.response.UnidadeMedidaResponse;
import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.CategoriaMedicamento;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.inventory.domain.EstoqueAtual;
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

    public CategoriaMedicamentoResponse toResponse(CategoriaMedicamento categoria) {
        return new CategoriaMedicamentoResponse(categoria.name(), categoria.getNome(), categoria.getDescricao());
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

    public EntradaEstoque toDomain(RegistrarEntradaRequest request) {
        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(request.unidadeId()));
        entrada.setQuantidade(request.quantidade());
        entrada.setOrigem(request.origem());
        entrada.setDocumento(request.documento());
        entrada.setObservacao(request.observacao());
        return entrada;
    }

    public EntradaEstoqueResponse toResponse(EntradaEstoque entrada) {
        return new EntradaEstoqueResponse(
                entrada.getId(),
            entrada.getUnidadeSaude() == null ? null : entrada.getUnidadeSaude().getId(),
                entrada.getMedicamento().getId(),
                entrada.getLote().getId(),
                entrada.getQuantidade(),
                entrada.getOrigem(),
                entrada.getDocumento(),
                entrada.getObservacao(),
                entrada.getDataEntrada(),
                entrada.getUsuarioResponsavelId());
    }

    public SaidaEstoque toDomain(RegistrarSaidaRequest request) {
        SaidaEstoque saida = new SaidaEstoque();
        saida.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(request.unidadeId()));
        saida.setQuantidadeTotal(request.quantidade());
        saida.setMotivo(request.motivo());
        saida.setObservacao(request.observacao());
        return saida;
    }

    public SaidaEstoqueResponse toResponse(SaidaEstoque saida) {
        var lotes = saida.getLotesUtilizados().stream()
                .map(item -> new SaidaEstoqueResponse.LoteConsumidoResponse(
                        item.getLoteId(), item.getNumeroLote(), null, item.getQuantidadeConsumida()))
                .toList();
        return new SaidaEstoqueResponse(
                saida.getId(),
            saida.getUnidadeSaude() == null ? null : saida.getUnidadeSaude().getId(),
                saida.getMedicamento().getId(),
                saida.getQuantidadeTotal(),
                saida.getMotivo(),
                saida.getObservacao(),
                lotes,
                saida.getDataSaida(),
                saida.getUsuarioResponsavelId());
    }

    public MovimentacaoEstoqueResponse toResponse(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueResponse(
                movimentacao.getId(),
                movimentacao.getTipo(),
                movimentacao.getUnidadeSaude() == null ? null : movimentacao.getUnidadeSaude().getId(),
                movimentacao.getMedicamento().getId(),
                movimentacao.getLote() == null ? null : movimentacao.getLote().getId(),
                movimentacao.getQuantidade(),
                movimentacao.getSaldoAposMovimentacao(),
                movimentacao.getMotivo(),
                movimentacao.getDataMovimentacao(),
                movimentacao.getUsuarioResponsavelId());
    }

    public SaldoEstoqueResponse toResponse(EstoqueAtual estoque) {
        return new SaldoEstoqueResponse(
                estoque.getMedicamento().getId(),
                estoque.getQuantidadeDisponivel(),
                estoque.getQuantidadeReservada(),
                estoque.getValidadeMaisProxima(),
                estoque.getLotesAtivos().stream().map(this::toResponse).toList());
    }

    public SaldoLoteResponse toResponse(com.pharmaguard.api.inventory.domain.SaldoLoteEstoque saldo) {
        return new SaldoLoteResponse(
            saldo.getMedicamentoId(),
                saldo.getLoteId(),
                saldo.getNumeroLote(),
                saldo.getDataValidade(),
                saldo.getQuantidadeDisponivel(),
                saldo.getStatusValidade());
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
