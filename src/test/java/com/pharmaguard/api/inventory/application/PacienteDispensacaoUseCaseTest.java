package com.pharmaguard.api.inventory.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pharmaguard.api.inventory.domain.Dispensacao;
import com.pharmaguard.api.inventory.domain.Paciente;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PacienteDispensacaoUseCaseTest {

    @Test
    void deveNormalizarCpfECriarPacienteAtivo() {
        PacienteUseCase useCase = new PacienteUseCaseImpl(new PacienteRepository());
        Paciente paciente = paciente();

        Paciente salvo = useCase.criar(paciente);

        assertThat(salvo.getCpf()).isEqualTo("52998224725");
        assertThat(salvo.getStatus()).isEqualTo(Paciente.Status.ATIVO);
        assertThat(salvo.getDataCriacao()).isNotNull();
    }

    @Test
    void deveRejeitarCpfInvalido() {
        Paciente paciente = paciente();

        assertThatThrownBy(() -> paciente.setCpf("111.111.111-11"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cpf invalido");
    }

    @Test
    void deveBuscarPacientePorCpfNormalizado() {
        Paciente paciente = paciente();
        paciente.setId(1L);
        PacienteUseCase useCase = new PacienteUseCaseImpl(new PacienteRepository(paciente));

        List<Paciente> encontrados = useCase.listar("529.982.247-25", null);

        assertThat(encontrados).containsExactly(paciente);
    }

    @Test
    void deveBloquearDispensacaoParaPacienteInativo() {
        Paciente paciente = paciente();
        paciente.setId(1L);
        paciente.setStatus(Paciente.Status.INATIVO);
        PacienteUseCase pacienteUseCase = new PacienteUseCaseImpl(new PacienteRepository(paciente));
        SaidaUseCase saidaUseCase = new SaidaUseCase();
        DispensacaoUseCase useCase = new DispensacaoUseCaseImpl(new DispensacaoRepository(), pacienteUseCase, saidaUseCase);

        assertThatThrownBy(() -> useCase.registrar(1L, 1L, 1L, 2, null, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("paciente inativo nao pode receber dispensacao");
        assertThat(saidaUseCase.registros).isEmpty();
    }

    @Test
    void deveRegistrarDispensacaoComSaidaFefoDelegada() {
        Paciente paciente = paciente();
        paciente.setId(1L);
        paciente.setStatus(Paciente.Status.ATIVO);
        PacienteUseCase pacienteUseCase = new PacienteUseCaseImpl(new PacienteRepository(paciente));
        SaidaUseCase saidaUseCase = new SaidaUseCase();
        DispensacaoUseCase useCase = new DispensacaoUseCaseImpl(new DispensacaoRepository(), pacienteUseCase, saidaUseCase);

        Dispensacao dispensacao = useCase.registrar(2L, 1L, 3L, 4, "RX-1", "CRM-SP 1", "Uso continuo");

        assertThat(dispensacao.getSaidaEstoque().getMotivo()).isEqualTo(SaidaEstoque.Motivo.DISPENSACAO);
        assertThat(saidaUseCase.registros).containsExactly("2:3:4");
        assertThat(dispensacao.getNumeroReceita()).isEqualTo("RX-1");
    }

    private Paciente paciente() {
        Paciente paciente = new Paciente();
        paciente.setNome("Maria Silva");
        paciente.setCpf("529.982.247-25");
        paciente.setDataNascimento(LocalDate.of(1980, 1, 1));
        return paciente;
    }

    private static final class PacienteRepository implements PacienteUseCase.PacienteRepositoryPort {
        private final List<Paciente> pacientes = new ArrayList<>();
        private PacienteRepository(Paciente... pacientes) { this.pacientes.addAll(List.of(pacientes)); }
        @Override public Paciente salvar(Paciente paciente) { if (paciente.getId() == null) paciente.setId((long) pacientes.size() + 1); pacientes.removeIf(item -> item.getId().equals(paciente.getId())); pacientes.add(paciente); return paciente; }
        @Override public Optional<Paciente> buscarPorId(Long id) { return pacientes.stream().filter(paciente -> paciente.getId().equals(id)).findFirst(); }
        @Override public List<Paciente> listar(String cpf, String nome) { return pacientes; }
        @Override public boolean existePorCpf(String cpf) { return pacientes.stream().anyMatch(paciente -> paciente.getCpf().equals(cpf)); }
    }

    private static final class DispensacaoRepository implements DispensacaoUseCase.DispensacaoRepositoryPort {
        @Override public Dispensacao salvar(Dispensacao dispensacao) { dispensacao.setId(1L); return dispensacao; }
        @Override public Optional<Dispensacao> buscarPorId(Long id) { return Optional.empty(); }
        @Override public List<Dispensacao> listar(Long unidadeId, Long pacienteId, Long medicamentoId, LocalDate dataInicial, LocalDate dataFinal) { return List.of(); }
    }

    private static final class SaidaUseCase implements SaidaEstoqueUseCase {
        private final List<String> registros = new ArrayList<>();
        @Override public SaidaEstoque registrar(Long unidadeId, Long medicamentoId, SaidaEstoque saida) {
            registros.add(unidadeId + ":" + medicamentoId + ":" + saida.getQuantidadeTotal());
            saida.setId(2L); saida.setDataSaida(LocalDateTime.now()); return saida;
        }
        @Override public SaidaEstoque buscarPorId(Long id) { throw new UnsupportedOperationException(); }
        @Override public List<SaidaEstoque> listar(Long medicamentoId) { return List.of(); }
    }
}