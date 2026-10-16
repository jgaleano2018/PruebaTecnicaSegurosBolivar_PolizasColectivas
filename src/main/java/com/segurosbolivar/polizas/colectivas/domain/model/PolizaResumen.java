package com.segurosbolivar.polizas.colectivas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaResumen(Long id, String numeroPoliza, String tipo, String estado, LocalDate fechaInicio,
                            LocalDate fechaFin, BigDecimal canonMensual, BigDecimal valorPrima,
                            boolean renovacionAutomatica, Actor tomador, int riesgosActivos) {
}
