package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Inmueble arrendado, arrendatario (asegurado) y arrendador (beneficiario)")
public record RiesgoRequest(
        @Schema(example = "Calle 10 # 43-20 Apto 501")
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 200) String direccion,
        @Schema(example = "Medellín")
        @NotBlank(message = "La ciudad es obligatoria") @Size(max = 80) String ciudad,
        @Size(max = 500) String caracteristicas,
        @Schema(example = "2500000")
        @NotNull(message = "El canon mensual es obligatorio")
        @DecimalMin(value = "0.01", message = "El canon mensual debe ser mayor a cero")
        @Digits(integer = 16, fraction = 2) BigDecimal canonMensual,
        @NotNull(message = "El arrendatario es obligatorio") @Valid ActorRequest arrendatario,
        @NotNull(message = "El arrendador es obligatorio") @Valid ActorRequest arrendador) {
}
