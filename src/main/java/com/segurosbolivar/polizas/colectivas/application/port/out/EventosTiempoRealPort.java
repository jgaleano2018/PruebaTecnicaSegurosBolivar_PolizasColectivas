package com.segurosbolivar.polizas.colectivas.application.port.out;

import com.segurosbolivar.polizas.colectivas.domain.model.EventoPoliza;
import reactor.core.publisher.Flux;

public interface EventosTiempoRealPort {

    void publicar(EventoPoliza evento);

    Flux<EventoPoliza> flujo();
}
