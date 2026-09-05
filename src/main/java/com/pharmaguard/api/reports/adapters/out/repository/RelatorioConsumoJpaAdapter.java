package com.pharmaguard.api.reports.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.MovimentacaoEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.reports.application.FiltroConsumo;
import com.pharmaguard.api.reports.application.RelatorioConsumoUseCase;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(name = "spring.datasource.url")
public class RelatorioConsumoJpaAdapter implements RelatorioConsumoUseCase.RelatorioConsumoRepositoryPort {

    private final MovimentacaoEstoqueJpaRepository movimentacaoJpa;

    public RelatorioConsumoJpaAdapter(MovimentacaoEstoqueJpaRepository movimentacaoJpa) {
        this.movimentacaoJpa = Objects.requireNonNull(movimentacaoJpa, "movimentacaoJpa e obrigatorio");
    }

    @Override
    public List<MovimentacaoEstoque> listarSaidas(FiltroConsumo filtro) {
        LocalDateTime inicio = filtro.periodoInicio() == null ? null : filtro.periodoInicio().atStartOfDay();
        LocalDateTime fim = filtro.periodoFim() == null ? null : filtro.periodoFim().atTime(LocalTime.MAX);

        return movimentacaoJpa.findByPeriodoObrigatorio(
                        filtro.medicamentoId(),
                        MovimentacaoEstoque.Tipo.SAIDA,
                        inicio,
                        fim,
                        filtro.unidadeSaudeId())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private MovimentacaoEstoque toDomain(MovimentacaoEstoqueEntity entity) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(entity.getId());
        movimentacao.setTipo(entity.getTipo());
        movimentacao.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(entity.getUnidadeSaude().getId()));
        movimentacao.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        if (entity.getLote() != null) {
            movimentacao.setLote(loteToDomain(entity.getLote()));
        }
        movimentacao.setQuantidade(entity.getQuantidade());
        movimentacao.setSaldoAposMovimentacao(entity.getSaldoAposMovimentacao());
        movimentacao.setDataMovimentacao(entity.getDataMovimentacao());
        movimentacao.setMotivo(entity.getMotivo());
        movimentacao.setUsuarioResponsavelId(entity.getUsuarioResponsavelId());
        return movimentacao;
    }

    private Lote loteToDomain(LoteEntity entity) {
        Lote lote = new Lote();
        lote.setId(entity.getId());
        lote.setNumeroLote(entity.getNumeroLote());
        lote.setDataValidade(entity.getDataValidade());
        lote.setQuantidadeInicial(entity.getQuantidadeInicial());
        lote.setMedicamento(medicamentoToDomain(entity.getMedicamento()));
        return lote;
    }

    private Medicamento medicamentoToDomain(MedicamentoEntity e) {
        return new Medicamento(e.getId(), e.getNome(), e.getApresentacao(), e.getDescricao(),
                categoriaToDomain(e.getCategoria()), unidadeMedidaToDomain(e.getUnidadeMedida()),
                e.getCriticidade(), e.getStatus(), e.getDataCriacao(), e.getDataUltimaAlteracao());
    }

    private Categoria categoriaToDomain(CategoriaEntity e) {
        return new Categoria(e.getId(), e.getNome(), e.getDescricao(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }

    private UnidadeMedida unidadeMedidaToDomain(UnidadeMedidaEntity e) {
        return new UnidadeMedida(e.getId(), e.getNome(), e.getSigla(), e.getStatus(),
                e.getDataCriacao(), e.getDataUltimaAlteracao());
    }
}
