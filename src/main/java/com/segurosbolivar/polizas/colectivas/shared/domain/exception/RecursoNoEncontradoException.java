package com.segurosbolivar.polizas.colectivas.shared.domain.exception;

public class RecursoNoEncontradoException extends DomainException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(ErrorCode.RECURSO_NO_ENCONTRADO, "%s con id %s no existe".formatted(recurso, id));
    }

    public RecursoNoEncontradoException(String mensaje) {
        super(ErrorCode.RECURSO_NO_ENCONTRADO, mensaje);
    }
}
