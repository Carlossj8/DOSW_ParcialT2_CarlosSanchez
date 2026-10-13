package edu.dosw.parcial.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DOSW — Parcial Práctico Segundo Tercio")
                .description("API REST para el examen práctico de DOSW - Segundo Tercio")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Carlos Andres Sanchez Jimenez")
                    .email("carlos.sanchez-j@mail.escuelaing.edu.co")));
    }
}
