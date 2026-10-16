package com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Envoltorio estándar de respuestas exitosas (confirmación o alerta)")
public record ApiResponse<T>(TipoMensaje tipo, String codigo, String mensaje, T data, Instant timestamp) {

    public static <T> ApiResponse<T> confirmacion(String codigo, String mensaje, T data) {
        return new ApiResponse<>(TipoMensaje.CONFIRMACION, codigo, mensaje, data, Instant.now());
    }

    public static <T> ApiResponse<T> alerta(String codigo, String mensaje, T data) {
        return new ApiResponse<>(TipoMensaje.ALERTA, codigo, mensaje, data, Instant.now());
    }
}
