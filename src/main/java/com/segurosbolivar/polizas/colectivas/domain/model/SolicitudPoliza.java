package com.segurosbolivar.polizas.colectivas.domain.model;

import java.time.LocalDate;
import java.util.List;

/** Solicitud de creación enviada al CORE. */
public record SolicitudPoliza(String tipo, LocalDate fechaInicio, int mesesVigencia, Actor tomador,
                              List<NuevoRiesgo> riesgos, boolean renovacionAutomatica) {
}
