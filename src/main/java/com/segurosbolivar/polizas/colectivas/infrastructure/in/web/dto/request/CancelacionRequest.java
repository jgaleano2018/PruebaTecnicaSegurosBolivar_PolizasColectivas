package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.Size;

public record CancelacionRequest(@Size(max = 250, message = "El motivo admite máximo 250 caracteres") String motivo) {
}
