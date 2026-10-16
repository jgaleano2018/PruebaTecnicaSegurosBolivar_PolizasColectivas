package com.segurosbolivar.polizas.colectivas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.segurosbolivar.polizas.colectivas.application.port.out.CorePolizasPort;
import com.segurosbolivar.polizas.colectivas.domain.model.Operacion;
import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.shared.domain.exception.RecursoNoEncontradoException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PolizaServiceTest {

    private final CorePolizasPort core = mock(CorePolizasPort.class);
    private final PolizaService service = new PolizaService(core);

    static Poliza poliza(String tipo) {
        return new Poliza(1L, "X-1", tipo, "ACTIVA", LocalDate.now(), LocalDate.now().plusMonths(12), 12,
                BigDecimal.TEN, BigDecimal.valueOf(120), null, true, null, null, List.of(), 0L);
    }

    @Test
    void noPermiteOperarPolizasDeOtroTipo() {
        when(core.obtener(1L)).thenReturn(Mono.just(poliza("INDIVIDUAL")));
        StepVerifier.create(service.renovar(1L, BigDecimal.ONE, null))
                .expectError(RecursoNoEncontradoException.class)
                .verify();
        verify(core, never()).renovar(any(), any(), any());
    }

    @Test
    void generaIdempotencyKeyCuandoNoSeEnvia() {
        Poliza p = poliza("COLECTIVA");
        ResultadoOperacion resultado = new ResultadoOperacion(
                new Operacion("op-1", "RENOVACION", "PENDIENTE_SINCRONIZACION", 1L, null, null), p, false, "ok");
        when(core.obtener(1L)).thenReturn(Mono.just(p));
        when(core.renovar(eq(1L), any(), anyString())).thenReturn(Mono.just(resultado));

        StepVerifier.create(service.renovar(1L, null, null)).expectNext(resultado).verifyComplete();

        ArgumentCaptor<String> clave = ArgumentCaptor.forClass(String.class);
        verify(core).renovar(eq(1L), any(), clave.capture());
        assertThat(clave.getValue()).isNotBlank();
    }

    @Test
    void cancelarRiesgoQueNoPerteneceALaPolizaRetorna404() {
        when(core.obtener(1L)).thenReturn(Mono.just(poliza("COLECTIVA")));
        StepVerifier.create(service.cancelarRiesgo(1L, 999L, "x", null))
                .expectError(RecursoNoEncontradoException.class)
                .verify();
        verify(core, never()).cancelarRiesgo(any(), any(), any());
    }
}
