package com.segurosbolivar.polizas.colectivas.domain.model;

/** Operación registrada en el CORE con su estado de sincronización con el sistema legado. */
public record Operacion(String operacionId, String tipo, String estado, Long polizaId, Long riesgoId, String detalle) {
}
