package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Conexión al CORE (monolito modular) que centraliza la base de datos. */
@ConfigurationProperties(prefix = "app.core")
public record CoreClientProperties(String baseUrl, String apiKey, Duration connectTimeout, Duration responseTimeout,
                                   String circuitBreaker, RetryPolicy retry) {
}
