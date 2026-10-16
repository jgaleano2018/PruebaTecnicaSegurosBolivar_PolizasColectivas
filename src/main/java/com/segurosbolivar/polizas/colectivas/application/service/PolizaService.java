package com.segurosbolivar.polizas.colectivas.application.service;

import com.segurosbolivar.polizas.colectivas.application.port.in.CancelarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.ConsultarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.CrearPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.ModificarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.in.RenovarPolizaUseCase;
import com.segurosbolivar.polizas.colectivas.application.port.out.CorePolizasPort;
import com.segurosbolivar.polizas.colectivas.domain.model.CambiosPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.PolizaResumen;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.TiposPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.SolicitudPolizaColectiva;
import com.segurosbolivar.polizas.colectivas.shared.domain.exception.RecursoNoEncontradoException;
import com.segurosbolivar.polizas.colectivas.shared.domain.model.Pagina;
import java.math.BigDecimal;
import java.util.UUID;
import com.segurosbolivar.polizas.colectivas.application.port.in.GestionarRiesgosUseCase;
import com.segurosbolivar.polizas.colectivas.domain.model.NuevoRiesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.Riesgo;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Casos de uso del microservicio de pólizas COLECTIVA. Delega la persistencia al CORE (BD centralizada)
 * y garantiza que solo se operen pólizas de su tipo. Siempre envía una Idempotency-Key para que los
 * reintentos (backoff + jitter) nunca dupliquen operaciones.
 */
@Service
public class PolizaService implements CrearPolizaUseCase, ConsultarPolizaUseCase, ModificarPolizaUseCase,
        RenovarPolizaUseCase, CancelarPolizaUseCase, GestionarRiesgosUseCase {

    private final CorePolizasPort core;

    public PolizaService(CorePolizasPort core) {
        this.core = core;
    }

    @Override
    public Mono<ResultadoOperacion> crear(SolicitudPolizaColectiva solicitud, String idempotencyKey) {
        return core.crear(solicitud.aSolicitudCore(), clave(idempotencyKey));
    }

    @Override
    public Mono<Poliza> obtener(Long polizaId) {
        return core.obtener(polizaId)
                .filter(p -> p.esDeTipo(TiposPoliza.GESTIONADO))
                .switchIfEmpty(Mono.error(() -> new RecursoNoEncontradoException(
                        "La póliza %d no existe o no es de tipo %s".formatted(polizaId, TiposPoliza.GESTIONADO))));
    }

    @Override
    public Mono<Pagina<PolizaResumen>> listar(String estado, int pagina, int tamano) {
        return core.listar(TiposPoliza.GESTIONADO, estado, pagina, tamano);
    }

    @Override
    public Mono<ResultadoOperacion> modificar(Long polizaId, CambiosPoliza cambios, String idempotencyKey) {
        return obtener(polizaId).then(core.modificar(polizaId, cambios, clave(idempotencyKey)));
    }

    @Override
    public Mono<ResultadoOperacion> renovar(Long polizaId, BigDecimal ipcPorcentaje, String idempotencyKey) {
        return obtener(polizaId).then(core.renovar(polizaId, ipcPorcentaje, clave(idempotencyKey)));
    }

    @Override
    public Mono<ResultadoOperacion> cancelar(Long polizaId, String motivo, String idempotencyKey) {
        return obtener(polizaId).then(core.cancelar(polizaId, motivo, clave(idempotencyKey)));
    }

    @Override
    public Mono<List<Riesgo>> listarRiesgos(Long polizaId) {
        return obtener(polizaId).map(Poliza::riesgos);
    }

    @Override
    public Mono<ResultadoOperacion> agregarRiesgo(Long polizaId, NuevoRiesgo riesgo, String idempotencyKey) {
        return obtener(polizaId).then(core.agregarRiesgo(polizaId, riesgo, clave(idempotencyKey)));
    }

    @Override
    public Mono<ResultadoOperacion> cancelarRiesgo(Long polizaId, Long riesgoId, String motivo, String idempotencyKey) {
        return obtener(polizaId)
                .flatMap(p -> p.contieneRiesgo(riesgoId)
                        ? core.cancelarRiesgo(riesgoId, motivo, clave(idempotencyKey))
                        : Mono.<ResultadoOperacion>error(new RecursoNoEncontradoException(
                                "El riesgo %d no pertenece a la póliza %d".formatted(riesgoId, polizaId))));
    }

    private static String clave(String idempotencyKey) {
        return idempotencyKey == null || idempotencyKey.isBlank() ? UUID.randomUUID().toString() : idempotencyKey;
    }
}
