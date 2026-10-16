package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Reloj inyectable (facilita pruebas de reglas basadas en fechas). */
@Configuration
public class ClockConfig {

    @Bean
    Clock clock(@Value("${app.zona-horaria:America/Bogota}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }
}
