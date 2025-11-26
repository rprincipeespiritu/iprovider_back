package com.incloud.hcp.service.extractor.impl;

import com.incloud.hcp.config.ToDate;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.enums.OpcionGenericaEnum;
import com.incloud.hcp.enums.OrdenCompraEstadoEnum;
import com.incloud.hcp.enums.OrdenCompraTipoEnum;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.extractor.ExtractorOCService;
import com.incloud.hcp.service.notificacion.ContactoPublicadaOCNotificacion;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.ws.enums.CodEmpresaEnum;
import com.incloud.hcp.ws.recepcionordencompra.bean.ObtenerOCPendienteBodyDetalle;
import com.incloud.hcp.ws.recepcionordencompra.bean.ObtenerOCPendienteBodyResponse;
import com.incloud.hcp.ws.recepcionordencompra.bean.ObtenerOCPendienteResponse;
import com.incloud.hcp.ws.recepcionordencompra.service.GSObtenerOCPendienteService;
import com.incloud.hcp.ws.enums.MonedaCompraEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ExtractorOCServiceImpl implements ExtractorOCService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AtomicBoolean ocProcessing = new AtomicBoolean(false);

    private GSObtenerOCPendienteService gsObtenerOCPendienteService;
    private OrdenCompraRepository ordenCompraRepository;
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private ProveedorService proveedorService;
    private UsuarioRepository usuarioRepository;
    private ContactoPublicadaOCNotificacion contactoPublicadaOCNotificacion;
    private ProveedorRepository proveedorRepository;
    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Autowired
    public ExtractorOCServiceImpl(GSObtenerOCPendienteService gsObtenerOCPendienteService,
                                  OrdenCompraRepository ordenCompraRepository,
                                  OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                                  ProveedorService proveedorService,
                                  UsuarioRepository usuarioRepository,
                                  ContactoPublicadaOCNotificacion contactoPublicadaOCNotificacion,
                                  ProveedorRepository proveedorRepository) {
        this.gsObtenerOCPendienteService = gsObtenerOCPendienteService;
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.proveedorService = proveedorService;
        this.usuarioRepository = usuarioRepository;
        this.contactoPublicadaOCNotificacion = contactoPublicadaOCNotificacion;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public void extraerOC(String fechaInicio, String fechaFinal, boolean enviarCorreoPublicacion) throws Exception {

        List<Integer> svEmpresas = new ArrayList<>();
        svEmpresas.add(CodEmpresaEnum.SILVESTRE.getValor());
        //empresas.add(CodEmpresaEnum.NEOAGRUM.getValor());

        if (!ocProcessing.get()) {
            ocProcessing.set(!ocProcessing.get());
            try {
                svEmpresas.forEach(e -> { // Empresas (Silvestre - Neoagrum)
                    String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR OC -- RANGO: " + fechaInicio + " - " + fechaFinal + " // ";
                    ObtenerOCPendienteResponse responseWS = gsObtenerOCPendienteService.obtenerOCPendiente(e, fechaInicio, fechaFinal, "");
                    List<ObtenerOCPendienteBodyResponse> body = responseWS.getBody();
                    logger.info(header1 + "CANTIDAD DE OC ENCONTRADOS: " + body.size());

                    if (body.size() > 0) {
                        List<String> codigoProveedores = body.stream().map(ObtenerOCPendienteBodyResponse::getiD_Agenda).distinct().collect(Collectors.toList());
                        List<Proveedor> proveedors = proveedorRepository.findByRucs(codigoProveedores);
                        body.forEach(oc -> {
                            //
                            Optional<Proveedor> proveedorOpt = proveedors.stream().filter(p -> p.getRuc().equals(oc.getiD_Agenda())).findFirst();
                            Proveedor p = proveedorOpt.orElse(new Proveedor(oc.getiD_Agenda()));
                            //
                            String opOrdenCompra = String.valueOf(oc.getOp());
                            Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero(opOrdenCompra);
                            String header2 = header1.concat("RUC: " + oc.getiD_Agenda() + " // " + "OC: " + oc.getOp());
                            if (!optionalOrdenCompra.isPresent()) { // OC no existe en HANA
                                // solo publicar OC si esta en estado liberado - Por defecto todas son liberadas
                                OrdenCompra ordenCompra = new OrdenCompra();
                                ordenCompra.setVersion(1); // porque OC es publicada por 1ra vez
                                ordenCompra.setIsActive(OpcionGenericaEnum.SI.getCodigo()); // OC es activa porque es la 1ra y unica version
                                ordenCompra.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId()); // estado inicial publicada
                                ordenCompra.setFechaPublicacion(DateUtils.getCurrentTimestamp());

                                this.getOCValue(e, ordenCompra, oc, p);

                                logger.info(header2 + " // WRITING NEW OC: " + ordenCompra.toString());
                                OrdenCompra newOrdenCompra = ordenCompraRepository.save(ordenCompra);
                                Integer idOrdenCompra = newOrdenCompra.getId();
                                Integer idTipoOrdenCompra = newOrdenCompra.getIdTipoOrdenCompra();

                                List<OrdenCompraDetalle> ordenCompraDetalles = new ArrayList<>();

                                oc.getDetalle().forEach(det -> {
                                    OrdenCompraDetalle detalle = new OrdenCompraDetalle();
                                    detalle.setIdOrdenCompra(idOrdenCompra);
                                    detalle.setTipoPosicion(idTipoOrdenCompra == OrdenCompraTipoEnum.MATERIAL.getId() ? "M" : "S");
                                    this.getOCDetalleValue(newOrdenCompra, detalle, det, p);

                                    logger.info(header2 + " // WRITING NEW OCD: " + detalle.toString());
                                    ordenCompraDetalleRepository.save(detalle);
                                    ordenCompraDetalles.add(detalle);
                                });

                                if (enviarCorreoPublicacion) {
                                    //Se omite el correo a comprador ya que la extracción Silvestre no hay esa información

                                    /*Enviando Correo*/
                                    Proveedor proveedor = proveedorService.getProveedorByRuc(newOrdenCompra.getProveedorRuc());
                                    Usuario proveedorUsuario = null;
                                    if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                                        proveedorUsuario = new Usuario();
                                        proveedorUsuario.setEmail(proveedor.getEmail());
                                        proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                    } else {
                                        List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(newOrdenCompra.getProveedorRuc());
                                        if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                            proveedorUsuario = posibleProveedorList.get(0);
                                    }
                                    String respuesta = "";
                                    if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())

                                        respuesta =  contactoPublicadaOCNotificacion.enviar(newOrdenCompra, proveedorUsuario, null, ordenCompraDetalles);

                                    LogTransaccion logTransaccion = new LogTransaccion();
                                    logTransaccion.setEnvioTrama("contactoPublicadaOCNotificacion");
                                    logTransaccion.setRespuestaCodigo(respuesta);
                                    logTransaccion.setTipoRegistro("Correo contactoPublicadaOCNotificacion");
                                    this.logTransaccionRepository.save(logTransaccion);
                                }
                            } else { // OC ya existe en HANA
                                OrdenCompra ocAnterior = optionalOrdenCompra.get();
                                Date fechaModAnterior = ocAnterior.getFechaModificacion();
                                Time horaModAnterior = ocAnterior.getHoraModificacion();
                                //Date fechaModNueva = oc.getFechaModificacion();
                                //Time horaModNueva = oc.getHoraModificacion();

                                boolean procede = true;

                                // No Aplica - Silvestre no retorna fecha de modificacion de una OC (Temporal - Se validará por los estados que maneje).
                                if (ocAnterior.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.APROBADA.getId()) == 0) {
                                    procede = false;
                                }
                                /*else {
                                    // evalua si la fecha y hora de modificacion del nuevo registro es mayor a la del registro existente
                                    procede = DateUtils.evaluarModificacionDeDocumento(fechaModAnterior, horaModAnterior, fechaModNueva, horaModNueva);
                                }*/

                                if (procede) {
                                    // solo publicar OC si esta en estado liberado - Por defecto todas son liberadas
                                    // se omite validación getEstadoSap (bloqueada o liberada) - Temporal validar por el estado != Aprobada
                                    if (oc.getEstadoOC().equalsIgnoreCase("Aprobado")) { // temporal - Solo actualiza nueva versión si los datos de silvestre cambian
                                        // Comentado en lo que se consulta
                                        /*ocAnterior.setIsActive(OpcionGenericaEnum.NO.getCodigo()); // la version anterior pasa a inactiva (no se visualizara)
                                        ocAnterior = ordenCompraRepository.save(ocAnterior);

                                        OrdenCompra ordenCompra = new OrdenCompra();
                                        this.getOCValue(e, ordenCompra, oc, p);
                                        ordenCompra.setVersion(ocAnterior.getVersion() + 1); // numero de version sgte al actual
                                        ordenCompra.setIsActive(OpcionGenericaEnum.SI.getCodigo()); // la ultima version es la unica activa (que se va a visualizar)
                                        ordenCompra.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId()); // estado inicial "Activa" (publicada)
                                        ordenCompra.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                        ordenCompra.setIdTipoOrdenCompra(ocAnterior.getIdTipoOrdenCompra());
                                        logger.error(header2 + " // WRITING NEXT VERSION OC: " + oc.toString());
                                        OrdenCompra newOrdenCompra = ordenCompraRepository.save(ordenCompra);
                                        Integer idOrdenCompra = newOrdenCompra.getId();
                                        Integer idTipoOrdenCompra = newOrdenCompra.getIdTipoOrdenCompra();

                                        oc.getDetalle().forEach(det -> {
                                            OrdenCompraDetalle detalle = new OrdenCompraDetalle();
                                            detalle.setIdOrdenCompra(idOrdenCompra);
                                            detalle.setTipoPosicion(idTipoOrdenCompra == OrdenCompraTipoEnum.MATERIAL.getId() ? "M" : "S");
                                            this.getOCDetalleValue(newOrdenCompra, detalle, det, p);

                                            logger.error(header2 + " // WRITING NEXT VERSION OCD: " + detalle.toString());
                                            ordenCompraDetalleRepository.save(detalle);
                                        });

                                        if(enviarCorreoPublicacion) {
                                            //Se omite el correo a comprador ya que la extracción Silvestre no hay esa información


                                            Proveedor proveedor = proveedorService.getProveedorByRuc(newOrdenCompra.getProveedorRuc());
                                            Usuario proveedorUsuario = null;
                                            if(proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()){
                                                proveedorUsuario = new Usuario();
                                                proveedorUsuario.setEmail(proveedor.getEmail());
                                                proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                            }
                                            else{
                                                List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(newOrdenCompra.getProveedorRuc());
                                                if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                                    proveedorUsuario = posibleProveedorList.get(0);
                                            }

                                            if(proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                                                contactoPublicadaOCNotificacion.enviar(newOrdenCompra, proveedorUsuario, null);
                                        }*/
                                    } else { // llega registro OC bloqueada o anulada -> Campo de silvestre estadoOC != aprobada
                                        //ocAnterior.setEstadoSap(oc.getEstadoSap());
                                        ocAnterior.setFechaModificacion(DateUtils.getCurrentTimestamp());
                                        //ocAnterior.setHoraModificacion();

                                        //if(oc.getEstadoSap().equalsIgnoreCase(OrdenCompraEstadoSapEnum.ANULADA.getCodigo())){
                                        ocAnterior.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.RECHAZADA.getId());
                                        //}

                                        logger.info(header2 + " // MOD CURRENT VERSION OC: " + ocAnterior.toString());
                                        ordenCompraRepository.save(ocAnterior);
                                    }
                                }
                            }
                        });
                    }
                    logger.info(header1 + "FINISHED");

                });

                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
            } catch (Exception e) {
                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                throw new Exception(e);
            }
        } else {
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + " // OrdenCompra Extraction esta procesando");
        }
    }

    private void getOCValue(Integer idEmpresa, OrdenCompra ordenCompra, ObtenerOCPendienteBodyResponse oc, Proveedor p) {
        ordenCompra.setIdEmpresa(idEmpresa);
        ordenCompra.setNumeroOrdenCompra(String.valueOf(oc.getOp()));
        //ordenCompra.setAutorizadorFechaLiberacion();
        //ordenCompra.setClaseOrdenCompra();
        ///ordenCompra.setCodigoClaseOrdenCompra();
        ordenCompra.setCodigoMondeda(oc.getiD_Moneda() == MonedaCompraEnum.SOLES.getValor() ? "PEN" : "USD");
        //ordenCompra.setCompradorNombre();
        //ordenCompra.setCompradorUsuarioSap();

        //ordenCompra.setCondicionPago(oc.getModoPago());
        ordenCompra.setCondicionPagoDescripcion(oc.getModoPago());
        ordenCompra.setEstadoSap("L");
        //ordenCompra.setFechaAprobacion();

        Optional<Date> dateEntrega = ToDate.getDateFromString(oc.getFechaEntrega(), "dd/MM/yyyy");
        Optional<Date> dateOrden = ToDate.getDateFromString(oc.getFechaOrden(), "dd/MM/yyyy");
        ordenCompra.setFechaRegistro(dateOrden.get());
        ordenCompra.setFechaEntrega(dateEntrega.get());
        ordenCompra.setFechaModificacion(new Date());
        //ordenCompra.setFechaVisualizacion();
        //ordenCompra.setHoraModificacion();
        //ordenCompra.setIdEstadoOrdenCompra(1); // 1 - aprobada //TODO: Hacer maestro de estado de OC
        ordenCompra.setIdTipoOrdenCompra(OrdenCompraTipoEnum.MATERIAL.getId()); //TODO: Hacer maestro de tipo oc, Material y Servicio
        //ordenCompra.setIndicadorContratoMarco(); // No aplica
        //ordenCompra.setLugarEntrega(); // No aplica
        ordenCompra.setMotivoRechazo("");
        ordenCompra.setNumeroOrdenCompra(String.valueOf(oc.getOp()));
        //ordenCompra.setProveedorCodigoSap(oc.getiD_Agenda());
        ordenCompra.setProveedorRazonSocial(p.getRazonSocial());
        ordenCompra.setProveedorRuc(p.getRuc());
        //ordenCompra.setSociedad("");
        ordenCompra.setTotal(oc.getTotal());
        //ordenCompra.setUltimoLiberadorUsuarioSap();
        ordenCompra.setDcto(oc.getDcto());
        ordenCompra.setIdAgendaDireccion(oc.getiD_AgendaDireccion()); //codigo de direccion fiscal (generado al crear el proveedor) -> En proveedor codigoDireccion
        ordenCompra.setIdPago(oc.getiD_Pago()); //1=credito / 0=contado
        //ordenCompra.setId_Empresa(); // no aplica
        ordenCompra.setImpuestos(oc.getImpuestos());
        ordenCompra.setNoRegistro(oc.getNoRegistro());
        ordenCompra.setNotasRecepcion(oc.getNotasRecepcion());
        ordenCompra.setObservaciones(oc.getObservaciones());
        ordenCompra.setSubtotal(oc.getSubTotal());
    }

    private void getOCDetalleValue(OrdenCompra ordenCompra, OrdenCompraDetalle detalle, ObtenerOCPendienteBodyDetalle det, Proveedor p) {
        detalle.setCantidad(det.getCantidad());
        //detalle.setCodigoSapAlmacen();
        detalle.setCodigoSapBienServicio(det.getCodigoProducto());
        //detalle.setCodigoSapCentro();
        //detalle.setDenominacionAlmacen();
        //detalle.setDenominacionCentro();
        //detalle.setDescripcionBienServicio(det.d); //Obtener del maestro de bien_servicio
        //detalle.setDireccionCentro();
        //detalle.setFechaEntrega();
        detalle.setIndicadorImpuesto("");
        detalle.setNumeroOrdenCompra(ordenCompra.getNumeroOrdenCompra());
        //detalle.setPosicion();}
        detalle.setPrecioTotal(det.getImporte());
        detalle.setPrecioUnitario(det.getPrecio());
        //detalle.setUnidadMedidaBienServicio();
        //detalle.setDescripcionBienServicio(det.getiD_UnidadDoc()); //Validar
        detalle.setDescripcionUnidadMedida(det.getiD_UnidadDoc());
        detalle.setCodigoProducto(det.getCodigoProducto());
        detalle.setIdAmarre(det.getiD_Amarre());
        //detalle.setIdAmarreSC(); // Mantenerlo viene de SC
        detalle.setKardex(det.getKardex());
        //detalle.setOpSolicitudCompra(); // Mantenerlo viene de SC
        detalle.setObservaciones(det.getObservaciones());
    }

    @Override
    public boolean toggleOrdenCompraExtractionProcessingState() {
        ocProcessing.set(!ocProcessing.get());
        return ocProcessing.get();
    }

    @Override
    public boolean currentOrdenCompraExtractionProcessingState() {
        return ocProcessing.get();
    }
}
