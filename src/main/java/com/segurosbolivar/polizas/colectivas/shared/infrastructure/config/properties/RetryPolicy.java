package com.segurosbolivar.polizas.colectivas.shared.infrastructure.config.properties;

import java.time.Duration;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

/**
 * Política de reintentos parametrizable: backoff exponencial + jitter.
 * Reutilizada (DRY) para BD (deadlocks), CORE/WebLogic y llamadas entre servicios.
 */
public record RetryPolicy(int maxAttempts, Duration initialBackoff, Duration maxBackoff, double jitter) {

    private static final Logger LOG = LoggerFactory.getLogger(RetryPolicy.class);

    public RetryBackoffSpec toSpec(Predicate<? super Throwable> esReintentable, String operacion) {
        return Retry.backoff(maxAttempts, initialBackoff)
                .maxBackoff(maxBackoff)
                .jitter(jitter)
                .filter(esReintentable)
                .doBeforeRetry(senal -> LOG.warn("Reintento {}/{} de '{}' por {}: {}",
                        senal.totalRetries() + 1, maxAttempts, operacion,
                        senal.failure().getClass().getSimpleName(), senal.failure().getMessage()))
                .onRetryExhaustedThrow((spec, senal) -> senal.failure());
    }
}
