package com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.filter;

import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/** Asigna/propaga el X-Correlation-Id para trazabilidad extremo a extremo. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdWebFilter implements WebFilter {

    public static final String HEADER = "X-Correlation-Id";
    public static final String ATRIBUTO = "correlationId";
    private static final Pattern FORMATO_VALIDO = Pattern.compile("^[A-Za-z0-9-]{1,64}$");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String recibido = exchange.getRequest().getHeaders().getFirst(HEADER);
        String correlationId = recibido != null && FORMATO_VALIDO.matcher(recibido).matches()
                ? recibido
                : UUID.randomUUID().toString();
        exchange.getAttributes().put(ATRIBUTO, correlationId);
        exchange.getResponse().getHeaders().set(HEADER, correlationId);
        return chain.filter(exchange).contextWrite(ctx -> ctx.put(ATRIBUTO, correlationId));
    }

    public static String obtener(ServerWebExchange exchange) {
        Object valor = exchange.getAttribute(ATRIBUTO);
        return valor == null ? null : valor.toString();
    }
}
