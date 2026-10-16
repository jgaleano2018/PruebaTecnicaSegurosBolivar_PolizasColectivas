package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Póliza colectiva: tomador = inmobiliaria o administración de copropiedad; 1..N riesgos")
public record CrearPolizaColectivaRequest(
        @Schema(example = "2026-11-01") @NotNull(message = "La fecha de inicio es obligatoria") LocalDate fechaInicio,
        @Schema(example = "12") @NotNull(message = "Los meses de vigencia son obligatorios")
        @Min(1) @Max(60) Integer mesesVigencia,
        @NotNull(message = "El tomador (inmobiliaria/administración) es obligatorio") @Valid ActorRequest tomador,
        @NotEmpty(message = "Debe enviar al menos un riesgo") @Size(max = 500)
        List<@Valid @NotNull RiesgoRequest> riesgos,
        Boolean renovacionAutomatica) {
}
