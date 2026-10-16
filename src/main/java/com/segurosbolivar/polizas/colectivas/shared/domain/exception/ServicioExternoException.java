package com.segurosbolivar.polizas.colectivas.shared.domain.exception;

/** Error devuelto por un servicio externo (CORE). Conserva el status HTTP original. */
public class ServicioExternoException extends DomainException {

    private final int status;

    public ServicioExternoException(int status, ErrorCode codigo, String mensaje) {
        super(codigo, mensaje);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
