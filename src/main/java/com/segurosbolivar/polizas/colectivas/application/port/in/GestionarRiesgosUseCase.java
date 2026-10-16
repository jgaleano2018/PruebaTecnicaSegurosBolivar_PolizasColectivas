package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.NuevoRiesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.Riesgo;
import java.util.List;
import reactor.core.publisher.Mono;

/** Para colectivas: agregar riesgos y eliminar (cancelar) riesgos. */
public interface GestionarRiesgosUseCase {

    Mono<List<Riesgo>> listarRiesgos(Long polizaId);

    Mono<ResultadoOperacion> agregarRiesgo(Long polizaId, NuevoRiesgo riesgo, String idempotencyKey);

    Mono<ResultadoOperacion> cancelarRiesgo(Long polizaId, Long riesgoId, String motivo, String idempotencyKey);
}
