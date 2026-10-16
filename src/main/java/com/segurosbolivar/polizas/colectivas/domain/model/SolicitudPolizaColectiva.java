package com.segurosbolivar.polizas.colectivas.domain.model;

import java.time.LocalDate;
import java.util.List;

/** Póliza colectiva: tomador = inmobiliaria / administración de copropiedad; 1..N riesgos. */
public record SolicitudPolizaColectiva(LocalDate fechaInicio, int mesesVigencia, Actor tomador,
                                       List<NuevoRiesgo> riesgos, boolean renovacionAutomatica) {

    public SolicitudPoliza aSolicitudCore() {
        return new SolicitudPoliza(TiposPoliza.GESTIONADO, fechaInicio, mesesVigencia, tomador, riesgos,
                renovacionAutomatica);
    }
}
