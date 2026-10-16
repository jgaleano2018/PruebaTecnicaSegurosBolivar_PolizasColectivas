package com.segurosbolivar.polizas.colectivas.infrastructure.in.web.mapper;

import com.segurosbolivar.polizas.colectivas.domain.model.Actor;
import com.segurosbolivar.polizas.colectivas.domain.model.CambiosPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.EventoPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.NuevoRiesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.PolizaResumen;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.Riesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.SolicitudPolizaColectiva;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.ActorRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.CrearPolizaColectivaRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.ModificarPolizaRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.request.RiesgoRequest;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.ActorResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.EventoPolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.OperacionPolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.OperacionResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.PaginaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.PolizaResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.PolizaResumenResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.in.web.dto.response.Responses.RiesgoResponse;
import com.segurosbolivar.polizas.colectivas.shared.domain.model.Pagina;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PolizaWebMapper {

    public SolicitudPolizaColectiva aSolicitud(CrearPolizaColectivaRequest r) {
        return new SolicitudPolizaColectiva(r.fechaInicio(), r.mesesVigencia(), aDominio(r.tomador()),
                r.riesgos().stream().map(this::aDominio).toList(), Boolean.TRUE.equals(r.renovacionAutomatica()));
    }

    public NuevoRiesgo aDominio(RiesgoRequest r) {
        return new NuevoRiesgo(r.direccion().trim(), r.ciudad().trim(), r.caracteristicas(), r.canonMensual(),
                aDominio(r.arrendatario()), aDominio(r.arrendador()));
    }

    public Actor aDominio(ActorRequest r) {
        return new Actor(null, r.tipoDocumento(), r.numeroDocumento().trim(), r.nombre().trim(), r.email(),
                r.telefono(), r.tipoPersona());
    }

    public CambiosPoliza aDominio(ModificarPolizaRequest r) {
        return new CambiosPoliza(r.renovacionAutomatica(), null, r.emailTomador(), r.telefonoTomador());
    }

    public PolizaResponse aResponse(Poliza p) {
        return new PolizaResponse(p.id(), p.numeroPoliza(), p.tipo(), p.estado(), p.fechaInicio(), p.fechaFin(),
                p.mesesVigencia(), p.canonMensual(), p.valorPrima(), p.ipcPorcentaje(), p.renovacionAutomatica(),
                aResponse(p.tomador()), p.motivoCancelacion(), aRiesgos(p.riesgos()));
    }

    public List<RiesgoResponse> aRiesgos(List<Riesgo> riesgos) {
        return riesgos.stream().map(r -> new RiesgoResponse(r.id(), r.direccion(), r.ciudad(), r.caracteristicas(),
                r.canonMensual(), r.estado(), aResponse(r.asegurado()), aResponse(r.beneficiario()),
                r.motivoCancelacion())).toList();
    }

    public ActorResponse aResponse(Actor a) {
        return a == null ? null : new ActorResponse(a.tipoDocumento(), a.numeroDocumento(), a.nombre(), a.email(),
                a.telefono(), a.tipoPersona());
    }

    public PaginaResponse<PolizaResumenResponse> aResponse(Pagina<PolizaResumen> p) {
        List<PolizaResumenResponse> contenido = p.contenido().stream()
                .map(r -> new PolizaResumenResponse(r.id(), r.numeroPoliza(), r.estado(), r.fechaInicio(), r.fechaFin(),
                        r.canonMensual(), r.valorPrima(), r.renovacionAutomatica(), aResponse(r.tomador()),
                        r.riesgosActivos()))
                .toList();
        return new PaginaResponse<>(contenido, p.pagina(), p.tamano(), p.totalElementos(), p.totalPaginas());
    }

    public OperacionPolizaResponse aResponse(ResultadoOperacion r) {
        return new OperacionPolizaResponse(new OperacionResponse(r.operacion().operacionId(), r.operacion().tipo(),
                r.operacion().estado(), r.operacion().riesgoId()), aResponse(r.poliza()));
    }

    public EventoPolizaResponse aResponse(EventoPoliza e) {
        return new EventoPolizaResponse(e.tipoOperacion(), e.polizaId(), e.numeroPoliza(), e.riesgoId(),
                e.estadoPoliza(), e.canonMensual(), e.valorPrima(), e.fecha());
    }
}
