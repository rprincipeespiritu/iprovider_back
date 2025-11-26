package com.incloud.hcp.rest;


import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.domain.ModificacionSolped;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.RubroBien;
import com.incloud.hcp.dto.ModificacionSolpedDto;
import com.incloud.hcp.jco.centro.dto.CentroRFCResponseDto;
import com.incloud.hcp.jco.centro.service.JCOCentroServiceNew;
import com.incloud.hcp.jco.centroAlmacen.dto.CentroAlmacenRFCResponseDto;
import com.incloud.hcp.jco.centroAlmacen.service.JCOCentroAlmacenService;
import com.incloud.hcp.jco.centroAlmacen.service.JCOCentroAlmacenServiceNew;
import com.incloud.hcp.jco.consultaProveedor.dto.ConsultaProveedorRFCResponseDto;
import com.incloud.hcp.jco.consultaProveedor.service.JCOConsultaProveedorService;
import com.incloud.hcp.jco.documentoAceptacion.service.JCODocumentoAceptacionService;
import com.incloud.hcp.jco.grupoArticulo.dto.GrupoArticuloRFCResponseDto;
import com.incloud.hcp.jco.grupoArticulo.service.JCOGrupoArticuloService;
import com.incloud.hcp.jco.grupoArticulo.service.JCOGrupoArticuloServiceNew;
import com.incloud.hcp.jco.materiales.dto.MaterialesRFCResponseDto;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesService;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesServiceNew;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPublicacionService;
import com.incloud.hcp.jco.proveedor.dto.ProveedorHomologacionRFCResponseDto;
import com.incloud.hcp.jco.proveedor.dto.ProveedorRFCResponseDto;
import com.incloud.hcp.jco.proveedor.service.JCOProveedorService;
import com.incloud.hcp.jco.servicios.dto.ServiciosRFCResponseDto;
import com.incloud.hcp.jco.servicios.service.JCOServiciosService;
import com.incloud.hcp.jco.servicios.service.JCOServiciosServiceNew;
import com.incloud.hcp.jco.peticionOferta.dto.PeticionOfertaRFCResponseDto;
import com.incloud.hcp.jco.peticionOferta.service.JCOPeticionOfertaService;
import com.incloud.hcp.jco.solped.dto.SolicitudPedidoRFCResponseDto;
import com.incloud.hcp.jco.solped.service.JCOSolicitudPedidoService;
import com.incloud.hcp.jco.tipoCambio.dto.TipoCambioRFCResponseDto;
import com.incloud.hcp.jco.tipoCambio.service.JCOTipoCambioService;
import com.incloud.hcp.jco.ubigeo.dto.UbigeoRFCResponseDto;
import com.incloud.hcp.jco.ubigeo.service.JCOUbigeoService;
import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCResponseDto;
import com.incloud.hcp.jco.unidadMedida.service.JCOUnidadMedidaServiceNew;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.LicitacionDetalleRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.ModificacionSolpedRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.notificacion.ModificarSolpedNotificacion;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/_jcoRest")
public class _JcoRest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JCOPeticionOfertaService jcoPeticionOfertaService;

    @Autowired
    private JCOGrupoArticuloServiceNew jcoGrupoArticuloServiceNew;

    @Autowired
    private JCOGrupoArticuloService jcoGrupoArticuloService;

    @Autowired
    private JCOUbigeoService jcoUbigeoService;

    @Autowired
    private JCOServiciosServiceNew jcoServiciosServiceNew;

    @Autowired
    private JCOConsultaProveedorService jcoConsultaProveedorService;

    @Autowired
    private JCOMaterialesServiceNew jcoMaterialesServiceNew;

    @Autowired
    private JCOMaterialesService jcoMaterialesService;

    @Autowired
    private JCOServiciosService jcoServiciosService;

    @Autowired
    private JCOTipoCambioService jcoTipoCambioService;

    @Autowired
    private JCOCentroServiceNew jcoCentroServiceNew;

    @Autowired
    private JCOUnidadMedidaServiceNew jcoUnidadMedidaServiceNew;

    @Autowired
    private JCOCentroAlmacenService jcoCentroAlmacenService;

    @Autowired
    private JCOCentroAlmacenServiceNew jcoCentroAlmacenServiceNew;

    @Autowired
    private JCOProveedorService jcoProveedorService;
    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Autowired
    private JCOSolicitudPedidoService jcoSolicitudPedidoService;

    @Autowired
    private ModificacionSolpedRepository modificacionSolpedRepository;

    @Autowired
    private ModificarSolpedNotificacion modificarSolpedNotificacion;

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private LicitacionDetalleRepository licitacionDetalleRepository;

    /* Peticion de Oferta */
