package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.PolizaResumen;
import com.segurosbolivar.polizas.colectivas.shared.domain.model.Pagina;
import reactor.core.publisher.Mono;

public interface ConsultarPolizaUseCase {

    Mono<Poliza> obtener(Long polizaId);

    Mono<Pagina<PolizaResumen>> listar(String estado, int pagina, int tamano);
}
