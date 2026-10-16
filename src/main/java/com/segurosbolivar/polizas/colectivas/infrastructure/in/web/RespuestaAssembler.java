package com.segurosbolivar.polizas.colectivas.infrastructure.in.web;

import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.OperacionPolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.mapper.PolizaWebMapper;
import com.segurosbolivar.polizas.colectivas.shared.infrastructure.in.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/** Respuestas de comandos: CONFIRMACION (cambio aplicado; CORE pendiente/confirmado) o ALERTA (duplicado). */
@Component
public class RespuestaAssembler {

    private final PolizaWebMapper mapper;

    public RespuestaAssembler(PolizaWebMapper mapper) {
        this.mapper = mapper;
    }

    public ResponseEntity<ApiResponse<OperacionPolizaResponse>> construir(ResultadoOperacion r, HttpStatus status,
                                                                        String mensaje) {
        OperacionPolizaResponse cuerpo = mapper.aResponse(r);
        if (r.repetida()) {
            return ResponseEntity.ok(ApiResponse.alerta("SOLICITUD_DUPLICADA", r.mensaje(), cuerpo));
        }
        return ResponseEntity.status(status).body(ApiResponse.confirmacion("OPERACION_REGISTRADA",
                mensaje + ". Sincronización con el CORE: " + r.operacion().estado(), cuerpo));
    }
}
