package com.segurosbolivar.polizas.colectivas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Riesgo(Long id, String codigo, String direccion, String ciudad, String caracteristicas,
                     BigDecimal canonMensual, String estado, Actor asegurado, Actor beneficiario,
                     String motivoCancelacion, LocalDateTime fechaCancelacion) {
}
