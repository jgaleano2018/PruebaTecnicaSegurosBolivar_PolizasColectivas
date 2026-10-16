package com.segurosbolivar.polizas.colectivas.domain.model;

public record Actor(Long id, String tipoDocumento, String numeroDocumento, String nombre, String email,
                    String telefono, String tipoPersona) {
}
