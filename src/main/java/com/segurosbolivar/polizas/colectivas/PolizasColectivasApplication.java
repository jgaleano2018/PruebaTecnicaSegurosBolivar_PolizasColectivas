package com.segurosbolivar.polizas.colectivas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** Microservicio de pólizas colectivas (arquitectura hexagonal). */
@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
@ConfigurationPropertiesScan
public class PolizasColectivasApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolizasColectivasApplication.class, args);
    }
}
