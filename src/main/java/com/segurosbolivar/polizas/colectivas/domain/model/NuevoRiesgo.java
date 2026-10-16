package com.segurosbolivar.polizas.colectivas.domain.model;

import java.math.BigDecimal;

public record NuevoRiesgo(String direccion, String ciudad, String caracteristicas, BigDecimal canonMensual,
                          Actor arrendatario, Actor arrendador) {
}
