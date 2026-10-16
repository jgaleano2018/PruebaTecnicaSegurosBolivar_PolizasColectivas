package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record ModificarPolizaRequest(
        Boolean renovacionAutomatica,
        @Email(message = "Correo electrónico inválido") String emailTomador,
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Teléfono inválido") String telefonoTomador) {

    @JsonIgnore
    @AssertTrue(message = "Debe informar al menos un campo a modificar")
    public boolean isAlgunCampoInformado() {
        return renovacionAutomatica != null || emailTomador != null || telefonoTomador != null;
    }
}
