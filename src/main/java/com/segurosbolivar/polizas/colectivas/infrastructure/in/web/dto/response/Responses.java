package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** DTOs de salida expuestos al front. */
public final class Responses {

    private Responses() {
    }

    public record ActorResponse(String tipoDocumento, String numeroDocumento, String nombre, String email,
                                String telefono, String tipoPersona) {
    }

    public record RiesgoResponse(Long id, String direccion, String ciudad, String caracteristicas,
                                 BigDecimal canonMensual, String estado, ActorResponse arrendatario,
                                 ActorResponse arrendador, String motivoCancelacion) {
    }

    public record PolizaResponse(Long id, String numeroPoliza, String tipo, String estado, LocalDate fechaInicio,
                                 LocalDate fechaFin, int mesesVigencia, BigDecimal canonMensual, BigDecimal valorPrima,
                                 BigDecimal ipcPorcentaje, boolean renovacionAutomatica, ActorResponse tomador,
                                 String motivoCancelacion, List<RiesgoResponse> riesgos) {
    }

    public record PolizaResumenResponse(Long id, String numeroPoliza, String estado, LocalDate fechaInicio,
                                        LocalDate fechaFin, BigDecimal canonMensual, BigDecimal valorPrima,
                                        boolean renovacionAutomatica, ActorResponse tomador, int riesgosActivos) {
    }

    public record PaginaResponse<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
    }

    public record OperacionResponse(String operacionId, String tipo, String estadoSincronizacionCore, Long riesgoId) {
    }

    public record OperacionPolizaResponse(OperacionResponse operacion, PolizaResponse poliza) {
    }

    public record EventoPolizaResponse(String tipoOperacion, Long polizaId, String numeroPoliza, Long riesgoId,
                                       String estadoPoliza, BigDecimal canonMensual, BigDecimal valorPrima,
                                       LocalDateTime fecha) {
    }
}
