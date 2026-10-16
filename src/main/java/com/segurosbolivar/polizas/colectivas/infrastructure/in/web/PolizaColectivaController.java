package com.segurosbolivar.polizas.colectivas.infrastructure.in.web;

import com.segurosbolivar.polizas.colectivas.application.port.in.CancelarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.ConsultarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.CrearPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.EventosPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.ModificarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.RenovarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.CancelacionRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.CrearPolizaColectivaRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.ModificarPolizaRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.RenovarPolizaRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.EventoPolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.OperacionPolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.PaginaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.PolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.PolizaResumenResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.mapper.PolizaWebMapper;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import com.segurosbolivar.polizas.colectivas.application.port.in.GestionarRiesgosUseCase;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.RiesgoRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.RiesgoResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/polizas-colectivas")
@Tag(name = "Pólizas Colectiva", description = "Pólizas colectivas para inmobiliarias y administraciones de copropiedad: agregar y cancelar riesgos (arrendatarios asegurados).")
public class PolizaColectivaController {

    private static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private final CrearPolizaUseCase crear;
    private final ConsultarPolizaUseCase consultar;
    private final ModificarPolizaUseCase modificar;
    private final RenovarPolizaUseCase renovar;
    private final CancelarPolizaUseCase cancelar;
    private final EventosPolizaUseCase eventos;
    private final GestionarRiesgosUseCase riesgos;
    private final PolizaWebMapper mapper;
    private final RespuestaAssembler respuestas;

    public PolizaColectivaController(CrearPolizaUseCase crear, ConsultarPolizaUseCase consultar, ModificarPolizaUseCase modificar,
                    RenovarPolizaUseCase renovar, CancelarPolizaUseCase cancelar, EventosPolizaUseCase eventos,
                    PolizaWebMapper mapper, RespuestaAssembler respuestas, GestionarRiesgosUseCase riesgos) {
        this.crear = crear;
        this.consultar = consultar;
        this.modificar = modificar;
        this.renovar = renovar;
        this.cancelar = cancelar;
        this.eventos = eventos;
        this.mapper = mapper;
        this.respuestas = respuestas;
        this.riesgos = riesgos;
    }

    @PostMapping
    @Operation(summary = "Crear póliza colectiva")
    public Mono<ResponseEntity<ApiResponse<OperacionPolizaResponse>>> crear(
            @Valid @RequestBody CrearPolizaColectivaRequest request,
            @RequestHeader(name = IDEMPOTENCY_KEY, required = false) @Size(max = 100) String idempotencyKey) {
        return crear.crear(mapper.aSolicitud(request), idempotencyKey)
                .map(r -> respuestas.construir(r, HttpStatus.CREATED, "Póliza " + r.poliza().numeroPoliza() + " creada"));
    }

    @GetMapping
    @Operation(summary = "Listar pólizas colectivas (filtro opcional por estado)")
    public Mono<ApiResponse<PaginaResponse<PolizaResumenResponse>>> listar(
            @RequestParam(required = false)
            @Pattern(regexp = "ACTIVA|RENOVADA|CANCELADA|SINIESTRADA", message = "Estado inválido") String estado,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int tamano) {
        return consultar.listar(estado, pagina, tamano)
                .map(p -> ApiResponse.confirmacion("CONSULTA_EXITOSA",
                        "Se encontraron %d pólizas".formatted(p.totalElementos()), mapper.aResponse(p)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar póliza colectiva")
    public Mono<ApiResponse<PolizaResponse>> obtener(@PathVariable @Positive Long id) {
        return consultar.obtener(id)
                .map(p -> ApiResponse.confirmacion("CONSULTA_EXITOSA", "Póliza encontrada", mapper.aResponse(p)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modificar póliza colectiva")
    public Mono<ResponseEntity<ApiResponse<OperacionPolizaResponse>>> modificar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ModificarPolizaRequest request,
            @RequestHeader(name = IDEMPOTENCY_KEY, required = false) @Size(max = 100) String idempotencyKey) {
        return modificar.modificar(id, mapper.aDominio(request), idempotencyKey)
                .map(r -> respuestas.construir(r, HttpStatus.OK, "Póliza modificada"));
    }

    @PostMapping("/{id}/renovaciones")
    @Operation(summary = "Solicitar renovación (+IPC)")
    public Mono<ResponseEntity<ApiResponse<OperacionPolizaResponse>>> renovar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody(required = false) RenovarPolizaRequest request,
            @RequestHeader(name = IDEMPOTENCY_KEY, required = false) @Size(max = 100) String idempotencyKey) {
        return renovar.renovar(id, request == null ? null : request.ipcPorcentaje(), idempotencyKey)
                .map(r -> respuestas.construir(r, HttpStatus.OK, "Póliza renovada"));
    }

    @PostMapping("/{id}/cancelaciones")
    @Operation(summary = "Cancelar póliza (cancela todos sus riesgos)")
    public Mono<ResponseEntity<ApiResponse<OperacionPolizaResponse>>> cancelar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody(required = false) CancelacionRequest request,
            @RequestHeader(name = IDEMPOTENCY_KEY, required = false) @Size(max = 100) String idempotencyKey) {
        return cancelar.cancelar(id, request == null ? null : request.motivo(), idempotencyKey)
                .map(r -> respuestas.construir(r, HttpStatus.OK, "Póliza cancelada"));
    }

    @GetMapping("/{id}/riesgos")
    @Operation(summary = "Listar los riesgos de la póliza colectiva")
    public Mono<ApiResponse<List<RiesgoResponse>>> listarRiesgos(@PathVariable @Positive Long id) {
        return riesgos.listarRiesgos(id)
                .map(r -> ApiResponse.confirmacion("CONSULTA_EXITOSA", "La póliza tiene %d riesgos".formatted(r.size()),
                        mapper.aRiesgos(r)));
    }

    @PostMapping("/{id}/riesgos")
    @Operation(summary = "Agregar riesgo a la póliza colectiva")
    public Mono<ResponseEntity<ApiResponse<OperacionPolizaResponse>>> agregarRiesgo(
            @PathVariable @Positive Long id,
            @Valid @RequestBody RiesgoRequest request,
            @RequestHeader(name = IDEMPOTENCY_KEY, required = false) @Size(max = 100) String idempotencyKey) {
        return riesgos.agregarRiesgo(id, mapper.aDominio(request), idempotencyKey)
                .map(r -> respuestas.construir(r, HttpStatus.CREATED, "Riesgo agregado"));
    }

    @PostMapping("/{id}/riesgos/{riesgoId}/cancelaciones")
    @Operation(summary = "Eliminar (cancelar) un riesgo de la póliza colectiva")
    public Mono<ResponseEntity<ApiResponse<OperacionPolizaResponse>>> cancelarRiesgo(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long riesgoId,
            @Valid @RequestBody(required = false) CancelacionRequest request,
            @RequestHeader(name = IDEMPOTENCY_KEY, required = false) @Size(max = 100) String idempotencyKey) {
        return riesgos.cancelarRiesgo(id, riesgoId, request == null ? null : request.motivo(), idempotencyKey)
                .map(r -> respuestas.construir(r, HttpStatus.OK, "Riesgo cancelado"));
    }

    @GetMapping(value = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Eventos en tiempo real (Server-Sent Events alimentados por Kafka)")
    public Flux<ServerSentEvent<EventoPolizaResponse>> eventos() {
        return eventos.suscribir()
                .map(e -> ServerSentEvent.<EventoPolizaResponse>builder()
                        .id(e.eventoId()).event(e.tipoOperacion()).data(mapper.aResponse(e)).build());
    }
}
