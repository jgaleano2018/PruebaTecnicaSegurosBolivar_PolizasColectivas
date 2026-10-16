package com.segurosbolivar.polizas.colectivas.application.port.out;

import com.segurosbolivar.polizas.colectivas.domain.model.CambiosPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.NuevoRiesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.PolizaResumen;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.SolicitudPoliza;
import com.segurosbolivar.polizas.colectivas.shared.domain.model.Pagina;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

/** Puerto de salida hacia el CORE (monolito modular que centraliza la base de datos). */
public interface CorePolizasPort {

    Mono<ResultadoOperacion> crear(SolicitudPoliza solicitud, String idempotencyKey);

    Mono<Poliza> obtener(Long polizaId);

    Mono<Pagina<PolizaResumen>> listar(String tipo, String estado, int pagina, int tamano);

    Mono<ResultadoOperacion> modificar(Long polizaId, CambiosPoliza cambios, String idempotencyKey);

    Mono<ResultadoOperacion> renovar(Long polizaId, BigDecimal ipcPorcentaje, String idempotencyKey);

    Mono<ResultadoOperacion> cancelar(Long polizaId, String motivo, String idempotencyKey);

    Mono<ResultadoOperacion> agregarRiesgo(Long polizaId, NuevoRiesgo riesgo, String idempotencyKey);

    Mono<ResultadoOperacion> cancelarRiesgo(Long riesgoId, String motivo, String idempotencyKey);
}
