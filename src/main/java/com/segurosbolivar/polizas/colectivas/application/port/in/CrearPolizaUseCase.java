package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.SolicitudPolizaColectiva;
import reactor.core.publisher.Mono;

public interface CrearPolizaUseCase {

    Mono<ResultadoOperacion> crear(SolicitudPolizaColectiva solicitud, String idempotencyKey);
}
