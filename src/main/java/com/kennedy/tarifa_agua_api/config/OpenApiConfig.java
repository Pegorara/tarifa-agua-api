package com.kennedy.tarifa_agua_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Tabela Tarifária de Água")
                        .description("Gerenciamento e cálculo de tarifas de água com base em categorias de consumidor e faixas progressivas de consumo.")
                        .version("v1"));
    }
}
