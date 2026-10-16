package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config;

import com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties.SecurityProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger / OpenAPI 3 con esquemas de seguridad Bearer JWT y x-api-key. */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI(SecurityProperties security,
                    @Value("${app.openapi.titulo}") String titulo,
                    @Value("${app.openapi.descripcion}") String descripcion,
                    @Value("${app.openapi.version}") String version) {
        return new OpenAPI()
                .info(new Info().title(titulo).description(descripcion).version(version)
                        .contact(new Contact().name("Plataforma de Gestión de Pólizas - Seguros Bolívar")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                .description("Token emitido por Keycloak (realm seguros-bolivar)"))
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)
                                .name(security.apiKeyHeader()).description("Valor por defecto: 123456")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("apiKey"));
    }
}
