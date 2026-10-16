package com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto;

public record CoreErrorResponse(String tipo, int status, String codigo, String mensaje) {
}
