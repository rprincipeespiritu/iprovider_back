package com.incloud.hcp.jco.documentoAceptacion.service.impl;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.enums.*;
import com.incloud.hcp.jco.documentoAceptacion.dto.SapTableItemDto;
import com.incloud.hcp.jco.documentoAceptacion.service.JCODocumentoAceptacionService;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPublicarOneService;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.service.BienServicioService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.notificacion.ContactoAprobadaRechazadaOCNotificacion;
import com.incloud.hcp.util.DateUtils;
//import com.sap.conn.jco.*;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.sql.Time;
import java.sql.Timestamp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCODocumentoAceptacionServiceImpl implements JCODocumentoAceptacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicBoolean daProcessing = new AtomicBoolean(false);

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Value("${API_URL_SAP}")
    private String urlSap;
    @Value("${USERNAME_SAP}")
    private String userSap;
    @Value("${PASSWORD_SAP}")
    private String passwordSap;

    private DocumentoAceptacionRepository documentoAceptacionRepository;
    private DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository;
    private OrdenCompraRepository ordenCompraRepository;
    private JCOOrdenCompraPublicarOneService jcoOrdenCompraPublicarOneService;
    private ContactoAprobadaRechazadaOCNotificacion contactoAprobadaRechazadaOCNotificacion;
    private UsuarioRepository usuarioRepository;
    private ProveedorService proveedorService;
    private ParametroMapper parametroMapper;
    private BienServicioService bienServicioService;

    @Autowired
    public JCODocumentoAceptacionServiceImpl(DocumentoAceptacionRepository documentoAceptacionRepository,
                                             DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository,
                                             OrdenCompraRepository ordenCompraRepository,
                                             JCOOrdenCompraPublicarOneService jcoOrdenCompraPublicarOneService,
                                             ContactoAprobadaRechazadaOCNotificacion contactoAprobadaRechazadaOCNotificacion,
                                             UsuarioRepository usuarioRepository,
                                             ProveedorService proveedorService,
                                             BienServicioService bienServicioService,
                                             ParametroMapper parametroMapper) {
        this.documentoAceptacionRepository = documentoAceptacionRepository;
        this.documentoAceptacionDetalleRepository = documentoAceptacionDetalleRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.jcoOrdenCompraPublicarOneService = jcoOrdenCompraPublicarOneService;
        this.contactoAprobadaRechazadaOCNotificacion = contactoAprobadaRechazadaOCNotificacion;
        this.usuarioRepository = usuarioRepository;
        this.proveedorService = proveedorService;
        this.parametroMapper = parametroMapper;
        this.bienServicioService = bienServicioService;
    }
    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Override
    public void extraerDocumentoAceptacionListRFC_old(String parametro1, String parametro2, boolean extraccionUnicoDocumento, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception {
        if(!daProcessing.get() || extraccionUnicoDocumento) {

            if(!daProcessing.get() && !extraccionUnicoDocumento)
                daProcessing.set(!daProcessing.get());

            try{
                String FUNCION_RFC = "ZPE_MM_ENTRADA_MERCADERIAS";
//I
                logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");
                String tramaXMLactualizar = this.armarTrama(parametro1, parametro2);
                /* Obteniendo los valores obtenidos del RFC */
                logger.error("02 - GET PROVEEDOR - FIN RFC");
                List<SapLog> listSapLog = new ArrayList<>();
                logger.info("RFC: DESTINATION - " + destinationProfit);
                Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

                logger.info(String.valueOf(destination2.get()));
                HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));


                logger.info(String.valueOf(client));
                String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
                Integer portx = 20003;

                String urlbase = String.valueOf(destination2.get().asHttp().getUri());
                String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_ENTRADA_MERCADERIAS?sap-client=400";

                logger.info("RFC: URL " + url);

                logger.info("RFC: Trama" + tramaXMLactualizar);



                final StringBuffer soap = new StringBuffer();
                soap.append("\n");
                soap.append("");
                // this is a sample data..you have create your own required data  BEGIN
                soap.append(" \n");
                soap.append(" \n");
                soap.append("" + tramaXMLactualizar);
                soap.append(" \n");
                soap.append(" \n");

                /* soap.append(body); */
                // END of MEssage Body
                soap.append("");

                HttpEntity strEntity = new StringEntity(tramaXMLactualizar, "text/xml", "UTF-8");

                logger.info(soap.toString());
                //HttpEntity strEntity = new StringEntity(soap.toString());
                //HttpEntity strEntity = new StringEntity(soap.toString(), ContentType.TEXT_XML);

                HttpPost post = new HttpPost(url);
                post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_ENTRADA_MERCADERIAS/ZMM_ENTRADA_MERCADERIASRequest");
                post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_ENTRADA_MERCADERIAS/ZMM_ENTRADA_MERCADERIASRequest");
                post.setHeader("Content-Type", "text/xml;charset=UTF-8");
                post.setHeader("Accept-Encoding", "gzip,deflate");

                post.setEntity(strEntity);

                logger.info("RFC: Trama Entity" + strEntity);

                logger.info("HTTP POS" + post.toString());

                HttpResponse response4 = client.execute(post);
                HttpEntity respEntity = response4.getEntity();
                String resultStr = EntityUtils.toString(respEntity);

                logger.info("RESULT" + resultStr);

                DocumentBuilderFactory domFactory = DocumentBuilderFactory
                        .newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder
                        .parse(new InputSource(new StringReader(resultStr)));

                //DocumentoAceptacionExtractorMapper documentoAceptacionExtractorMapper = DocumentoAceptacionExtractorMapper.newMapper(exportParameterList);
                DocumentoAceptacionExtractorMapper documentoAceptacionExtractorMapper = new DocumentoAceptacionExtractorMapper();

                List<SapTableItemDto> sapTableItemDtoList = documentoAceptacionExtractorMapper.getSapTableItemDtoList(doc);
                List<SapTableItemDto> sapTableItemDtoListTdPedido = documentoAceptacionExtractorMapper.getSapTableItemTdPedidoDtoList(doc);

                sapTableItemDtoList.addAll(sapTableItemDtoListTdPedido);

                List<String> movimientosIncluidos = Arrays.asList(MovimientoEntregaMercaderiaTipoEnum.LIBERACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.LIBERACION_2_PASO.getCodigo(),
                        MovimientoEntregaMercaderiaTipoEnum.ANULACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.ANULACION_2_PASO.getCodigo(), MovimientoEntregaMercaderiaTipoEnum.DEVOLUCIONES.getCodigo());
                List<String> movimientosDeAnulacion = Arrays.asList(MovimientoEntregaMercaderiaTipoEnum.ANULACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.ANULACION_2_PASO.getCodigo());

                if (extraccionUnicoDocumento){
                    sapTableItemDtoList = sapTableItemDtoList.stream()
                            .filter(item -> item.getNumeroDocumentoAceptacion().equals(parametro2))
                            .collect(Collectors.toList());
                }

                Map<String,List<SapTableItemDto>> entradaMercaderiaItemMap = sapTableItemDtoList.stream()
                        .filter(item -> movimientosIncluidos.contains(item.getMovimiento())) // solo items de EM con movimiento 101,102,105,106,122
                        .collect(Collectors.groupingBy(SapTableItemDto::getNumeroDocumentoAceptacion,Collectors.toList()));

                Map<String,List<SapTableItemDto>> hojaServicioItemMap = sapTableItemDtoList.stream()
                        .filter(item -> item.getMovimiento().equals("")) // solo items de HES (sin movimiento)
                        .collect(Collectors.groupingBy(SapTableItemDto::getNumeroDocumentoAceptacion,Collectors.toList()));

                Long cantidadEntregaMercaderiaExcluida = sapTableItemDtoList.stream()
                        .filter(item -> !movimientosIncluidos.contains(item.getMovimiento()) && !item.getMovimiento().equals("")) // solo items de EM con movimientos que no son 101,102,105,106, 122
                        .count();

                List<String> numEntradaMercaderiaPosibleAnuladaList = new ArrayList<>();

                String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR DA -- RANGO: " + parametro1 + " - " + parametro2 + " // ";
//                logger.error(header1 + "Rango de Fechas : " + fechaInicio + " - " + fechaFin);
                logger.error(header1 + "CANTIDAD DE EM ENCONTRADAS: " + entradaMercaderiaItemMap.size());
                logger.error(header1 + "CANTIDAD DE EM EXCLUIDAS: " + cantidadEntregaMercaderiaExcluida);
                logger.error(header1 + "CANTIDAD DE HES ENCONTRADAS: " + hojaServicioItemMap.size());

                if (extraccionUnicoDocumento){
                    for(int i=0 ; i < sapTableItemDtoList.size() ; i++){
                        logger.error(header1 + "SAP ITEM " + i + ": " + sapTableItemDtoList.get(i).toString());
                    }
                }

                entradaMercaderiaItemMap.forEach((numeroEntradaMercaderia, itemList) -> {
                    Integer idEntradaMercaderiaExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumero(numeroEntradaMercaderia);
                    if (idEntradaMercaderiaExistente == null) { // si no existe previamente la entrada de mercaderia
                        String header2 = header1.concat("EM: " + numeroEntradaMercaderia);
                        SapTableItemDto primerItem = itemList.get(0); // trae primer item para obtener datos comunes de la EM
                        boolean esEmDeAnulacion = movimientosDeAnulacion.contains(primerItem.getMovimiento());
                        boolean procedePublicar = true;
                        String numeroOrdenCompra = primerItem.getNumeroOrdenCompra();
                        DocumentoAceptacion entradaMercaderia = new DocumentoAceptacion();


                        if(esEmDeAnulacion){ // si es una EM de Anulacion, busca si ya este publicada la EM cuyas posiciones apunta a anular y que se encuentre en estado ACTIVO
                            Optional<DocumentoAceptacion> opDocAceptacionRelacionado = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(primerItem.getNumDocApectacionRelacionado());
                            if(!opDocAceptacionRelacionado.isPresent() || opDocAceptacionRelacionado.get().getIdEstadoDocumentoAceptacion().compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) != 0)
                                procedePublicar = false;
                        }

                        if(procedePublicar) {
                            entradaMercaderia.setNumeroDocumentoAceptacion(numeroEntradaMercaderia);

                            entradaMercaderia.setIdEstadoDocumentoAceptacion(this.asignarIdEstadoDocumentoAceptacion(primerItem.getMovimiento()));
                            entradaMercaderia.setNumeroOrdenCompra(numeroOrdenCompra);
                            entradaMercaderia.setNumeroGuiaProveedor(primerItem.getNumeroGuiaProveedor());
                            entradaMercaderia.setUsuarioSapRecepcion(primerItem.getUsuarioSapRecepcion());
                            entradaMercaderia.setCodigoMoneda(primerItem.getCodigoMoneda());
                            entradaMercaderia.setFechaEmision(primerItem.getFechaEmision());
                            entradaMercaderia.setValorImpuesto(primerItem.getValorImpuesto());
                            entradaMercaderia.setSociedad(primerItem.getSociedad());
                            entradaMercaderia.setNroGuiaRemision(primerItem.getGuiaRemision()); // mizalo

                            Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);
                            OrdenCompra ordenCompra = new OrdenCompra();

                            if (optionalOrdenCompra.isPresent()) {
                                ordenCompra = optionalOrdenCompra.get();
                                logger.error(header2 + " // OC ENCONTRADA: " + ordenCompra.toString());

                                // AQUI MECANICA QUE MODIFICA OC ENCONTRADA A ESTADO "APROBADA" (por proveedor) (SOLO SI EM tiene Mov 101,105)
                                entradaMercaderia = this.actualizarDocumentoAceptacionAndOrdenCompra("EM", entradaMercaderia, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);
                            } else {
                                if (!esEmDeAnulacion) { // trata de publicar su OC solo si no es una EM de anulacion
                                    try {
                                        jcoOrdenCompraPublicarOneService.extraerOneOrdenCompraRFC(numeroOrdenCompra, false);
                                        Optional<OrdenCompra> optionalOrdenCompraExtraida = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);

                                        if (optionalOrdenCompraExtraida.isPresent()) {
                                            ordenCompra = optionalOrdenCompraExtraida.get();
                                            logger.error(header2 + " // OC PUBLICADA: " + ordenCompra.toString());

                                            // AQUI MECANICA QUE MODIFICA OC PUBLICADA A ESTADO "APROBADA" (por proveedor) (SOLO SI EM tiene Mov 101,105)
                                            entradaMercaderia = this.actualizarDocumentoAceptacionAndOrdenCompra("EM", entradaMercaderia, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);
                                        } else {
                                            logger.error(header2 + " // NO SE PUBLICO OC: " + numeroOrdenCompra);
                                        }
                                    } catch (Exception e) {
                                        logger.error(header2 + " // ERROR AL EXTRAER OC: " + numeroOrdenCompra);
                                    }
                                }
                            }

                            if (entradaMercaderia.getIdOrdenCompra() != null) { // si es que la OC no ha sido publicada, no se publica la EM
                                entradaMercaderia.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                logger.error(header2 + " // WRITING NEW EM: " + entradaMercaderia.toString());
                                logger.error("idTipoDocAceptacion" + entradaMercaderia.getIdTipoDocumentoAceptacion());

                                if (primerItem.getMovimiento().contains("122")) {
                                    entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.DEVOLUCION_SALIDA.getId());
                                    logger.error("Entro 1");
                                } else {
                                    entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.ENTRADA_MERCADERIA.getId());
                                }
                                entradaMercaderia = documentoAceptacionRepository.save(entradaMercaderia);
                                Integer idEntradaMercaderia = entradaMercaderia.getId();

                                itemList.forEach(item -> {
                                    DocumentoAceptacionDetalle entradaMercaderiaDetalle = new DocumentoAceptacionDetalle();
                                    String movimiento = item.getMovimiento();

                                    entradaMercaderiaDetalle.setIdDocumentoAceptacion(idEntradaMercaderia);
                                    entradaMercaderiaDetalle.setIdEstadoDocumentoAceptacionDetalle(this.asignarIdEstadoDocumentoAceptacion(movimiento));
                                    entradaMercaderiaDetalle.setNumeroDocumentoAceptacion(numeroEntradaMercaderia);
                                    entradaMercaderiaDetalle.setNumeroItem(item.getNumeroItem());
                                    entradaMercaderiaDetalle.setNumeroOrdenCompra(item.getNumeroOrdenCompra());
                                    entradaMercaderiaDetalle.setPosicionOrdenCompra(item.getPosicionOrdenCompra());
                                    entradaMercaderiaDetalle.setCodigoSapBienServicio(item.getCodigoMaterial());
                                    entradaMercaderiaDetalle.setDescripcionBienServicio(!item.getDescripcionMaterial().isEmpty() ? item.getDescripcionMaterial() : item.getDescripcionServicio());
                                    entradaMercaderiaDetalle.setUnidadMedida(item.getUnidadMedidaMaterial());
                                    entradaMercaderiaDetalle.setCantidadAceptadaCliente(item.getCantidadAceptadaClienteMaterial());
                                    entradaMercaderiaDetalle.setCantidadPendiente(item.getCantidadPendiente());
                                    entradaMercaderiaDetalle.setValorRecibidoMonedalocal(item.getValorRecibidoMonedalocal());
                                    entradaMercaderiaDetalle.setPrecioUnitario(item.getPrecioUnitario());
                                    entradaMercaderiaDetalle.setValorRecibido(item.getValorRecibido());
                                    entradaMercaderiaDetalle.setMovimiento(movimiento);
                                    entradaMercaderiaDetalle.setIndicadorImpuesto(item.getIndicadorImpuesto());

                                    if (movimientosDeAnulacion.contains(primerItem.getMovimiento())) {
                                        String numEmPorAnular = item.getNumDocApectacionRelacionado();
                                        Integer numItemPorAnular = item.getNumItemRelacionado();
                                        String posicionOrdenCompra = item.getPosicionOrdenCompra();
                                        BigDecimal cantidadAceptadaCliente = item.getCantidadAceptadaClienteMaterial();

                                        entradaMercaderiaDetalle.setNumDocApectacionRelacionado(numEmPorAnular);
                                        entradaMercaderiaDetalle.setNumItemRelacionado(numItemPorAnular);

                                        logger.error(header2 + " // WRITING NEW EM POS (ANUL): " + entradaMercaderiaDetalle.toString());
                                        documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);
//                                    Optional<DocumentoAceptacionDetalle> optionalEntradaMercaderiaDetalleExistente = documentoAceptacionDetalleRepository.findByNumeroDocumentoAceptacionAndNumeroItem(numEmPorAnular, numItemPorAnular);
                                        Optional<DocumentoAceptacionDetalle> optionalEntradaMercaderiaDetalleExistente = documentoAceptacionDetalleRepository.findByNumeroDocumentoAceptacionAndPosicionOrdenCompraAndCantidadAceptadaCliente(numEmPorAnular, posicionOrdenCompra, cantidadAceptadaCliente);
                                        DocumentoAceptacionDetalle entradaMercaderiaDetalleExistente = new DocumentoAceptacionDetalle();

                                        if (optionalEntradaMercaderiaDetalleExistente.isPresent()) {
                                            entradaMercaderiaDetalleExistente = optionalEntradaMercaderiaDetalleExistente.get();
                                            logger.error(header2 + " // FOUND EM POS (POR ANULAR): " + entradaMercaderiaDetalleExistente.toString());
                                            Integer idEstadoItem = entradaMercaderiaDetalleExistente.getIdEstadoDocumentoAceptacionDetalle();

                                            if (idEstadoItem.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) == 0
                                                    || idEstadoItem.compareTo(DocumentoAceptacionEstadoEnum.TRANSITO.getId()) == 0) {
                                                entradaMercaderiaDetalleExistente.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());
                                                logger.error(header2 + " // ANULANDO EM POS: " + entradaMercaderiaDetalleExistente.toString());
                                                documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalleExistente);
                                                String numEntradaMercaderiaExistente = entradaMercaderiaDetalleExistente.getNumeroDocumentoAceptacion();

                                                if (!numEntradaMercaderiaPosibleAnuladaList.contains(numEntradaMercaderiaExistente))
                                                    numEntradaMercaderiaPosibleAnuladaList.add(numEntradaMercaderiaExistente);
                                            }
                                        } else {
                                            logger.error(header2 + " // FOUND EM POS (POR ANULAR): " + optionalEntradaMercaderiaDetalleExistente.toString());
                                        }
                                    } else {
                                        logger.error(header2 + " // WRITING NEW EM POS: " + entradaMercaderiaDetalle.toString());
                                        documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);
                                    }
                                });
                            }
                        }
                    }
                });

                numEntradaMercaderiaPosibleAnuladaList.forEach(num -> {
                    Optional<DocumentoAceptacion> optionalEntradaMercaderiaPosibleAnulada = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(num);
                    String header2 = header1.concat("EM: " + num);
                    if(optionalEntradaMercaderiaPosibleAnulada.isPresent()) {
                        boolean emAnulada = true;
                        DocumentoAceptacion entradaMercaderiaPosibleAnulada = optionalEntradaMercaderiaPosibleAnulada.get();
                        logger.error(header2 + " // EM POSIBLE A ANULAR: " + entradaMercaderiaPosibleAnulada.toString());
                        List<DocumentoAceptacionDetalle> emPosibleAnuladaPosicionList = Optional.ofNullable(entradaMercaderiaPosibleAnulada.getDocumentoAceptacionDetalleList()).orElse(new ArrayList<>());

                        if(emPosibleAnuladaPosicionList.isEmpty()) {
                            emPosibleAnuladaPosicionList.addAll(documentoAceptacionDetalleRepository.getDocumentoAceptacionDetalleListByIdDocumentoAceptacion(entradaMercaderiaPosibleAnulada.getId()));
                            logger.error(header2 + " // LISTA POSICIONES DE EM POSIBLE A ANULAR: " + emPosibleAnuladaPosicionList.toString());
                        }

                        for (DocumentoAceptacionDetalle posicion : emPosibleAnuladaPosicionList) {
                            if (posicion.getIdEstadoDocumentoAceptacionDetalle().compareTo(DocumentoAceptacionEstadoEnum.ANULADO.getId()) != 0) {
                                emAnulada = false;
                                break;
                            }
                        }

                        if (emAnulada && !emPosibleAnuladaPosicionList.isEmpty()) {
                            entradaMercaderiaPosibleAnulada.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());
                            logger.error(header2 + " // ANULANDO EM: " + entradaMercaderiaPosibleAnulada.toString());
                            documentoAceptacionRepository.save(entradaMercaderiaPosibleAnulada);
                        }
                        else{
                            logger.error(header2 + " // NO SE ANULO EM: " + entradaMercaderiaPosibleAnulada.toString());
                        }
                    }
                });

                hojaServicioItemMap.forEach((numeroHojaServicio, itemList) -> {
                    Integer idHojaServicioExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumero(numeroHojaServicio);

                    if(idHojaServicioExistente == null){ // si no existe previamente la hoja de entrada de servicio
                        SapTableItemDto unicoItem = itemList.get(0); // HES solo tiene un item (posicion)
                        String statusSap = unicoItem.getStatus();
                        if (statusSap != null && !statusSap.isEmpty()
                                && (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion())
                                || statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.BORRADO.getDescripcion()))) { // solo publica si statusSap existe
                            String header2 = header1.concat("HES: " + numeroHojaServicio);
                            String numeroOrdenCompra = unicoItem.getNumeroOrdenCompra();
                            String posicionOrdencompra = unicoItem.getPosicionOrdenCompra();
                            DocumentoAceptacion hojaServicio = new DocumentoAceptacion();

                            hojaServicio.setNumeroDocumentoAceptacion(numeroHojaServicio);
                            hojaServicio.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.HOJA_ENTRADA_SERVICIO.getId());
                            hojaServicio.setNumeroOrdenCompra(numeroOrdenCompra);
                            hojaServicio.setPosicionOrdenCompra(posicionOrdencompra);
                            hojaServicio.setUsuarioSapRecepcion(unicoItem.getUsuarioSapRecepcion());
                            hojaServicio.setCodigoMoneda(unicoItem.getCodigoMoneda());
                            hojaServicio.setFechaEmision(unicoItem.getFechaEmision());
                            hojaServicio.setFechaAceptacion(unicoItem.getFechaAceptacion());
                            hojaServicio.setValorImpuesto(unicoItem.getValorImpuesto());
                            hojaServicio.setSociedad(unicoItem.getSociedad());

                            hojaServicio.setStatusSap(statusSap);
                            if (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion()))
                                hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                            else // statusSap == "Borrado"
                                hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                            OrdenCompra ordenCompra = new OrdenCompra();
                            Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);

                            if (optionalOrdenCompra.isPresent()) {
                                ordenCompra = optionalOrdenCompra.get();
                                logger.error(header2 + " // OC ENCONTRADA: " + ordenCompra.toString());

                                // AQUI MECANICA QUE MODIFICA OC ENCONTRADA A ESTADO "APROBADA" (por proveedor) (SOLO SI HES SUBE ACEPTADA)
                                hojaServicio = this.actualizarDocumentoAceptacionAndOrdenCompra("HES", hojaServicio, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);

                                hojaServicio.setProveedorRuc(ordenCompra.getProveedorRuc());
                                hojaServicio.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());
                            } else {
                                try {
                                    jcoOrdenCompraPublicarOneService.extraerOneOrdenCompraRFC(numeroOrdenCompra,false);
                                    Optional<OrdenCompra> optionalOrdenCompraExtraida = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);

                                    if (optionalOrdenCompraExtraida.isPresent()) {
                                        ordenCompra = optionalOrdenCompraExtraida.get();
                                        logger.error(header2 + " // OC PUBLICADA: " + ordenCompra.toString());

                                        // AQUI MECANICA QUE MODIFICA OC PUBLICADA A ESTADO "APROBADA" (por proveedor) (SOLO SI HES SUBE ACEPTADA)
                                        hojaServicio = this.actualizarDocumentoAceptacionAndOrdenCompra("HES", hojaServicio, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);
                                    } else {
                                        logger.error(header2 + " // NO SE PUBLICO OC: " + numeroOrdenCompra);
                                    }
                                } catch (Exception e) {
                                    logger.error(header2 + " // ERROR AL EXTRAER OC: " + numeroOrdenCompra + " // " + e.getClass().getName() + " -- " + e.getMessage());
                                }
                            }

                            if(hojaServicio.getIdOrdenCompra() != null) { // si es que la OC no ha sido publicada, no se publica la nueva HES
                                hojaServicio.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                logger.error(header2 + " // WRITING NEW HES: " + hojaServicio.toString());
                                
                                hojaServicio = documentoAceptacionRepository.save(hojaServicio);

                                DocumentoAceptacionDetalle hojaServicioDetalle = new DocumentoAceptacionDetalle();

                                hojaServicioDetalle.setIdDocumentoAceptacion(hojaServicio.getId());
                                hojaServicioDetalle.setNumeroDocumentoAceptacion(numeroHojaServicio);
                                hojaServicioDetalle.setNumeroOrdenCompra(numeroOrdenCompra);
                                hojaServicioDetalle.setPosicionOrdenCompra(posicionOrdencompra);
                                hojaServicioDetalle.setCodigoSapBienServicio(unicoItem.getCodigoMaterial());
                                hojaServicioDetalle.setDescripcionBienServicio(unicoItem.getDescripcionServicio());
                                hojaServicioDetalle.setUnidadMedida(unicoItem.getUnidadMedidaServicio());
                                hojaServicioDetalle.setCantidadAceptadaCliente(unicoItem.getCantidadAceptadaClienteServicio());
                                hojaServicioDetalle.setPrecioUnitario(unicoItem.getPrecioUnitario());
                                hojaServicioDetalle.setValorRecibido(unicoItem.getValorRecibidoServicio());
                                hojaServicioDetalle.setValorRecibidoMonedalocal(unicoItem.getValorRecibidoMonedalocal());
                                hojaServicioDetalle.setIndicadorImpuesto(unicoItem.getIndicadorImpuesto());
                                hojaServicioDetalle.setNumeroItem(unicoItem.getNumeroItem());

                                Integer idEstadoHojaServicio = hojaServicio.getIdEstadoDocumentoAceptacion();
                                if (idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) == 0)
                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                else // idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ANULADO.getId()) == 0
                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                                logger.error(header2 + " // WRITING NEW HES POS: " + hojaServicioDetalle.toString());
                                documentoAceptacionDetalleRepository.save(hojaServicioDetalle);
                            }
                        }
                    }
                    else{ // (idHojaServicioExistente != null) si ya existe previamente la hoja de entrada de servicio
                        String header2 = header1.concat("HES EXISTENTE: " + numeroHojaServicio);
                        SapTableItemDto unicoItem = itemList.get(0); // HES solo tiene un item (posicion)
                        DocumentoAceptacion hojaServicioExistente = documentoAceptacionRepository.getDocumentoAceptacionById(DocumentoAceptacionTipoEnum.HOJA_ENTRADA_SERVICIO.getId(),idHojaServicioExistente);

                        String hesExistenteStatusSap = Optional.ofNullable(hojaServicioExistente.getStatusSap()).orElse("");
                        String hesExtraidaStatusSap = Optional.ofNullable(unicoItem.getStatus()).orElse("");
                        Integer IdEstadoHesExistente  = Optional.ofNullable(hojaServicioExistente.getIdEstadoDocumentoAceptacion()).orElse(-1);

                        if(extraccionUnicoDocumento){
                            logger.error(header2 + " // HES STATUS_SAP ACTUAL: " + hesExistenteStatusSap);
                            logger.error(header2 + " // HES STATUS_SAP EXTRAIDO: " + hesExtraidaStatusSap);
                            logger.error(header2 + " // HES ID_ESTADO ACTUAL: " + IdEstadoHesExistente);
                        }

                        if(hesExistenteStatusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion())
                                && hesExtraidaStatusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.BORRADO.getDescripcion())){
                            hojaServicioExistente.setStatusSap(hesExtraidaStatusSap);

                            if(IdEstadoHesExistente.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId())==0
                                    || IdEstadoHesExistente.compareTo(DocumentoAceptacionEstadoEnum.PREFACTURADO.getId())==0) {
                                hojaServicioExistente.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                                DocumentoAceptacionDetalle hojaServicioDetalleExistente = hojaServicioExistente.getDocumentoAceptacionDetalleList().get(0);
                                hojaServicioDetalleExistente.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());
                                documentoAceptacionDetalleRepository.save(hojaServicioDetalleExistente);
                            }

                            logger.error(header2 + " // REGISTRO HES MODIFICADO: " + hojaServicioExistente.toString());
                            documentoAceptacionRepository.save(hojaServicioExistente);
                        }
                    }
                });
                logger.error(header1 + "FINISHED");

                if(daProcessing.get())
                    daProcessing.set(!daProcessing.get());
            }
            catch (Exception e){
                if(daProcessing.get())
                    daProcessing.set(!daProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                throw new Exception(e);
            }
        }
        else{
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + " // DocumentoAceptacion Extraction esta procesando");
        }
    }

    private String armarTrama(String parametro1, String parametro2){
        String trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_ENTRADA_MERCADERIAS>\n" +
                "         <!--Optional:-->\n" +
                "         <I_CPUDTF>"+ parametro2 +"</I_CPUDTF>\n" +
                "         <!--Optional:-->\n" +
                "         <I_CPUDTI>"+ parametro1 +"</I_CPUDTI>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EBELN>\n" +
                "         </I_EBELN>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EBELP></I_EBELP>\n" +
                "         <!--Optional:-->\n" +
                "         <I_PACKNO></I_PACKNO>\n" +
                "         <T_DPEDIDO>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </T_DPEDIDO>\n" +
                "         <T_FLAG>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT></MANDT>\n" +
                "               <EBELN></EBELN>\n" +
                "               <EBELP></EBELP>\n" +
                "               <ELIKZ></ELIKZ>\n" +
                "            </item>\n" +
                "         </T_FLAG>\n" +
                "         <T_HPEDIDO>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT></MANDT>\n" +
                "               <EBELN></EBELN>\n" +
                "               <EBELP></EBELP>\n" +
                "               <ZEKKN></ZEKKN>\n" +
                "               <VGABE></VGABE>\n" +
                "               <GJAHR></GJAHR>\n" +
                "               <BELNR></BELNR>\n" +
                "               <BUZEI></BUZEI>\n" +
                "               <BEWTP></BEWTP>\n" +
                "               <BWART></BWART>\n" +
                "               <BUDAT></BUDAT>\n" +
                "               <WRBTR></WRBTR>\n" +
                "               <WAERS></WAERS>\n" +
                "               <IGVPH></IGVPH>\n" +
                "               <ESTPH></ESTPH>\n" +
                "               <MWSKZ></MWSKZ>\n" +
                "               <XBLNR></XBLNR>\n" +
                "               <BUKRS></BUKRS>\n" +
                "               <LIFNR></LIFNR>\n" +
                "               <MENGE></MENGE>\n" +
                "               <TXZ01></TXZ01>\n" +
                "               <PSTYP></PSTYP>\n" +
                "               <ERFME></ERFME>\n" +
                "               <WERKS></WERKS>\n" +
                "               <SHKZG></SHKZG>\n" +
                "               <LFBNR></LFBNR>\n" +
                "               <ERNAM></ERNAM>\n" +
                "               <ERDAT></ERDAT>\n" +
                "               <BLDAT></BLDAT>\n" +
                "               <NETPR></NETPR>\n" +
                "               <MENGEP></MENGEP>\n" +
                "               <DMBTR></DMBTR>\n" +
                "               <SRVPOS></SRVPOS>\n" +
                "               <KTEXT1></KTEXT1>\n" +
                "               <MATNR></MATNR>\n" +
                "               <MAKTX></MAKTX>\n" +
                "               <ERFMG></ERFMG>\n" +
                "               <LFPOS></LFPOS>\n" +
                "               <ZEILE></ZEILE>\n" +
                "               <MEINS></MEINS>\n" +
                "               <ACT_MENGE></ACT_MENGE>\n" +
                "               <ACT_WERT></ACT_WERT>\n" +
                "               <STATUS></STATUS>\n" +
                "               <TAX_CODE></TAX_CODE>\n" +
                "            </item>\n" +
                "         </T_HPEDIDO>\n" +
                "         <T_SPEDIDO>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT></MANDT>\n" +
                "               <PACKNO></PACKNO>\n" +
                "               <INTROW></INTROW>\n" +
                "               <EXTROW></EXTROW>\n" +
                "               <SRVPOS></SRVPOS>\n" +
                "               <KTEXT1></KTEXT1>\n" +
                "               <MENGE></MENGE>\n" +
                "               <MEINS></MEINS>\n" +
                "               <BRTWR></BRTWR>\n" +
                "               <NETWR></NETWR>\n" +
                "               <ACT_MENGE></ACT_MENGE>\n" +
                "               <ACT_WERT></ACT_WERT>\n" +
                "               <SUB_PACKNO></SUB_PACKNO>\n" +
                "               <EBELN></EBELN>\n" +
                "               <EBELP></EBELP>\n" +
                "               <BELNR></BELNR>\n" +
                "               <WT_WITHCD></WT_WITHCD>\n" +
                "            </item>\n" +
                "         </T_SPEDIDO>\n" +
                "      </urn:ZMM_ENTRADA_MERCADERIAS>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        /*String trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_ENTRADA_MERCADERIAS>\n" +
                "         <!--Optional:-->\n" +
                "         <I_CPUDTF>"+ parametro2 +"</I_CPUDTF>\n" +
                "         <!--Optional:-->\n" +
                "         <I_CPUDTI>"+ parametro1 +"</I_CPUDTI>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EBELN>\n" +
                "            <!--Zero or more repetitions:-->\n" +
//                "            <item>\n" +
//                "               <SIGN>I</SIGN>\n" +
//                "               <OPTION>EQ</OPTION>\n" +
//                "               <LOW>4590036439</LOW>\n" +
//                "               <HIGH></HIGH>\n" +
//                "            </item>\n" +
                "         </I_EBELN>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EBELP></I_EBELP>\n" +
                "         <!--Optional:-->\n" +
                "         <I_PACKNO></I_PACKNO>\n" +
                "         <T_FLAG>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "\n" +
                "         </T_FLAG>\n" +
                "         <T_HPEDIDO>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "       \n" +
                "         </T_HPEDIDO>\n" +
                "         <T_SPEDIDO>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            \n" +
                "         </T_SPEDIDO>\n" +
                "      </urn:ZPE_MM_ENTRADA_MERCADERIAS>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";*/

        return trama;
    }

    @Override
    public List<SapTableItemDto> extraerDataDocumentoAceptacionRFC(String parametro1, String parametro2, boolean unicoDocumentoAceptacion) throws Exception {
        try{
            String FUNCION_RFC = "ZPE_MM_ENTRADA_MERCADERIAS";

//            JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//            JCoRepository repository = destination.getRepository();
//
//            JCoFunction jCoFunction = repository.getFunction(FUNCION_RFC);
//            this.mapFilters(jCoFunction, parametro1, parametro2, true);
//            jCoFunction.execute(destination);
//
//            JCoParameterList exportParameterList = jCoFunction.getTableParameterList();
//            DocumentoAceptacionExtractorMapper documentoAceptacionExtractorMapper = DocumentoAceptacionExtractorMapper.newMapper(exportParameterList);

            List<SapTableItemDto> sapTableItemDtoList = null;//documentoAceptacionExtractorMapper.getSapTableItemDtoList();
            List<String> movimientosIncluidos = Arrays.asList("101","102","105","106");

            if (unicoDocumentoAceptacion){
                sapTableItemDtoList = sapTableItemDtoList.stream()
                        .filter(item -> item.getNumeroDocumentoAceptacion().equals(parametro2))
                        .collect(Collectors.toList());
            }

            Map<String,List<SapTableItemDto>> entradaMercaderiaItemMap = sapTableItemDtoList.stream()
                    .filter(item -> movimientosIncluidos.contains(item.getMovimiento())) // solo items de EM con movimiento 101,102,105,106
                    .collect(Collectors.groupingBy(SapTableItemDto::getNumeroDocumentoAceptacion,Collectors.toList()));

            Map<String,List<SapTableItemDto>> hojaServicioItemMap = sapTableItemDtoList.stream()
                    .filter(item -> item.getMovimiento().equals("")) // solo items de HES (sin movimiento)
                    .collect(Collectors.groupingBy(SapTableItemDto::getNumeroDocumentoAceptacion,Collectors.toList()));

            Long cantidadEntregaMercaderiaExcluida = sapTableItemDtoList.stream()
                    .filter(item -> !movimientosIncluidos.contains(item.getMovimiento()) && !item.getMovimiento().equals("")) // solo items de EM con movimientos que no son 101,102,103,104,105,106
                    .count();

            String header1 = "";
            if (unicoDocumentoAceptacion)
                header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR DATA DA -- RANGO: " + parametro1 + " - " + parametro2 + " // ";
            else
                header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR DATA DA -- RANGO: " + parametro1 + " - ALL // ";

            logger.error(header1 + "CANTIDAD DE EM ENCONTRADAS: " + entradaMercaderiaItemMap.size());
            logger.error(header1 + "CANTIDAD DE EM EXCLUIDAS: " + cantidadEntregaMercaderiaExcluida);
            logger.error(header1 + "CANTIDAD DE HES ENCONTRADAS: " + hojaServicioItemMap.size());

            for(int i=0 ; i < sapTableItemDtoList.size() ; i++){
                logger.error(header1 + "SAP ITEM " + i + ": " + sapTableItemDtoList.get(i).toString());
            }

            logger.error(header1 + "FINISHED");

            return sapTableItemDtoList;
        }
        catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }


    private DocumentoAceptacion actualizarDocumentoAceptacionAndOrdenCompra(String tipoDocAceptacion, DocumentoAceptacion documentoAceptacion, OrdenCompra ordenCompra, boolean aprobarOrdenCompra, boolean enviarCorreo){
        /*if(aprobarOrdenCompra
                && documentoAceptacion.getIdEstadoDocumentoAceptacion().compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId())==0
                && (ordenCompra.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.ACTIVA.getId())==0 || ordenCompra.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.VISUALIZADA.getId())==0))
        {*/
            ordenCompra.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.APROBADA.getId());
            ordenCompra.setFechaAprobacion(DateUtils.getCurrentTimestamp());
            ordenCompraRepository.save(ordenCompra);

            if (enviarCorreo && !tipoDocAceptacion.equals("HES")) {
            /*Enviando correo*/
                String respuesta = "";
                Usuario comprador = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
                if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty()){}
                    //respuesta = contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(), ordenCompra,null, comprador);
                    //respuesta =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion(tipoDocAceptacion, parametroMapper.getMailSetting(),documentoAceptacion, null, comprador,null);

                LogTransaccion logTransaccion = new LogTransaccion();
                logTransaccion.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                logTransaccion.setRespuestaCodigo(respuesta);
                logTransaccion.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                this.logTransaccionRepository.save(logTransaccion);
                Proveedor proveedor = proveedorService.getProveedorByRuc(ordenCompra.getProveedorRuc());
                Usuario proveedorUsuario = null;
                if(proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()){
                    proveedorUsuario = new Usuario();
                    proveedorUsuario.setEmail(proveedor.getEmail());
                    proveedorUsuario.setApellido(proveedor.getRazonSocial());
                }
                else{
                    List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompra.getProveedorRuc());
                    if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                        proveedorUsuario = posibleProveedorList.get(0);
                }
                String respuesta1 = "";
                
                if(proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty()){}
                    //respuesta1 =   contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(),ordenCompra, proveedorUsuario, null);
                    //respuesta1 =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion(tipoDocAceptacion, parametroMapper.getMailSetting(),documentoAceptacion, proveedorUsuario, null,null);
                    

                LogTransaccion logTransaccion1 = new LogTransaccion();
                logTransaccion1.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                logTransaccion1.setRespuestaCodigo(respuesta1);
                logTransaccion1.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                this.logTransaccionRepository.save(logTransaccion1);
            }
        //}

        documentoAceptacion.setIdOrdenCompra(ordenCompra.getId());
        documentoAceptacion.setProveedorRuc(ordenCompra.getProveedorRuc());
        documentoAceptacion.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());

        if(tipoDocAceptacion.equals("HES")){
            documentoAceptacion.setUsuarioSapAutoriza(ordenCompra.getUltimoLiberadorUsuarioSap());
        }

        return documentoAceptacion;
    }


    private int asignarIdEstadoDocumentoAceptacion(String movimiento){
        switch (movimiento) {
            case "101": // Liberación EM
                return DocumentoAceptacionEstadoEnum.ACTIVO.getId();
            case "103": // Liberación 1er Paso
                return DocumentoAceptacionEstadoEnum.TRANSITO.getId();
            case "105": // Liberación 2d Paso
                return DocumentoAceptacionEstadoEnum.ACTIVO.getId();
            case "122":
                return DocumentoAceptacionEstadoEnum.ACTIVO.getId();
            default: // Anulacion EM, Anulacion 1er Paso, Anulacion 2do Paso ("102","104","106")
                return DocumentoAceptacionEstadoEnum.ANULACION.getId();
        }
    }


    public boolean toggleDocumentoAceptacionExtractionProcessingState() {
        daProcessing.set(!daProcessing.get());
        return daProcessing.get();
    }

    public boolean currentDocumentoAceptacionExtractionProcessingState() {
        return daProcessing.get();
    }

    @Transactional
    @Override
    public void extraerDocumentoAceptacionAnuladasList(boolean extraccionUnicoDocumento) throws Exception {
        if (!daProcessing.get() || extraccionUnicoDocumento) {

            if (!daProcessing.get() && !extraccionUnicoDocumento)
                daProcessing.set(true);

            logger.info("=== INICIO extracción de Documentos de Aceptación Anulados ===");
            logger.info("Parámetro extraccionUnicoDocumento: {}", extraccionUnicoDocumento);

            try {
                String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ANULADOSHES_CDS/YY1_AnuladosHES"
                        + "?$format=json";

                String authString = userSap + ":" + passwordSap;
                String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url_sap)
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Basic " + encodedAuth)
                        .get()
                        .build();

                logger.info("Ejecutando consulta OData hacia SAP: {}", url_sap);

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.error("Error en la consulta OData. Código de respuesta: {}", response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray resultsArray = jsonObject.getJSONObject("d").getJSONArray("results");

                    logger.info("Cantidad de documentos encontrados en SAP: {}", resultsArray.length());

                    int countUpdates = 0;

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject result = resultsArray.getJSONObject(i);
                        String serviceEntrySheet = result.getString("PurchasingHistoryDocument");
                        String matDocument = result.getString("MaterialDocument");

                        logger.info("Procesando documento N° {}: {}", i + 1, serviceEntrySheet);

                        // Buscar en la BD
                        List<Object[]> objDocumento = documentoAceptacionRepository
                                .getIdDocumentoAceptacionAndEstadoByNumeroAndDocumento(serviceEntrySheet,matDocument);

                        Integer idDocumento = null;
                        Integer idEstadoDocumento = null;

                        if (!objDocumento.isEmpty()) {
                            Object[] row = objDocumento.get(0);
                            idDocumento = (Integer) row[0];
                            idEstadoDocumento = (Integer) row[1];
                        }

                        if (idDocumento != null && idEstadoDocumento != null && idEstadoDocumento != 5) {
                            documentoAceptacionRepository.updateEstadoDocumentoAceptacionById(5, idDocumento);
                            countUpdates++;
                            logger.info("✔ Documento {} actualizado a estado 'Anulada'", serviceEntrySheet);

                        } else if (idDocumento != null && idEstadoDocumento != null && idEstadoDocumento == 5) {
                            logger.warn("⚠ Documento {} ya se encuentra Anulado. No se actualizó.", serviceEntrySheet);

                        } else {
                            logger.warn("⚠ Documento {} no encontrado en BD. No se actualizó.", serviceEntrySheet);
                        }
                    }

                    logger.info("=== FIN de la extracción ===");
                    logger.info("Total de documentos anulados actualizados en BD: {}", countUpdates);
                }

            } catch (Exception e) {
                logger.error("❌ Error en extracción de DA anuladas", e);
                LogTransaccion logTransaccion3 = new LogTransaccion();
                logTransaccion3.setEnvioTrama(e.getMessage());
                logTransaccion3.setRespuestaCodigo(e.getMessage());
                logTransaccion3.setTipoRegistro("ExcepcionLogsAceptacion3");
                this.logTransaccionRepository.save(logTransaccion3);
                throw e;
            } finally {
                if (daProcessing.get())
                    daProcessing.set(false);
                logger.info("Liberado flag de procesamiento (daProcessing).");
            }

        } else {
            logger.error("INI: {} // DocumentoAceptacion Extraction ya en proceso", DateUtils.getCurrentTimestamp());
        }
    }


    @Transactional
    @Override
    public void extraerDocumentoAceptacionHES(String parametro1, String parametro2, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception {

        if(!daProcessing.get()) {
            daProcessing.set(true);

            try {
                String url_sap = urlSap + "/sap/opu/odata4/sap/api_serviceentrysheet/srvd_a2x/sap/serviceentrysheet/0001/ServiceEntrySheet?$filter=ApprovalDateTime ge " 
                    + parametro1 + " and ApprovalDateTime le " + parametro2 + "&$top=200&$orderby=ApprovalDateTime asc";

                String usrSap = userSap;
                String pwdSap = passwordSap;
                String authString = usrSap + ":" + pwdSap;
                String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

                OkHttpClient client = new OkHttpClient().newBuilder().build();

                Request request = new Request.Builder()
                        .url(url_sap)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Basic " + encodedAuth)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray resultsArray = jsonObject.getJSONArray("value");

                    logger.error("Rango de Fechas : " + parametro1 + " - " + parametro2);
                    logger.error("CANTIDAD DE HES ENCONTRADOS: " + resultsArray.length());

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject result = resultsArray.getJSONObject(i);
                        String serviceEntrySheet = result.getString("ServiceEntrySheet");
                        String materialDocument = result.getString("MaterialDocument");

                        System.out.println("Procesando HES: " + serviceEntrySheet);

                        // Buscar en la BD
                        List<Object[]> objDocumento = documentoAceptacionRepository
                                .getIdDocumentoAceptacionAndEstadoByNumero(serviceEntrySheet);

                        // Validar si hay más de un registro
                        System.out.println(" OBJDOCUMENTOS"+ objDocumento.size());
                        if (objDocumento.size() > 1) {
                            System.out.println("Se encontraron multiples registros para la HES: " + serviceEntrySheet + ". Se omite este elemento.");
                            continue; // pasa al siguiente elemento del for
                        }
                        
                        Integer idHojaServicioExistente = null;
                        Integer idEstadoDocumento = null;

                        if (!objDocumento.isEmpty()) {
                            Object[] row = objDocumento.get(0);
                            idHojaServicioExistente = (Integer) row[0];
                            idEstadoDocumento = (Integer) row[1];
                        }

                        if (idHojaServicioExistente != null && (idEstadoDocumento == 4 || idEstadoDocumento == 5)) {
                            System.out.println("Proceso de extraccion iniciada para HES: " + serviceEntrySheet + " con Material Document: " + materialDocument);
                            this.extraerDocumentoAceptacionListMaterialDocumentHES(
                                materialDocument, 
                                serviceEntrySheet, 
                                aprobarOrdenCompra, 
                                enviarCorreoAprobacion,
                                idHojaServicioExistente
                            );
                        } else if (idHojaServicioExistente == null) {
                            System.out.println("Proceso de extraccion iniciada para HES: " + serviceEntrySheet + " con Material Document: " + materialDocument);
                            this.extraerDocumentoAceptacionListMaterialDocumentHES(
                                materialDocument, 
                                serviceEntrySheet, 
                                aprobarOrdenCompra, 
                                enviarCorreoAprobacion,
                                null
                            );
                        } else {
                            System.out.println("Ya existe la HES para " + serviceEntrySheet + " con ID: " + idHojaServicioExistente);
                        }
                    }

                }
            } catch (Exception e) {
                logger.error("❌ Error en extracción de DA HES", e);
                LogTransaccion logTransaccion3 = new LogTransaccion();
                logTransaccion3.setEnvioTrama(e.getMessage());
                logTransaccion3.setRespuestaCodigo(e.getMessage());
                logTransaccion3.setTipoRegistro("ExcepcionLogsAceptacion3");
                this.logTransaccionRepository.save(logTransaccion3);
                throw e;
            } finally {
                if (daProcessing.get())
                    daProcessing.set(false);
                logger.info("Liberado flag de procesamiento (daProcessing).");
            }

        } else {
            logger.error("INI: {} // DocumentoAceptacion HES Extraction ya en proceso", DateUtils.getCurrentTimestamp());
        }
    }

    public void extraerDocumentoAceptacionListMaterialDocumentHES(
            String materialDocument,
            String serviceEntrySheet,
            boolean aprobarOrdenCompra,
            boolean enviarCorreoAprobacion,
            Integer idHojaServicioExistenteOg) throws Exception {

        System.out.println("=== Inicio ejecución extraerDocumentoAceptacionListMaterialDocumentHES ===");
        System.out.println("Parámetros recibidos -> materialDocument: " + materialDocument 
                        + ", serviceEntrySheet: " + serviceEntrySheet 
                        + ", aprobarOrdenCompra: " + aprobarOrdenCompra 
                        + ", enviarCorreoAprobacion: " + enviarCorreoAprobacion);

            try {
                String url_sap = urlSap + "/sap/opu/odata/sap/YY1_MATERIALDOCUMENT_CDS/YY1_MaterialDocument?$filter=MaterialDocument eq '" + materialDocument + "'";
                System.out.println("Llamando a SAP con URL: " + url_sap);

                String usrSap = userSap;
                String pwdSap = passwordSap;
                String authString = usrSap + ":" + pwdSap;
                String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

                OkHttpClient client = new OkHttpClient().newBuilder().build();

                Request request = new Request.Builder()
                        .url(url_sap)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Basic " + encodedAuth)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                System.out.println("Respuesta HTTP de SAP: " + response.code());

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Respuesta recibida desde SAP (parcial): " 
                                        + responseBody.substring(0, Math.min(responseBody.length(), 200)) + "...");

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject dObject = jsonObject.getJSONObject("d");
                        JSONArray resultsArray = dObject.getJSONArray("results");
                        System.out.println("Cantidad de registros en results: " + resultsArray.length());

                        Map<String, List<JSONObject>> groupedData = new HashMap<>();

                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject result = resultsArray.getJSONObject(i);
                            String MaterialDocument = result.getString("MaterialDocument");
                            groupedData.computeIfAbsent(MaterialDocument, k -> new ArrayList<>()).add(result);
                        }
                        System.out.println("Documentos agrupados: " + groupedData.keySet());

                        for (Map.Entry<String, List<JSONObject>> entry : groupedData.entrySet()) {
                            String MaterialDocument = entry.getKey();
                            List<JSONObject> details = entry.getValue();
                            System.out.println("Procesando documento: " + MaterialDocument + " con " + details.size() + " detalles.");

                            String tipo = "";
                            List<BienServicio> bienServicios = bienServicioService.getListBienServicioByCodigoSap(details.get(0).getString("Material"));

                            if(bienServicios.size() > 0){
                                String tipoValidacion = bienServicios.get(0).getTipoItem();
                                tipo = "MATERIAL".equals(tipoValidacion) ? "EM" : "SERVICIO".equals(tipoValidacion) ? "HES" : "";
                            }
                            System.out.println("Tipo identificado: " + tipo);

                            if(tipo.equals("HES")) {
                                Integer idHojaServicioExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumeroRecepcion(MaterialDocument);
                                System.out.println("Id hoja de servicio existente: " + idHojaServicioExistente);

                                if(idHojaServicioExistente == null){ 
                                    System.out.println("No existe hoja de servicio previa, creando nueva...");

                                    String statusSap = "Aceptado";  
                                    System.out.println("Status SAP forzado: " + statusSap);

                                    if (!statusSap.isEmpty()
                                            && (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion())
                                            || statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.BORRADO.getDescripcion()))) {

                                        String numeroOrdenCompra = details.get(0).getString("PurchaseOrder");
                                        String posicionOrdencompra = details.get(0).getString("PurchaseOrderItem");
                                        System.out.println("Orden de compra detectada: " + numeroOrdenCompra + " - Posición: " + posicionOrdencompra);

                                        DocumentoAceptacion hojaServicio = new DocumentoAceptacion();
                                        if (idHojaServicioExistenteOg != null){
                                            hojaServicio.setId(idHojaServicioExistenteOg);
                                        }
                                        hojaServicio.setNumeroDocumentoAceptacion(MaterialDocument);
                                        hojaServicio.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.HOJA_ENTRADA_SERVICIO.getId());
                                        hojaServicio.setNumeroOrdenCompra(numeroOrdenCompra);
                                        hojaServicio.setPosicionOrdenCompra(posicionOrdencompra);
                                        hojaServicio.setUsuarioSapRecepcion(details.get(0).getString("CreatedByUser"));
                                        hojaServicio.setCodigoMoneda(details.get(0).getString("CompanyCodeCurrency"));
                                        hojaServicio.setFechaEmision(convertStringToDate(details.get(0).getString("CreationDate")));
                                        hojaServicio.setFechaAceptacion(convertStringToDate(details.get(0).getString("PostingDate")));
                                        hojaServicio.setValorImpuesto(BigDecimal.valueOf(0.00));
                                        hojaServicio.setSociedad(details.get(0).getString("CompanyCode"));
                                        hojaServicio.setNroGuiaRemision(details.get(0).getString("ReferenceDocument"));
                                        hojaServicio.setNumeroLote(details.get(0).getString("Batch"));
                                        hojaServicio.setNumeroRecepcion(MaterialDocument);
                                        hojaServicio.setEntregaCompleta(details.get(0).getBoolean("IsCompletelyDelivered"));

                                        // Buscar datos del usuario
                                        String usuarioNombreRecepcion = "";
                                        String url_sap_recepcion = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                        System.out.println("Consultando usuario en: " + url_sap_recepcion);

                                        OkHttpClient client_recepcion = new OkHttpClient().newBuilder().build();
                                        Request postRequest_pk_recepcion = new Request.Builder()
                                                .url(url_sap_recepcion)
                                                .addHeader("Content-Type", "application/json")
                                                .addHeader("Accept", "application/json")
                                                .addHeader("Authorization", "Basic " + encodedAuth)
                                                .get()
                                                .build();

                                        Response postResponse_pk_recepcion = client_recepcion.newCall(postRequest_pk_recepcion).execute();
                                        System.out.println("Respuesta consulta usuario: " + postResponse_pk_recepcion.code());

                                        if (postResponse_pk_recepcion.isSuccessful()) {
                                            String responseBody_recepcion = postResponse_pk_recepcion.body().string();
                                            JSONObject jsonObject_recepcion = new JSONObject(responseBody_recepcion);
                                            JSONArray resultsArray_recepcion = jsonObject_recepcion.getJSONObject("d").getJSONArray("results");

                                            if (resultsArray_recepcion.length() > 0) {
                                                usuarioNombreRecepcion = resultsArray_recepcion.getJSONObject(0).getString("PersonFullName");
                                                System.out.println("Usuario SAP recepcionado: " + usuarioNombreRecepcion);
                                            }
                                        }
                                        hojaServicio.setUsuarioNombreRecepcion(usuarioNombreRecepcion);
                                        hojaServicio.setStatusSap(statusSap);

                                        if (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion()))
                                            hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                        else
                                            hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                                        // Orden de compra
                                        OrdenCompra ordenCompra = new OrdenCompra();
                                        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);
                                        if (optionalOrdenCompra.isPresent()) {
                                            ordenCompra = optionalOrdenCompra.get();
                                            hojaServicio = this.actualizarDocumentoAceptacionAndOrdenCompra("HES", hojaServicio, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);
                                            hojaServicio.setCodigoMoneda(ordenCompra.getCodigoMondeda());
                                            hojaServicio.setProveedorRuc(ordenCompra.getProveedorRuc());
                                            hojaServicio.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());
                                            System.out.println("Orden de compra " + numeroOrdenCompra + " asociada exitosamente.");
                                        }

                                        if(hojaServicio.getIdOrdenCompra() != null) {
                                            hojaServicio.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                            hojaServicio.setNumeroDocumentoAceptacion(serviceEntrySheet);
                                            hojaServicio = documentoAceptacionRepository.save(hojaServicio);
                                            System.out.println("Documento aceptación HES guardado con ID: " + hojaServicio.getId());

                                            List<DocumentoAceptacionDetalle> detalleHojaServicio = new ArrayList<>();
                                            for (JSONObject detail : details) {
                                                DocumentoAceptacionDetalle hojaServicioDetalle = new DocumentoAceptacionDetalle();
                                                hojaServicioDetalle.setIdDocumentoAceptacion(hojaServicio.getId());
                                                hojaServicioDetalle.setNumeroDocumentoAceptacion(serviceEntrySheet);
                                                hojaServicioDetalle.setNumeroOrdenCompra(numeroOrdenCompra);
                                                hojaServicioDetalle.setPosicionOrdenCompra(detail.getString("PurchaseOrderItem"));
                                                hojaServicioDetalle.setCodigoSapBienServicio(detail.getString("Material"));
                                                hojaServicioDetalle.setDescripcionBienServicio(
                                                        bienServicioService.getListBienServicioByCodigoSap(detail.getString("Material")).get(0).getDescripcion()
                                                );
                                                hojaServicioDetalle.setUnidadMedida(detail.getString("EntryUnit"));
                                                hojaServicioDetalle.setCantidadAceptadaCliente(BigDecimal.valueOf(Double.valueOf(detail.getString("QuantityInEntryUnit"))));
                                                hojaServicioDetalle.setPrecioUnitario(BigDecimal.ZERO);
                                                hojaServicioDetalle.setValorRecibido(BigDecimal.ZERO);
                                                hojaServicioDetalle.setValorRecibidoMonedalocal(BigDecimal.ZERO);
                                                hojaServicioDetalle.setIndicadorImpuesto("");
                                                hojaServicioDetalle.setNumeroItem(Integer.parseInt(detail.getString("PurchaseOrderItem")));
                                                hojaServicioDetalle.setUnidadMedida(bienServicioService.getListBienServicioByCodigoSap(detail.getString("Material")).get(0).getUnidadMedida().getCodigoSap());
                                                hojaServicioDetalle.setNroGuiaRemision(detail.getString("ReferenceDocument"));

                                                Integer idEstadoHojaServicio = hojaServicio.getIdEstadoDocumentoAceptacion();
                                                if (idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) == 0)
                                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                                else
                                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                                                documentoAceptacionDetalleRepository.save(hojaServicioDetalle);
                                                detalleHojaServicio.add(hojaServicioDetalle);
                                            }
                                            System.out.println("Detalles de documento aceptación HES guardados.");

                                            if (enviarCorreoAprobacion ) {
                                                //Enviando correo
                                                String respuesta = "";
                                                Usuario comprador = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
                                                if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                                    //respuesta = contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(), ordenCompra,null, comprador);
                                                    respuesta =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion("HES", parametroMapper.getMailSetting(),hojaServicio, null, comprador,detalleHojaServicio);

                                                LogTransaccion logTransaccion = new LogTransaccion();
                                                logTransaccion.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                                                logTransaccion.setRespuestaCodigo(respuesta);
                                                logTransaccion.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                                                this.logTransaccionRepository.save(logTransaccion);
                                                Proveedor proveedor = proveedorService.getProveedorByRuc(ordenCompra.getProveedorRuc());

                                                if(proveedor == null){
                                                    //CONSULTAR EL API DE SAP PARA TRAER EL BP
                                                    proveedor = new Proveedor();
                                                    JSONObject supplier = this.consultarSupplier(details.get(0).getString("Supplier"));
                                                    if(supplier !=null){
                                                        //Validar si Existe correo
                                                        if(!supplier.getString("EmailAddress").isEmpty()){
                                                            proveedor.setEmail(supplier.getString("EmailAddress"));
                                                            proveedor.setRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                                            proveedor.setRuc(supplier.getString("BPTaxNumber"));
                                                        }
                                                    }
                                                }

                                                Usuario proveedorUsuario = null;
                                                if(proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()){
                                                    proveedorUsuario = new Usuario();
                                                    proveedorUsuario.setEmail(proveedor.getEmail());
                                                    proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                                }
                                                else{
                                                    List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompra.getProveedorRuc());
                                                    if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                                        proveedorUsuario = posibleProveedorList.get(0);
                                                }
                                                String respuesta1 = "";

                                                if(proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                                                    respuesta1 =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion("HES", parametroMapper.getMailSetting(),hojaServicio, proveedorUsuario, null,detalleHojaServicio);


                                                LogTransaccion logTransaccion1 = new LogTransaccion();
                                                logTransaccion1.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                                                logTransaccion1.setRespuestaCodigo(respuesta1);
                                                logTransaccion1.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                                                this.logTransaccionRepository.save(logTransaccion1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        System.err.println("Error al procesar el JSON: " + e.getMessage());
                    }
                }
            } finally {
                
                System.out.println("Fin del proceso.");
            }

        System.out.println("=== Fin ejecución extraerDocumentoAceptacionListMaterialDocumentHES ===");
    }


    @Transactional
    @Override
    public void extraerDocumentoAceptacionListRFC(String parametro1, String parametro2, boolean extraccionUnicoDocumento, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception {
        
        if(!daProcessing.get() || extraccionUnicoDocumento) {

            if(!daProcessing.get() && !extraccionUnicoDocumento)
                daProcessing.set(!daProcessing.get());

            try{
                
                List<String> movimientosIncluidos = Arrays.asList(MovimientoEntregaMercaderiaTipoEnum.LIBERACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.LIBERACION_2_PASO.getCodigo(),
                        MovimientoEntregaMercaderiaTipoEnum.ANULACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.ANULACION_2_PASO.getCodigo(), MovimientoEntregaMercaderiaTipoEnum.DEVOLUCIONES.getCodigo());
                List<String> movimientosDeAnulacion = Arrays.asList(MovimientoEntregaMercaderiaTipoEnum.ANULACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.ANULACION_2_PASO.getCodigo());

                String body = this.armarTrama(parametro1, parametro2);

                String url_sap = urlSap + "/sap/opu/odata/sap/YY1_MATERIALDOCUMENT_CDS/YY1_MaterialDocument?$filter=PurchaseOrder ne '' and PostingDate ge datetime'" + parametro1 + "' and PostingDate le datetime'" + parametro2 + "'";
                String usrSap = userSap;
                String pwdSap = passwordSap;
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

                Response postResponse_pk = client.newCall(postRequest_pk).execute();

                if (postResponse_pk.isSuccessful()) {
                    String responseBody = postResponse_pk.body().string();

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
                            String MaterialDocument = result.getString("MaterialDocument");
                
                            // Obtener la lista existente o crear una nueva si no existe
                            groupedData.computeIfAbsent(MaterialDocument, k -> new ArrayList<>()).add(result);
                        }
                
                        logger.error("Rango de Fechas : " + parametro1 + " - " + parametro1);
                        logger.error("CANTIDAD DE DA ENCONTRADOS: " + groupedData.size());
                        logger.error("CANTIDAD DE DA ENCONTRADOS: " + resultsArray.length());
                        
                        // Iterar sobre el mapa para procesar las cabeceras y detalles
                        for (Map.Entry<String, List<JSONObject>> entry : groupedData.entrySet()) {
                            String MaterialDocument = entry.getKey();
                            List<JSONObject> details = entry.getValue();
                
                            System.out.println("Procesando EM HES: " + MaterialDocument);

                            String tipo = "";
                            
                            /*Validamos si el producto principal es es S o M */
                            List<BienServicio> bienServicios = bienServicioService.getListBienServicioByCodigoSap(details.get(0).getString("Material"));
                            
                            if(bienServicios.size() > 0){

                                String tipoValidacion = bienServicios.get(0).getTipoItem();

                                if ("MATERIAL".equals(tipoValidacion)) {
                                    tipo = "EM";
                                } else if ("SERVICIO".equals(tipoValidacion)) {
                                    tipo = "HES";
                                }
                            }

                            //details.get(0).getString("GoodsMovementType")

                            /*VALIDAMOS SI ES HES o EM por el CAMPO GoodsMovementType */
                            if(tipo.equals("HES"))
                            {/*
                                Integer idHojaServicioExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumeroRecepcion(MaterialDocument);
                                
                                if(idHojaServicioExistente == null){ // si no existe previamente la hoja de entrada de servicio
                                    
                                    String statusSap = "Aceptado";  //No se tiene dato desde Public

                                    if (statusSap != null && !statusSap.isEmpty()
                                            && (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion())
                                            || statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.BORRADO.getDescripcion()))) { // solo publica si statusSap existe
                                        
                                        //String header2 = header1.concat("HES: " + numeroHojaServicio);
                                        String numeroOrdenCompra = details.get(0).getString("PurchaseOrder");
                                        String posicionOrdencompra = details.get(0).getString("PurchaseOrderItem");
                                        DocumentoAceptacion hojaServicio = new DocumentoAceptacion();
            
                                        hojaServicio.setNumeroDocumentoAceptacion(MaterialDocument);
                                        hojaServicio.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.HOJA_ENTRADA_SERVICIO.getId());
                                        hojaServicio.setNumeroOrdenCompra(numeroOrdenCompra);
                                        hojaServicio.setPosicionOrdenCompra(posicionOrdencompra);
                                        hojaServicio.setUsuarioSapRecepcion(details.get(0).getString("CreatedByUser"));
                                        hojaServicio.setCodigoMoneda(details.get(0).getString("CompanyCodeCurrency"));
                                        hojaServicio.setFechaEmision(convertStringToDate(details.get(0).getString("CreationDate")));
                                        hojaServicio.setFechaAceptacion(convertStringToDate(details.get(0).getString("PostingDate")));
                                        hojaServicio.setValorImpuesto(BigDecimal.valueOf(0.00));
                                        hojaServicio.setSociedad(details.get(0).getString("CompanyCode"));
                                        hojaServicio.setNroGuiaRemision(details.get(0).getString("ReferenceDocument"));
                                        hojaServicio.setNumeroLote(details.get(0).getString("Batch"));
                                        hojaServicio.setNumeroRecepcion(MaterialDocument);
                                        hojaServicio.setEntregaCompleta(details.get(0).getBoolean("IsCompletelyDelivered"));

                                        String usuarioNombreRecepcion = "";

                                        String url_sap_recepcion = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                        OkHttpClient client_recepcion = new OkHttpClient().newBuilder().build();

                                        Request postRequest_pk_recepcion = new Request.Builder()
                                        .url(url_sap_recepcion)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Basic " + encodedAuth)                       
                                        .get()
                                        .build();

                                        Response postResponse_pk_recepcion = client_recepcion.newCall(postRequest_pk_recepcion).execute();

                                        if (postResponse_pk_recepcion.isSuccessful()) {

                                            String responseBody_recepcion = postResponse_pk_recepcion.body().string();
                                            JSONObject jsonObject_recepcion = new JSONObject(responseBody_recepcion);
                                            JSONObject dObject_recepcion = jsonObject_recepcion.getJSONObject("d");
                                            JSONArray resultsArray_recepcion = dObject_recepcion.getJSONArray("results");

                                            if (resultsArray_recepcion.length() > 0) {
                                                JSONObject firstResult = resultsArray_recepcion.getJSONObject(0); // Tomar el primer elemento
                                                usuarioNombreRecepcion = firstResult.getString("PersonFullName");
                                                
                                                System.out.println("PersonFullName: " + usuarioNombreRecepcion);
                                            } else {
                                                System.out.println("No hay resultados disponibles.");
                                            }
                                        }

                                        hojaServicio.setUsuarioNombreRecepcion(usuarioNombreRecepcion);

                                        hojaServicio.setStatusSap(statusSap);
                                        if (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion()))
                                            hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                        else // statusSap == "Borrado"
                                            hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());
            
                                        OrdenCompra ordenCompra = new OrdenCompra();
                                        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);
            
                                        if (optionalOrdenCompra.isPresent()) {
                                            ordenCompra = optionalOrdenCompra.get();
            
                                            // AQUI MECANICA QUE MODIFICA OC ENCONTRADA A ESTADO "APROBADA" (por proveedor) (SOLO SI HES SUBE ACEPTADA)
                                            hojaServicio = this.actualizarDocumentoAceptacionAndOrdenCompra("HES", hojaServicio, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);

                                            hojaServicio.setCodigoMoneda(ordenCompra.getCodigoMondeda());
                                            hojaServicio.setProveedorRuc(ordenCompra.getProveedorRuc());
                                            hojaServicio.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());
                                        }
            
                                        if(hojaServicio.getIdOrdenCompra() != null) { // si es que la OC no ha sido publicada, no se publica la nueva HES
                                            
                                            String NumeroHES = "";
                                            // Buscamos HES

                                            String url_numero_hes = urlSap + "/sap/opu/odata4/sap/api_serviceentrysheet/srvd_a2x/sap/serviceentrysheet/0001/ServiceEntrySheet?$filter=MaterialDocument eq '" + MaterialDocument + "'";
                                            OkHttpClient client_numero_hes = new OkHttpClient().newBuilder().build();

                                            Request postRequest_pk_numero_hes = new Request.Builder()
                                            .url(url_numero_hes)
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("Accept", "application/json")
                                            .addHeader("Authorization", "Basic " + encodedAuth)                       
                                            .get()
                                            .build();

                                            Response postResponse_pk_numero_hes = client_numero_hes.newCall(postRequest_pk_numero_hes).execute();

                                            if (postResponse_pk_numero_hes.isSuccessful()) {

                                                String responseBody_numero_hes = postResponse_pk_numero_hes.body().string();
                                                JSONObject jsonObject_numero_hes = new JSONObject(responseBody_numero_hes);
                                                JSONArray resultsArray_numero_hes = jsonObject_numero_hes.getJSONArray("value");

                                                if (resultsArray_numero_hes.length() > 0) {
                                                    JSONObject firstObject = resultsArray_numero_hes.getJSONObject(0);
                                                    NumeroHES = firstObject.getString("ServiceEntrySheet");

                                                    System.out.println("NumeroHES: " + NumeroHES);
                                                } else {
                                                    System.out.println("No hay resultados disponibles.");
                                                }
                                            }

                                            hojaServicio.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                            hojaServicio.setNumeroDocumentoAceptacion(NumeroHES);

                                            
                                            if(NumeroHES.equals("")){
                                                continue;
                                            }

                                            hojaServicio = documentoAceptacionRepository.save(hojaServicio);

                                            List<DocumentoAceptacionDetalle> detalleHojaServicio = new ArrayList<>();

                                            for (JSONObject detail : details) {

                                                List<BienServicio> servicios = bienServicioService.getListBienServicioByCodigoSap(detail.getString("Material"));
                                                DocumentoAceptacionDetalle hojaServicioDetalle = new DocumentoAceptacionDetalle();

                                                hojaServicioDetalle.setIdDocumentoAceptacion(hojaServicio.getId());
                                                hojaServicioDetalle.setNumeroDocumentoAceptacion(NumeroHES);//MaterialDocument
                                                hojaServicioDetalle.setNumeroOrdenCompra(numeroOrdenCompra);
                                                hojaServicioDetalle.setPosicionOrdenCompra(detail.getString("PurchaseOrderItem"));
                                                hojaServicioDetalle.setCodigoSapBienServicio(detail.getString("Material"));
                                                hojaServicioDetalle.setDescripcionBienServicio(servicios.get(0).getDescripcion());
                                                hojaServicioDetalle.setUnidadMedida(detail.getString("EntryUnit"));
                                                hojaServicioDetalle.setCantidadAceptadaCliente(BigDecimal.valueOf(Double.valueOf(detail.getString("QuantityInEntryUnit"))));
                                                hojaServicioDetalle.setPrecioUnitario(BigDecimal.valueOf(0.00)); //No viene de public
                                                hojaServicioDetalle.setValorRecibido(BigDecimal.valueOf(0.00));//No viene de public
                                                hojaServicioDetalle.setValorRecibidoMonedalocal(BigDecimal.valueOf(0.00));//No viene de public
                                                hojaServicioDetalle.setIndicadorImpuesto("");//No viene de public
                                                hojaServicioDetalle.setNumeroItem(Integer.parseInt(detail.getString("PurchaseOrderItem")));
                                                hojaServicioDetalle.setUnidadMedida(servicios.get(0).getUnidadMedida().getCodigoSap());
                                                hojaServicioDetalle.setNroGuiaRemision(detail.getString("ReferenceDocument"));//No viene
                                                //hojaServicioDetalle.setNumeroGuiaProveedor(details.get(0).getString("ReferenceDocument"));
                
                                                Integer idEstadoHojaServicio = hojaServicio.getIdEstadoDocumentoAceptacion();
                                                if (idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) == 0)
                                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                                else // idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ANULADO.getId()) == 0
                                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());
                
    
                                                documentoAceptacionDetalleRepository.save(hojaServicioDetalle);
                                                detalleHojaServicio.add(hojaServicioDetalle);
                                            }

                                            if (enviarCorreoAprobacion ) {
                                                //Enviando correo
                                                String respuesta = "";
                                                Usuario comprador = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
                                                if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                                    //respuesta = contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(), ordenCompra,null, comprador);
                                                    respuesta =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion("HES", parametroMapper.getMailSetting(),hojaServicio, null, comprador,detalleHojaServicio);

                                                LogTransaccion logTransaccion = new LogTransaccion();
                                                logTransaccion.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                                                logTransaccion.setRespuestaCodigo(respuesta);
                                                logTransaccion.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                                                this.logTransaccionRepository.save(logTransaccion);
                                                Proveedor proveedor = proveedorService.getProveedorByRuc(ordenCompra.getProveedorRuc());

                                                if(proveedor == null){
                                                    //CONSULTAR EL API DE SAP PARA TRAER EL BP
                                                    proveedor = new Proveedor();
                                                    JSONObject supplier = this.consultarSupplier(details.get(0).getString("Supplier"));
                                                    if(supplier !=null){
                                                        //Validar si Existe correo
                                                        if(!supplier.getString("EmailAddress").isEmpty()){
                                                            proveedor.setEmail(supplier.getString("EmailAddress"));
                                                            proveedor.setRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                                            proveedor.setRuc(supplier.getString("BPTaxNumber"));
                                                        }
                                                    }
                                                }

                                                Usuario proveedorUsuario = null;
                                                if(proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()){
                                                    proveedorUsuario = new Usuario();
                                                    proveedorUsuario.setEmail(proveedor.getEmail());
                                                    proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                                }
                                                else{
                                                    List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompra.getProveedorRuc());
                                                    if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                                        proveedorUsuario = posibleProveedorList.get(0);
                                                }
                                                String respuesta1 = "";

                                                if(proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                                                    respuesta1 =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion("HES", parametroMapper.getMailSetting(),hojaServicio, proveedorUsuario, null,detalleHojaServicio);


                                                LogTransaccion logTransaccion1 = new LogTransaccion();
                                                logTransaccion1.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                                                logTransaccion1.setRespuestaCodigo(respuesta1);
                                                logTransaccion1.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                                                this.logTransaccionRepository.save(logTransaccion1);
                                            }

                                        }
                                    }
                                }
                                */
                            }
                            else
                            {

                                Integer idEntradaMercaderiaExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumero(MaterialDocument);

                                if (idEntradaMercaderiaExistente == null) { // si no existe previamente la entrada de mercaderia
                                    
                                    boolean esEmDeAnulacion = movimientosDeAnulacion.contains(details.get(0).getString("GoodsMovementType"));
                                    boolean procedePublicar = true;
                                    String numeroOrdenCompra = details.get(0).getString("PurchaseOrder");
                                    DocumentoAceptacion entradaMercaderia = new DocumentoAceptacion();

                                    if(procedePublicar) {
                                        entradaMercaderia.setNumeroDocumentoAceptacion(MaterialDocument);

                                        entradaMercaderia.setIdEstadoDocumentoAceptacion(this.asignarIdEstadoDocumentoAceptacion(details.get(0).getString("GoodsMovementType")));
                                        entradaMercaderia.setNumeroOrdenCompra(numeroOrdenCompra);
                                        entradaMercaderia.setNumeroGuiaProveedor(details.get(0).getString("ReferenceDocument")); //no viene
                                        entradaMercaderia.setUsuarioSapRecepcion(details.get(0).getString("CreatedByUser"));
                                        entradaMercaderia.setCodigoMoneda(details.get(0).getString("CompanyCodeCurrency"));
                                        entradaMercaderia.setFechaEmision(convertStringToDate(details.get(0).getString("CreationDate")));
                                        entradaMercaderia.setValorImpuesto(BigDecimal.valueOf(0.00));
                                        entradaMercaderia.setSociedad(details.get(0).getString("CompanyCode"));
                                        entradaMercaderia.setNroGuiaRemision(details.get(0).getString("ReferenceDocument"));
                                        entradaMercaderia.setNumeroLote(details.get(0).getString("Batch"));
                                        entradaMercaderia.setNumeroRecepcion(MaterialDocument);
                                        entradaMercaderia.setEntregaCompleta(details.get(0).getBoolean("IsCompletelyDelivered"));
                                        entradaMercaderia.setStatusSap("Aceptado");
                                        String usuarioNombreRecepcion = "";

                                        String url_sap_recepcion = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                        OkHttpClient client_recepcion = new OkHttpClient().newBuilder().build();

                                        Request postRequest_pk_recepcion = new Request.Builder()
                                        .url(url_sap_recepcion)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Basic " + encodedAuth)                       
                                        .get()
                                        .build();
                                        Response postResponse_pk_recepcion = client_recepcion.newCall(postRequest_pk_recepcion).execute();

                                        if (postResponse_pk_recepcion.isSuccessful()) {

                                            String responseBody_recepcion = postResponse_pk_recepcion.body().string();
                                            JSONObject jsonObject_recepcion = new JSONObject(responseBody_recepcion);
                                            JSONObject dObject_recepcion = jsonObject_recepcion.getJSONObject("d");
                                            JSONArray resultsArray_recepcion = dObject_recepcion.getJSONArray("results");

                                            if (resultsArray_recepcion.length() > 0) {
                                                JSONObject firstResult = resultsArray_recepcion.getJSONObject(0); // Tomar el primer elemento
                                                usuarioNombreRecepcion = firstResult.getString("PersonFullName");
                                                
                                                System.out.println("PersonFullName: " + usuarioNombreRecepcion);
                                            } else {
                                                System.out.println("No hay resultados disponibles.");
                                            }
                                        }

                                        entradaMercaderia.setUsuarioNombreRecepcion(usuarioNombreRecepcion);

                                        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);
                                        OrdenCompra ordenCompra = new OrdenCompra();

                                        if (optionalOrdenCompra.isPresent()) {
                                            ordenCompra = optionalOrdenCompra.get();
                                            // AQUI MECANICA QUE MODIFICA OC ENCONTRADA A ESTADO "APROBADA" (por proveedor) (SOLO SI EM tiene Mov 101,105)
                                            entradaMercaderia = this.actualizarDocumentoAceptacionAndOrdenCompra("EM", entradaMercaderia, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);

                                            entradaMercaderia.setCodigoMoneda(ordenCompra.getCodigoMondeda());
                                            entradaMercaderia.setProveedorRuc(ordenCompra.getProveedorRuc());
                                            entradaMercaderia.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());
                                        } 

                                        if (entradaMercaderia.getIdOrdenCompra() != null) { // si es que la OC no ha sido publicada, no se publica la EM
                                            entradaMercaderia.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                            logger.error(" // WRITING NEW EM: " + entradaMercaderia.toString());
                                            logger.error("idTipoDocAceptacion" + entradaMercaderia.getIdTipoDocumentoAceptacion());

                                            if (details.get(0).getString("GoodsMovementType").contains("122")) {
                                                entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.DEVOLUCION_SALIDA.getId());
                                                logger.error("Entro 1");
                                            } else {
                                                entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.ENTRADA_MERCADERIA.getId());
                                            }
                                            entradaMercaderia = documentoAceptacionRepository.save(entradaMercaderia);
                                            Integer idEntradaMercaderia = entradaMercaderia.getId();
                                            List<DocumentoAceptacionDetalle> detalleEntrada = new ArrayList<>();
                                            
                                            for (JSONObject detail : details) {
                                            
                                                DocumentoAceptacionDetalle entradaMercaderiaDetalle = new DocumentoAceptacionDetalle();
                                                String movimiento = detail.getString("GoodsMovementType");

                                                List<BienServicio> materiales = bienServicioService.getListBienServicioByCodigoSap(detail.getString("Material"));
                            
                                                if(materiales.size() > 0){
                                                    entradaMercaderiaDetalle.setDescripcionBienServicio(materiales.get(0).getDescripcion());
                                                    entradaMercaderiaDetalle.setUnidadMedida(materiales.get(0).getUnidadMedida().getCodigoSap());
                                                }

                                                entradaMercaderiaDetalle.setIdDocumentoAceptacion(idEntradaMercaderia);
                                                entradaMercaderiaDetalle.setIdEstadoDocumentoAceptacionDetalle(this.asignarIdEstadoDocumentoAceptacion(movimiento));
                                                entradaMercaderiaDetalle.setNumeroDocumentoAceptacion(MaterialDocument);
                                                entradaMercaderiaDetalle.setNumeroItem(Integer.valueOf(detail.getString("PurchaseOrderItem")));
                                                entradaMercaderiaDetalle.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));
                                                entradaMercaderiaDetalle.setPosicionOrdenCompra(detail.getString("PurchaseOrderItem"));
                                                entradaMercaderiaDetalle.setCodigoSapBienServicio(detail.getString("Material"));
                                                //entradaMercaderiaDetalle.setDescripcionBienServicio("");//No viene
                                                //entradaMercaderiaDetalle.setUnidadMedida(detail.getString("EntryUnit"));
                                                entradaMercaderiaDetalle.setCantidadAceptadaCliente(BigDecimal.valueOf(Double.valueOf(detail.getString("QuantityInEntryUnit"))));
                                                entradaMercaderiaDetalle.setCantidadPendiente(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setValorRecibidoMonedalocal(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setPrecioUnitario(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setValorRecibido(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setMovimiento(movimiento);
                                                entradaMercaderiaDetalle.setIndicadorImpuesto("");//No viene
                                                entradaMercaderiaDetalle.setNroGuiaRemision(detail.getString("ReferenceDocument"));//No viene

                                                documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);
                                                detalleEntrada.add(entradaMercaderiaDetalle);
                                            }

                                            if (enviarCorreoAprobacion ) {
                                                //Enviando correo
                                                String respuesta = "";
                                                Usuario comprador = usuarioRepository.findByCodigoSap(ordenCompra.getCompradorUsuarioSap());
                                                if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                                    //respuesta = contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(), ordenCompra,null, comprador);
                                                    respuesta =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion("EM", parametroMapper.getMailSetting(),entradaMercaderia, null, comprador,detalleEntrada);

                                                LogTransaccion logTransaccion = new LogTransaccion();
                                                logTransaccion.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                                                logTransaccion.setRespuestaCodigo(respuesta);
                                                logTransaccion.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                                                this.logTransaccionRepository.save(logTransaccion);
                                                Proveedor proveedor = proveedorService.getProveedorByRuc(ordenCompra.getProveedorRuc());

                                                if(proveedor == null){
                                                    //CONSULTAR EL API DE SAP PARA TRAER EL BP
                                                    proveedor = new Proveedor();
                                                    JSONObject supplier = this.consultarSupplier(details.get(0).getString("Supplier"));
                                                    if(supplier !=null){
                                                        //Validar si Existe correo
                                                        if(!supplier.getString("EmailAddress").isEmpty()){
                                                            proveedor.setEmail(supplier.getString("EmailAddress"));
                                                            proveedor.setRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                                            proveedor.setRuc(supplier.getString("BPTaxNumber"));
                                                        }
                                                    }
                                                }

                                                Usuario proveedorUsuario = null;
                                                if(proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()){
                                                    proveedorUsuario = new Usuario();
                                                    proveedorUsuario.setEmail(proveedor.getEmail());
                                                    proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                                }
                                                else{
                                                    List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(ordenCompra.getProveedorRuc());
                                                    if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                                        proveedorUsuario = posibleProveedorList.get(0);
                                                }
                                                String respuesta1 = "";

                                                if(proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                                                    //respuesta1 =   contactoAprobadaRechazadaOCNotificacion.enviar(parametroMapper.getMailSetting(),ordenCompra, proveedorUsuario, null);
                                                    respuesta1 =   contactoAprobadaRechazadaOCNotificacion.enviarDocumentoAceptacion("EM", parametroMapper.getMailSetting(),entradaMercaderia, proveedorUsuario, null,detalleEntrada);


                                                LogTransaccion logTransaccion1 = new LogTransaccion();
                                                logTransaccion1.setEnvioTrama("actualizarDocumentoAceptacionAndOrdenCompra");
                                                logTransaccion1.setRespuestaCodigo(respuesta1);
                                                logTransaccion1.setTipoRegistro("contactoAprobadaRechazadaOCNotificacion");
                                                this.logTransaccionRepository.save(logTransaccion1);
                                            }

                                        }


                                    }
                                }   
                            }
                        }
                    } catch (JSONException e) {
                        LogTransaccion logTransaccion2 = new LogTransaccion();
                        logTransaccion2.setEnvioTrama(e.getMessage());
                        logTransaccion2.setRespuestaCodigo(e.getMessage());
                        logTransaccion2.setTipoRegistro("ExcepcionLogsAceptacion2");
                        this.logTransaccionRepository.save(logTransaccion2);
                        System.err.println("Error al procesar el JSON: " + e.getMessage());
                    }
                    
                
                }

               logger.error("FINISHED");

                if(daProcessing.get())
                    daProcessing.set(!daProcessing.get());
            }
            catch (Exception e){
                if(daProcessing.get())
                    daProcessing.set(!daProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                LogTransaccion logTransaccion3 = new LogTransaccion();
                logTransaccion3.setEnvioTrama(e.getMessage());
                logTransaccion3.setRespuestaCodigo(e.getMessage());
                logTransaccion3.setTipoRegistro("ExcepcionLogsAceptacion3");
                this.logTransaccionRepository.save(logTransaccion3);
                throw new Exception(e);
            }
        }
        else{
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + " // DocumentoAceptacion Extraction esta procesando");
        }
    }


    @Override
    public void extraerDocumentoAceptacionListMaterialDocument(String materialDocument, boolean extraccionUnicoDocumento, boolean aprobarOrdenCompra, boolean enviarCorreoAprobacion) throws Exception {

        if(!daProcessing.get() || extraccionUnicoDocumento) {

            if(!daProcessing.get() && !extraccionUnicoDocumento)
                daProcessing.set(!daProcessing.get());

            try{

                List<String> movimientosDeAnulacion = Arrays.asList(MovimientoEntregaMercaderiaTipoEnum.ANULACION_EM.getCodigo(),MovimientoEntregaMercaderiaTipoEnum.ANULACION_2_PASO.getCodigo());


                String url_sap = urlSap + "/sap/opu/odata/sap/YY1_MATERIALDOCUMENT_CDS/YY1_MaterialDocument?$filter=MaterialDocument eq '"+materialDocument+"'";
                String usrSap = userSap;
                String pwdSap = passwordSap;
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

                Response postResponse_pk = client.newCall(postRequest_pk).execute();

                if (postResponse_pk.isSuccessful()) {
                    String responseBody = postResponse_pk.body().string();

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
                            String MaterialDocument = result.getString("MaterialDocument");

                            // Obtener la lista existente o crear una nueva si no existe
                            groupedData.computeIfAbsent(MaterialDocument, k -> new ArrayList<>()).add(result);
                        }


                        // Iterar sobre el mapa para procesar las cabeceras y detalles
                        for (Map.Entry<String, List<JSONObject>> entry : groupedData.entrySet()) {
                            String MaterialDocument = entry.getKey();
                            List<JSONObject> details = entry.getValue();

                            System.out.println("Procesando EM HES: " + MaterialDocument);

                            String tipo = "";

                            /*Validamos si el producto principal es es S o M */
                            List<BienServicio> bienServicios = bienServicioService.getListBienServicioByCodigoSap(details.get(0).getString("Material"));

                            if(bienServicios.size() > 0){

                                String tipoValidacion = bienServicios.get(0).getTipoItem();

                                if ("MATERIAL".equals(tipoValidacion)) {
                                    tipo = "EM";
                                } else if ("SERVICIO".equals(tipoValidacion)) {
                                    tipo = "HES";
                                }
                            }

                            //details.get(0).getString("GoodsMovementType")

                            /*VALIDAMOS SI ES HES o EM por el CAMPO GoodsMovementType */
                            if(tipo.equals("HES"))
                            {
                                Integer idHojaServicioExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumeroRecepcion(MaterialDocument);

                                if(idHojaServicioExistente == null){ // si no existe previamente la hoja de entrada de servicio

                                    String statusSap = "Aceptado";  //No se tiene dato desde Public

                                    if (statusSap != null && !statusSap.isEmpty()
                                            && (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion())
                                            || statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.BORRADO.getDescripcion()))) { // solo publica si statusSap existe

                                        //String header2 = header1.concat("HES: " + numeroHojaServicio);
                                        String numeroOrdenCompra = details.get(0).getString("PurchaseOrder");
                                        String posicionOrdencompra = details.get(0).getString("PurchaseOrderItem");
                                        DocumentoAceptacion hojaServicio = new DocumentoAceptacion();

                                        hojaServicio.setNumeroDocumentoAceptacion(MaterialDocument);
                                        hojaServicio.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.HOJA_ENTRADA_SERVICIO.getId());
                                        hojaServicio.setNumeroOrdenCompra(numeroOrdenCompra);
                                        hojaServicio.setPosicionOrdenCompra(posicionOrdencompra);
                                        hojaServicio.setUsuarioSapRecepcion(details.get(0).getString("CreatedByUser"));
                                        hojaServicio.setCodigoMoneda(details.get(0).getString("CompanyCodeCurrency"));
                                        hojaServicio.setFechaEmision(convertStringToDate(details.get(0).getString("CreationDate")));
                                        hojaServicio.setFechaAceptacion(convertStringToDate(details.get(0).getString("PostingDate")));
                                        hojaServicio.setValorImpuesto(BigDecimal.valueOf(0.00));
                                        hojaServicio.setSociedad(details.get(0).getString("CompanyCode"));
                                        hojaServicio.setNroGuiaRemision(details.get(0).getString("ReferenceDocument"));
                                        hojaServicio.setNumeroLote(details.get(0).getString("Batch"));
                                        hojaServicio.setNumeroRecepcion(MaterialDocument);
                                        hojaServicio.setEntregaCompleta(details.get(0).getBoolean("IsCompletelyDelivered"));

                                        String usuarioNombreRecepcion = "";

                                        String url_sap_recepcion = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                        OkHttpClient client_recepcion = new OkHttpClient().newBuilder().build();

                                        Request postRequest_pk_recepcion = new Request.Builder()
                                                .url(url_sap_recepcion)
                                                .addHeader("Content-Type", "application/json")
                                                .addHeader("Accept", "application/json")
                                                .addHeader("Authorization", "Basic " + encodedAuth)
                                                .get()
                                                .build();

                                        Response postResponse_pk_recepcion = client_recepcion.newCall(postRequest_pk_recepcion).execute();

                                        if (postResponse_pk_recepcion.isSuccessful()) {

                                            String responseBody_recepcion = postResponse_pk_recepcion.body().string();
                                            JSONObject jsonObject_recepcion = new JSONObject(responseBody_recepcion);
                                            JSONObject dObject_recepcion = jsonObject_recepcion.getJSONObject("d");
                                            JSONArray resultsArray_recepcion = dObject_recepcion.getJSONArray("results");

                                            if (resultsArray_recepcion.length() > 0) {
                                                JSONObject firstResult = resultsArray_recepcion.getJSONObject(0); // Tomar el primer elemento
                                                usuarioNombreRecepcion = firstResult.getString("PersonFullName");

                                                System.out.println("PersonFullName: " + usuarioNombreRecepcion);
                                            } else {
                                                System.out.println("No hay resultados disponibles.");
                                            }
                                        }

                                        hojaServicio.setUsuarioNombreRecepcion(usuarioNombreRecepcion);

                                        hojaServicio.setStatusSap(statusSap);
                                        if (statusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion()))
                                            hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                        else // statusSap == "Borrado"
                                            hojaServicio.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                                        OrdenCompra ordenCompra = new OrdenCompra();
                                        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);

                                        if (optionalOrdenCompra.isPresent()) {
                                            ordenCompra = optionalOrdenCompra.get();

                                            // AQUI MECANICA QUE MODIFICA OC ENCONTRADA A ESTADO "APROBADA" (por proveedor) (SOLO SI HES SUBE ACEPTADA)
                                            hojaServicio = this.actualizarDocumentoAceptacionAndOrdenCompra("HES", hojaServicio, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);

                                            hojaServicio.setCodigoMoneda(ordenCompra.getCodigoMondeda());
                                            hojaServicio.setProveedorRuc(ordenCompra.getProveedorRuc());
                                            hojaServicio.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());
                                        }

                                        if(hojaServicio.getIdOrdenCompra() != null) { // si es que la OC no ha sido publicada, no se publica la nueva HES

                                            String NumeroHES = "";
                                            /* Buscamos HES */

                                            String url_numero_hes = urlSap + "/sap/opu/odata4/sap/api_serviceentrysheet/srvd_a2x/sap/serviceentrysheet/0001/ServiceEntrySheet?$filter=MaterialDocument eq '" + MaterialDocument + "'";
                                            OkHttpClient client_numero_hes = new OkHttpClient().newBuilder().build();

                                            Request postRequest_pk_numero_hes = new Request.Builder()
                                                    .url(url_numero_hes)
                                                    .addHeader("Content-Type", "application/json")
                                                    .addHeader("Accept", "application/json")
                                                    .addHeader("Authorization", "Basic " + encodedAuth)
                                                    .get()
                                                    .build();

                                            Response postResponse_pk_numero_hes = client_numero_hes.newCall(postRequest_pk_numero_hes).execute();

                                            if (postResponse_pk_numero_hes.isSuccessful()) {

                                                String responseBody_numero_hes = postResponse_pk_numero_hes.body().string();
                                                JSONObject jsonObject_numero_hes = new JSONObject(responseBody_numero_hes);
                                                JSONArray resultsArray_numero_hes = jsonObject_numero_hes.getJSONArray("value");

                                                if (resultsArray_numero_hes.length() > 0) {
                                                    JSONObject firstObject = resultsArray_numero_hes.getJSONObject(0);
                                                    NumeroHES = firstObject.getString("ServiceEntrySheet");

                                                    System.out.println("NumeroHES: " + NumeroHES);
                                                } else {
                                                    System.out.println("No hay resultados disponibles.");
                                                }
                                            }

                                            hojaServicio.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                            hojaServicio.setNumeroDocumentoAceptacion(NumeroHES);

                                            if(NumeroHES.equals("")){
                                                continue;
                                            }

                                            hojaServicio = documentoAceptacionRepository.save(hojaServicio);

                                            for (JSONObject detail : details) {

                                                List<BienServicio> servicios = bienServicioService.getListBienServicioByCodigoSap(detail.getString("Material"));
                                                DocumentoAceptacionDetalle hojaServicioDetalle = new DocumentoAceptacionDetalle();

                                                hojaServicioDetalle.setIdDocumentoAceptacion(hojaServicio.getId());
                                                hojaServicioDetalle.setNumeroDocumentoAceptacion(NumeroHES);//MaterialDocument
                                                hojaServicioDetalle.setNumeroOrdenCompra(numeroOrdenCompra);
                                                hojaServicioDetalle.setPosicionOrdenCompra(detail.getString("PurchaseOrderItem"));
                                                hojaServicioDetalle.setCodigoSapBienServicio(detail.getString("Material"));
                                                hojaServicioDetalle.setDescripcionBienServicio(servicios.get(0).getDescripcion());
                                                hojaServicioDetalle.setUnidadMedida(detail.getString("EntryUnit"));
                                                hojaServicioDetalle.setCantidadAceptadaCliente(BigDecimal.valueOf(Double.valueOf(detail.getString("QuantityInEntryUnit"))));
                                                hojaServicioDetalle.setPrecioUnitario(BigDecimal.valueOf(0.00)); //No viene de public
                                                hojaServicioDetalle.setValorRecibido(BigDecimal.valueOf(0.00));//No viene de public
                                                hojaServicioDetalle.setValorRecibidoMonedalocal(BigDecimal.valueOf(0.00));//No viene de public
                                                hojaServicioDetalle.setIndicadorImpuesto("");//No viene de public
                                                hojaServicioDetalle.setNumeroItem(Integer.parseInt(detail.getString("PurchaseOrderItem")));
                                                hojaServicioDetalle.setUnidadMedida(servicios.get(0).getUnidadMedida().getCodigoSap());
                                                hojaServicioDetalle.setNroGuiaRemision(detail.getString("ReferenceDocument"));//No viene
                                                //hojaServicioDetalle.setNumeroGuiaProveedor(details.get(0).getString("ReferenceDocument"));

                                                Integer idEstadoHojaServicio = hojaServicio.getIdEstadoDocumentoAceptacion();
                                                if (idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) == 0)
                                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ACTIVO.getId());
                                                else // idEstadoHojaServicio.compareTo(DocumentoAceptacionEstadoEnum.ANULADO.getId()) == 0
                                                    hojaServicioDetalle.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());


                                                documentoAceptacionDetalleRepository.save(hojaServicioDetalle);
                                            }
                                        }
                                    }
                                }
                                else{

                                    /*DocumentoAceptacion hojaServicioExistente = documentoAceptacionRepository.getDocumentoAceptacionById(DocumentoAceptacionTipoEnum.HOJA_ENTRADA_SERVICIO.getId(),idHojaServicioExistente);

                                    String hesExistenteStatusSap = Optional.ofNullable(hojaServicioExistente.getStatusSap()).orElse("");
                                    String hesExtraidaStatusSap = "Aceptado";  //No se tiene dato desde Public
                                    Integer IdEstadoHesExistente  = Optional.ofNullable(hojaServicioExistente.getIdEstadoDocumentoAceptacion()).orElse(-1);

                                    if(hesExistenteStatusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.ACEPTADO.getDescripcion())
                                            && hesExtraidaStatusSap.equalsIgnoreCase(DocumentoAceptacionStatusSapEnum.BORRADO.getDescripcion())){
                                        hojaServicioExistente.setStatusSap(hesExtraidaStatusSap);

                                        if(IdEstadoHesExistente.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId())==0
                                                || IdEstadoHesExistente.compareTo(DocumentoAceptacionEstadoEnum.PREFACTURADO.getId())==0) {
                                            hojaServicioExistente.setIdEstadoDocumentoAceptacion(DocumentoAceptacionEstadoEnum.ANULADO.getId());

                                            DocumentoAceptacionDetalle hojaServicioDetalleExistente = hojaServicioExistente.getDocumentoAceptacionDetalleList().get(0);
                                            hojaServicioDetalleExistente.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());
                                            documentoAceptacionDetalleRepository.save(hojaServicioDetalleExistente);
                                        }

                                        documentoAceptacionRepository.save(hojaServicioExistente);
                                    }*/
                                }

                            }
                            else
                            {

                                Integer idEntradaMercaderiaExistente = documentoAceptacionRepository.getIdDocumentoAceptacionByNumero(MaterialDocument);

                                if (idEntradaMercaderiaExistente == null) { // si no existe previamente la entrada de mercaderia

                                    boolean esEmDeAnulacion = movimientosDeAnulacion.contains(details.get(0).getString("GoodsMovementType"));
                                    boolean procedePublicar = true;
                                    String numeroOrdenCompra = details.get(0).getString("PurchaseOrder");
                                    DocumentoAceptacion entradaMercaderia = new DocumentoAceptacion();


                                    /*if(esEmDeAnulacion){ // si es una EM de Anulacion, busca si ya este publicada la EM cuyas posiciones apunta a anular y que se encuentre en estado ACTIVO
                                        Optional<DocumentoAceptacion> opDocAceptacionRelacionado = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(primerItem.getNumDocApectacionRelacionado());
                                        if(!opDocAceptacionRelacionado.isPresent() || opDocAceptacionRelacionado.get().getIdEstadoDocumentoAceptacion().compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) != 0)
                                            procedePublicar = false;
                                    }*/

                                    if(procedePublicar) {
                                        entradaMercaderia.setNumeroDocumentoAceptacion(MaterialDocument);

                                        entradaMercaderia.setIdEstadoDocumentoAceptacion(this.asignarIdEstadoDocumentoAceptacion(details.get(0).getString("GoodsMovementType")));
                                        entradaMercaderia.setNumeroOrdenCompra(numeroOrdenCompra);
                                        entradaMercaderia.setNumeroGuiaProveedor(details.get(0).getString("ReferenceDocument")); //no viene
                                        entradaMercaderia.setUsuarioSapRecepcion(details.get(0).getString("CreatedByUser"));
                                        entradaMercaderia.setCodigoMoneda(details.get(0).getString("CompanyCodeCurrency"));
                                        entradaMercaderia.setFechaEmision(convertStringToDate(details.get(0).getString("CreationDate")));
                                        entradaMercaderia.setValorImpuesto(BigDecimal.valueOf(0.00));
                                        entradaMercaderia.setSociedad(details.get(0).getString("CompanyCode"));
                                        entradaMercaderia.setNroGuiaRemision(details.get(0).getString("ReferenceDocument"));
                                        entradaMercaderia.setNumeroLote(details.get(0).getString("Batch"));
                                        entradaMercaderia.setNumeroRecepcion(MaterialDocument);
                                        entradaMercaderia.setEntregaCompleta(details.get(0).getBoolean("IsCompletelyDelivered"));
                                        entradaMercaderia.setStatusSap("Aceptado");
                                        String usuarioNombreRecepcion = "";

                                        String url_sap_recepcion = urlSap +  "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                        OkHttpClient client_recepcion = new OkHttpClient().newBuilder().build();

                                        Request postRequest_pk_recepcion = new Request.Builder()
                                                .url(url_sap_recepcion)
                                                .addHeader("Content-Type", "application/json")
                                                .addHeader("Accept", "application/json")
                                                .addHeader("Authorization", "Basic " + encodedAuth)
                                                .get()
                                                .build();
                                        Response postResponse_pk_recepcion = client_recepcion.newCall(postRequest_pk_recepcion).execute();

                                        if (postResponse_pk_recepcion.isSuccessful()) {

                                            String responseBody_recepcion = postResponse_pk_recepcion.body().string();
                                            JSONObject jsonObject_recepcion = new JSONObject(responseBody_recepcion);
                                            JSONObject dObject_recepcion = jsonObject_recepcion.getJSONObject("d");
                                            JSONArray resultsArray_recepcion = dObject_recepcion.getJSONArray("results");

                                            if (resultsArray_recepcion.length() > 0) {
                                                JSONObject firstResult = resultsArray_recepcion.getJSONObject(0); // Tomar el primer elemento
                                                usuarioNombreRecepcion = firstResult.getString("PersonFullName");

                                                System.out.println("PersonFullName: " + usuarioNombreRecepcion);
                                            } else {
                                                System.out.println("No hay resultados disponibles.");
                                            }
                                        }

                                        entradaMercaderia.setUsuarioNombreRecepcion(usuarioNombreRecepcion);

                                        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);
                                        OrdenCompra ordenCompra = new OrdenCompra();

                                        if (optionalOrdenCompra.isPresent()) {
                                            ordenCompra = optionalOrdenCompra.get();
                                            // AQUI MECANICA QUE MODIFICA OC ENCONTRADA A ESTADO "APROBADA" (por proveedor) (SOLO SI EM tiene Mov 101,105)
                                            entradaMercaderia = this.actualizarDocumentoAceptacionAndOrdenCompra("EM", entradaMercaderia, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);

                                            entradaMercaderia.setCodigoMoneda(ordenCompra.getCodigoMondeda());
                                            entradaMercaderia.setProveedorRuc(ordenCompra.getProveedorRuc());
                                            entradaMercaderia.setProveedorRazonSocial(ordenCompra.getProveedorRazonSocial());
                                        } /*else {
                                            if (!esEmDeAnulacion) { // trata de publicar su OC solo si no es una EM de anulacion
                                                try {
                                                    jcoOrdenCompraPublicarOneService.extraerOneOrdenCompraRFC(numeroOrdenCompra, false);
                                                    Optional<OrdenCompra> optionalOrdenCompraExtraida = ordenCompraRepository.getOrdenCompraLiberadaActivaValidaByNumero(numeroOrdenCompra);

                                                    if (optionalOrdenCompraExtraida.isPresent()) {
                                                        ordenCompra = optionalOrdenCompraExtraida.get();
                                                        logger.error(" // OC PUBLICADA: " + ordenCompra.toString());

                                                        // AQUI MECANICA QUE MODIFICA OC PUBLICADA A ESTADO "APROBADA" (por proveedor) (SOLO SI EM tiene Mov 101,105)
                                                        entradaMercaderia = this.actualizarDocumentoAceptacionAndOrdenCompra("EM", entradaMercaderia, ordenCompra, aprobarOrdenCompra, enviarCorreoAprobacion);
                                                    } else {
                                                        logger.error(" // NO SE PUBLICO OC: " + numeroOrdenCompra);
                                                    }
                                                } catch (Exception e) {
                                                    logger.error(" // ERROR AL EXTRAER OC: " + numeroOrdenCompra);
                                                }
                                            }
                                        }*/

                                        if (entradaMercaderia.getIdOrdenCompra() != null) { // si es que la OC no ha sido publicada, no se publica la EM
                                            entradaMercaderia.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                            logger.error(" // WRITING NEW EM: " + entradaMercaderia.toString());
                                            logger.error("idTipoDocAceptacion" + entradaMercaderia.getIdTipoDocumentoAceptacion());

                                            if (details.get(0).getString("GoodsMovementType").contains("122")) {
                                                entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.DEVOLUCION_SALIDA.getId());
                                                logger.error("Entro 1");
                                            } else {
                                                entradaMercaderia.setIdTipoDocumentoAceptacion(DocumentoAceptacionTipoEnum.ENTRADA_MERCADERIA.getId());
                                            }
                                            entradaMercaderia = documentoAceptacionRepository.save(entradaMercaderia);
                                            Integer idEntradaMercaderia = entradaMercaderia.getId();

                                            for (JSONObject detail : details) {

                                                DocumentoAceptacionDetalle entradaMercaderiaDetalle = new DocumentoAceptacionDetalle();
                                                String movimiento = detail.getString("GoodsMovementType");

                                                List<BienServicio> materiales = bienServicioService.getListBienServicioByCodigoSap(detail.getString("Material"));

                                                if(materiales.size() > 0){
                                                    entradaMercaderiaDetalle.setDescripcionBienServicio(materiales.get(0).getDescripcion());
                                                    entradaMercaderiaDetalle.setUnidadMedida(materiales.get(0).getUnidadMedida().getCodigoSap());
                                                }

                                                entradaMercaderiaDetalle.setIdDocumentoAceptacion(idEntradaMercaderia);
                                                entradaMercaderiaDetalle.setIdEstadoDocumentoAceptacionDetalle(this.asignarIdEstadoDocumentoAceptacion(movimiento));
                                                entradaMercaderiaDetalle.setNumeroDocumentoAceptacion(MaterialDocument);
                                                entradaMercaderiaDetalle.setNumeroItem(Integer.valueOf(detail.getString("PurchaseOrderItem")));
                                                entradaMercaderiaDetalle.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));
                                                entradaMercaderiaDetalle.setPosicionOrdenCompra(detail.getString("PurchaseOrderItem"));
                                                entradaMercaderiaDetalle.setCodigoSapBienServicio(detail.getString("Material"));
                                                //entradaMercaderiaDetalle.setDescripcionBienServicio("");//No viene
                                                //entradaMercaderiaDetalle.setUnidadMedida(detail.getString("EntryUnit"));
                                                entradaMercaderiaDetalle.setCantidadAceptadaCliente(BigDecimal.valueOf(Double.valueOf(detail.getString("QuantityInEntryUnit"))));
                                                entradaMercaderiaDetalle.setCantidadPendiente(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setValorRecibidoMonedalocal(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setPrecioUnitario(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setValorRecibido(BigDecimal.valueOf(Double.valueOf(0)));//No viene
                                                entradaMercaderiaDetalle.setMovimiento(movimiento);
                                                entradaMercaderiaDetalle.setIndicadorImpuesto("");//No viene
                                                entradaMercaderiaDetalle.setNroGuiaRemision(detail.getString("ReferenceDocument"));//No viene

                                                documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);

                                                /*if (movimientosDeAnulacion.contains(details.get(0).getString("GoodsMovementType"))) {
                                                    String numEmPorAnular = item.getNumDocApectacionRelacionado();
                                                    Integer numItemPorAnular = item.getNumItemRelacionado();
                                                    String posicionOrdenCompra = item.getPosicionOrdenCompra();
                                                    BigDecimal cantidadAceptadaCliente = item.getCantidadAceptadaClienteMaterial();

                                                    entradaMercaderiaDetalle.setNumDocApectacionRelacionado(numEmPorAnular);
                                                    entradaMercaderiaDetalle.setNumItemRelacionado(numItemPorAnular);

                                                    logger.error(header2 + " // WRITING NEW EM POS (ANUL): " + entradaMercaderiaDetalle.toString());
                                                    documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);
            //                                    Optional<DocumentoAceptacionDetalle> optionalEntradaMercaderiaDetalleExistente = documentoAceptacionDetalleRepository.findByNumeroDocumentoAceptacionAndNumeroItem(numEmPorAnular, numItemPorAnular);
                                                    Optional<DocumentoAceptacionDetalle> optionalEntradaMercaderiaDetalleExistente = documentoAceptacionDetalleRepository.findByNumeroDocumentoAceptacionAndPosicionOrdenCompraAndCantidadAceptadaCliente(numEmPorAnular, posicionOrdenCompra, cantidadAceptadaCliente);
                                                    DocumentoAceptacionDetalle entradaMercaderiaDetalleExistente = new DocumentoAceptacionDetalle();

                                                    if (optionalEntradaMercaderiaDetalleExistente.isPresent()) {
                                                        entradaMercaderiaDetalleExistente = optionalEntradaMercaderiaDetalleExistente.get();
                                                        logger.error(header2 + " // FOUND EM POS (POR ANULAR): " + entradaMercaderiaDetalleExistente.toString());
                                                        Integer idEstadoItem = entradaMercaderiaDetalleExistente.getIdEstadoDocumentoAceptacionDetalle();

                                                        if (idEstadoItem.compareTo(DocumentoAceptacionEstadoEnum.ACTIVO.getId()) == 0
                                                                || idEstadoItem.compareTo(DocumentoAceptacionEstadoEnum.TRANSITO.getId()) == 0) {
                                                            entradaMercaderiaDetalleExistente.setIdEstadoDocumentoAceptacionDetalle(DocumentoAceptacionEstadoEnum.ANULADO.getId());
                                                            logger.error(header2 + " // ANULANDO EM POS: " + entradaMercaderiaDetalleExistente.toString());
                                                            documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalleExistente);
                                                            String numEntradaMercaderiaExistente = entradaMercaderiaDetalleExistente.getNumeroDocumentoAceptacion();

                                                            if (!numEntradaMercaderiaPosibleAnuladaList.contains(numEntradaMercaderiaExistente))
                                                                numEntradaMercaderiaPosibleAnuladaList.add(numEntradaMercaderiaExistente);
                                                        }
                                                    } else {
                                                        logger.error(" // FOUND EM POS (POR ANULAR): " + optionalEntradaMercaderiaDetalleExistente.toString());
                                                    }
                                                } else {
                                                    logger.error(header2 + " // WRITING NEW EM POS: " + entradaMercaderiaDetalle.toString());
                                                    documentoAceptacionDetalleRepository.save(entradaMercaderiaDetalle);
                                                }*/
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        System.err.println("Error al procesar el JSON: " + e.getMessage());
                    }


                }

                logger.error("FINISHED");

                if(daProcessing.get())
                    daProcessing.set(!daProcessing.get());
            }
            catch (Exception e){
                if(daProcessing.get())
                    daProcessing.set(!daProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                throw new Exception(e);
            }
        }
        else{
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + " // DocumentoAceptacion Extraction esta procesando");
        }
    }


    public static Date convertStringToDate(String dateText) {
        String millisecondsString = dateText.replaceAll("[^0-9]", "");
        long milliseconds = Long.parseLong(millisecondsString);
        return new Date(milliseconds);
    }

    public JSONObject consultarSupplier(String codSuplier) throws IOException {
        /*Obtener datos del supplier */
        String url_sap_supplier = urlSap + "/sap/opu/odata/sap/YY1_PROVEEDOR_DBM_CDS/YY1_Proveedor_DBM?$filter=Supplier eq '"+codSuplier+"'";
        OkHttpClient client_supplier = new OkHttpClient().newBuilder().build();

        String usrSap = userSap;
        String pwdSap = passwordSap;
        String authString = usrSap + ":" + pwdSap;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());


        Request postRequest_pk_supplier = new Request.Builder()
                .url(url_sap_supplier)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Basic " + encodedAuth)
                .get()
                .build();

        Response postResponse_pk_supplier = client_supplier.newCall(postRequest_pk_supplier).execute();

        if (postResponse_pk_supplier.isSuccessful()) {

            String responseBody_supplier= postResponse_pk_supplier.body().string();
            JSONObject jsonObject_supplier = new JSONObject(responseBody_supplier);
            JSONObject dObject_supplier = jsonObject_supplier.getJSONObject("d");
            JSONArray resultsArray_supplier = dObject_supplier.getJSONArray("results");

            if (resultsArray_supplier.length() > 0) {
                JSONObject firstResult = resultsArray_supplier.getJSONObject(0); // Tomar el primer elemento
                return firstResult;
            } else {
                return null;
            }
        }
        return null;
    }

}