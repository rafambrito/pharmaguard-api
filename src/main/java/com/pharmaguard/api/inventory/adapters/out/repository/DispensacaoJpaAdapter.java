package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.DispensacaoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.PacienteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.SaidaEstoqueEntity;
import com.pharmaguard.api.inventory.application.DispensacaoUseCase.DispensacaoRepositoryPort;
import com.pharmaguard.api.inventory.domain.Dispensacao;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.Paciente;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class DispensacaoJpaAdapter implements DispensacaoRepositoryPort {
    private final DispensacaoJpaRepository jpa;
    private final PacienteJpaRepository pacienteJpa;
    private final SaidaEstoqueJpaRepository saidaJpa;
    public DispensacaoJpaAdapter(DispensacaoJpaRepository jpa, PacienteJpaRepository pacienteJpa, SaidaEstoqueJpaRepository saidaJpa) {
        this.jpa = jpa; this.pacienteJpa = pacienteJpa; this.saidaJpa = saidaJpa;
    }
    @Override public Dispensacao salvar(Dispensacao dispensacao) { return toDomain(jpa.save(toEntity(dispensacao))); }
    @Override public Optional<Dispensacao> buscarPorId(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Dispensacao> listar(Long unidadeId, Long pacienteId, Long medicamentoId, LocalDate dataInicial, LocalDate dataFinal) {
        LocalDateTime inicio = dataInicial == null ? null : dataInicial.atStartOfDay();
        LocalDateTime fim = dataFinal == null ? null : dataFinal.atTime(LocalTime.MAX);
        return jpa.findByFiltros(unidadeId, pacienteId, medicamentoId, inicio, fim).stream().map(this::toDomain).toList();
    }
    private DispensacaoEntity toEntity(Dispensacao dispensacao) {
        DispensacaoEntity entity = new DispensacaoEntity(); entity.setId(dispensacao.getId());
        entity.setPaciente(pacienteJpa.getReferenceById(dispensacao.getPaciente().getId()));
        entity.setSaidaEstoque(saidaJpa.getReferenceById(dispensacao.getSaidaEstoque().getId()));
        entity.setNumeroReceita(dispensacao.getNumeroReceita()); entity.setCrmPrescritor(dispensacao.getCrmPrescritor()); return entity;
    }
    private Dispensacao toDomain(DispensacaoEntity entity) {
        Dispensacao dispensacao = new Dispensacao(); dispensacao.setId(entity.getId());
        dispensacao.setPaciente(toPaciente(entity.getPaciente())); dispensacao.setSaidaEstoque(toSaida(entity.getSaidaEstoque()));
        dispensacao.setNumeroReceita(entity.getNumeroReceita()); dispensacao.setCrmPrescritor(entity.getCrmPrescritor()); return dispensacao;
    }
    private Paciente toPaciente(PacienteEntity entity) {
        Paciente paciente = new Paciente(); paciente.setId(entity.getId()); paciente.setNome(entity.getNome()); paciente.setCpf(entity.getCpf());
        paciente.setDataNascimento(entity.getDataNascimento()); paciente.setCartaoSus(entity.getCartaoSus()); paciente.setTelefone(entity.getTelefone());
        paciente.setEmail(entity.getEmail()); paciente.setCidade(entity.getCidade()); paciente.setUf(entity.getUf()); paciente.setStatus(entity.getStatus());
        paciente.setDataCriacao(entity.getDataCriacao()); paciente.setDataUltimaAlteracao(entity.getDataUltimaAlteracao()); return paciente;
    }
    private SaidaEstoque toSaida(SaidaEstoqueEntity entity) {
        SaidaEstoque saida = new SaidaEstoque(); saida.setId(entity.getId());
        Medicamento medicamento = new Medicamento(); medicamento.setId(entity.getMedicamento().getId()); saida.setMedicamento(medicamento);
        saida.setUnidadeSaude(new UnidadeSaude(entity.getUnidadeSaude().getId())); saida.setQuantidadeTotal(entity.getQuantidadeTotal());
        saida.setDataSaida(entity.getDataSaida()); saida.setMotivo(entity.getMotivo()); saida.setObservacao(entity.getObservacao());
        saida.setUsuarioResponsavelId(entity.getUsuarioResponsavelId());
        List<SaidaEstoque.LoteUtilizado> lotes = entity.getLotesUtilizados().stream()
                .map(lote -> new SaidaEstoque.LoteUtilizado(lote.getLote().getId(), lote.getNumeroLote(), lote.getQuantidadeConsumida())).toList();
        if (!lotes.isEmpty()) saida.definirLotesUtilizados(lotes); return saida;
    }
}