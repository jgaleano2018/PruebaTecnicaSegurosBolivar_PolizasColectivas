package com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Contrato JSON del CORE (anticorruption layer: se mapea al dominio en CoreDtoMapper). */
public final class CoreDtos {

    private CoreDtos() {
    }

    public record ActorDto(Long id, String tipoDocumento, String numeroDocumento, String nombre, String email,
                           String telefono, String tipoPersona) {
    }

    public record RiesgoDto(Long id, String codigo, String direccion, String ciudad, String caracteristicas,
                            BigDecimal canonMensual, String estado, ActorDto asegurado, ActorDto beneficiario,
                            String motivoCancelacion, LocalDateTime fechaCancelacion) {
    }

    public record PolizaDto(Long id, String numeroPoliza, String tipo, String estado, LocalDate fechaInicio,
                            LocalDate fechaFin, int mesesVigencia, BigDecimal canonMensual, BigDecimal valorPrima,
                            BigDecimal ipcPorcentaje, boolean renovacionAutomatica, ActorDto tomador,
                            String motivoCancelacion, List<RiesgoDto> riesgos, Long version) {
    }

    public record PolizaResumenDto(Long id, String numeroPoliza, String tipo, String estado, LocalDate fechaInicio,
                                   LocalDate fechaFin, BigDecimal canonMensual, BigDecimal valorPrima,
                                   boolean renovacionAutomatica, ActorDto tomador, int riesgosActivos) {
    }

    public record PaginaDto<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
    }

    public record OperacionDto(String operacionId, String tipo, String estado, Long polizaId, Long riesgoId,
                               String detalle) {
    }

    public record OperacionPolizaDto(OperacionDto operacion, PolizaDto poliza) {
    }

    // ---- solicitudes ----
    public record CrearPolizaDto(String tipo, LocalDate fechaInicio, Integer mesesVigencia, ActorDto tomador,
                                 List<RiesgoNuevoDto> riesgos, Boolean renovacionAutomatica) {
    }

    public record RiesgoNuevoDto(String direccion, String ciudad, String caracteristicas, BigDecimal canonMensual,
                                 ActorDto arrendatario, ActorDto arrendador) {
    }

    public record ModificarPolizaDto(Boolean renovacionAutomatica, BigDecimal canonMensual, String emailTomador,
                                     String telefonoTomador) {
    }

    public record RenovarDto(BigDecimal ipcPorcentaje) {
    }

    public record CancelacionDto(String motivo) {
    }
}
