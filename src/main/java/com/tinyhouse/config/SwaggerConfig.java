package com.tinyhouse.config;
import io.swagger.v3.oas.models.OpenAPI; import io.swagger.v3.oas.models.info.*; import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration; import java.util.List;
@Configuration
public class SwaggerConfig {
    @Bean public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title("TinyHouse API").version("1.0.0").description("Tiny House Rezervasyon ve Yönetim Sistemi API")
                .contact(new Contact().name("TinyHouse Team").email("info@tinyhouse.com")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
