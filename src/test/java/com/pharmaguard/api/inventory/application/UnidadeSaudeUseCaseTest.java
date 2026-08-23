package com.pharmaguard.api.inventory.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UnidadeSaudeUseCaseTest {

    @Test
    void deveCriarUnidadeAtivaComDataDeCadastro() {
        FakeRepository repository = new FakeRepository();
        UnidadeSaude criada = new UnidadeSaudeUseCaseImpl(repository).criar(unidade("UBS-01"));

        assertThat(criada.getId()).isEqualTo(1L);
        assertThat(criada.getStatus()).isEqualTo(UnidadeSaude.Status.ATIVA);
        assertThat(criada.getDataCadastro()).isNotNull();
    }

    @Test
    void deveRejeitarIdentificacaoDuplicadaEPermitirInativacao() {
        FakeRepository repository = new FakeRepository();
        UnidadeSaudeUseCase useCase = new UnidadeSaudeUseCaseImpl(repository);
        useCase.criar(unidade("UBS-01"));

        assertThatThrownBy(() -> useCase.criar(unidade("ubs-01")))
                .isInstanceOf(BusinessException.class);

        useCase.inativar(1L);
        assertThat(useCase.buscarPorId(1L).getStatus()).isEqualTo(UnidadeSaude.Status.INATIVA);
    }

    @Test
    void deveAtualizarDadosDaUnidade() {
        FakeRepository repository = new FakeRepository();
        UnidadeSaudeUseCase useCase = new UnidadeSaudeUseCaseImpl(repository);
        UnidadeSaude criada = useCase.criar(unidade("UBS-01"));
        criada.setNome("Unidade Norte");

        UnidadeSaude atualizada = useCase.atualizar(criada);

        assertThat(atualizada.getNome()).isEqualTo("Unidade Norte");
        assertThat(atualizada.getDataAtualizacao()).isNotNull();
    }

    private UnidadeSaude unidade(String identificacao) {
        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setIdentificacao(identificacao);
        unidade.setNome("Unidade Central");
        unidade.setTipo("UBS");
        unidade.setEndereco("Rua Principal, 1");
        return unidade;
    }

    private static class FakeRepository implements UnidadeSaudeUseCase.UnidadeSaudeRepositoryPort {
        private final List<UnidadeSaude> unidades = new ArrayList<>();

        @Override public UnidadeSaude salvar(UnidadeSaude unidade) { unidade.setId(1L); unidades.add(unidade); return unidade; }
        @Override public UnidadeSaude atualizar(UnidadeSaude unidade) { return unidade; }
        @Override public Optional<UnidadeSaude> buscarPorId(Long id) { return unidades.stream().filter(u -> id.equals(u.getId())).findFirst(); }
        @Override public List<UnidadeSaude> listarTodos() { return unidades; }
        @Override public boolean existePorIdentificacao(String identificacao) { return unidades.stream().anyMatch(u -> u.getIdentificacao().equalsIgnoreCase(identificacao)); }
    }
}