package com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.filter;

import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.ErrorResponse;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.TipoMensaje;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Escribe errores JSON desde filtros (fuera del alcance de @RestControllerAdvice). */
@Component
public class JsonErrorWriter {

    private final ObjectMapper objectMapper;

    public JsonErrorWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<Void> escribir(ServerWebExchange exchange, HttpStatus status, String codigo, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.empty();
        }
        ErrorResponse cuerpo = new ErrorResponse(TipoMensaje.ERROR, status.value(), codigo, mensaje,
                exchange.getRequest().getPath().value(), CorrelationIdWebFilter.obtener(exchange), List.of(), Instant.now());
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(cuerpo);
        } catch (JsonProcessingException e) {
            bytes = ("{\"codigo\":\"" + codigo + "\"}").getBytes(StandardCharsets.UTF_8);
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
