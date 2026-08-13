package com.pharmaguard.api.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI pharmaGuardApiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PharmaGuard API")
                        .description("API para gestão inteligente de estoque farmacêutico")
                        .version("0.1.0"));
    }
}
