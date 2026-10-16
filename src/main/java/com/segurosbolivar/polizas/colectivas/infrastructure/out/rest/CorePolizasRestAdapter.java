package com.segurosbolivar.polizas.colectivas.infrastructure.out.rest;

import com.segurosbolivar.polizas.colectivas.application.port.out.CorePolizasPort;
import com.segurosbolivar.polizas.colectivas.domain.model.CambiosPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.NuevoRiesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.PolizaResumen;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.SolicitudPoliza;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreApiResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.CancelacionDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.OperacionPolizaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.PaginaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.PolizaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.PolizaResumenDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.RenovarDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreErrorResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.mapper.CoreDtoMapper;
import com.segurosbolivar.polizas.colectivas.shared.domain.exception.ErrorCode;
import com.segurosbolivar.polizas.colectivas.shared.domain.exception.ErrorNegocioCoreException;
import com.segurosbolivar.polizas.colectivas.shared.domain.exception.ServicioExternoException;
import com.segurosbolivar.polizas.colectivas.shared.domain.model.Pagina;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties.CoreClientProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

/**
 * Adapter de salida hacia el CORE. Resiliencia: timeout, circuit breaker, retry con backoff exponencial + jitter
 * (seguro porque toda escritura viaja con Idempotency-Key). Los rechazos de negocio (4xx) se propagan tal cual.
 */
@Component
public class CorePolizasRestAdapter implements CorePolizasPort {

    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";
    private static final ParameterizedTypeReference<CoreApiResponse<OperacionPolizaDto>> OPERACION =
            new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<CoreApiResponse<PolizaDto>> POLIZA =
            new ParameterizedTypeReference<>() { };
    private static final ParameterizedTypeReference<CoreApiResponse<PaginaDto<PolizaResumenDto>>> PAGINA =
            new ParameterizedTypeReference<>() { };

    private final WebClient client;
    private final CoreDtoMapper mapper;
    private final CoreClientProperties properties;
    private final CircuitBreaker circuitBreaker;

    public CorePolizasRestAdapter(@Qualifier("coreWebClient") WebClient client, CoreDtoMapper mapper,
                                  CoreClientProperties properties, CircuitBreakerRegistry registry) {
        this.client = client;
        this.mapper = mapper;
        this.properties = properties;
        this.circuitBreaker = registry.circuitBreaker(properties.circuitBreaker());
    }

    @Override
    public Mono<ResultadoOperacion> crear(SolicitudPoliza solicitud, String idempotencyKey) {
        return ejecutar(client.post().uri("/api/v1/polizas").header(IDEMPOTENCY_KEY, idempotencyKey)
                .bodyValue(mapper.aDto(solicitud)), OPERACION, "crear póliza").map(mapper::aDominio);
    }

    @Override
    public Mono<Poliza> obtener(Long polizaId) {
        return ejecutar(client.get().uri("/api/v1/polizas/{id}", polizaId), POLIZA, "consultar póliza")
                .map(r -> mapper.aDominio(r.data()));
    }

    @Override
    public Mono<Pagina<PolizaResumen>> listar(String tipo, String estado, int pagina, int tamano) {
        return ejecutar(client.get().uri(b -> b.path("/api/v1/polizas")
                        .queryParam("tipo", tipo)
                        .queryParamIfPresent("estado", java.util.Optional.ofNullable(estado))
                        .queryParam("pagina", pagina)
                        .queryParam("tamano", tamano)
                        .build()),
                PAGINA, "listar pólizas").map(r -> mapper.aDominio(r.data()));
    }

    @Override
    public Mono<ResultadoOperacion> modificar(Long polizaId, CambiosPoliza cambios, String idempotencyKey) {
        return ejecutar(client.patch().uri("/api/v1/polizas/{id}", polizaId).header(IDEMPOTENCY_KEY, idempotencyKey)
                .bodyValue(mapper.aDto(cambios)), OPERACION, "modificar póliza").map(mapper::aDominio);
    }

    @Override
    public Mono<ResultadoOperacion> renovar(Long polizaId, BigDecimal ipcPorcentaje, String idempotencyKey) {
        return ejecutar(client.post().uri("/api/v1/polizas/{id}/renovaciones", polizaId)
                .header(IDEMPOTENCY_KEY, idempotencyKey).bodyValue(new RenovarDto(ipcPorcentaje)),
                OPERACION, "renovar póliza").map(mapper::aDominio);
    }

    @Override
    public Mono<ResultadoOperacion> cancelar(Long polizaId, String motivo, String idempotencyKey) {
        return ejecutar(client.post().uri("/api/v1/polizas/{id}/cancelaciones", polizaId)
                .header(IDEMPOTENCY_KEY, idempotencyKey).bodyValue(new CancelacionDto(motivo)),
                OPERACION, "cancelar póliza").map(mapper::aDominio);
    }

    @Override
    public Mono<ResultadoOperacion> agregarRiesgo(Long polizaId, NuevoRiesgo riesgo, String idempotencyKey) {
        return ejecutar(client.post().uri("/api/v1/polizas/{id}/riesgos", polizaId)
                        .header(IDEMPOTENCY_KEY, idempotencyKey).bodyValue(mapper.aDto(riesgo)),
                OPERACION, "agregar riesgo").map(mapper::aDominio);
    }

    @Override
    public Mono<ResultadoOperacion> cancelarRiesgo(Long riesgoId, String motivo, String idempotencyKey) {
        return ejecutar(client.post().uri("/api/v1/riesgos/{id}/cancelaciones", riesgoId)
                        .header(IDEMPOTENCY_KEY, idempotencyKey).bodyValue(new CancelacionDto(motivo)),
                OPERACION, "cancelar riesgo").map(mapper::aDominio);
    }

    /** Plantilla común (DRY) de invocación resiliente al CORE. */
    private <T> Mono<CoreApiResponse<T>> ejecutar(WebClient.RequestHeadersSpec<?> peticion,
                                                  ParameterizedTypeReference<CoreApiResponse<T>> tipo,
                                                  String operacion) {
        return peticion.retrieve()
                .onStatus(HttpStatusCode::isError, this::aExcepcion)
                .bodyToMono(tipo)
                .timeout(properties.responseTimeout())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .retryWhen(properties.retry().toSpec(CorePolizasRestAdapter::esTransitorio, operacion));
    }

    private Mono<? extends Throwable> aExcepcion(ClientResponse respuesta) {
        int status = respuesta.statusCode().value();
        return respuesta.bodyToMono(CoreErrorResponse.class)
                .onErrorResume(e -> Mono.empty())
                .defaultIfEmpty(new CoreErrorResponse("ERROR", status, ErrorCode.ERROR_SERVICIO_EXTERNO.name(),
                        "El CORE respondió con estado " + status))
                .map(error -> {
                    ErrorCode codigo = Arrays.stream(ErrorCode.values())
                            .filter(c -> c.name().equals(error.codigo())).findFirst()
                            .orElse(status >= 500 ? ErrorCode.ERROR_SERVICIO_EXTERNO : ErrorCode.REGLA_NEGOCIO);
                    String mensaje = error.mensaje() == null ? "Error del CORE" : error.mensaje();
                    return status < 500
                            ? new ErrorNegocioCoreException(status, codigo, mensaje)
                            : new ServicioExternoException(status, codigo, mensaje);
                });
    }

    static boolean esTransitorio(Throwable e) {
        if (e instanceof ErrorNegocioCoreException negocio) {
            return negocio.getStatus() == 429;
        }
        return e instanceof WebClientRequestException
                || e instanceof TimeoutException
                || e instanceof ServicioExternoException;
    }
}
