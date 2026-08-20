package com.pharmaguard.api.inventory.api.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.inventory.api.controller.CategoriaController;
import com.pharmaguard.api.inventory.api.controller.LoteController;
import com.pharmaguard.api.inventory.api.controller.MedicamentoController;
import com.pharmaguard.api.inventory.api.controller.UnidadeMedidaController;
import com.pharmaguard.api.inventory.api.mapper.InventoryApiMapper;
import com.pharmaguard.api.inventory.application.CategoriaUseCase;
import com.pharmaguard.api.inventory.application.CategoriaUseCaseImpl;
import com.pharmaguard.api.inventory.application.LoteUseCase;
import com.pharmaguard.api.inventory.application.LoteUseCaseImpl;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import com.pharmaguard.api.inventory.application.MedicamentoUseCaseImpl;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCaseImpl;
import com.pharmaguard.api.inventory.support.InventoryTestSupport;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class InventoryControllerIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InventoryTestSupport.InventoryStore store = InventoryTestSupport.store();
        CategoriaUseCase categoriaUseCase = new CategoriaUseCaseImpl(new InventoryTestSupport.CategoriaRepositoryAdapter(store));
        UnidadeMedidaUseCase unidadeMedidaUseCase = new UnidadeMedidaUseCaseImpl(new InventoryTestSupport.UnidadeMedidaRepositoryAdapter(store));
        MedicamentoUseCase medicamentoUseCase = new MedicamentoUseCaseImpl(new InventoryTestSupport.MedicamentoRepositoryAdapter(store));
        LoteUseCase loteUseCase = new LoteUseCaseImpl(new InventoryTestSupport.LoteRepositoryAdapter(store));
        InventoryApiMapper mapper = new InventoryApiMapper();

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new CategoriaController(categoriaUseCase, mapper),
                        new UnidadeMedidaController(unidadeMedidaUseCase, mapper),
                        new MedicamentoController(medicamentoUseCase, mapper),
                        new LoteController(loteUseCase, mapper))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .setValidator(validator)
                .build();
    }

    @Test
    void deveExecutarCrudDeCategoriaEUnidadeMedida() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Antibioticos",
                                  "descricao": "Medicamentos para infeccoes"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/categorias/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Antibioticos")))
                .andExpect(jsonPath("$.ativo", is(true)));

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Antibioticos")));

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Antibioticos Atualizados",
                                  "descricao": "Atualizado",
                                  "ativo": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Antibioticos Atualizados")))
                .andExpect(jsonPath("$.ativo", is(false)));

        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/unidades-medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Miligrama",
                                  "sigla": "mg"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/unidades-medida/2"))
                .andExpect(jsonPath("$.sigla", is("MG")))
                .andExpect(jsonPath("$.ativo", is(true)));
    }

    @Test
    void deveExecutarCrudDeMedicamentoELote() throws Exception {
        criarCategoria();
        criarUnidadeMedida();

        mockMvc.perform(post("/api/v1/medicamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Amoxicilina",
                                  "apresentacao": "500mg capsula",
                                  "descricao": "Antibiotico",
                                  "categoriaId": 1,
                                  "unidadeMedidaId": 2,
                                  "criticidade": "MEDIA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/medicamentos/3"))
                .andExpect(jsonPath("$.nome", is("Amoxicilina")))
                .andExpect(jsonPath("$.categoria.id", is(1)))
                .andExpect(jsonPath("$.unidadeMedida.id", is(2)));

        mockMvc.perform(get("/api/v1/medicamentos/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Amoxicilina")));

        mockMvc.perform(get("/api/v1/medicamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/api/v1/medicamentos/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Amoxicilina Atualizada",
                                  "apresentacao": "500mg capsula",
                                  "descricao": "Antibiotico atualizado",
                                  "categoriaId": 1,
                                  "unidadeMedidaId": 2,
                                  "criticidade": "ALTA",
                                  "ativo": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Amoxicilina Atualizada")))
                .andExpect(jsonPath("$.criticidade", is("ALTA")))
                .andExpect(jsonPath("$.ativo", is(false)));

        mockMvc.perform(post("/api/v1/medicamentos/3/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "numeroLote": "LOT-2026-001",
                                  "dataValidade": "%s",
                                  "quantidadeInicial": 100
                                }
                                """.formatted(LocalDate.now().plusDays(30))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/medicamentos/3/lotes/4"))
                .andExpect(jsonPath("$.numeroLote", is("LOT-2026-001")))
                .andExpect(jsonPath("$.statusValidade", is("VALIDO")));

        mockMvc.perform(get("/api/v1/medicamentos/3/lotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/medicamentos/3/lotes/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroLote", is("LOT-2026-001")));

        mockMvc.perform(delete("/api/v1/medicamentos/3/lotes/4"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/medicamentos/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroDeValidacaoQuandoCategoriaNaoTiverNome() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "descricao": "Sem nome"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Requisicao invalida. Verifique os campos informados.")));
    }

    private void criarCategoria() throws Exception {
        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Antibioticos",
                                  "descricao": "Medicamentos para infeccoes"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    private void criarUnidadeMedida() throws Exception {
        mockMvc.perform(post("/api/v1/unidades-medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Miligrama",
                                  "sigla": "mg"
                                }
                                """))
                .andExpect(status().isCreated());
    }
}
