package com.incloud.hcp.service.impl;

import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.enums.OrdenCompraEstadoEnum;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.contratoMarco.dto.ContratoMarcoPdfSapDto;
import com.incloud.hcp.jco.contratoMarco.service.JCOContratoMarcoPdfService;
import com.incloud.hcp.jco.contratoMarco.service.JCOContratoMarcoPublicacionService;
import com.incloud.hcp.jco.ordenCompra.dto.*;
import com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.*;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPdfService;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraService;
import com.incloud.hcp.jco.proveedor.service.JCOProveedorService;
import com.incloud.hcp.jco.solped.dto.SolicitudPedidoRFCResponseDto;
import com.incloud.hcp.jco.solped.service.JCOSolicitudPedidoService;
import com.incloud.hcp.myibatis.mapper.OrdenCompraMapper;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.pdf.PdfGeneratorFactory;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.sap.ordenCompra.CreacionOcMasivoSAP;
import com.incloud.hcp.sap.ordenCompra.OrdenCompraSapService;
import com.incloud.hcp.service.OrdenCompraDetalleService;
import com.incloud.hcp.service.OrdenCompraService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.extractor.ExtractorOCService;
import com.incloud.hcp.service.notificacion.ContactoAprobadaRechazadaOCNotificacion;
import com.incloud.hcp.service.notificacion.ContactoPublicadaOCNotificacion;
import com.incloud.hcp.service.notificacion.ContactoVisualizadoOCNotificacion;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private OrdenCompraRepository ordenCompraRepository;
    private ModificacionSolpedRepository modificacionSolpedRepository;
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private ContactoVisualizadoOCNotificacion contactoVisualizadoOCNotificacion;
    private ContactoAprobadaRechazadaOCNotificacion contactoAprobadaRechazadaOCNotificacion;
    private ProveedorRepository proveedorRepository;
    private UsuarioRepository usuarioRepository;
    private JCOContratoMarcoPublicacionService jcoContratoMarcoPublicacionService;
    private JCOOrdenCompraPdfService jcoOrdenCompraPdfService;
    private JCOOrdenCompraService jcoOrdenCompraService;
    private JCOSolicitudPedidoService jcoSolicitudPedidoService;
    private OrdenCompraDetalleService ordenCompraDetalleService;
    private JCOContratoMarcoPdfService jcoContratoMarcoPdfService;
    private ProveedorService proveedorService;
    private CotizacionDetalleRepository cotizacionDetalleRepository;
    private CotizacionRepository cotizacionRepository;
    private LicitacionRepository licitacionRepository;
    private LicitacionDetalleRepository licitacionDetalleRepository;
    private DocumentoAceptacionRepository documentoAceptacionRepository;
    private CreacionOcMasivoSAP creacionOcMasivoSAP;
    private IndicadorRepository indicadorRepository;

    private LogTransaccionRepository logTransaccionRepository;

    private ExtractorOCService extractorOCService;

    private ParametroRepository parametroRepository;

    private OrdenCompraSapService ordenCompraSapService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ContactoPublicadaOCNotificacion contactoPublicadaOCNotificacion;

    @Autowired
    public OrdenCompraServiceImpl(OrdenCompraRepository ordenCompraRepository,
                                  OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                                  ContactoVisualizadoOCNotificacion contactoVisualizadoOCNotificacion,
                                  ContactoAprobadaRechazadaOCNotificacion contactoAprobadaRechazadaOCNotificacion,
                                  ProveedorRepository proveedorRepository,
                                  UsuarioRepository usuarioRepository,
                                  JCOContratoMarcoPublicacionService jcoContratoMarcoPublicacionService,
                                  JCOOrdenCompraPdfService jcoOrdenCompraPdfService,
                                  OrdenCompraDetalleService ordenCompraDetalleService,
                                  JCOContratoMarcoPdfService jcoContratoMarcoPdfService,
                                  ProveedorService proveedorService,
                                  ExtractorOCService extractorOCService,
                                  JCOOrdenCompraService jcoOrdenCompraService,
                                  CotizacionDetalleRepository cotizacionDetalleRepository,
                                  CotizacionRepository cotizacionRepository,
                                  LicitacionRepository licitacionRepository,
                                  LicitacionDetalleRepository licitacionDetalleRepository,
                                  JCOSolicitudPedidoService jcoSolicitudPedidoService,
                                  ModificacionSolpedRepository modificacionSolpedRepository,
                                  DocumentoAceptacionRepository documentoAceptacionRepository,
                                  CreacionOcMasivoSAP creacionOcMasivoSAP,
                                  LogTransaccionRepository logTransaccionRepository,
                                  OrdenCompraSapService ordenCompraSapService,
                                  IndicadorRepository indicadorRepository,
                                  ParametroRepository parametroRepository) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.contactoVisualizadoOCNotificacion = contactoVisualizadoOCNotificacion;
        this.contactoAprobadaRechazadaOCNotificacion = contactoAprobadaRechazadaOCNotificacion;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.jcoContratoMarcoPublicacionService = jcoContratoMarcoPublicacionService;
        this.jcoOrdenCompraPdfService = jcoOrdenCompraPdfService;
        this.ordenCompraDetalleService = ordenCompraDetalleService;
        this.jcoContratoMarcoPdfService = jcoContratoMarcoPdfService;
        this.proveedorService = proveedorService;
        this.extractorOCService = extractorOCService;
        this.jcoOrdenCompraService = jcoOrdenCompraService;
        this.cotizacionDetalleRepository = cotizacionDetalleRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.licitacionRepository = licitacionRepository;
        this.jcoSolicitudPedidoService = jcoSolicitudPedidoService;
        this.licitacionDetalleRepository = licitacionDetalleRepository;
        this.modificacionSolpedRepository = modificacionSolpedRepository;
        this.documentoAceptacionRepository = documentoAceptacionRepository;
        this.creacionOcMasivoSAP = creacionOcMasivoSAP;
        this.logTransaccionRepository = logTransaccionRepository;
        this.ordenCompraSapService = ordenCompraSapService;
        this.indicadorRepository = indicadorRepository;
        this.parametroRepository = parametroRepository;
    }

    @Override
    public List<OrdenCompra> getAllOrdenCompra() {
        return ordenCompraRepository.getAllActive();
    }

    @Override
    public OrdenCompra getOrdenCompraById(Integer idOrdenCompra) {
        return ordenCompraRepository.getOrdenCompraById(idOrdenCompra);
    }

    @Override
    public OrdenCompra getNroOrdenCompraById(String nroOrdenCompra) {
        return ordenCompraRepository.findByNumeroOrdenCompra(nroOrdenCompra);
    }


    @Autowired
    private SociedadRepository sociedadRepository;

    @Autowired
    private OrdenCompraMapper ordenCompraMapper;

    @Autowired
    private ParametroMapper parametroMapper;

    @Override
    public List<OrdenCompra> getOrdenCompraListPorFechasAndRuc( String email, FiltroOrdenCompraDto filtroOrdenCompraDto) {

        if (email == null || email.isEmpty()) {
            /*filtroOrdenCompraDto.setFechaInicio(fechaInicio);
            filtroOrdenCompraDto.setFechaFin(fechaFin);*/

            List<OrdenCompra> listByFiltroOrdenCompra = ordenCompraMapper.getListByFiltroOrdenCompra(filtroOrdenCompraDto);
            //listByFiltroOrdenCompra.sort(Comparator.comparing(OrdenCompra::getId).reversed());
            return listByFiltroOrdenCompra;
            //return  ordenCompraMapper.getListByFiltroOrdenCompra(filtroOrdenCompraDto);
            //return ordenCompraRepository.getOrdenCompraByFechaRegistroBetween(fechaInicio, fechaFin);
        } else {
            Proveedor proveedor = this.proveedorRepository.getProveedorByEmail(email);
            filtroOrdenCompraDto.setRuc(proveedor.getRuc().trim());
            /*filtroOrdenCompraDto.setFechaInicio(fechaInicio);
            filtroOrdenCompraDto.setFechaFin(fechaFin);*/
            List<OrdenCompra> listByFiltroOrdenCompra = ordenCompraMapper.getListByFiltroOrdenCompra(filtroOrdenCompraDto);
            //listByFiltroOrdenCompra.sort(Comparator.comparing(OrdenCompra::getId).reversed());
            return listByFiltroOrdenCompra;
            //return  ordenCompraMapper.getListByFiltroOrdenCompra(filtroOrdenCompraDto);
            //return ordenCompraRepository.getOrdenCompraByFechaRegistroBetweenAndProveedorRuc(fechaInicio, fechaFin, proveedor.getRuc());
        }
    }

    @Override
    @Transactional
    /* En este metodo se guarda la fecha de actualización cuando se abre por primera vez la orden de compra
     * Además, se cambia de estado de orden de compra a '2: Visualiza'*/
    public OrdenCompraRespuestaDto updateOrdenCompraFechaVisualizacion(Integer idOrdenCompra) {
        OrdenCompraRespuestaDto ordenCompraRespuestaDto = new OrdenCompraRespuestaDto();
        List<String> mensajes = new ArrayList<>();
        Optional<OrdenCompra> ordenCompraOptional = ordenCompraRepository.findByIdAndIsActive(idOrdenCompra);
        OrdenCompra ordenCompra;

        if (ordenCompraOptional.isPresent()) {
            ordenCompra = ordenCompraOptional.get();

            if (ordenCompra.getFechaVisualizacion() == null) {
                ordenCompra.setFechaVisualizacion(DateUtils.getCurrentTimestamp());

                if (ordenCompra.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.ACTIVA.getId()) == 0) {
                    ordenCompra.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.VISUALIZADA.getId());
                }
                ordenCompra = ordenCompraRepository.save(ordenCompra);
                ordenCompraRespuestaDto.setOrdenCompra(ordenCompra);

                /*Enviando Correo*/
                Usuario comprador = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
                if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                    contactoVisualizadoOCNotificacion.enviar(ordenCompra, null, comprador);
                else
                    mensajes.add("Error al obtener los datos del comprador para envio de correo.");

                Proveedor proveedor = proveedorService.getProveedorByRuc(ordenCompra.getProveedorRuc());
                Usuario proveedorUsuario = null;
                if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                    proveedorUsuario = new Usuario();
                    proveedorUsuario.setEmail(proveedor.getEmail());
                    proveedorUsuario.setApellido(proveedor.getRazonSocial());
                } else {
                    List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompra.getProveedorRuc());
                    if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                        proveedorUsuario = posibleProveedorList.get(0);
                }

                if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                    contactoVisualizadoOCNotificacion.enviar(ordenCompra, proveedorUsuario, null);
                else
                    mensajes.add("Error al obtener los datos del proveedor para envio de correo.");

                ordenCompraRespuestaDto.setMensajes(mensajes);
            }
        }
        return ordenCompraRespuestaDto;
    }

    @Override
    public OrdenCompraRespuestaDto aprobarRechazarOrdenCompra(Integer idOrdenCompra, int estado, String textoRechazo) {
        OrdenCompraRespuestaDto ordenCompraRespuestaDto = new OrdenCompraRespuestaDto();
        Optional<OrdenCompra> ordenCompraOptional = ordenCompraRepository.findByIdAndIsActive(idOrdenCompra);
        OrdenCompra ordenCompra;
        List<String> mensajes = new ArrayList<>();

        if (ordenCompraOptional.isPresent()) {
            ordenCompra = ordenCompraOptional.get();

            ordenCompra.setIdEstadoOrdenCompra(estado);
            ordenCompra.setFechaAprobacion(DateUtils.getCurrentTimestamp());

            if (estado == OrdenCompraEstadoEnum.RECHAZADA.getId()) {
                ordenCompra.setMotivoRechazo(textoRechazo);
                List<DocumentoAceptacion> listDocAcep = documentoAceptacionRepository.getIdDocumentoAceptacionByIdOrdenCompra(idOrdenCompra);
                if (listDocAcep != null) {

                    if(listDocAcep.size() > 0){
                        OrdenCompra oc = new OrdenCompra();
                        oc.setId(idOrdenCompra);
                        ordenCompraRespuestaDto.setOrdenCompra(oc);
                        mensajes.add("No se puede rechazar ya que la Orden de Compra cuenta con EM y HES creadas.");
                        ordenCompraRespuestaDto.setMensajes(mensajes);
                        return ordenCompraRespuestaDto;
                    }
                    
                }
            }


            logger.error("OC " + ordenCompra.getNumeroOrdenCompra() + " APROB/RECHAZO estado (antes) :" + ordenCompra.getIdEstadoOrdenCompra());
            ordenCompra = ordenCompraRepository.save(ordenCompra);
            logger.error("OC " + ordenCompra.getNumeroOrdenCompra() + " APROB/RECHAZO estado (despues) :" + ordenCompra.getIdEstadoOrdenCompra());
            ordenCompraRespuestaDto.setOrdenCompra(ordenCompra);


            /*Enviando Correo*/
            Usuario comprador = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
            if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(), ordenCompra, null, comprador);
            else
                mensajes.add("Error al obtener los datos del comprador para envio de correo de aprobacion de orden de compra.");

            Proveedor proveedor = proveedorService.getProveedorByRuc(ordenCompra.getProveedorRuc());
            Usuario proveedorUsuario = null;
            if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                proveedorUsuario = new Usuario();
                proveedorUsuario.setEmail(proveedor.getEmail());
                proveedorUsuario.setApellido(proveedor.getRazonSocial());
            } else {
                List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompra.getProveedorRuc());
                if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                    proveedorUsuario = posibleProveedorList.get(0);
            }

            if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty()) {
                contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(), ordenCompra, proveedorUsuario, null);
                System.out.println("");
            } else {
                mensajes.add("Error al obtener los datos del proveedor para envio de correo de aprobacion de orden de compra.");
            }

            if (mensajes.size() == 0) {
                mensajes.add("");
            }

            ordenCompraRespuestaDto.setMensajes(mensajes);
        }
        return ordenCompraRespuestaDto;
    }


    @Override
    public void extraerOrdenCompraMasivoByRangoFechas(Date fechaInicio, Date fechaFin, boolean enviarCorreoPublicacion) {
        LocalDate currentlyExtractLocalDate = DateUtils.utilDateToLocalDate(fechaInicio);
        LocalDate finalLocalDate = DateUtils.utilDateToLocalDate(fechaFin);
        logger.info("EXTRACCION ORDEN_COMPRA MASIVA - FECHA INICIO: " + currentlyExtractLocalDate.toString());
        logger.info("EXTRACCION ORDEN_COMPRA MASIVA - FECHA FIN: " + finalLocalDate.toString());

        while (currentlyExtractLocalDate.isBefore(finalLocalDate.plusDays(1))) {
            try {
                String currentDateAsString = DateUtils.localDateToStringPattern(currentlyExtractLocalDate, DateUtils.STANDARD_DATE_FORMAT);
                extractorOCService.extraerOC(currentDateAsString, currentDateAsString, false);
                //String currentDateAsSapString = DateUtils.localDateToSapString(currentlyExtractLocalDate);
                //jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(currentDateAsSapString, currentDateAsSapString, enviarCorreoPublicacion);
                currentlyExtractLocalDate = currentlyExtractLocalDate.plusDays(1);
            } catch (Exception e) {
                String error = Utils.obtieneMensajeErrorException(e);
                logger.error("ERROR al extraer ordenes de compra de la fecha " + DateUtils.localDateToString(currentlyExtractLocalDate) + " : " + error);
                currentlyExtractLocalDate = currentlyExtractLocalDate.plusDays(1);
            }
        }
    }


    @Override
    public void extraerContratoMarcoMasivoByRangoFechas(Date fechaInicio, Date fechaFin, boolean enviarCorreoPublicacion) {
        LocalDate currentlyExtractLocalDate = DateUtils.utilDateToLocalDate(fechaInicio);
        LocalDate finalLocalDate = DateUtils.utilDateToLocalDate(fechaFin);
        logger.error("EXTRACCION CONTRATO_MARCO MASIVA - FECHA INICIO: " + currentlyExtractLocalDate.toString());
        logger.error("EXTRACCION CONTRATO_MARCO MASIVA - FECHA FIN: " + finalLocalDate.toString());

        while (currentlyExtractLocalDate.isBefore(finalLocalDate.plusDays(1))) {
            try {
                String currentDateAsSapString = DateUtils.localDateToSapString(currentlyExtractLocalDate);
                jcoContratoMarcoPublicacionService.extraerContratoMarcoListRFC(currentDateAsSapString, currentDateAsSapString, enviarCorreoPublicacion);
                currentlyExtractLocalDate = currentlyExtractLocalDate.plusDays(1);
            } catch (Exception e) {
                String error = Utils.obtieneMensajeErrorException(e);
                logger.error("ERROR al extraer contratos marco de la fecha " + DateUtils.localDateToString(currentlyExtractLocalDate) + " : " + error);
                currentlyExtractLocalDate = currentlyExtractLocalDate.plusDays(1);
            }
        }
    }


    @Override
    public String getOrdenCompraPdfContent(String numeroOrdenCompra) throws Exception {

        String ordenCompraPdfDto = jcoOrdenCompraPdfService.obtenerBase64OC(numeroOrdenCompra);
        //byte[] valueDecoded = org.apache.commons.codec.binary.Base64.decodeBase64(ordenCompraPdfDto);
        return ordenCompraPdfDto;
    }


    @Override
    public String getContratoMarcoPdfContent(String numeroContratoMarco) throws Exception {
        OrdenCompraPdfDto contratoMarcoPdfDto = new OrdenCompraPdfDto();
        Optional<OrdenCompra> optionalContratoMarco = ordenCompraRepository.getOrdenCompraActivaByNumero(numeroContratoMarco);

        if (optionalContratoMarco.isPresent()) {
            OrdenCompra contratoMarco = optionalContratoMarco.get();
            ContratoMarcoPdfSapDto contratoMarcoPdfSapDto = jcoContratoMarcoPdfService.extraerContratoMarcoPdfDtoRFC(numeroContratoMarco);


            contratoMarcoPdfDto.setOrdenCompraNumero(contratoMarco.getNumeroOrdenCompra());
            contratoMarcoPdfDto.setOrdenCompraTipo(contratoMarco.getTipoOrdenCompra().getDescripcion());
            contratoMarcoPdfDto.setOrdenCompraVersion(String.valueOf(contratoMarco.getVersion()));
            contratoMarcoPdfDto.setOrdenCompraFechaCreacion(contratoMarco.getFechaRegistro());
            contratoMarcoPdfDto.setOrdenCompraFormaPago(contratoMarco.getCondicionPagoDescripcion());
            contratoMarcoPdfDto.setOrdenCompraMoneda(contratoMarco.getCodigoMondeda());
//            contratoMarcoPdfDto.setOrdenCompraAutorizador(contratoMarco.getAutorizadorFechaLiberacion());
            contratoMarcoPdfDto.setOrdenCompraAutorizador(contratoMarcoPdfSapDto.getClienteAutorizador());
            contratoMarcoPdfDto.setOrdenCompraPersonaContacto(contratoMarcoPdfSapDto.getClientePersonaContacto());

            contratoMarcoPdfDto.setClienteRuc(contratoMarco.getInfoSociedad().getRuc());
            contratoMarcoPdfDto.setClienteTelefono(contratoMarco.getInfoSociedad().getTelefono());
            contratoMarcoPdfDto.setClienteDireccion(contratoMarco.getInfoSociedad().getDireccionFiscal());
            contratoMarcoPdfDto.setClienteRazonSocial(contratoMarco.getInfoSociedad().getRazonSocial());

            contratoMarcoPdfDto.setProveedorRazonSocial(contratoMarco.getProveedorRazonSocial());
            contratoMarcoPdfDto.setProveedorRuc(contratoMarco.getProveedorRuc());
            if (contratoMarco.getProveedorCodigoSap() != null && !contratoMarco.getProveedorCodigoSap().isEmpty())
                contratoMarcoPdfDto.setProveedorNumero(String.valueOf(Integer.parseInt(contratoMarco.getProveedorCodigoSap())));
            contratoMarcoPdfDto.setProveedorDireccion(contratoMarcoPdfSapDto.getProveedorDireccion());
            contratoMarcoPdfDto.setProveedorContactoNombre(contratoMarcoPdfSapDto.getProveedorContactoNombre());
            contratoMarcoPdfDto.setProveedorContactoTelefono(contratoMarcoPdfSapDto.getProveedorContactoTelefono());

//            contratoMarcoPdfDto.setMontoSubtotal();
//            contratoMarcoPdfDto.setMontoDescuento();
//            contratoMarcoPdfDto.setMontoIgv();
            contratoMarcoPdfDto.setMontoImporteTotal(contratoMarco.getTotal());

            List<OrdenCompraDetalle> ordenCompraDetalleList = ordenCompraDetalleService.getOrdenCompraDetalleListByIdOc(contratoMarco.getId());
            List<OrdenCompraPosicionPdfDto> posicionPdfDtoList = new ArrayList<>();

            ordenCompraDetalleList.forEach(ocd -> {
                OrdenCompraPosicionPdfDto posicionPdfDto = new OrdenCompraPosicionPdfDto();

                if (ocd.getPosicion() != null && !ocd.getPosicion().isEmpty())
                    posicionPdfDto.setPosicion(String.valueOf(Integer.parseInt(ocd.getPosicion())));
                posicionPdfDto.setCentro(ocd.getDenominacionCentro());
                if (ocd.getCodigoSapBienServicio() != null && !ocd.getCodigoSapBienServicio().isEmpty())
                    posicionPdfDto.setMaterial(String.valueOf(Integer.parseInt(ocd.getCodigoSapBienServicio())));
                posicionPdfDto.setDescripcion(ocd.getDescripcionBienServicio());
                posicionPdfDto.setCantidad(ocd.getCantidad());
                posicionPdfDto.setUnidad(ocd.getUnidadMedidaBienServicio());
                posicionPdfDto.setFechaEntrega(ocd.getFechaEntrega());
                posicionPdfDto.setPrecioUnitario(ocd.getPrecioUnitario());
                posicionPdfDto.setImporte(ocd.getPrecioTotal());

                posicionPdfDtoList.add(posicionPdfDto);
            });

            contratoMarcoPdfDto.setOrdenCompraPosicionPdfDtoList(posicionPdfDtoList);
        } else {
            return null;
        }

        byte[] generateContratoMarcoBytes = PdfGeneratorFactory.getJasperGenerator().generateContratoMarcoPdfBytes(contratoMarcoPdfDto);
        return Base64.getEncoder().encodeToString(generateContratoMarcoBytes);
    }

    @Autowired
    private CondicionPagoReposity condicionPagoReposity;

    @Autowired
    private JCOProveedorService jcoProveedorService;

    public List<ProveedoresFinalDto> crearOrdenCompraIprovider(OrdenCompraGenerarDto bean) throws Exception {


        Usuario comprador = usuarioRepository.findByEmail(bean.getEmailComprador());
        if(comprador == null){ //consultar si exite en proveedores
            throw new PortalException("No cuenta con permisos suficientes para esta acción.");
        }
        Integer licitacionId = bean.getIdLicitacion();

        //validar solped
        bean.getOrdenCompraDetalleList().forEach(item->{
            List<ModificacionSolped> modificacionSolpeds = modificacionSolpedRepository.
                    findByNroSolpedPosicion(item.getOpSolicitudCompra(),item.getPosicion());
            if(modificacionSolpeds.size() >0){
                modificacionSolpeds.forEach(solp->{
                    if(!solp.getEstadoSolped().equals("X")){
                        throw new PortalException("No es posible Generar la OC , el nro de solped :"+solp.getNroSolped()+ " se encuentra en proceso.." );
                    }
                });

            }
        });


        List<OrdenCompra> lista = new ArrayList<>();
        bean.getProveedores().forEach(item -> {
            //CREAR DATA EN ORDEN DE COMPRA
            String mensaje="";
            OrdenCompra ocompra = new OrdenCompra();
            //GENERAR ORDEN DE COMPRA
            //consultar ruc proveedor
            Integer IdProveedor = Integer.parseInt(item.getIdProveedor());
            Proveedor proveedor = this.proveedorRepository.getProveedorByIdProveedor(IdProveedor);

            //consultar Sociedad
            //Sociedad sociedad = sociedadRepository.getByCodigoSociedad("1100"); //sociedad pruebea 0019
            //ocompra.setId(null);
            ocompra.setCondicionPago(bean.getOrdenCompra().getCondicionPago());
            ocompra.setCodigoMondeda(proveedor.getMoneda().getCodigoMoneda());
            ocompra.setImpuestos(bean.getOrdenCompra().getImpuestos());
            ocompra.setIsActive(bean.getOrdenCompra().getIsActive());
            ocompra.setLugarEntrega(bean.getOrdenCompra().getLugarEntrega());
            ocompra.setVersion(1);
            ocompra.setNumeroOrdenCompra("");
            ocompra.setTipoOrdenCompra(bean.getOrdenCompra().getTipoOrdenCompra());
            ocompra.setIdEstadoOrdenCompra(1);//Activa
            ocompra.setProveedorRuc((proveedor.getRuc()));//setear ruc proveedor
            ocompra.setProveedorRazonSocial(proveedor.getRazonSocial());
            ocompra.setProveedorCodigoSap(proveedor.getAcreedorCodigoSap());
            ocompra.setClaseOrdenCompra("ZNAM");//Pedido Centenario
            ocompra.setCompradorNombre(comprador.getNombre() +" " + comprador.getApellido());
            ocompra.setCompradorEmail(comprador.getEmail());
            ocompra.setCompradorUsuarioSap(comprador.getCodigoSap());
            ocompra.setClaseDoc(bean.getOrdenCompraDetalleList().get(0).getClaseDoc());
            Date fechaActual = DateUtils.obtenerFechaActual();
            ocompra.setFechaRegistro(fechaActual);
            ocompra.setFechaPublicacion(new Timestamp(fechaActual.getTime()));
            ocompra.setSociedad("P100"); //sociedad

            String solped = String.valueOf(bean.getOrdenCompraDetalleList().get(0).getOpSolicitudCompra());
            String areaSolicitante = "";
            String url_sap = "https://my419028-api.s4hana.cloud.sap/sap/opu/odata/sap/YY1_PURCHASEREQUISITION_CDS/YY1_PurchaseRequisition?$filter=PurchaseRequisition eq '"+solped +"'";

            String usrSap = "RMJ-IPA";
            String pwdSap = "DzBpqubKhGleURSvZGVqNXoPt4mRCAseYehGus}t";
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            //okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .get()
                    .build();

            Response postResponse_pk = null;
            try {
                postResponse_pk = client.newCall(postRequest_pk).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (postResponse_pk.isSuccessful()) {

                String responseBody = null;
                try {
                    responseBody = postResponse_pk.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    // Convertir la respuesta en un objeto JSON
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject dObject = jsonObject.getJSONObject("d");
                    JSONArray resultsArray = dObject.getJSONArray("results");

                    // Crear un Map para agrupar los detalles por PurchaseOrder
                    Map<String, List<JSONObject>> groupedData = new HashMap<>();

                    // Recorrer el array y agrupar los datos
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject result = resultsArray.getJSONObject(i);
                        areaSolicitante = result.getString("YY1_AreaSolicitante_PRI");

                    }

                }catch ( Exception e){
                    System.out.println(e);
                }

            }
            Parametro parametro = parametroRepository.getParametroByModuloAndTipoAndCodigo("AREA_SOLICITANTE", "AREA_SOLICITANTE", areaSolicitante);
            ocompra.setSedeSolicitud(parametro!=null ? parametro.getValor() : "");

            OrdenCompra ocompraX = ocompra;//this.ordenCompraRepository.save(ocompra);

            List<OrdenCompraDetalle> ListOCDetalleFinal = new ArrayList<>();


            //recorrer orden compra detalle
            for (OrdenCompraDetalle ocd : bean.getOrdenCompraDetalleList()) {
                //validar si ya se a utilizado la solped para generar oc
                //validar el id del proveedor
                System.out.println("");
                Integer idProveedor = 0;
                if(ocd.getNumeroOrdenCompra().length() > 9){
                    idProveedor = 0;
                }else{
                    idProveedor = ocd.getNumeroOrdenCompra().equals("") ? 0 : Integer.parseInt(ocd.getNumeroOrdenCompra());
                }
                if(proveedor.getIdProveedor().equals(idProveedor)){

                    try {
//                        String proveedorRFCResponseDto =
//                                this.jcoProveedorService.homologarProveedor(idProveedor,"");
                        String proveedorRFCResponseDto = "01";
                        if (proveedorRFCResponseDto.equals("01")) {
                            List<OrdenCompraDetalle> ordenCompraDetallec = ordenCompraDetalleRepository
                                    .findBySolpedPosicion(ocd.getOpSolicitudCompra(),ocd.getPosicion());
                            //if(ordenCompraDetallec.size() > 0 ){
                            if(true == false){
                                // continuar
                                mensaje = mensaje + "\n<br/> Ya se a generado OC Nro ("+ordenCompraDetallec.get(0).getNumeroOrdenCompra()+") para la solped :"+ ocd.getOpSolicitudCompra() +" posición :"+ocd.getPosicion();
                                //mensaje = mensaje + "\n"+ordenCompraDetallec.get(0).getNumeroOrdenCompra()+"";
                                ocompraX.setNumeroOrdenCompra(mensaje);
                                continue;
                            }else{


                                //validar el id del proveedor
                                Integer idP = ocd.getNumeroOrdenCompra().equals("") ? 0 : Integer.parseInt(ocd.getNumeroOrdenCompra());
                                if (proveedor.getIdProveedor().equals(idP)) {
                                    ocd.setNumeroOrdenCompra("");
                                    //ocd.setOrdenCompra(ocompraX);
                                    ocd.setFechaEntrega(fechaActual);
                                    ocd.setIdOrdenCompra(ocompraX.getId());
                                    ocompraX.setNumeroOrdenCompra("");
                                    ocd.setOrdenCompra(ocompraX);

                                    ocd.setPosicion(StringUtils.leftPad(ocd.getPosicion().trim(), 5, '0'));
                                    ocd.setCondicionPago(ocd.getCondicionPago());
                                    try {
                                        //this.ordenCompraDetalleRepository.save(ocd);
                                        ListOCDetalleFinal.add(ocd);
                                        //ListOCDetalleFinal.add(this.ordenCompraDetalleRepository.save(ocd));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        String error = Utils.obtieneMensajeErrorException(ex);
                                        throw new RuntimeException(error);
                                    }
                                }
                            }
                        }else {
                            ocompraX.setNumeroOrdenCompra("Proveedor no se encuentra homologado externamente :"+proveedorRFCResponseDto);
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ocompraX.setNumeroOrdenCompra(e.getMessage());
                        continue;
                    }

                }

            }
            bean.getSociedades().forEach(sociedades -> {
                List<OrdenCompraDetalle> ListOCDetalleFinalSociedad = new ArrayList<>();
                ListOCDetalleFinal.forEach(ordenDetalle->{
                    if(ordenDetalle.getSociedad().equals(sociedades.getSociedad())){
                        ListOCDetalleFinalSociedad.add(ordenDetalle);
                    }
                });
                if(ListOCDetalleFinalSociedad.size() > 0 ){
                    OrdenCompra ordenCompraNueva = new OrdenCompra();
                    ordenCompraNueva.setCondicionPago(bean.getOrdenCompra().getCondicionPago());
                    ordenCompraNueva.setCodigoMondeda(proveedor.getMoneda().getCodigoMoneda());
                    ordenCompraNueva.setImpuestos(bean.getOrdenCompra().getImpuestos());
                    ordenCompraNueva.setIsActive(bean.getOrdenCompra().getIsActive());
                    ordenCompraNueva.setLugarEntrega(bean.getOrdenCompra().getLugarEntrega());
                    ordenCompraNueva.setVersion(1);
                    ordenCompraNueva.setNumeroOrdenCompra("");
                    ordenCompraNueva.setTipoOrdenCompra(bean.getOrdenCompra().getTipoOrdenCompra());
                    ordenCompraNueva.setIdEstadoOrdenCompra(1);//Activa
                    ordenCompraNueva.setProveedorRuc((proveedor.getRuc()));//setear ruc proveedor
                    ordenCompraNueva.setProveedorRazonSocial(proveedor.getRazonSocial());
                    ordenCompraNueva.setProveedorCodigoSap(proveedor.getAcreedorCodigoSap());
                    ordenCompraNueva.setClaseOrdenCompra("ZNAM");//Pedido Centenario
                    ordenCompraNueva.setCompradorNombre(comprador.getNombre() +" " + comprador.getApellido());
                    ordenCompraNueva.setCompradorEmail(comprador.getEmail());
                    ordenCompraNueva.setCompradorUsuarioSap(comprador.getCodigoSap());
                    Date fechaActuals = DateUtils.obtenerFechaActual();
                    ordenCompraNueva.setFechaRegistro(fechaActuals);
                    ordenCompraNueva.setClaseDoc(bean.getOrdenCompraDetalleList().get(0).getClaseDoc());
                    ordenCompraNueva.setFechaPublicacion(new Timestamp(fechaActuals.getTime()));
                    ordenCompraNueva.setSociedad("P100");
                    ordenCompraNueva.setSedeSolicitud(parametro!=null ? parametro.getValor() : "");
                    ordenCompraNueva = ordenCompraNueva;//this.ordenCompraRepository.save(ordenCompraNueva);
                    ordenCompraNueva.setId(1);

                    try {
                        if(ListOCDetalleFinalSociedad.size() > 0 ){
                            //OrdenCompraResponseDto ordenCompraResponseDtoFinal = jcoOrdenCompraService.grabarOrdenCompraSAP(proveedor, ordenCompraNueva, ListOCDetalleFinalSociedad, licitacionId);
                            // Convertir el DTO a JSON
                            String purchaseOrderJsonString = ordenCompraSapService.setearOrdenCompra(proveedor, ordenCompraNueva, ListOCDetalleFinalSociedad, licitacionId);

                            OrdenCompraResponseDto ordenCompraResponseDtoFinal = ordenCompraSapService.crearOrdenCompra(purchaseOrderJsonString);
//                            OrdenCompraResponseDto ordenCompraResponseDtoFinal = new OrdenCompraResponseDto();
//                            ordenCompraResponseDtoFinal.setNumeroOrdenCompra("111111111119");
                            if(ordenCompraResponseDtoFinal.getMessageSap() != null){
                                ordenCompraNueva.setNumeroOrdenCompra(ordenCompraResponseDtoFinal.getMessageSap());
                                try {
                                    //No hacer nada
                                    //ordenCompraRepository.deleteById(ordenCompraNueva.getId());
                                }catch (Exception ex){
                                    ordenCompraNueva.setNumeroOrdenCompra(ex.getMessage());
                                    ex.printStackTrace();
                                }
                            } else if(ordenCompraResponseDtoFinal.getNumeroOrdenCompra() != null){
                                /*ordenCompraNueva.setEstadoSap("1");*/
                                ordenCompraNueva.setIsActive("1");
                                ordenCompraNueva.setNumeroOrdenCompra(ordenCompraResponseDtoFinal.getNumeroOrdenCompra());
                                ordenCompraNueva.setEstadoSap("05");
                                Indicador indicador = indicadorRepository.getIndicadorByCodigoTipoValorIndicador(ListOCDetalleFinalSociedad.get(0).getIndicadorImpuesto());
                                ordenCompraNueva.setIndicadorImpuesto(indicador.getDescripcionIndicador());


                                BigDecimal totalOc = new BigDecimal(0);
                                Integer contadorTipoS = 0;
                                Integer contadorTipoM = 0;
                                if(ListOCDetalleFinalSociedad.size() > 0){
                                    for (int i = 0; i < ListOCDetalleFinalSociedad.size(); i++) {
                                        OrdenCompraDetalle ordenCompraDetallex = ListOCDetalleFinalSociedad.get(i);
                                        totalOc  = totalOc.add(ordenCompraDetallex.getPrecioTotal());

                                        if(ordenCompraDetallex.getTipoPosicion().equals("S")){
                                            contadorTipoS++;
                                        }

                                        if(ordenCompraDetallex.getTipoPosicion().equals("M")){
                                            contadorTipoM++;
                                        }

                                    }
                                }
                                if(contadorTipoM > 0 && contadorTipoS > 0){
                                    ordenCompraNueva.setIdTipoOrdenCompra(3); //los dos s/m
                                }else if(contadorTipoS != 0){
                                    ordenCompraNueva.setIdTipoOrdenCompra(2); //servicio
                                }else if(contadorTipoM != 0){
                                    ordenCompraNueva.setIdTipoOrdenCompra(1); //maTERIAL
                                }
                                ordenCompraNueva.setSubtotal(totalOc);
                                BigDecimal porcentajeImpuesto = indicador.getValorIntermedio();
                                ordenCompraNueva.setValorImpuesto((totalOc.multiply(porcentajeImpuesto)).divide(new BigDecimal(100)));
                                ordenCompraNueva.setTotal(totalOc.add((totalOc.multiply(porcentajeImpuesto)).divide(new BigDecimal(100))));
                                ordenCompraNueva.setCondicionPago(ListOCDetalleFinalSociedad.get(0).getCondicionPago());
                                CondicionPago condicionPago = condicionPagoReposity.getById(Integer.parseInt(ListOCDetalleFinalSociedad.get(0).getCondicionPago()));
                                if(condicionPago !=null){
                                    ordenCompraNueva.setCondicionPagoDescripcion(condicionPago.getDescripcion());
                                    ordenCompraNueva.setCondicionPago(condicionPago.getCodigoSap());
                                }
                                ordenCompraNueva.setFechaInicioContrato(ListOCDetalleFinalSociedad.get(0).getFechaInicioContrato());
                                ordenCompraNueva.setFechaFinContrato(ListOCDetalleFinalSociedad.get(0).getFechaFinContrato());
                                ordenCompraNueva.setTipoExcepcion(ListOCDetalleFinalSociedad.get(0).getTipoExcepcion());
                                AtomicInteger i = new AtomicInteger();
                                OrdenCompra finalOrdenCompraNueva = ordenCompraNueva;
                                ListOCDetalleFinalSociedad.forEach(ocD->{

                                    Integer contador = new Integer(10 * (i.getAndIncrement() + 1));
                                    String scontador = StringUtils.leftPad(contador.toString().trim(), 5, '0');
                                    String PO_ITEM = String.valueOf(scontador);
                                    ocD.setPosicionOc(PO_ITEM);
                                    ocD.setCantidadOriginal(ocD.getCantidad());
                                    ocD.setPrecioTotalOriginal(ocD.getPrecioTotal());
                                    ocD.setNumeroOrdenCompra(ordenCompraResponseDtoFinal.getNumeroOrdenCompra());
                                    ocD.setIdOrdenCompra(finalOrdenCompraNueva.getId());
                                    //this.ordenCompraDetalleRepository.save(ocD);
                                    //Actualizar licitacion Detalle
                                    LicitacionDetalle licitacionDetalleOne = licitacionDetalleRepository.getById(ocD.getIdLicitacionDetalle());
                                    licitacionDetalleOne.setIndGeneradaOc("X");
                                    licitacionDetalleOne.setNumeroOrdenCompra(ordenCompraResponseDtoFinal.getNumeroOrdenCompra());
                                    licitacionDetalleRepository.save(licitacionDetalleOne);
                                });
                                OrdenCompra ordenCompraAct = ordenCompraNueva;//this.ordenCompraRepository.save(ordenCompraNueva);
                                //lista.add(ordenCompraNueva);

                                //---------------
                                //// prueba
                                /*Enviando Correo*/
                                //tipo documento = pedido estadar
                                if (bean.getOrdenCompraDetalleList().get(0).getClaseDoc().equals("NB") && ordenCompraAct != null){
                                    //Usuario comprador_sap = usuarioRepository.findByEmail(bean.getEmailComprador());
                                    if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                        contactoPublicadaOCNotificacion.enviar(ordenCompraAct, null, comprador,bean.getOrdenCompraDetalleList() );
                                    //Proveedor proveedor = proveedorService.getProveedorByRuc(oc.getProveedorRuc());
                                    Usuario proveedorUsuario = null;
                                    if(proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()){
                                        proveedorUsuario = new Usuario();
                                        proveedorUsuario.setEmail(proveedor.getEmail());
                                        proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                    }
                                    else{
                                        List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompraAct.getProveedorRuc());
                                        if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                            proveedorUsuario = posibleProveedorList.get(0);
                                    }
                                    if(proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                                    {
                                        contactoPublicadaOCNotificacion.enviar(ordenCompraAct, proveedorUsuario, null,bean.getOrdenCompraDetalleList() );
                                    }
                                    ordenCompraNueva.setEstadoSolicitud("Enviado");
                                    //this.ordenCompraRepository.save(ordenCompraNueva);
                                }
                                //---------------
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ordenCompraNueva.setNumeroOrdenCompra(e.getMessage());
                        try {
                            // no hacer nada
                            //ordenCompraRepository.deleteById(ordenCompraNueva.getId());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    lista.add(ordenCompraNueva);
                }
            });
            if(ocompraX.getNumeroOrdenCompra() !=""){
                lista.add(ocompraX);
            }
        });
        SolicitudPedido solped = new SolicitudPedido();
//            Integer codigoSolicitud = 0;
//            String posicion ="";
//            /*Enviamos a SAP*/
//            try {
//
//                if(ListOCDetalleFinal.size() > 0){
//                    Optional<LicitacionDetalle> licitacionDetalle = this.licitacionDetalleRepository.findById(ListOCDetalleFinal.get(0).getIdLicitacionDetalle());
//                    Integer idlicitacion = licitacionDetalle.get().getLicitacion().getIdLicitacion();
//                    OrdenCompraResponseDto ordenCompraResponseDto =
//                            jcoOrdenCompraService.grabarOrdenCompraSAP(proveedor, ocompraX, ListOCDetalleFinal, idlicitacion);
//
//                    //ocompraX.setNumeroOrdenCompra(ordenCompraResponseDto.getNumeroOrdenCompra());
//                    if(ordenCompraResponseDto.getMessageSap() != null){
//                        ocompraX.setNumeroOrdenCompra(ordenCompraResponseDto.getMessageSap());
//                        try {
//                            ordenCompraRepository.deleteById(ocompraX.getId());
//                        }catch (Exception ex){
//                            ex.printStackTrace();
//                        }
//
//                    } else if(ordenCompraResponseDto.getNumeroOrdenCompra() != null){
//                        ocompraX.setEstadoSap("L");
//                        ocompraX.setNumeroOrdenCompra(ordenCompraResponseDto.getNumeroOrdenCompra());
//                        BigDecimal totalOc = new BigDecimal(0);
//                        Integer contadorTipoS = 0;
//                        Integer contadorTipoM = 0;
//                        if(ListOCDetalleFinal.size() > 0){
//                            for (int i = 0; i < ListOCDetalleFinal.size(); i++) {
//                                OrdenCompraDetalle ordenCompraDetallex = ListOCDetalleFinal.get(i);
//                                totalOc  = totalOc.add(ordenCompraDetallex.getPrecioTotal());
//
//                                if(ordenCompraDetallex.getTipoPosicion().equals("S")){
//                                    contadorTipoS++;
//                                }
//
//                                if(ordenCompraDetallex.getTipoPosicion().equals("M")){
//                                    contadorTipoM++;
//                                }
//
//                            }
//                        }
//                        if(contadorTipoM > 0 && contadorTipoS > 0){
//                            ocompraX.setIdTipoOrdenCompra(3); //los dos s/m
//                        }else if(contadorTipoS != 0){
//                            ocompraX.setIdTipoOrdenCompra(2); //servicio
//                        }else if(contadorTipoM != 0){
//                            ocompraX.setIdTipoOrdenCompra(1); //maTERIAL
//                        }
//                        ocompraX.setTotal(totalOc);
//                        ocompraX.setCondicionPago(ListOCDetalleFinal.get(0).getCondicionPago());
//                        CondicionPago condicionPago = condicionPagoReposity.getById(Integer.parseInt(ListOCDetalleFinal.get(0).getCondicionPago()));
//                        if(condicionPago !=null){
//                            ocompraX.setCondicionPagoDescripcion(condicionPago.getDescripcion());
//                        }
//                        ocompraX.setFechaInicioContrato(ListOCDetalleFinal.get(0).getFechaInicioContrato());
//                        ocompraX.setFechaFinContrato(ListOCDetalleFinal.get(0).getFechaFinContrato());
//                        ocompraX.setTipoExcepcion(ListOCDetalleFinal.get(0).getTipoExcepcion());
//                        AtomicInteger i = new AtomicInteger();
//                        ListOCDetalleFinal.forEach(ocD->{
//
//                            Integer contador = new Integer(10 * (i.getAndIncrement() + 1));
//                            String scontador = StringUtils.leftPad(contador.toString().trim(), 5, '0');
//                            String PO_ITEM = String.valueOf(scontador);
//                            ocD.setPosicionOc(PO_ITEM);
//                            ocD.setCantidadOriginal(ocD.getCantidad());
//                            ocD.setPrecioTotalOriginal(ocD.getPrecioTotal());
//                            ocD.setNumeroOrdenCompra(ordenCompraResponseDto.getNumeroOrdenCompra());
//                            this.ordenCompraDetalleRepository.save(ocD);
//                            //Actualizar licitacion Detalle
//                            LicitacionDetalle licitacionDetalleOne = licitacionDetalleRepository.getById(ocD.getIdLicitacionDetalle());
//                            licitacionDetalleOne.setIndGeneradaOc("X");
//                            licitacionDetalleRepository.save(licitacionDetalleOne);
//                        });
//                        this.ordenCompraRepository.save(ocompraX);
//
//                        //Licitacion licitacion = licitacionRepository.getById(idlicitacion);
//                        //licitacion.setEstadoLicitacion("ES");
//                        //this.licitacionRepository.save(licitacion);
//                    }
//                }else {
//                    try {
//                        ordenCompraRepository.deleteById(ocompraX.getId());
//                    }catch (Exception ex){
//                        ex.printStackTrace();
//                    }
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new PortalException(e.getMessage());
//            }
//            lista.add(ocompraX);
//        });


        //validar items de la oc
        Licitacion licitacion = licitacionRepository.getById(licitacionId);
        List<LicitacionDetalle> licitacionDetalles = licitacionDetalleRepository.findByLicitacion(licitacion);
        AtomicInteger contadorOc = new AtomicInteger();
        if(licitacionDetalles.size() > 0){
            licitacionDetalles.forEach(item->{
                if(item.getIndGeneradaOc() == null){
                    contadorOc.set(1);
                }
            });
            if(contadorOc.get() == 0){
                licitacion.setEstadoLicitacion("AD");//adjudicada final
                this.licitacionRepository.save(licitacion);
            }
        }

        //retornar mensajes de la lista
        List<ProveedoresFinalDto> rrdenCompraRespDto = new ArrayList<>();
        bean.getProveedores().forEach(proveedorFinal ->{
            ProveedoresFinalDto finalDto = new ProveedoresFinalDto();
            Proveedor proveedor = proveedorRepository.getById(Integer.parseInt(proveedorFinal.getIdProveedor()));
            finalDto.setNombreProveedor(proveedor.getRazonSocial());
            finalDto.setRuc(proveedor.getRuc());

            //consultar ordenes de compra
            List<OrdenCompra> ordenCompraList = new ArrayList<>();
            lista.forEach(ordenFinal ->{
                if(ordenFinal.getProveedorRuc().equals(proveedor.getRuc())){
                    //agregar ordenes por proveedor
                    ordenCompraList.add(ordenFinal);
                }
            });
            finalDto.setOrdenesList(ordenCompraList);
            rrdenCompraRespDto.add(finalDto);
        });


        return rrdenCompraRespDto;
    }

    public List<ZPE_MM_GENERAR_OC> getOrdenCompraTramaJSON() throws Exception {
        List<ZPE_MM_GENERAR_OC> zpe_mm_generar_ocList = new ArrayList<>();

        List<OrdenCompra> ordenCompraList = ordenCompraRepository.getOrdenCompraSinNumeroDeOrden();

        ordenCompraList.forEach(ordenCompra -> {
            List<OrdenCompraDetalle> ordenCompraDetalleList = ordenCompraDetalleRepository.getAllByIdOrdenCompra(ordenCompra.getId());

            if (!ordenCompraDetalleList.isEmpty()) {
                Proveedor proveedor = proveedorRepository.getProveedorByRuc(ordenCompra.getProveedorRuc());

                ZPE_MM_GENERAR_OC zpe_mm_generar_oc = new ZPE_MM_GENERAR_OC();

                I_POHEADER i_poheader = new I_POHEADER();

                String COMP_CODE = "0021";
                String DOC_TYPE = "ZC01";
                String STATUS = "9";
                String CREATED_BY = "QA_CSTI";
                String ITEM_INTVL = "00010";
                String VENDOR = proveedor.getAcreedorCodigoSap();
                String LANGU = "S";
                String PMNTTRMS = "C090";
                String PURCH_ORG = "0010";
                String PUR_GROUP = proveedor.getCodigoGrupoCompra();
                String CURRENCY = proveedor.getMoneda().getCodigoMoneda();
                String COLLECT_NO = "DESCENTRAL";

                i_poheader.setCOMP_CODE(COMP_CODE);
                i_poheader.setDOC_TYPE(DOC_TYPE);
                i_poheader.setSTATUS(STATUS);
                i_poheader.setCREATED_BY(CREATED_BY);
                i_poheader.setITEM_INTVL(ITEM_INTVL);
                i_poheader.setVENDOR(VENDOR);
                i_poheader.setLANGU(LANGU);
                i_poheader.setPMNTTRMS(PMNTTRMS);
                i_poheader.setPURCH_ORG(PURCH_ORG);
                i_poheader.setPUR_GROUP(PUR_GROUP);
                i_poheader.setCURRENCY(CURRENCY);
                i_poheader.setCOLLECT_NO(COLLECT_NO);

                zpe_mm_generar_oc.setI_POHEADER(i_poheader);

                I_POHEADERX i_poheaderx = new I_POHEADERX();

                zpe_mm_generar_oc.setI_POHEADERX(i_poheaderx);

                List<T_POACCOUNT> t_poaccountsList = new ArrayList<>();
                List<T_POITEM> t_poitemList = new ArrayList<>();
                Integer idLicitacion = ordenCompraRepository.getIdLicitacionByOrdenCompra(ordenCompra.getId());
                List<CotizacionDetalle> cotizacionDetalles = new ArrayList<>();
                if (idLicitacion != null) {
                    Licitacion licitacion = licitacionRepository.findById(idLicitacion).get();
                    cotizacionDetalles = cotizacionDetalleRepository.findByCotizacion(licitacion, proveedor);
                }
                AtomicInteger i = new AtomicInteger(0);
                List<CotizacionDetalle> finalCotizacionDetalles = cotizacionDetalles;
                ordenCompraDetalleList.forEach(ordenCompraDetalle -> {
                    Integer contador = new Integer(10 * (i.get() + 1));
                    String sScontador = StringUtils.leftPad(contador.toString().trim(), 5, '0');
                    String SERIAL_NO = "01";
                    String QUANTITY = "1000";
                    String NET_VALUE = "1000.0";
                    String GL_ACCOUNT = "0659610073";
                    String CO_AREA = "GC00";
                    String PROFIT_CTR = "21O142100D";
                    String WBS_ELEMENT = "21AF.1151.61.0001.A.03";
                    String CMMT_ITEM = "ZINVA";
                    String FUNC_AREA = "INV";


                    T_POACCOUNT t_poaccount = new T_POACCOUNT();
                    t_poaccount.setPO_ITEM(sScontador);
                    t_poaccount.setSERIAL_NO(SERIAL_NO);
                    t_poaccount.setQUANTITY(QUANTITY);
                    t_poaccount.setNET_VALUE(NET_VALUE);
                    t_poaccount.setGL_ACCOUNT(GL_ACCOUNT);
                    t_poaccount.setCO_AREA(CO_AREA);
                    t_poaccount.setPROFIT_CTR(PROFIT_CTR);
                    t_poaccount.setWBS_ELEMENT(WBS_ELEMENT);
                    t_poaccount.setCMMT_ITEM(CMMT_ITEM);
                    t_poaccount.setFUNC_AREA(FUNC_AREA);

                    t_poaccountsList.add(t_poaccount);

                    if (!finalCotizacionDetalles.isEmpty()) {
                        String PO_ITEM = String.valueOf(sScontador);
                        String SHORT_TEXT = ordenCompraDetalle.getDescripcionBienServicio();
                        String EMATERIAL = finalCotizacionDetalles.get(i.get()).getBienServicio().getCodigoSap();
                        String PLANT = ordenCompraDetalle.getCodigoSapCentro();
                        String MATL_GROUP = finalCotizacionDetalles.get(i.get()).getBienServicio().getRubroBien().getCodigoSap();
                        String QUANTITYY = String.valueOf(ordenCompraDetalle.getCantidad());
                        String PO_UNIT = ordenCompraDetalle.getUnidadMedidaBienServicio(); /*Aqui puede dar error*/
                        String CONV_NUM1 = "1";
                        String CONV_DEN1 = "1";
                        String NET_PRICE = String.valueOf(ordenCompraDetalle.getPrecioTotal());
                        String TAX_CODE = ordenCompraDetalle.getIndicadorImpuesto();
                        String PRNT_PRICE = "X";
                        String UNLIMITED_DLV = "X";

                        String ITEM_CAT = "9";
                        String ACCTASSCAT = "F";
                        String GR_IND = "X";
                        String IR_IND = "X";
                        String GR_BASEDIV = "X";

                        String PREQ_NO = String.valueOf(ordenCompraDetalle.getOpSolicitudCompra()); //!!
                        String PREQ_ITEM = ordenCompraDetalle.getPosicion(); //!!

                        String PREQ_NAME = "QA_CSTI";
                        String PERIOD_IND_EXPIRATION_DATE = "";

                        T_POITEM t_poitem = new T_POITEM();
                        t_poitem.setPO_ITEM(PO_ITEM);
                        t_poitem.setSHORT_TEXT(SHORT_TEXT);
                        t_poitem.setEMATERIAL(EMATERIAL);
                        t_poitem.setPLANT(PLANT);
                        t_poitem.setMATL_GROUP(MATL_GROUP);
                        t_poitem.setQUANTITY(QUANTITYY);
                        t_poitem.setPO_UNIT(PO_UNIT);
                        t_poitem.setCONV_NUM1(CONV_NUM1);
                        t_poitem.setCONV_DEN1(CONV_DEN1);
                        t_poitem.setNET_PRICE(NET_PRICE);
                        t_poitem.setTAX_CODE(TAX_CODE);
                        t_poitem.setPRNT_PRICE(PRNT_PRICE);
                        t_poitem.setUNLIMITED_DLV(UNLIMITED_DLV);
                        t_poitem.setITEM_CAT(ITEM_CAT);
                        t_poitem.setACCTASSCAT(ACCTASSCAT);
                        t_poitem.setGR_IND(GR_IND);
                        t_poitem.setIR_IND(IR_IND);
                        t_poitem.setGR_BASEDIV(GR_BASEDIV);
                        t_poitem.setPREQ_NO(PREQ_NO);
                        t_poitem.setPREQ_ITEM(PREQ_ITEM);
                        t_poitem.setPREQ_NAME(PREQ_NAME);
                        t_poitem.setPERIOD_IND_EXPIRATION_DATE(PERIOD_IND_EXPIRATION_DATE);

                        t_poitemList.add(t_poitem);
                    }
                    i.getAndIncrement();
                });

                zpe_mm_generar_oc.setT_POACCOUNT(t_poaccountsList);
                zpe_mm_generar_oc.setT_POITEM(t_poitemList);

                zpe_mm_generar_oc.setT_POACCOUNTX("");
                zpe_mm_generar_oc.setT_POITEMX("");
                zpe_mm_generar_oc.setT_POSCHEDULE("");
                zpe_mm_generar_oc.setT_POSCHEDULEX("");
                zpe_mm_generar_oc.setT_POSERVICES("");

                List<T_POSRVACCESSVALUES> t_posrvaccessvaluesList = new ArrayList<>();
                T_POSRVACCESSVALUES t_posrvaccessvalues = new T_POSRVACCESSVALUES();
                t_posrvaccessvaluesList.add(t_posrvaccessvalues);
                zpe_mm_generar_oc.setT_POSRVACCESSVALUES(t_posrvaccessvaluesList);

                List<T_RETURN> t_returnList = new ArrayList<>();
                T_RETURN t_return = new T_RETURN();
                t_returnList.add(t_return);
                zpe_mm_generar_oc.setT_RETURN(t_returnList);

                zpe_mm_generar_ocList.add(zpe_mm_generar_oc);
            }
        });

        return zpe_mm_generar_ocList;
    }

    public OrdenCompraRequestRPA getOrdenComprasRPA() throws Exception{

        OrdenCompraRequestRPA ordenCompraRequestRPAS = new OrdenCompraRequestRPA();

        List<OrdenCompraRPA> ordenCompraRPAList = new ArrayList<>();

        OrdenCompraRPA ordenCompraRPA = new OrdenCompraRPA();
        ordenCompraRPA.setTexto("H|0021|ZC01|9|QA_CSTI|00010|0001007026|S|C090|0010|F31|PEN|DESCENTRAL\u2028I|00010|SRV OBRA||000000001100000416|6835|7214|1.000|SRV|169215.660000000|C3|X|X|9|P|X|X|X||1000038704|00010||\u2028A|00010|01|1.0|169215.66|0659610073|GC00|21O142100D|21AF.1151.61.0001.A.03|ZINVA|INV");
        ordenCompraRPA.setIdentificador("1");
        ordenCompraRPAList.add(ordenCompraRPA);

        OrdenCompraRPA ordenCompraRPA2 = new OrdenCompraRPA();
        ordenCompraRPA2.setTexto("H|0019|ZC01|9|QA_CSTI|00010|0001008652|S||0010|F31|PEN|DESCENTRAL\u2028I|00010|BANDEJA FIJA SOPORTA 60 KG.-|000000000820000964|000000000820000964|6801|3116|1.000|UND|165.380000000|||||||||ZINVG|1000038580|00080|00080|\u2028A|00010|01|1.0|16538.000000000|0659610003|GC00|10U1000DIN||ZINVG|INV");
        ordenCompraRPA2.setIdentificador("1");
        ordenCompraRPAList.add(ordenCompraRPA2);

        ordenCompraRequestRPAS.setOrdenCompraRPAList(ordenCompraRPAList);

        return ordenCompraRequestRPAS;
    }

    public OrdenCompraResponseRPA setOrdenComprasRPA(OrdenCompraRequestRespuestaRPA ordenCompraRequestRespuestaRPA) throws Exception{

        OrdenCompraResponseRPA ordenCompraRequestRPAS = new OrdenCompraResponseRPA();
        ordenCompraRequestRPAS.setMensaje("00");
        ordenCompraRequestRPAS.setCodigoRespuesta("Proceso correcto");

        return ordenCompraRequestRPAS;
    }

    public CompletableFuture<List<OrdenCompraMasivoDto>> readExcel(MultipartFile file, String email) {
        String emailBtp = email;

        List<OrdenCompraMasivoDto> validOrdenCompraMasivoDtoList = new ArrayList<>(); // Lista para DTOs que pasen la validación
        List<String> validationErrorLogs = new ArrayList<>(); // **Lista central para acumular TODOS los errores de validación del Excel**
        String uuidRegister = UUID.randomUUID().toString();
        List<String> mensajes = new ArrayList<>();


        //MAPAS PARA CONSISTENCIA POR IDENTIFICADOR OC ---
        Map<String, String> identificadorOcToMonedaMap = new HashMap<>();
        Map<String, String> identificadorOcToAreaSolicitanteMap = new HashMap<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next(); // Consume la fila del encabezado.
            }

            int rowNum = 1;
            List<String> currentRowErrors = new ArrayList<>(); // Errores específicos para la fila ACTUAL
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                rowNum++; // Incrementa para la fila actual de datos

                if (isRowEmpty(currentRow)) {
                    continue; // Omite filas completamente vacías
                }

                OrdenCompraMasivoDto ordenCompraMasivoDto = new OrdenCompraMasivoDto();


                // --- Validaciones campo por campo ---
                // IdentificadorOc (Columna 0)
                String identificadorOc = getCellValueAsString(currentRow.getCell(0));
                if (isValid(identificadorOc)) {
                    ordenCompraMasivoDto.setIdentificadorOc(identificadorOc);
                } else {
                    currentRowErrors.add("Identificador OC (columna A) está vacío o es inválido.");
                }

                // AcreedorCodigoSap (Columna 1)
                String acreedorCodigoSap = getCellValueAsString(currentRow.getCell(1));
                Proveedor proveedor = isValidAcreedorSap(acreedorCodigoSap);

                if (isValid(acreedorCodigoSap) && proveedor != null) {
                    ordenCompraMasivoDto.setAcreedorCodigoSap(acreedorCodigoSap);
                } else {
                    currentRowErrors.add("Acreedor Código SAP (columna B) está vacío o es inválido.");
                }

                // CodigoComprador (Columna 2)
                String codigoComprador = getCellValueAsString(currentRow.getCell(2)) != null?getCellValueAsString(currentRow.getCell(2)) : "";

                ordenCompraMasivoDto.setCodigoComprador(codigoComprador);


                // Moneda (Columna 3)
                String moneda = getCellValueAsString(currentRow.getCell(3));
                if (isValid(moneda)) {
                    // --- NUEVA LÓGICA DE VALIDACIÓN: CONSISTENCIA DE MONEDA ---
                    if (identificadorOcToMonedaMap.containsKey(identificadorOc)) {
                        String monedaEsperada = identificadorOcToMonedaMap.get(identificadorOc);
                        if (!moneda.equals(monedaEsperada)) {
                            currentRowErrors.add("Moneda (columna D) inconsistente.");
                        }
                    } else {
                        // Es la primera vez que vemos este identificador OC, registramos su moneda
                        identificadorOcToMonedaMap.put(identificadorOc, moneda);
                    }
                    // --- FIN NUEVA LÓGICA ---
                    ordenCompraMasivoDto.setMoneda(moneda);
                } else {
                    currentRowErrors.add("Moneda (columna D) está vacía o es inválida.");
                }

                // NroSolped (Columna 8)
                String nroSolped = getCellValueAsString(currentRow.getCell(8));
                String solpedConverter = String.format("%010d",Integer.valueOf(nroSolped));

                SolicitudPedidoRFCResponseDto solped = isValidSolped(solpedConverter);
                String statusResponseSolped = solped.getSapLog().getCode();
                if (isValid(nroSolped) && statusResponseSolped  != "400") {
                    ordenCompraMasivoDto.setNroSolped(nroSolped);
                    String posicionSolpedStr = getCellValueAsString(currentRow.getCell(9));
                    Integer enteroPosicionSolped = Integer.valueOf(posicionSolpedStr);
                    String solpedItemFormateado = String.format("%05d", enteroPosicionSolped);
                    Optional<SolicitudPedido> solpedLinea =  solped.getListaSolped().stream().filter(posicionSolped -> posicionSolped.getPosicion().equals(solpedItemFormateado)).findFirst();
                    // PosicionSolped (Columna 9)
                    if (isValid(posicionSolpedStr) && solpedLinea.isPresent()) {
                        try {

                            ordenCompraMasivoDto.setPosicionSolped(solpedItemFormateado);
                            // CodigoItem (Columna 4)
                            String codigoItem = getCellValueAsString(currentRow.getCell(4))!= null? getCellValueAsString(currentRow.getCell(4)):solpedLinea.get().getBienServicio().getCodigoSap() ;
                            if (isValid(codigoItem)) {
                                ordenCompraMasivoDto.setCodigoItem(codigoItem);
                            } else {
                                currentRowErrors.add("Código Ítem (columna E) está vacío o es inválido.");
                            }

                            // Cantidad (Columna 5)
                            BigDecimal cantidad = getCellValueAsBigDecimal(currentRow.getCell(5)) != null? getCellValueAsBigDecimal(currentRow.getCell(5)): solpedLinea.get().getCantidad() ;
                            if (cantidad != null) {
                                ordenCompraMasivoDto.setCantidad(cantidad.setScale(2, RoundingMode.HALF_UP));
                            } else {
                                currentRowErrors.add("Cantidad (columna F) está vacía o no es un número válido.");
                            }
                            //Unidad medida
                            ordenCompraMasivoDto.setUnidadMedida(solpedLinea.get().getSapUnidadMedida() != null ? solpedLinea.get().getSapUnidadMedida() : "EA");

                            // Centro (Columna 6)
                            String centro = getCellValueAsString(currentRow.getCell(6)) != null ? getCellValueAsString(currentRow.getCell(6)): solpedLinea.get().getSapCentro();
                            if (isValid(centro)) {
                                ordenCompraMasivoDto.setCentro(centro);
                            } else {
                                currentRowErrors.add("Centro es inválido.");
                            }

                            // CostoUnitario (Columna 7)
                            BigDecimal costoUnitario = getCellValueAsBigDecimal(currentRow.getCell(7))!= null ?getCellValueAsBigDecimal(currentRow.getCell(7)): solpedLinea.get().getPrecio();
                            if (costoUnitario != null) {
                                ordenCompraMasivoDto.setCostoUnitario(formatBigDecimalToTwoDecimals(costoUnitario));
                            } else {
                                currentRowErrors.add("Costo Unitario (columna H) está vacío o no es un número válido.");
                            }

                        } catch (NumberFormatException e) {
                            currentRowErrors.add("Posición Solped (columna J) no es válido.");
                        }
                    } else {
                        currentRowErrors.add("Posición Solped (columna J) es inválida.");
                    }


                } else {
                    currentRowErrors.add("Nro Solped (columna I) está vacío o es inválido.");
                }

                // AreaSolicitante (Columna 10)
                String areaSolicitante =  getCellValueAsString(currentRow.getCell(10));
                if (isValid(areaSolicitante)) {
                    if (identificadorOcToAreaSolicitanteMap.containsKey(identificadorOc)) {
                        String areaEsperada = identificadorOcToAreaSolicitanteMap.get(identificadorOc);
                        if (!areaSolicitante.equals(areaEsperada)) {
                            currentRowErrors.add("Área Solicitante (columna K) inconsistente.");
                        }
                    } else {
                        // Si es la primera vez que vemos este identificador OC, registramos su área solicitante
                        identificadorOcToAreaSolicitanteMap.put(identificadorOc, areaSolicitante);
                    }
                    // --- FIN PUNTO EXACTO ---
                    ordenCompraMasivoDto.setAreaSolicitante(areaSolicitante);
                } else {
                    currentRowErrors.add("Área Solicitante (columna K) está vacía o es inválida.");
                }


                // GrupoCompras (Columna 11)
                String grupoCompras = getCellValueAsString(currentRow.getCell(11));
                if (isValid(grupoCompras)) {
                    ordenCompraMasivoDto.setGrupoCompras(grupoCompras);
                } else {
                    currentRowErrors.add("Grupo Compras (columna L) está vacío o es inválido.");
                }

                // ClaseDocCompras (Columna 12)
                String claseDocCompras = getCellValueAsString(currentRow.getCell(12));
                if (isValid(claseDocCompras)) {
                    if (isClaseDocComprasValida(claseDocCompras)) {
                        ordenCompraMasivoDto.setClaseDocCompras(claseDocCompras);
                    } else {
                        currentRowErrors.add("Clase Doc. Compras (columna M) tiene un valor inválido: " + claseDocCompras);
                    }
                } else {
                    currentRowErrors.add("Clase Doc. Compras (columna M) está vacía o es inválida.");
                }

                // OrganizacionCompras (Columna 13)
                String organizacionCompras = getCellValueAsString(currentRow.getCell(13));
                if (isValid(organizacionCompras)) {
                    ordenCompraMasivoDto.setOrganizacionCompras(organizacionCompras);
                } else {
                    currentRowErrors.add("Organización Compras (columna N) está vacía o es inválida.");
                }

                // Sociedad (Columna 14)
                String sociedad = getCellValueAsString(currentRow.getCell(14));
                if (isValid(sociedad) && isValidSociedad(sociedad)) {
                    ordenCompraMasivoDto.setSociedad(sociedad);
                } else {
                    currentRowErrors.add("Sociedad (columna O) está vacía o es inválida. Sociedades admitidas P100 y P200");
                }

                String fechaEntrega = getCellValueAsDate(currentRow.getCell(15));

                if(fechaEntrega != null && fechaEntrega != ""){
                    if ( isValidDate(fechaEntrega)) {
                        ordenCompraMasivoDto.setFechaEntrega(fechaEntrega);
                    } else {
                        currentRowErrors.add("Fecha de entrega (columna P) está es inválida. el formato debe ser yyyy-MM-dd");
                    }
                }else{
                    ordenCompraMasivoDto.setFechaEntrega("");
                }


                // IndicadorImpuesto (Columna 15)


/*                String indicadorImpuesto = getCellValueAsString(currentRow.getCell(15));
                if (isValid(indicadorImpuesto)) {
                    ordenCompraMasivoDto.setIndicadorImpuesto(indicadorImpuesto);
                } else {
                    currentRowErrors.add("Indicador Impuesto (columna P) está vacío o es inválido.");
                }*/

                // CondicionPago


/*                String condicionPago = proveedor != null?proveedor.getCondicionPago().getCodigoSap(): "";
                ordenCompraMasivoDto.setCondicionEntrega(condicionPago);*/

                // Si la fila actual NO tiene errores de validación, se añade a la lista de DTOs válidos
                if (currentRowErrors.isEmpty()) {
                    validOrdenCompraMasivoDtoList.add(ordenCompraMasivoDto);
                } else {
                    // Si la fila actual TIENE errores, se agregan al log general de errores de validación
                    // con el número de fila para facilitar la identificación.
                    int finalRowNum = rowNum;
                    currentRowErrors.forEach(error -> validationErrorLogs.add("Fila " + finalRowNum + ": " + error));
                    currentRowErrors.clear();
                }
            }

            workbook.close();
            LogTransaccion logTransaccion = new LogTransaccion();
            // --- Lógica de Decisión Final: ¿Hubo errores de validación en el Excel? ---
            if (!validationErrorLogs.isEmpty()) {
                // Si la lista de errores de validación NO está vacía, significa que hay errores en el Excel.
                // En este caso, NO se envía NADA a SAP. Se notifica al usuario los errores y se termina.
                contactoPublicadaOCNotificacion.enviarLogs(uuidRegister ,validationErrorLogs,email); // Envía UN SOLO CORREO con todos los errores del Excel
                logTransaccion.setRespuestaTexto(validationErrorLogs.toString());
                logTransaccion.setTipoTransaccion("Carga Masiva OC");
                logTransaccion.setLogUsuario(uuidRegister);
                logTransaccionRepository.save(logTransaccion);
                // Se lanza una excepción para que el controlador (o quien llame a este método) sepa que la operación falló.
                throw new RuntimeException("El archivo Excel contiene errores de validación. Por favor, revise el correo para más detalles.");

            } else if (validOrdenCompraMasivoDtoList.isEmpty()) {
                mensajes.add("Carga masiva de Órdenes de Compra - Archivo sin datos válidos");

                contactoPublicadaOCNotificacion.enviarLogs(uuidRegister, mensajes,email);
                logTransaccion.setRespuestaTexto("Carga masiva de Órdenes de Compra - Archivo sin datos válidos");
                logTransaccion.setTipoTransaccion("Carga Masiva OC");
                logTransaccion.setLogUsuario(uuidRegister);
                logTransaccionRepository.save(logTransaccion);
                // Si no hay errores de validación, pero la lista de DTOs válidos está vacía (ej. el archivo solo tenía encabezado o filas vacías).
                /*emailService.sendEmail(email, "Carga masiva de Órdenes de Compra - Archivo sin datos válidos",
                        "Estimado(a) usuario(a),\n\nSu archivo de carga masiva de Órdenes de Compra no contenía datos válidos para procesar. Por favor, revise el contenido del archivo.\n\nAtentamente,\nSu equipo de soporte.");*/
                return CompletableFuture.completedFuture(new ArrayList<>()); // Devuelve una lista vacía para indicar que no hubo DTOs válidos

            } else {
                // Si la lista de errores de validación ESTÁ vacía y hay DTOs válidos, procedemos con SAP.
                // Se envía la lista de DTOs (que ya pasaron las validaciones de Excel) a SAP.
                Map<String, Map<String, String>> sapResponses = creacionOcMasivoSAP.processAndSendAllOrdersToSap(validOrdenCompraMasivoDtoList);
                mensajes = new ArrayList<>();
                for (Map.Entry<String, Map<String, String>> entry : sapResponses.entrySet()) {
                    String identificadorOc = entry.getKey();
                    Map<String, String> detallesRespuesta = entry.getValue();

                    StringBuilder mensajeDetallado = new StringBuilder();
                    mensajeDetallado.append("OC: ").append(identificadorOc).append("-");

                    if (detallesRespuesta != null) {
                        String status = detallesRespuesta.get("status");
                            String responseBody = detallesRespuesta.get("responseBody");
                            String itemUpdateErrors = detallesRespuesta.get("itemUpdateErrors");


                        if ("1".equals(status)) {
                            // Éxito: responseBody contiene el número de OC de SAP
                            mensajeDetallado.append("Número OC SAP: ").append(responseBody != null ? responseBody : "N/A");
                            if (itemUpdateErrors != null && !itemUpdateErrors.isEmpty()) {
                                mensajeDetallado.append(". Fallos en actualización de ítems: ").append(itemUpdateErrors);
                            }

                        } else {
                            // Error: responseBody contiene el código/mensaje de error
                            mensajeDetallado.append("Error SAP: ").append(responseBody != null ? responseBody : "Detalle no disponible.");
                        }
                    } else {
                        mensajeDetallado.append("No se pudieron obtener los detalles de la respuesta de SAP.");
                    }
                    mensajes.add(mensajeDetallado.toString()); // Añade la cadena construida a la lista
                }


                logTransaccion.setTipoTransaccion("Carga masiva OC - SAP");
                logTransaccion.setRespuestaTexto(mensajes.toString());
                logTransaccion.setLogUsuario(uuidRegister);
                logTransaccionRepository.save(logTransaccion);
                contactoPublicadaOCNotificacion.enviarLogs(uuidRegister ,mensajes,email);

                // Envía un correo con el resultado de la integración con SAP (éxitos/fallos de SAP)
                //emailService.sendSapIntegrationResultEmail(email, sapResponses);

                // Retornar la lista de DTOs que se intentaron enviar a SAP
                return CompletableFuture.completedFuture(validOrdenCompraMasivoDtoList);
            }

        } catch (IOException e) {
            System.err.println("Error de E/S al procesar el archivo de Excel para el usuario '" + email + "': " + e.getMessage());
            //emailService.sendEmail(email, "Error al procesar archivo Excel", "Estimado(a) usuario(a),\n\nHubo un error de lectura/escritura al procesar su archivo Excel: " + e.getMessage() + ".\n\nPor favor, verifique que el archivo no esté corrupto o en uso e intente de nuevo.\n\nAtentamente,\nSu equipo de soporte.");
            LogTransaccion logTransaccion  = new LogTransaccion();
            logTransaccion.setTipoTransaccion("Carga masiva OC");
            logTransaccion.setRespuestaTexto(e.getMessage());
            logTransaccion.setLogUsuario(uuidRegister);
            logTransaccionRepository.save(logTransaccion);
            mensajes.add("Error de E/S al procesar el archivo de Excel para el usuario '" + email + "': " + e.getMessage());

            contactoPublicadaOCNotificacion.enviarLogs(uuidRegister ,mensajes,email);
            throw new RuntimeException("Error al procesar el archivo de Excel: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado durante el procesamiento del archivo de Excel para el usuario '" + email + "': " + e.getMessage());
           // emailService.sendEmail(email, "Error inesperado en carga masiva", "Estimado(a) usuario(a),\n\nSe produjo un error inesperado al procesar su archivo de carga masiva: " + e.getMessage() + ".\n\nPor favor, contacte a soporte técnico para asistencia.\n\nAtentamente,\nSu equipo de soporte.");
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }



    @Override
    public List<LogTransaccion> getLogsTrasaccion(String identificador) {
        List<LogTransaccion> logTransaccionList = logTransaccionRepository.findByLogUsuario(identificador);

        if (logTransaccionList.isEmpty()){
            throw new RuntimeException("El identificador no es válido o no existe");
        }

        return logTransaccionList;
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidDate(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(fecha);
            return true;
        }catch (ParseException e){
            return false;
        }catch (NullPointerException e) {

            return false;
        }

    }





    private Proveedor isValidAcreedorSap(String codigosap){

        Proveedor proveedor = proveedorRepository.getProveedorByAcreedorCodigoSap(codigosap);
        if (proveedor != null){
            return  proveedor;
        }else {
            return  null;
        }

    }
    private static final Set<String> CLASES_DOC_COMPRAS_VALIDAS = new HashSet<>(Arrays.asList(
            "NB", "ZPAD", "ZPBR", "ZPFR", "ZPHU", "ZPMA", "ZPPA",
            "ZPPD", "ZPSJ", "ZPTI", "ZPTR", "ZPYA", "ZPYU", "ZPSC","ZPHG"
    ));

    private boolean isClaseDocComprasValida(String value) {
        return CLASES_DOC_COMPRAS_VALIDAS.contains(value);
    }

    private boolean isValidSociedad(String sociedad){
        Sociedad sociedadEncontrada = sociedadRepository.getByCodigoSociedad(sociedad);
        if (sociedadEncontrada != null){
            return true;
        }else {
            return  false;
        }
    }
    private SolicitudPedidoRFCResponseDto isValidSolped(String solped) throws Exception {
        return this.jcoSolicitudPedidoService.getSolpedResponseByCodigo(solped);
    }

    // Métodos auxiliares para obtener valores de celdas de forma segura
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {

            return String.valueOf((long) cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return null;
        }
    }


    private static BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            try {

                return new BigDecimal(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {

                System.err.println("Advertencia: El valor '" + cell.getStringCellValue() + "' no es un número válido.");
                return null;
            }
        }
        return null;
    }
    private String getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }


        if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            try {
                Date date = cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(date);
            } catch (IllegalStateException e) {

                return null;
            }
        } else if (cellType == CellType.STRING) {

            String cellValue = cell.getStringCellValue().trim();

            return cellValue;
        }

        return null;
    }


    public static String formatBigDecimalToString(BigDecimal value) {
        if (value == null) {
            return null;
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US); // Use US locale for '.' as decimal separator

        DecimalFormat decimalFormat = new DecimalFormat("0.00", symbols);

        return decimalFormat.format(value);
    }
    public static BigDecimal formatBigDecimalToTwoDecimals(BigDecimal value) {
        if (value == null) {
            return null;
        }
        // Usa setScale para asegurar 2 decimales.
        // RoundingMode.HALF_UP es un modo de redondeo común (redondea hacia arriba si el decimal es >= 5)
        return value.setScale(2, RoundingMode.HALF_UP);
    }




    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.getStringCellValue().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}