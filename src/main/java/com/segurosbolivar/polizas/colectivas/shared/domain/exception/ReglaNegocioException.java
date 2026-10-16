package com.segurosbolivar.polizas.colectivas.shared.domain.exception;

public class ReglaNegocioException extends DomainException {

    public ReglaNegocioException(ErrorCode codigo, String mensaje) {
        super(codigo, mensaje);
    }

    public ReglaNegocioException(String mensaje) {
        super(ErrorCode.REGLA_NEGOCIO, mensaje);
    }
}
