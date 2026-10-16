package com.segurosbolivar.polizas.colectivas.infrastructure.out.realtime;

import com.segurosbolivar.polizas.colectivas.application.port.out.EventosTiempoRealPort;
import com.segurosbolivar.polizas.colectivas.domain.model.EventoPoliza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/** Difusión en memoria (multicast) para Server-Sent Events. Un suscriptor lento no bloquea a los demás. */
@Component
public class SinkEventosTiempoRealAdapter implements EventosTiempoRealPort {

    private static final Logger log = LoggerFactory.getLogger(SinkEventosTiempoRealAdapter.class);

    private final Sinks.Many<EventoPoliza> sink = Sinks.many().multicast().directBestEffort();

    @Override
    public void publicar(EventoPoliza evento) {
        Sinks.EmitResult resultado = sink.tryEmitNext(evento);
        if (resultado.isFailure() && resultado != Sinks.EmitResult.FAIL_ZERO_SUBSCRIBER) {
            log.debug("Evento {} no emitido: {}", evento.eventoId(), resultado);
        }
    }

    @Override
    public Flux<EventoPoliza> flujo() {
        return sink.asFlux();
    }
}
