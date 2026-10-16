package com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto;

public record CoreApiResponse<T>(String tipo, String codigo, String mensaje, T data) {
}
