package com.segurosbolivar.polizas.colectivas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record Poliza(Long id, String numeroPoliza, String tipo, String estado, LocalDate fechaInicio,
                     LocalDate fechaFin, int mesesVigencia, BigDecimal canonMensual, BigDecimal valorPrima,
                     BigDecimal ipcPorcentaje, boolean renovacionAutomatica, Actor tomador, String motivoCancelacion,
                     List<Riesgo> riesgos, Long version) {

    public Poliza {
        riesgos = riesgos == null ? List.of() : List.copyOf(riesgos);
    }

    public boolean esDeTipo(String tipoEsperado) {
        return Objects.equals(tipo, tipoEsperado);
    }

    public boolean contieneRiesgo(Long riesgoId) {
        return riesgos.stream().anyMatch(r -> Objects.equals(r.id(), riesgoId));
    }
}
