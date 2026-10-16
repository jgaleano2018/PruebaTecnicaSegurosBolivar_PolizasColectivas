package com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Respuesta estándar de error (manejo centralizado)")
public record ErrorResponse(
        TipoMensaje tipo,
        int status,
        String codigo,
        String mensaje,
        String path,
        String correlationId,
        List<CampoError> errores,
        Instant timestamp) {

    public record CampoError(String campo, String mensaje) {
    }
}
