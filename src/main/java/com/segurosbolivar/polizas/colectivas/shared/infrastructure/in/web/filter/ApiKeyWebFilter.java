package com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.filter;

import com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties.SecurityProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Valida el header obligatorio x-api-key antes de la cadena de Spring Security (orden -200 &lt; -100).
 * La comparación es en tiempo constante para evitar ataques de temporización.
 */
@Component
@Order(-200)
public class ApiKeyWebFilter implements WebFilter {

    private final SecurityProperties properties;
    private final JsonErrorWriter errorWriter;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public ApiKeyWebFilter(SecurityProperties properties, JsonErrorWriter errorWriter) {
        this.properties = properties;
        this.errorWriter = errorWriter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod()) || esPublica(path)) {
            return chain.filter(exchange);
        }
        String recibida = exchange.getRequest().getHeaders().getFirst(properties.apiKeyHeader());
        if (recibida != null && MessageDigest.isEqual(
                recibida.getBytes(StandardCharsets.UTF_8), properties.apiKey().getBytes(StandardCharsets.UTF_8))) {
            return chain.filter(exchange);
        }
        return errorWriter.escribir(exchange, HttpStatus.UNAUTHORIZED, "API_KEY_INVALIDA",
                "El header obligatorio '%s' no fue enviado o es inválido".formatted(properties.apiKeyHeader()));
    }

    private boolean esPublica(String path) {
        return properties.publicPaths().stream().anyMatch(patron -> matcher.match(patron, path));
    }
}
