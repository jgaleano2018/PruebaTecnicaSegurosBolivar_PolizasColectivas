package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Parámetros de seguridad (x-api-key + Keycloak/JWT). Se alimentan desde el archivo .env.
 *
 * @param publicPaths       rutas sin api-key ni JWT (swagger, health)
 * @param jwtExcludedPaths  rutas que exigen api-key pero no JWT (p. ej. core-mock)
 */
@Validated
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        @NotBlank String apiKeyHeader,
        @NotBlank String apiKey,
        boolean jwtEnabled,
        String jwkSetUri,
        String issuerUri,
        List<String> publicPaths,
        List<String> jwtExcludedPaths) {

    public SecurityProperties {
        publicPaths = publicPaths == null ? List.of() : List.copyOf(publicPaths);
        jwtExcludedPaths = jwtExcludedPaths == null ? List.of() : List.copyOf(jwtExcludedPaths);
    }
}
