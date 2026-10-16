package com.segurosbolivar.polizas.colectivas.domain.model;

import java.math.BigDecimal;

public record CambiosPoliza(Boolean renovacionAutomatica, BigDecimal canonMensual, String emailTomador,
                            String telefonoTomador) {
}
