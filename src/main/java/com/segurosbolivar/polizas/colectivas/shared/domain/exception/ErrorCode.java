package com.segurosbolivar.polizas.colectivas.shared.domain.exception;

/** Catálogo centralizado de códigos de error de negocio (independiente de HTTP). */
public enum ErrorCode {
    RECURSO_NO_ENCONTRADO,
    REGLA_NEGOCIO,
    ESTADO_INVALIDO,
    TIPO_POLIZA_INVALIDO,
    DATOS_INVALIDOS,
    CONFLICTO_CONCURRENCIA,
    SERVICIO_EXTERNO_NO_DISPONIBLE,
    ERROR_SERVICIO_EXTERNO
}
