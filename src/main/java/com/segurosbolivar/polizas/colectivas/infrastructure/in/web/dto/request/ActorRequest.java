package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActorRequest(
        @Schema(example = "CC")
        @NotBlank(message = "El tipo de documento es obligatorio")
        @Pattern(regexp = "CC|CE|NIT|PA|TI", message = "Tipo de documento inválido (CC, CE, NIT, PA, TI)")
        String tipoDocumento,

        @Schema(example = "1037654321")
        @NotBlank(message = "El número de documento es obligatorio")
        @Pattern(regexp = "^[0-9A-Za-z-]{4,20}$", message = "Número de documento inválido")
        String numeroDocumento,

        @Schema(example = "Laura Gómez")
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String nombre,

        @Schema(example = "laura.gomez@correo.com")
        @Email(message = "Correo electrónico inválido") @Size(max = 150) String email,

        @Schema(example = "+573001234567")
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Teléfono inválido") String telefono,

        @Schema(example = "NATURAL", allowableValues = {"NATURAL", "JURIDICA"})
        @NotBlank(message = "El tipo de persona es obligatorio")
        @Pattern(regexp = "NATURAL|JURIDICA", message = "Tipo de persona inválido") String tipoPersona) {
}
