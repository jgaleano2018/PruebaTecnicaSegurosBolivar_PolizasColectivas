package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.CambiosPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import reactor.core.publisher.Mono;

public interface ModificarPolizaUseCase {

    Mono<ResultadoOperacion> modificar(Long polizaId, CambiosPoliza cambios, String idempotencyKey);
}
