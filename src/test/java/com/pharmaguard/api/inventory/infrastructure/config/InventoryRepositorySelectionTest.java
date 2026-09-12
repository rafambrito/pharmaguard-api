package com.pharmaguard.api.inventory.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryMedicamentoRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.MedicamentoJpaAdapter;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/pharmaguard",
    "spring.datasource.username=admin",
    "spring.datasource.password=admin",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=true",
    "spring.security.user.name=unused",
    "spring.security.user.password=unused"
})
class InventoryRepositorySelectionTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldUseJpaRepositoryInsteadOfInMemoryFallbackWhenDatabaseIsAvailable() {
        Object repository = context.getBean(MedicamentoUseCase.MedicamentoRepositoryPort.class);
        System.out.println("medicamento repository bean: " + repository.getClass().getName());
        System.out.println("bean names: " + java.util.Arrays.toString(context.getBeanNamesForType(MedicamentoUseCase.MedicamentoRepositoryPort.class)));

        assertThat(repository)
            .isInstanceOf(MedicamentoJpaAdapter.class)
            .isNotInstanceOf(InMemoryMedicamentoRepositoryAdapter.class);
    }
}
