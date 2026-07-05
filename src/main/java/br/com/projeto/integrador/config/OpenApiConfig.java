package br.com.projeto.integrador.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
            .title("Projeto Integrador - Sistema de Chamados")
            .description("API REST para gerenciamento de chamados desenvolvida em Java com Spring Boot.")
            .version("1.0.0"));
    }
}
