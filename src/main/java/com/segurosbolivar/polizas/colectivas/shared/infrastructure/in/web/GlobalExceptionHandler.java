package com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web;

import com.segurosbolivar.polizas.colectivas.shared.domain.exception.DomainException;
import com.segurosbolivar.polizas.colectivas.shared.domain.exception.ErrorCode;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.filter.CorrelationIdWebFilter;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.ErrorResponse;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.ErrorResponse.CampoError;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.TipoMensaje;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

/**
 * Manejo centralizado de errores: traduce excepciones a códigos HTTP y a un cuerpo uniforme
 * con tipo de mensaje (ERROR / ALERTA), código, mensaje, path y correlationId.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> dominio(DomainException ex, ServerWebExchange exchange) {
        HttpStatus status = resolverStatus(ex);
        TipoMensaje tipo = status.is4xxClientError() && status != HttpStatus.NOT_FOUND ? TipoMensaje.ALERTA : TipoMensaje.ERROR;
        if (status.is5xxServerError()) {
            log.error("Error de dominio [{}]: {}", ex.getCodigo(), ex.getMessage());
        } else {
            log.info("Regla de negocio/validación [{}]: {}", ex.getCodigo(), ex.getMessage());
        }
        return construir(status, tipo, ex.getCodigo().name(), ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> validacionCuerpo(WebExchangeBindException ex, ServerWebExchange exchange) {
        List<CampoError> errores = new ArrayList<>();
        ex.getFieldErrors().forEach(e -> errores.add(new CampoError(e.getField(), e.getDefaultMessage())));
        ex.getGlobalErrors().forEach(e -> errores.add(new CampoError(e.getObjectName(), e.getDefaultMessage())));
        return construir(HttpStatus.BAD_REQUEST, TipoMensaje.ALERTA, ErrorCode.DATOS_INVALIDOS.name(),
                "Los datos de entrada no son válidos", exchange, errores);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> validacionParametros(HandlerMethodValidationException ex, ServerWebExchange exchange) {
        List<CampoError> errores = new ArrayList<>();
        ex.getAllValidationResults().forEach(resultado -> resultado.getResolvableErrors().forEach(error ->
                errores.add(new CampoError(resultado.getMethodParameter().getParameterName(), error.getDefaultMessage()))));
        return construir(HttpStatus.BAD_REQUEST, TipoMensaje.ALERTA, ErrorCode.DATOS_INVALIDOS.name(),
                "Los parámetros de la solicitud no son válidos", exchange, errores);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> violacionRestricciones(ConstraintViolationException ex, ServerWebExchange exchange) {
        List<CampoError> errores = ex.getConstraintViolations().stream()
                .map(v -> new CampoError(v.getPropertyPath().toString(), v.getMessage())).toList();
        return construir(HttpStatus.BAD_REQUEST, TipoMensaje.ALERTA, ErrorCode.DATOS_INVALIDOS.name(),
                "Los parámetros de la solicitud no son válidos", exchange, errores);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> entradaInvalida(ServerWebInputException ex, ServerWebExchange exchange) {
        return construir(HttpStatus.BAD_REQUEST, TipoMensaje.ALERTA, ErrorCode.DATOS_INVALIDOS.name(),
                "Solicitud mal formada: " + ex.getReason(), exchange, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> statusException(ResponseStatusException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        status = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
        return construir(status, TipoMensaje.ERROR, status.name(), ex.getReason(), exchange, List.of());
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ErrorResponse> circuitoAbierto(CallNotPermittedException ex, ServerWebExchange exchange) {
        log.warn("Circuit breaker abierto: {}", ex.getMessage());
        return construir(HttpStatus.SERVICE_UNAVAILABLE, TipoMensaje.ALERTA, ErrorCode.SERVICIO_EXTERNO_NO_DISPONIBLE.name(),
                "Servicio dependiente temporalmente no disponible. Intente más tarde", exchange, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex, ServerWebExchange exchange) {
        log.error("Error no controlado [correlationId={}]", CorrelationIdWebFilter.obtener(exchange), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, TipoMensaje.ERROR, "ERROR_INTERNO",
                "Ocurrió un error inesperado. Reporte el correlationId al equipo de soporte", exchange, List.of());
    }

    private HttpStatus resolverStatus(DomainException ex) {
        if (ex instanceof com.segurosbolivar.polizas.colectivas.shared.domain.exception.ServicioExternoException externo && externo.getStatus() > 0) {
            HttpStatus status = HttpStatus.resolve(externo.getStatus());
            if (status != null) {
                return status;
            }
        }
        return switch (ex.getCodigo()) {
            case RECURSO_NO_ENCONTRADO -> HttpStatus.NOT_FOUND;
            case REGLA_NEGOCIO, ESTADO_INVALIDO, TIPO_POLIZA_INVALIDO -> HttpStatus.UNPROCESSABLE_ENTITY;
            case DATOS_INVALIDOS -> HttpStatus.BAD_REQUEST;
            case CONFLICTO_CONCURRENCIA -> HttpStatus.CONFLICT;
            case SERVICIO_EXTERNO_NO_DISPONIBLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case ERROR_SERVICIO_EXTERNO -> HttpStatus.BAD_GATEWAY;
        };
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, TipoMensaje tipo, String codigo, String mensaje,
                                                    ServerWebExchange exchange, List<CampoError> errores) {
        ErrorResponse cuerpo = new ErrorResponse(tipo, status.value(), codigo, mensaje,
                exchange.getRequest().getPath().value(), CorrelationIdWebFilter.obtener(exchange), errores, Instant.now());
        return ResponseEntity.status(status).body(cuerpo);
    }
}
