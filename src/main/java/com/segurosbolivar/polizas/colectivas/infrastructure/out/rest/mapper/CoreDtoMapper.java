package com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.mapper;

import com.segurosbolivar.polizas.colectivas.domain.model.Actor;
import com.segurosbolivar.polizas.colectivas.domain.model.CambiosPoliza;
import com.segurosbolivar.polizas.colectivas.domain.model.NuevoRiesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.Operacion;
import com.segurosbolivar.polizas.colectivas.domain.model.Poliza;
import com.segurosbolivar.polizas.colectivas.domain.model.PolizaResumen;
import com.segurosbolivar.polizas.colectivas.domain.model.ResultadoOperacion;
import com.segurosbolivar.polizas.colectivas.domain.model.Riesgo;
import com.segurosbolivar.polizas.colectivas.domain.model.SolicitudPoliza;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreApiResponse;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.ActorDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.CrearPolizaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.ModificarPolizaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.OperacionPolizaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.PaginaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.PolizaDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.PolizaResumenDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.RiesgoDto;
import com.segurosbolivar.polizas.colectivas.infrastructure.out.rest.dto.CoreDtos.RiesgoNuevoDto;
import com.segurosbolivar.polizas.colectivas.shared.domain.model.Pagina;
import org.springframework.stereotype.Component;

@Component
public class CoreDtoMapper {

    public CrearPolizaDto aDto(SolicitudPoliza s) {
        return new CrearPolizaDto(s.tipo(), s.fechaInicio(), s.mesesVigencia(), aDto(s.tomador()),
                s.riesgos().stream().map(this::aDto).toList(), s.renovacionAutomatica());
    }

    public RiesgoNuevoDto aDto(NuevoRiesgo r) {
        return new RiesgoNuevoDto(r.direccion(), r.ciudad(), r.caracteristicas(), r.canonMensual(),
                aDto(r.arrendatario()), aDto(r.arrendador()));
    }

    public ModificarPolizaDto aDto(CambiosPoliza c) {
        return new ModificarPolizaDto(c.renovacionAutomatica(), c.canonMensual(), c.emailTomador(), c.telefonoTomador());
    }

    public ActorDto aDto(Actor a) {
        return a == null ? null : new ActorDto(null, a.tipoDocumento(), a.numeroDocumento(), a.nombre(), a.email(),
                a.telefono(), a.tipoPersona());
    }

    public Actor aDominio(ActorDto a) {
        return a == null ? null : new Actor(a.id(), a.tipoDocumento(), a.numeroDocumento(), a.nombre(), a.email(),
                a.telefono(), a.tipoPersona());
    }

    public Riesgo aDominio(RiesgoDto r) {
        return new Riesgo(r.id(), r.codigo(), r.direccion(), r.ciudad(), r.caracteristicas(), r.canonMensual(),
                r.estado(), aDominio(r.asegurado()), aDominio(r.beneficiario()), r.motivoCancelacion(),
                r.fechaCancelacion());
    }

    public Poliza aDominio(PolizaDto p) {
        return new Poliza(p.id(), p.numeroPoliza(), p.tipo(), p.estado(), p.fechaInicio(), p.fechaFin(),
                p.mesesVigencia(), p.canonMensual(), p.valorPrima(), p.ipcPorcentaje(), p.renovacionAutomatica(),
                aDominio(p.tomador()), p.motivoCancelacion(),
                p.riesgos() == null ? null : p.riesgos().stream().map(this::aDominio).toList(), p.version());
    }

    public PolizaResumen aDominio(PolizaResumenDto p) {
        return new PolizaResumen(p.id(), p.numeroPoliza(), p.tipo(), p.estado(), p.fechaInicio(), p.fechaFin(),
                p.canonMensual(), p.valorPrima(), p.renovacionAutomatica(), aDominio(p.tomador()), p.riesgosActivos());
    }

    public Pagina<PolizaResumen> aDominio(PaginaDto<PolizaResumenDto> p) {
        return new Pagina<>(p.contenido().stream().map(this::aDominio).toList(), p.pagina(), p.tamano(),
                p.totalElementos(), p.totalPaginas());
    }

    public ResultadoOperacion aDominio(CoreApiResponse<OperacionPolizaDto> r) {
        OperacionPolizaDto d = r.data();
        Operacion op = new Operacion(d.operacion().operacionId(), d.operacion().tipo(), d.operacion().estado(),
                d.operacion().polizaId(), d.operacion().riesgoId(), d.operacion().detalle());
        return new ResultadoOperacion(op, aDominio(d.poliza()), "SOLICITUD_DUPLICADA".equals(r.codigo()), r.mensaje());
    }
}
