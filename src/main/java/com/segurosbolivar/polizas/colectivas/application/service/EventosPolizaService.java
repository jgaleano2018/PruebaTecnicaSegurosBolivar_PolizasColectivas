package com.segurosbolivar.polizas.colectivas.application.service;

import com.segurosbolivar.polizas.colectivas.application.port.in.EventosPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.out.EventosTiempoRealPort;
import com.segurosbolivar.polizas.colectivas.domain.model.EventoPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.TiposPoliza;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/** Filtra los eventos de su tipo de póliza y los difunde en tiempo real a los suscriptores (front). */
@Service
public class EventosPolizaService implements EventosPolizaUseCase {

    private final EventosTiempoRealPort tiempoReal;

    public EventosPolizaService(EventosTiempoRealPort tiempoReal) {
        this.tiempoReal = tiempoReal;
    }

    @Override
    public void recibir(EventoPoliza evento) {
        if (TiposPoliza.GESTIONADO.equals(evento.tipoPoliza())) {
            tiempoReal.publicar(evento);
        }
    }

    @Override
    public Flux<EventoPoliza> suscribir() {
        return tiempoReal.flujo();
    }
}
