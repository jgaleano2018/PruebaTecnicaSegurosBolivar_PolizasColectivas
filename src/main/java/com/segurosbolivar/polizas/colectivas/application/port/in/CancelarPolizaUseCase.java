package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import reactor.core.publisher.Mono;

public interface CancelarPolizaUseCase {

    Mono<ResultadoOperacion> cancelar(Long polizaId, String motivo, String idempotencyKey);
}
