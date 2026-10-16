package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public record RenovarPolizaRequest(
        @Schema(example = "5.20", description = "Opcional: si no se envía se aplica el IPC vigente del CORE")
        @DecimalMin("0.00") @DecimalMax("100.00") @Digits(integer = 3, fraction = 4) BigDecimal ipcPorcentaje) {
}
