package com.segurosbolivar.polizas.colectivas.shared.domain.exception;

/** Rechazo de negocio (4xx) del CORE: no es una falla técnica, el circuit breaker lo ignora. */
public class ErrorNegocioCoreException extends ServicioExternoException {

    public ErrorNegocioCoreException(int status, ErrorCode codigo, String mensaje) {
        super(status, codigo, mensaje);
    }
}
