package com.segurosbolivar.polizas.colectivas.infrastructure.in.messaging;

import com.segurosbolivar.polizas.colectivas.application.port.in.EventosPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.domain.model.EventoPoliza;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Consumidor de eventos de pólizas publicados por el CORE (consumer group propio de este microservicio). */
@Component
public class PolizaEventosKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(PolizaEventosKafkaListener.class);

    private final EventosPolizaUseCase eventos;
    private final ObjectMapper objectMapper;

    public PolizaEventosKafkaListener(EventosPolizaUseCase eventos, ObjectMapper objectMapper) {
        this.eventos = eventos;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(id = "${spring.application.name}-eventos", topics = "${app.kafka.topic-eventos}",
            groupId = "${app.kafka.grupo}", autoStartup = "${app.kafka.listeners-enabled}")
    public void onEvento(ConsumerRecord<String, String> registro) {
        try {
            EventoPoliza evento = objectMapper.readValue(registro.value(), EventoPoliza.class);
            log.info("Evento {} de póliza {} ({}) recibido", evento.tipoOperacion(), evento.polizaId(), evento.tipoPoliza());
            eventos.recibir(evento);
        } catch (Exception e) {
            log.warn("Evento inválido descartado (offset {}): {}", registro.offset(), e.getMessage());
        }
    }
}
