package com.segurosbolivar.polizas.colectivas.domain.model;

public record ResultadoOperacion(Operacion operacion, Poliza poliza, boolean repetida, String mensaje) {
}
