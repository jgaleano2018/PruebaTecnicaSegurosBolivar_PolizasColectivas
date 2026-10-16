package com.segurosbolivar.polizas.colectivas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Evento de póliza recibido por Kafka (publicado por el CORE vía Outbox). */
public record EventoPoliza(String eventoId, String operacionId, String tipoOperacion, Long polizaId,
                           String numeroPoliza, Long riesgoId, String tipoPoliza, String estadoPoliza,
                           BigDecimal canonMensual, BigDecimal valorPrima, LocalDate fechaInicio, LocalDate fechaFin,
                           LocalDateTime fecha) {
}
