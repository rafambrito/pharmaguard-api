package com.pharmaguard.api.supplier.adapters.in.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import com.pharmaguard.api.supplier.adapters.in.controller.ContatoFornecedorController;
import com.pharmaguard.api.supplier.adapters.in.controller.FornecedorController;
import com.pharmaguard.api.supplier.adapters.in.controller.LeadTimeFornecedorController;
import com.pharmaguard.api.supplier.adapters.in.mapper.SupplierAdapterInMapper;
import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCase;
import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCaseImpl;
import com.pharmaguard.api.supplier.application.FornecedorUseCase;
import com.pharmaguard.api.supplier.application.FornecedorUseCaseImpl;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCase;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCaseImpl;
import com.pharmaguard.api.supplier.support.SupplierTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class FornecedorControllerIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SupplierTestSupport.SupplierStore store = SupplierTestSupport.store();
        FornecedorUseCase fornecedorUseCase = new FornecedorUseCaseImpl(new SupplierTestSupport.FornecedorRepositoryAdapter(store));
        ContatoFornecedorUseCase contatoFornecedorUseCase = new ContatoFornecedorUseCaseImpl(new SupplierTestSupport.ContatoFornecedorRepositoryAdapter(store));
        LeadTimeFornecedorUseCase leadTimeFornecedorUseCase = new LeadTimeFornecedorUseCaseImpl(new SupplierTestSupport.LeadTimeFornecedorRepositoryAdapter(store));
        SupplierAdapterInMapper mapper = new SupplierAdapterInMapper();

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new FornecedorController(fornecedorUseCase, mapper),
                        new ContatoFornecedorController(contatoFornecedorUseCase, mapper),
                        new LeadTimeFornecedorController(leadTimeFornecedorUseCase, mapper))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .setValidator(validator)
                .build();
    }

    @Test
    void deveExecutarCrudDeFornecedorContatoELeadTime() throws Exception {
        mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Distribuidora Alpha",
                                  "codigo": "FORN-001",
                                  "documento": "12.345.678/0001-90",
                                  "observacao": "Fornecedor principal",
                                  "leadTimeDias": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/fornecedores/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Distribuidora Alpha")))
                .andExpect(jsonPath("$.ativo", is(true)));

        mockMvc.perform(get("/api/v1/fornecedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Distribuidora Alpha")));

        mockMvc.perform(get("/api/v1/fornecedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/api/v1/fornecedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Distribuidora Alpha Atualizada",
                                  "codigo": "FORN-001",
                                  "documento": "12.345.678/0001-90",
                                  "observacao": "Atualizado",
                                  "leadTimeDias": 7,
                                  "ativo": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Distribuidora Alpha Atualizada")))
                .andExpect(jsonPath("$.ativo", is(false)))
                .andExpect(jsonPath("$.leadTimeDias", is(7)));

        mockMvc.perform(post("/api/v1/fornecedores/1/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Souza",
                                  "cargo": "Executiva de contas",
                                  "telefone": "+55 11 99999-9999",
                                  "email": "maria@fornecedor.com",
                                  "canalPrincipal": "EMAIL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/fornecedores/1/contatos/2"))
                .andExpect(jsonPath("$.nome", is("Maria Souza")))
                .andExpect(jsonPath("$.canalPrincipal", is("EMAIL")));

        mockMvc.perform(get("/api/v1/fornecedores/1/contatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/api/v1/fornecedores/1/contatos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Maria Souza Atualizada",
                                  "cargo": "Executiva de contas",
                                  "telefone": "+55 11 98888-8888",
                                  "email": "maria.atualizada@fornecedor.com",
                                  "canalPrincipal": "EMAIL",
                                  "ativo": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Maria Souza Atualizada")))
                .andExpect(jsonPath("$.ativo", is(false)));

        mockMvc.perform(get("/api/v1/fornecedores/1/lead-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadTimeDias", is(7)));

        mockMvc.perform(put("/api/v1/fornecedores/1/lead-time")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "leadTimeDias": 31
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadTimeDias", is(31)))
                .andExpect(jsonPath("$.classificacao", is("ELEVADO")));

        mockMvc.perform(delete("/api/v1/fornecedores/1/contatos/2"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/fornecedores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroDeValidacaoQuandoFornecedorNaoTiverNome() throws Exception {
        mockMvc.perform(post("/api/v1/fornecedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "FORN-001",
                                  "leadTimeDias": 5
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Requisicao invalida. Verifique os campos informados.")));
    }
}