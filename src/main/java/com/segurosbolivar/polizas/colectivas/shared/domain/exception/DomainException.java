package com.segurosbolivar.polizas.colectivas.shared.domain.exception;

/** Excepción base del dominio. La capa web la traduce a códigos HTTP. */
public class DomainException extends RuntimeException {

    private final ErrorCode codigo;

    public DomainException(ErrorCode codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public DomainException(ErrorCode codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public ErrorCode getCodigo() {
        return codigo;
    }
}
