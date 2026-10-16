package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface RenovarPolizaUseCase {

    Mono<ResultadoOperacion> renovar(Long polizaId, BigDecimal ipcPorcentaje, String idempotencyKey);
}
