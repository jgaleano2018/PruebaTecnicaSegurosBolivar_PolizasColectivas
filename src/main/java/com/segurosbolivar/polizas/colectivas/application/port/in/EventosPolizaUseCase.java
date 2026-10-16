package com.segurosbolivar.polizas.colectivas.application.port.in;

import com.segurosbolivar.polizas.colectivas.domain.model.EventoPoliza;
import reactor.core.publisher.Flux;

/** Eventos de pólizas en tiempo real (Kafka -> Server-Sent Events). */
public interface EventosPolizaUseCase {

    void recibir(EventoPoliza evento);

    Flux<EventoPoliza> suscribir();
}