//    @ApiOperation(value = "Busca Peticion de Oferta en base al Numero de Solicitud o Numero de Licitacion SAP", produces = "application/json")
//    @GetMapping(value = "/getSolpedByCodigo/{solped}", produces = APPLICATION_JSON_VALUE)
//    public ResponseEntity<PeticionOfertaRFCResponseDto> getSolpedByCodigo(@PathVariable String solped) throws URISyntaxException {
//        log.debug("Find by id getPeticionOfertaByCodigo : {}", solped);
//        try {
//            return Optional.ofNullable(this.jcoPeticionOfertaService.getPeticionOfertaResponseByCodigo(solped, false))
//                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//        } catch (Exception e) {
//            String error = Utils.obtieneMensajeErrorException(e);
//            throw new RuntimeException(error);
//        }
//    }

    /* Solicitud de Pedido */
    @ApiOperation(value = "Busca Solicitud de Pedido en base al Numero de Solicitud", produces = "application/json")
    @GetMapping(value = "/getSolpedByCodigo/{solped}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitudPedidoRFCResponseDto> getSolpedByCodigo(@PathVariable String solped) throws URISyntaxException {
        log.debug("Find by id getSolpedByCodigo : {}", solped);
        try {

            SolicitudPedidoRFCResponseDto solicitudPedidoRFCResponseDto = this.jcoSolicitudPedidoService.getSolpedResponseByCodigo(solped);
            List<SolicitudPedido> pedidoList = solicitudPedidoRFCResponseDto.getListaSolped();
            List<SolicitudPedido> solicitudPedidoList = new ArrayList<>();

            for (SolicitudPedido bean: pedidoList) {
                if(bean.getEstadoSolped().equals("X"))
                {
                    solicitudPedidoList.add(bean);
                }
            }

            solicitudPedidoRFCResponseDto.setListaSolped(solicitudPedidoList);

            return Optional.ofNullable(solicitudPedidoRFCResponseDto)
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
    }

    /* Solicitud de Pedido */
    @ApiOperation(value = "Busca Solicitud de Pedido en base al Numero de Solicitud", produces = "application/json")
    @GetMapping(value = "/getSolpedByCodigoAdjudicar/{solped}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitudPedidoRFCResponseDto> getSolpedByCodigoAdjudicar(@PathVariable String solped) throws URISyntaxException {
        log.debug("Find by id getSolpedByCodigo : {}", solped);
        try {

            SolicitudPedidoRFCResponseDto solicitudPedidoRFCResponseDto = this.jcoSolicitudPedidoService.getSolpedResponseByCodigo(solped);
            List<SolicitudPedido> pedidoList = solicitudPedidoRFCResponseDto.getListaSolped();
            List<SolicitudPedido> solicitudPedidoList = new ArrayList<>();

            for (SolicitudPedido bean: pedidoList) {
                    solicitudPedidoList.add(bean);
            }

            solicitudPedidoRFCResponseDto.setListaSolped(solicitudPedidoList);

            return Optional.ofNullable(solicitudPedidoRFCResponseDto)
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    /* Solicitud de Pedido */

    @RequestMapping(value = "/getSolpedUpdate/{codigoSolicitud}/{solicitudPedido}/{precioOc}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> actualizarSolped(@PathVariable("codigoSolicitud") Integer codigoSolicitud,
                                              @PathVariable("solicitudPedido") String solicitudPedido,
                                              @PathVariable("precioOc") BigDecimal precioOc,
                                              @PathVariable("cantidad") BigDecimal cantidad) throws Exception {

        try {
            return Optional.ofNullable(this.jcoSolicitudPedidoService.modificarSolped(codigoSolicitud, solicitudPedido,cantidad, precioOc))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @RequestMapping(value = "/modicarSolped/{email}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> modificacionSolped(@RequestBody ModificacionSolped modificacionSolped,
                                                @PathVariable("email") String email) throws Exception {

        try {
            String respuesta = this.jcoSolicitudPedidoService.modificarSolped(modificacionSolped.getNroSolped(),
                    modificacionSolped.getPosicionSolped(), modificacionSolped.getCantidadModificada(),modificacionSolped.getPrecioUnitario());
            if (respuesta == "Solped actualizada") {
                this.modificacionSolpedRepository.save(modificacionSolped);

                boolean adjudicacionesProveedor = this.licitacionDetalleRepository
                        .countAdjudicacionDeLicitacionPorProveedor(
                                modificacionSolped.getLicitacionDetalle().getLicitacion().getIdLicitacion(),
                                modificacionSolped.getLicitacionDetalle().getIdProveedor()
                        ) > 0? false : true;

                if(adjudicacionesProveedor) {
                    String respuesta1 = "";
                    respuesta1 = this.modificarSolpedNotificacion.enviar(parametroMapper.getMailSetting(), modificacionSolped, email);
                    LogTransaccion logTransaccion = new LogTransaccion();
                    logTransaccion.setEnvioTrama("modificarSolpedNotificacion");
                    logTransaccion.setRespuestaCodigo(respuesta1);
                    logTransaccion.setTipoRegistro("Correo modificarSolpedNotificacion");
                    this.logTransaccionRepository.save(logTransaccion);
                }
            }
            return Optional.ofNullable(respuesta)
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }




    /* Grupo Articulo */
    @ApiOperation(value = "Lista Grupo Articulos by Codigo", produces = "application/json")
    @GetMapping(value = "/getGrupoArticuloByCodigo/{codigo}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<GrupoArticuloRFCResponseDto> getGrupoByCodigo(@PathVariable String codigo) throws URISyntaxException {
        log.debug("Find by id getSolpedByCodigo : {}", codigo);
        try {
            return Optional.ofNullable(this.jcoGrupoArticuloServiceNew.getGrupoArticulo(codigo))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }



    @ApiOperation(value = "Lista y Guarda Grupo Articulos nuevos o actualizados", produces = "application/json")
    @PostMapping(value = "/actualizarGrupoArticulo/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <List<RubroBien>> actualizarGrupoArticulo() throws URISyntaxException {
        log.debug("Lista Grupo Articulos : {}");
        try {
            return Optional.ofNullable(this.jcoGrupoArticuloService.actualizarGrupoArticulo())
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    /* Tasa de Cambio */
    @ApiOperation(value = "Graba Tasa Cambio del RFC de acuerdo a la Fecha Actual", produces = "application/json")
    @GetMapping(value = "/actualizarTipoCambioFechaActual/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <TipoCambioRFCResponseDto> actualizarTipoCambioFechaActual() throws URISyntaxException {
        log.debug("Lista actualizarTipoCambioFechaActual : {}");
        Date fechaActual = DateUtils.obtenerFechaActual();
        //String sFecha = DateUtils.convertDateToString("yyyyMMdd",fechaActual);
        String sFecha = DateUtils.convertDateToString("dd.MM.yyyy",fechaActual);
        try {
            return Optional.ofNullable(this.jcoTipoCambioService.actualizarTipoCambio(sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    /* Materiales */
    @ApiOperation(value = "Lista Materiales del RFC", produces = "application/json")
    @GetMapping(value = "/getListMaterialesByRFC/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <MaterialesRFCResponseDto> getListaMaterialesByRFC(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) throws URISyntaxException {
        try {


            return Optional.ofNullable(this.jcoMaterialesServiceNew.getListMaterialesRFC(fechaInicio,fechaFin))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista Materiales del RFC", produces = "application/json")
    @GetMapping(value = "/getListMaterialesByRFCFechaActual/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <MaterialesRFCResponseDto> getListMaterialesByRFCFechaActual(
    ) throws URISyntaxException {
        try {
            Date fecha = DateUtils.obtenerFechaActual();
            String sFecha = DateUtils.convertDateToString("yyyyMMdd",fecha);
            return Optional.ofNullable(this.jcoMaterialesServiceNew.getListMaterialesRFC(sFecha,sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista y Guarda Materiales nuevos o actualizados", produces = "application/json")
    @PostMapping(value = "/actualizarMaterialFechaActual/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <MaterialesRFCResponseDto> actualizarMaterialFechaActual() throws URISyntaxException {
        log.debug("Lista actualizarMaterialFechaActual : {}");
        try {
            Date fecha = DateUtils.obtenerFechaActual();
            String sFecha = DateUtils.convertDateToString("yyyyMMdd",fecha);
            return Optional.ofNullable(this.jcoMaterialesService.actualizarMaterialesRFC(sFecha, sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista y Guarda Materiales nuevos o actualizados (Todos)", produces = "application/json")
    @PostMapping(value = "/actualizarMaterialTodos/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <MaterialesRFCResponseDto> actualizarMaterialTodos() throws URISyntaxException {
        log.debug("Lista actualizarMaterialTodos : {}");
        try {

            String sFecha = "";
            return Optional.ofNullable(this.jcoMaterialesService.actualizarMaterialesRFC(sFecha, sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista y Guarda Materiales nuevos o actualizados (Por Fecha)", produces = "application/json")
    @PostMapping(value = "/actualizarMaterialxFecha/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <MaterialesRFCResponseDto> actualizarMaterialxFecha(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) throws URISyntaxException {
        log.debug("Lista actualizarMaterialTodos : {}");
        try {

            String sFecha = "";
            return Optional.ofNullable(this.jcoMaterialesService.actualizarMaterialesRFC(fechaInicio, fechaFin))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }



    /* Servicios */
    @ApiOperation(value = "Lista Servicios del RFC By Rango Fechas", produces = "application/json")
    @GetMapping(value = "/getListaServiciosbyRfc/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ServiciosRFCResponseDto> getListaServiciosbyRFC(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin) throws URISyntaxException {
        log.debug("Lista Grupo Servicios : {}");
        try {
            return Optional.ofNullable(this.jcoServiciosServiceNew.getListServicios(fechaInicio,fechaFin))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista Servicios del RFC fechaActual", produces = "application/json")
    @GetMapping(value = "/getListServiciosByRFCFechaActual/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ServiciosRFCResponseDto> getListServiciosByFechaActual(
    ) throws URISyntaxException {
        try {
            Date fecha = DateUtils.obtenerFechaActual();
            String sFecha = DateUtils.convertDateToString("yyyyMMdd",fecha);
            return Optional.ofNullable(this.jcoServiciosServiceNew.getListServicios(sFecha,sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista y Guarda Servicios nuevos o actualizados", produces = "application/json")
    @PostMapping(value = "/actualizarServicioFechaActual/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ServiciosRFCResponseDto> actualizarServicioFechaActual() throws URISyntaxException {
        log.debug("Lista actualizarServicioFechaActual : {}");
        try {
            Date fecha = DateUtils.obtenerFechaActual();
            String sFecha = DateUtils.convertDateToString("yyyyMMdd",fecha);
            return Optional.ofNullable(this.jcoServiciosService.actualizarMaterialesRFC(sFecha, sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @ApiOperation(value = "Lista y Guarda Servicios nuevos o actualizados (Todos)", produces = "application/json")
    @PostMapping(value = "/actualizarServicioTodos/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ServiciosRFCResponseDto> actualizarServicioTodos() throws URISyntaxException {
        log.debug("Lista actualizarServicioTodos : {}");
        try {

            String sFecha = "";
            return Optional.ofNullable(this.jcoServiciosService.actualizarMaterialesRFC(sFecha, sFecha))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista y Guarda Servicios nuevos o actualizados (Desde fechas indicadas)", produces = "application/json")
    @PostMapping(value = "/actualizarServicioxFecha/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ServiciosRFCResponseDto> actualizarServicioxFecha(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) throws URISyntaxException {
        log.debug("Lista actualizarServicioxFecha : {}");
        try {

            return Optional.ofNullable(this.jcoServiciosService.actualizarMaterialesRFC(fechaInicio, fechaFin))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    /* Centro Almacen */
    @ApiOperation(value = "Actualiza Centro Almacen x Centro", produces = "application/json")
    @PostMapping(value = "/actualizaCentroAlmacenxCentro/{centro}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <CentroAlmacenRFCResponseDto> actualizaCentroAlmacen(@PathVariable String centro) throws URISyntaxException {
        log.debug("Lista Centro Almacen : {}");
        try {
            return Optional.ofNullable(this.jcoCentroAlmacenService.actualizaCentroAlmacen(centro))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Actualiza Centro Almacen(Todos)", produces = "application/json")
    @PostMapping(value = "/actualizaCentroAlmacenTodos", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <CentroAlmacenRFCResponseDto> actualizaCentroAlmacenTodos() throws URISyntaxException {
        log.debug("Lista Centro Almacen : {}");
        try {
            return Optional.ofNullable(this.jcoCentroAlmacenService.actualizaCentroAlmacen(""))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista Centro Almacen(Todos)", produces = "application/json")
    @PostMapping(value = "/getListaCentroAlmacenTodos", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <CentroAlmacenRFCResponseDto> getListaCentroAlmacenTodos() throws URISyntaxException {
        log.debug("Lista Centro Almacen : {}");
        try {
            return Optional.ofNullable(this.jcoCentroAlmacenServiceNew.getListaCentroAlmacen(""))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }



    @ApiOperation(value = "Actualiza Centros RFC", produces = "application/json")
    @GetMapping(value = "/actualizarCentrosRFC", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <CentroRFCResponseDto> actualizarCentrosRFC() throws URISyntaxException {
        log.debug("Lista Centro  : {}");

        //String sFecha = DateUtils.convertDateToString("yyyyMMdd",fecha);
//        String sociedad="SFER";
        try {
            return Optional.ofNullable(this.jcoCentroServiceNew.actualizarCentro(""))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Lista Unidad Medida RFC", produces = "application/json")
    @GetMapping(value = "/actualizarUnidadMedidaRFC", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <UnidadMedidaRFCResponseDto> actualizarUnidadMedidaRFC() throws URISyntaxException {
        log.debug("Lista Unidad Medida  : {}");
        try {
            return Optional.ofNullable(this.jcoUnidadMedidaServiceNew.actualizarUnidadMedida())
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Crear Proveedor RFC", produces = "application/json")
    @PostMapping(value = "/crearProveedor/{idProveedor}/{usuarioSap}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ProveedorRFCResponseDto> crearProveedorSap(@PathVariable Integer idProveedor,@PathVariable String usuarioSap) throws URISyntaxException {
        log.debug("Lista Centro Almacen : {}");
        try {
            return Optional.ofNullable(this.jcoProveedorService.grabarProveedor(idProveedor,usuarioSap))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Crear UbigeoSAP", produces = "application/json")
    @PostMapping(value = "/crearUbigeo", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <UbigeoRFCResponseDto> crearUbigeo() throws URISyntaxException {
        log.debug("Lista Ubigeos : {}");
        try {
            return Optional.ofNullable(this.jcoUbigeoService.listarandActualizarUbigeoRFC())
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

//    @ApiOperation(value = "Lista Proveedores RFC", produces = "application/json")
//    @GetMapping(value = "/listaProveedoresByRFC/{nroAcreedor}/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
//    public ResponseEntity <ConsultaProveedorRFCResponseDto> listarProveedoresRFC(@PathVariable String nroAcreedor,
//                                                                                 @PathVariable String fechaInicio,
//                                                                                 @PathVariable String fechaFin) throws URISyntaxException {
//        log.debug("Lista Unidad Medida  : {}");
//        try {
//            return Optional.ofNullable(this.jcoConsultaProveedorService.listaProveedorByRFC(nroAcreedor,fechaInicio,fechaFin,""))
//                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//        } catch (Exception e) {
//            String error = Utils.obtieneMensajeErrorException(e);
//            throw new RuntimeException(error);
//        }
//    }


    @ApiOperation(value = "Lista Proveedores RFC", produces = "application/json")
    @GetMapping(value = "/listaProveedoresByRFC", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <ConsultaProveedorRFCResponseDto> listarProveedoresRFC(@RequestParam(value = "nroAcreedor", required = false) String nroAcreedor,
                                                                                 @RequestParam(value = "email", required = false) String email,
                                                                                 @RequestParam(value = "tipoPersona", required = false) String tipoPersona,
                                                                                 @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
                                                                                 @RequestParam(value = "fechaFin", required = false) String fechaFin) {
        try {
            return Optional.ofNullable(this.jcoConsultaProveedorService.listaProveedorByRFC(nroAcreedor,fechaInicio,fechaFin,(email != null ? email : ""),tipoPersona))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    /* Solicitud de Pedido JOB */
    @ApiOperation(value = "Busca Solicitud de Pedido en base al Numero de Solicitud en la tabla modifcar solped", produces = "application/json")
    @GetMapping(value = "/getConsultarEstadoSolped", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitudPedidoRFCResponseDto>> getConsultarEstadoSolped() throws URISyntaxException {

        try {
            //consultar solpeds
            List<ModificacionSolped> modificacionSolpeds = this.modificacionSolpedRepository.findAll();

            //crear array de rfc
            List<SolicitudPedidoRFCResponseDto> rfcResponseDto =new ArrayList<>();
            //Recorrer las solped
            if(modificacionSolpeds.size() > 0){
                modificacionSolpeds.forEach(item->{
                    //validar si la solped es diferente de estado X
                    if(!item.getEstadoSolped().equals("X")){
                        try {
                            SolicitudPedidoRFCResponseDto rfResponse = new SolicitudPedidoRFCResponseDto();
                            rfResponse =
                                    this.jcoSolicitudPedidoService.getSolpedResponseByCodigo(String.valueOf(item.getNroSolped()));

                            //validar si la solped viene en estado X
                            if(rfResponse.getEstadoSolped().equals("X")){
                                modificacionSolpedRepository.modificarEstadoSolped("X",item.getNroSolped());
                            }

                            rfcResponseDto.add(rfResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        //modificarlas directamente
                        modificacionSolpedRepository.modificarEstadoSolped("X",item.getNroSolped());
                    }
                });
            }


            return Optional.ofNullable(rfcResponseDto)
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @ApiOperation(value = "Notificacion Homologacion vencida Proveedores", produces = "application/json")
    @GetMapping(value = "/notificarHomologacion", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> notificarHomologacion() throws URISyntaxException {

        try {
            //consultar solpeds

            this.jcoProveedorService.notificacionHomologacion();

            return Optional.ofNullable("Ok")
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Notificacion Homologacion vencida Proveedores", produces = "application/json")
    @GetMapping(value = "/{idProveedor}/obtenerFechaHomologacion", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> obtenerFechaHomologacion(@PathVariable("idProveedor") Integer idProveedor) throws URISyntaxException {

        try {
            Proveedor p = this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
            ProveedorHomologacionRFCResponseDto ProveedorHomologacionRFCResponseDto =  this.jcoProveedorService.obtenerFechaHomologacion(p.getAcreedorCodigoSap());

            return Optional.ofNullable(ProveedorHomologacionRFCResponseDto)
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @Autowired
    private JCOOrdenCompraPublicacionService jcoOrdenCompraPublicacionService;

    /* ORden de compras */
    @ApiOperation(value = "Lista oc del RFC", produces = "application/json")
    @GetMapping(value = "/getListOrdenCompraByRFC/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <?> getListOrdenCompraByRFC(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) throws URISyntaxException {
        try {
            return Optional.ofNullable(this.jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(fechaInicio, fechaFin,true))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }
    @ApiOperation(value = "Lista oc del RFC", produces = "application/json")
    @GetMapping(value = "/getListOrdenCompraByRFC/{ordenCompra}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <?> getListOrdenCompraByRFC(
            @PathVariable String ordenCompra
    ) throws URISyntaxException {
        try {
            return Optional.ofNullable(this.jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFCporOC(false, ordenCompra ))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @Autowired
    private JCODocumentoAceptacionService jcoDocumentoAceptacionService;

    /* Documento aceptacion */
    @ApiOperation(value = "Lista documentos acp del RFC", produces = "application/json")
    @GetMapping(value = "/getListDocumentoAceptacionByRFC/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <?> getListDocumentoAceptacionByRFC(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) throws URISyntaxException {
        try {
            this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionListRFC(fechaInicio, fechaFin, true, false, true);
            return Optional.ofNullable("ok")
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    /* Documento aceptacion */
    @ApiOperation(value = "Lista documentos acp del RFC", produces = "application/json")
    @GetMapping(value = "/getListDocumentoAceptacionByRFC/nroRecepcion/{nro}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity <?> getListDocumentoAceptacionByRFC(
            @PathVariable String nro
    ) throws URISyntaxException {
        try {
            this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionListMaterialDocument(nro, true, false, true);
            return Optional.ofNullable("ok")
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }
    @ApiOperation(value = "Lista documentos HES por rango de fechas", produces = "application/json")
    @GetMapping(value = "/getListDocumentoAceptacionHES/{fechaInicio}/{fechaFin}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getListDocumentoAceptacionHES(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) throws URISyntaxException {
        try {
            this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionHES(fechaInicio, fechaFin, false, true);
            return Optional.ofNullable("ok")
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    /* Documento aceptacion anuladas */
    @ApiOperation(value = "Extraer documentos anulados desde OData", produces = "application/json")
    @GetMapping(value = "/extraerDocumentoAceptacionAnuladas", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> extraerDocumentoAceptacionAnuladas() {
        try {
            this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionAnuladasList(false);
            return ResponseEntity.ok("Extracción de documentos anulados ejecutada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            String error = Utils.obtieneMensajeErrorException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
