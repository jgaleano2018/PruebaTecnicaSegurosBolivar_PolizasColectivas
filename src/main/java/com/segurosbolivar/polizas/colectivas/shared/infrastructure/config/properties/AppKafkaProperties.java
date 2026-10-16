package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record AppKafkaProperties(String topicEventos, String grupo, boolean listenersEnabled) {
}
