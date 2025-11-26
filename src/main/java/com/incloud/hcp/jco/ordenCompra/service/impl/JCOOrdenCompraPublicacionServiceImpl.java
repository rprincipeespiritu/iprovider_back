package com.incloud.hcp.jco.ordenCompra.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.domain.CondicionPago;
import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.domain.OrdenCompraDetalleTexto;
import com.incloud.hcp.domain.OrdenCompraDetalleTextoMaterialAmpliado;
import com.incloud.hcp.domain.OrdenCompraDetalleTextoRegistroInfo;
import com.incloud.hcp.domain.OrdenCompraTextoCabecera;
import com.incloud.hcp.domain.Parametro;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.TipoOrdenCompra;
import com.incloud.hcp.domain.Usuario;
import com.incloud.hcp.dto.ConsultaOrdenCompra;
import com.incloud.hcp.dto.ProveedorDto;
import com.incloud.hcp.enums.OpcionGenericaEnum;
import com.incloud.hcp.enums.OrdenCompraEstadoEnum;
import com.incloud.hcp.enums.OrdenCompraEstadoSapEnum;
import com.incloud.hcp.enums.OrdenCompraTipoEnum;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPublicacionService;
import com.incloud.hcp.repository.BienServicioRepository;
import com.incloud.hcp.repository.CentroAlmacenRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.OrdenCompraDetalleRepository;
import com.incloud.hcp.repository.OrdenCompraDetalleTextoMaterialAmpliadoRepository;
import com.incloud.hcp.repository.OrdenCompraDetalleTextoRegistroInfoRepository;
import com.incloud.hcp.repository.OrdenCompraDetalleTextoRepository;
import com.incloud.hcp.repository.OrdenCompraRepository;
import com.incloud.hcp.repository.OrdenCompraTextoCabeceraRepository;
import com.incloud.hcp.repository.UsuarioRepository;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.service.CondicionPagoService;
import com.incloud.hcp.service.ParametroService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.notificacion.ContactoPublicadaOCNotificacion;
import com.incloud.hcp.util.DateUtils;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;

import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOOrdenCompraPublicacionServiceImpl implements JCOOrdenCompraPublicacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Value("${API_URL_SAP}")
    private String urlSap;
    @Value("${USERNAME_SAP}")
    private String userSap;
    @Value("${PASSWORD_SAP}")
    private String passwordSap;

    private final AtomicBoolean ocProcessing = new AtomicBoolean(false);

    private UsuarioRepository usuarioRepository;
    private OrdenCompraRepository ordenCompraRepository;
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;
    private OrdenCompraDetalleTextoRepository ordenCompraDetalleTextoRepository;
    private ContactoPublicadaOCNotificacion contactoPublicadaOCNotificacion;
    private OrdenCompraTextoCabeceraRepository ordenCompraTextoCabeceraRepository;
    private OrdenCompraDetalleTextoRegistroInfoRepository ordenCompraDetalleTextoRegistroInfoRepository;
    private OrdenCompraDetalleTextoMaterialAmpliadoRepository ordenCompraDetalleTextoMaterialAmpliadoRepository;
    private ProveedorService proveedorService;
    private CondicionPagoService condicionPagoService;
    private ParametroService parametroService;
    private CentroAlmacenRepository centroAlmacenRepository;


    @Autowired
    public JCOOrdenCompraPublicacionServiceImpl(UsuarioRepository usuarioRepository,
                                                OrdenCompraRepository ordenCompraRepository,
                                                OrdenCompraDetalleRepository ordenCompraDetalleRepository,
                                                OrdenCompraDetalleTextoRepository ordenCompraDetalleTextoRepository,
                                                ContactoPublicadaOCNotificacion contactoPublicadaOCNotificacion,
                                                OrdenCompraTextoCabeceraRepository ordenCompraTextoCabeceraRepository,
                                                OrdenCompraDetalleTextoRegistroInfoRepository ordenCompraDetalleTextoRegistroInfoRepository,
                                                OrdenCompraDetalleTextoMaterialAmpliadoRepository ordenCompraDetalleTextoMaterialAmpliadoRepository,
                                                ProveedorService proveedorService,
                                                CondicionPagoService condicionPagoService,
                                                ParametroService parametroService,
                                                CentroAlmacenRepository centroAlmacenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.ordenCompraDetalleRepository = ordenCompraDetalleRepository;
        this.ordenCompraDetalleTextoRepository = ordenCompraDetalleTextoRepository;
        this.contactoPublicadaOCNotificacion = contactoPublicadaOCNotificacion;
        this.ordenCompraTextoCabeceraRepository = ordenCompraTextoCabeceraRepository;
        this.ordenCompraDetalleTextoRegistroInfoRepository = ordenCompraDetalleTextoRegistroInfoRepository;
        this.ordenCompraDetalleTextoMaterialAmpliadoRepository = ordenCompraDetalleTextoMaterialAmpliadoRepository;
        this.proveedorService = proveedorService;
        this.condicionPagoService = condicionPagoService;
        this.parametroService = parametroService;
        this.centroAlmacenRepository = centroAlmacenRepository;
    }

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Autowired
    private BienServicioRepository bienServicioRepository;

    @Override
    public ConsultaOrdenCompra extraerOrdenCompraListRFC_old(String fechaInicio, String fechaFin, boolean enviarCorreoPublicacion) throws Exception {
        ConsultaOrdenCompra consultaOrdenCompra = new ConsultaOrdenCompra();
        if (!ocProcessing.get()) {
            ocProcessing.set(!ocProcessing.get());
            try {


                logger.error("RFC PROVEEDOR", "JCO DESTINATION MANAGER Exe");
                String tramaXMLactualizar = armarTrama(fechaInicio, fechaFin);
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
                String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_COMPRAS_DETAIL?sap-client=400";

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
                post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_COMPRAS_DETAIL/ZMM_COMPRAS_DETAILRequest");
                post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_COMPRAS_DETAIL/ZMM_COMPRAS_DETAILRequest");
                post.setHeader("Content-Type", "text/xml;charset=UTF-8");
                post.setHeader("Accept-Encoding", "gzip,deflate");

                post.setEntity(strEntity);

                logger.info("RFC: Trama Entity" + strEntity);

                logger.info("HTTP POS" + post.toString());

                HttpResponse response4 = client.execute(post);
                HttpEntity respEntity = response4.getEntity();
                String result = EntityUtils.toString(respEntity);


                if (!result.contains("<soap-env:Fault>")) {
                    DocumentBuilderFactory domFactory = DocumentBuilderFactory
                            .newInstance();
                    domFactory.setNamespaceAware(true);
                    DocumentBuilder builder = domFactory.newDocumentBuilder();
                    Document doc = builder
                            .parse(new InputSource(new StringReader(result)));

                    logger.info("RFC: Resultado :");
                    logger.info(result);

                    NodeList NodeOrdenCompraSapList = doc.getElementsByTagName("PO_HEADER").item(0).getChildNodes();
                    NodeList NodeOrdenCompraTextoCabeceraSapList = doc.getElementsByTagName("PO_TEXTO_CABECERA").item(0).getChildNodes();
                    NodeList NodeOrdenCompraDetalleSapList = doc.getElementsByTagName("PO_ITEMS_AUX").item(0).getChildNodes();
                    NodeList NodeOrdenCompraDetalleTextoPosicionSapList = doc.getElementsByTagName("PO_TEXT").item(0).getChildNodes();
                    NodeList NodeOrdenCompraDetalleTextoRegistroInfoSapList = doc.getElementsByTagName("PO_TEXTO_REG_POS").item(0).getChildNodes();
                    NodeList NodeOrdenCompraDetalleTextoMaterialAmpliadoSapList = doc.getElementsByTagName("PO_TEXTO_AMPL_MAT").item(0).getChildNodes();

                    List<OrdenCompra> ordenCompraSapList = Optional.ofNullable(OrdenCompraExtractorMapper.getOrdenCompraList(NodeOrdenCompraSapList)).orElse(new ArrayList<>());
                    List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraSapList = Optional.ofNullable(OrdenCompraExtractorMapper.getOrdenCompraTextoCabeceraList(NodeOrdenCompraTextoCabeceraSapList)).orElse(new ArrayList<>());
                    List<OrdenCompraDetalle> ordenCompraDetalleSapList = Optional.ofNullable(OrdenCompraExtractorMapper.getOrdenCompraDetalleList(NodeOrdenCompraDetalleSapList)).orElse(new ArrayList<>());
                    List<OrdenCompraDetalleTexto> ordenCompraDetalleTextoPosicionSapList = Optional.ofNullable(OrdenCompraExtractorMapper.getOrdenCompraDetalleTextoList(NodeOrdenCompraDetalleTextoPosicionSapList)).orElse(new ArrayList<>());
                    List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoSapList = Optional.ofNullable(OrdenCompraExtractorMapper.getOrdenCompraDetalleTextoRegistroInfoList(NodeOrdenCompraDetalleTextoRegistroInfoSapList)).orElse(new ArrayList<>());
                    List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoSapList = Optional.ofNullable(OrdenCompraExtractorMapper.getOrdenCompraDetalleTextoMaterialAmpliadoList(NodeOrdenCompraDetalleTextoMaterialAmpliadoSapList)).orElse(new ArrayList<>());

                    String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR OC -- RANGO: " + fechaInicio + " - " + fechaFin + "  ";
                    logger.error(header1 + "Rango de Fechas : " + fechaInicio + " - " + fechaFin);
                    logger.error(header1 + "CANTIDAD DE OC ENCONTRADOS: " + ordenCompraSapList.size());
                    logger.error(header1 + "CANTIDAD DE OCD ENCONTRADOS: " + ordenCompraDetalleSapList.size());
                    ordenCompraSapList.forEach(oc -> {
                        String numOrdenCompra = oc.getNumeroOrdenCompra();
                        Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero(numOrdenCompra);
                        String header2 = header1.concat("OC: " + oc.getNumeroOrdenCompra());

                        if (oc.getNumeroOrdenCompra().equals("4590036439")) {
                            String dato = "waaaaa aqui es";
                        }

                        if (!optionalOrdenCompra.isPresent()) {  //OC no existe en HANA
                            if (!oc.getEstadoSap().equalsIgnoreCase(OrdenCompraEstadoSapEnum.NOLIBERADA.getCodigo())) {  //solo publicar OC si esta en estado liberado
                                oc.setVersion(1); // porque OC es publicada por 1ra vez
                                oc.setIsActive(OpcionGenericaEnum.SI.getCodigo());  //OC es activa porque es la 1ra y unica version
                                oc.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId());  //estado inicial publicada
                                oc.setFechaPublicacion(DateUtils.getCurrentTimestamp());

                                logger.error(header2 + "  WRITING NEW OC: " + oc.toString());
                                oc = ordenCompraRepository.save(oc);
                                Integer idOrdenCompra = oc.getId();
                                Integer idTipoOrdenCompra = oc.getIdTipoOrdenCompra();

                                ordenCompraTextoCabeceraSapList.stream()
                                        .filter(octc -> octc.getNumeroOrdenCompra().equals(numOrdenCompra))
                                        .forEach(octc -> {
                                            octc.setIdOrdenCompra(idOrdenCompra);

                                            logger.error(header2 + "  WRITING NEW OCTC: " + octc.toString());
                                            ordenCompraTextoCabeceraRepository.save(octc);
                                        });

                                final BigDecimal[] valorImpuesto = {BigDecimal.valueOf(0.0)};
                                ordenCompraDetalleSapList.stream()
                                        .filter(ocd -> ocd.getNumeroOrdenCompra().equals(numOrdenCompra))
                                        .forEach(ocd -> {
                                            ocd.setIdOrdenCompra(idOrdenCompra);
                                            ocd.setTipoPosicion(idTipoOrdenCompra == OrdenCompraTipoEnum.MATERIAL.getId() ? "M" : "S");

                                            valorImpuesto[0] = ocd.getValorImpuesto();
                                            BigDecimal cantidadBase = ocd.getPrecioTotal();
                                            BigDecimal precioUnitarioBase = ocd.getPrecioUnitario();
                                            BigDecimal precioUnitario = precioUnitarioBase.divide(cantidadBase, 4, RoundingMode.HALF_UP);

                                            ocd.setPrecioUnitario(precioUnitario);
                                            ocd.setPrecioTotal(ocd.getCantidad().multiply(precioUnitario).setScale(4, RoundingMode.HALF_UP));

                                            logger.error(header2 + "  WRITING NEW OCD: " + ocd.toString());
                                            ocd = ordenCompraDetalleRepository.save(ocd);
                                            Integer idOrdenCompraDetalle = ocd.getId();
                                            String posicion = ocd.getPosicion();

                                            ordenCompraDetalleTextoPosicionSapList.stream()
                                                    .filter(ocdt -> ocdt.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdt.getPosicion().equals(posicion))
                                                    .forEach(ocdt -> {
                                                        ocdt.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDT: " + ocdt.toString());
                                                        ordenCompraDetalleTextoRepository.save(ocdt);
                                                    });

                                            ordenCompraDetalleTextoRegistroInfoSapList.stream()
                                                    .filter(ocdtri -> ocdtri.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtri.getPosicion().equals(posicion))
                                                    .forEach(ocdtri -> {
                                                        ocdtri.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDTRI: " + ocdtri.toString());
                                                        ordenCompraDetalleTextoRegistroInfoRepository.save(ocdtri);
                                                    });

                                            ordenCompraDetalleTextoMaterialAmpliadoSapList.stream()
                                                    .filter(ocdtma -> ocdtma.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtma.getPosicion().equals(posicion))
                                                    .forEach(ocdtma -> {
                                                        ocdtma.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDTMA: " + ocdtma.toString());
                                                        ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ocdtma);
                                                    });
                                        });

                                oc.setValorImpuesto(valorImpuesto[0]);

                                oc = ordenCompraRepository.save(oc);
                                if (enviarCorreoPublicacion) {
                                    /*Enviando Correo*/
                                    String respuesta = "";
                                    Usuario comprador = usuarioRepository.findByCodigoSap(oc.getCompradorUsuarioSap());
                                    if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                        respuesta = contactoPublicadaOCNotificacion.enviar(oc, null, comprador, null);
                                    LogTransaccion logTransaccion = new LogTransaccion();
                                    logTransaccion.setEnvioTrama("contactoPublicadaOCNotificacion");
                                    logTransaccion.setRespuestaCodigo(respuesta);
                                    logTransaccion.setTipoRegistro("extraerOrdenCompraListRFC");
                                    this.logTransaccionRepository.save(logTransaccion);

                                    Proveedor proveedor = proveedorService.getProveedorByRuc(oc.getProveedorRuc());
                                    Usuario proveedorUsuario = null;
                                    if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                                        proveedorUsuario = new Usuario();
                                        proveedorUsuario.setEmail(proveedor.getEmail());
                                        proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                    } else {
                                        List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(oc.getProveedorRuc());
                                        if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                            proveedorUsuario = posibleProveedorList.get(0);
                                    }
                                    String respuesta1 = "";
                                    if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())

                                        respuesta1 = contactoPublicadaOCNotificacion.enviar(oc, proveedorUsuario, null, null);

                                    LogTransaccion logTransaccion1 = new LogTransaccion();
                                    logTransaccion1.setEnvioTrama("contactoPublicadaOCNotificacion");
                                    logTransaccion1.setRespuestaCodigo(respuesta);
                                    logTransaccion1.setTipoRegistro("extraerOrdenCompraListRFC");
                                    this.logTransaccionRepository.save(logTransaccion);
                                }
                            }
                        } else { // OC ya existe en HANA
                            OrdenCompra ocAnterior = optionalOrdenCompra.get();
                            Date fechaModAnterior = ocAnterior.getFechaModificacion();
                            Time horaModAnterior = ocAnterior.getHoraModificacion();
                            Date fechaModNueva = oc.getFechaModificacion();
                            Time horaModNueva = oc.getHoraModificacion();

                            boolean procede;
                            logger.error("ocAnterior.getIdEstadoOrdenCompra() " + ocAnterior.getIdEstadoOrdenCompra() + "-" + OrdenCompraEstadoEnum.APROBADA.getId() + "-" + ocAnterior.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.APROBADA.getId()));
                            if (ocAnterior.getIdEstadoOrdenCompra().compareTo(OrdenCompraEstadoEnum.APROBADA.getId()) == 0) {
                                procede = false;
                            } else {
                                logger.error("horas " + fechaModAnterior + "-" + horaModAnterior + "-" + fechaModNueva + "-" + horaModNueva);
                                // evalua si la fecha y hora de modificacion del nuevo registro es mayor a la del registro existente
                                procede = DateUtils.evaluarModificacionDeDocumento(fechaModAnterior, horaModAnterior, fechaModNueva, horaModNueva);
                            }
                            logger.error("procede " + procede);
                            if (procede) {
                                logger.error("oc.getEstadoSap() " + oc.getEstadoSap());
                                //if oc.getEstadoSap(); //Estoy poniendo esto para probar  mizalo
                                //if (oc.getEstadoSap().equalsIgnoreCase(OrdenCompraEstadoSapEnum.LIBERADA.getCodigo())) { // llega registro OC liberada
                                if (oc.getEstadoSap() == "1" && oc.getEstadoSap() == "L") {//.equalsIgnoreCase(OrdenCompraEstadoSapEnum.LIBERADA.getCodigo())) {
                                    logger.error("ocAnterior.getEstadoSap() " + ocAnterior.getEstadoSap());
                                    if (ocAnterior.getEstadoSap().equalsIgnoreCase(OrdenCompraEstadoSapEnum.BLOQUEADA.getCodigo())
                                            || ocAnterior.getEstadoSap().equalsIgnoreCase(OrdenCompraEstadoSapEnum.LIBERADA.getCodigo())) {
                                        ocAnterior.setIsActive(OpcionGenericaEnum.NO.getCodigo());//  la version anterior pasa a inactiva (no se visualizara)
                                        ocAnterior = ordenCompraRepository.save(ocAnterior);

                                        oc.setVersion(ocAnterior.getVersion() + 1);  //numero de version sgte al actual
                                        oc.setIsActive(OpcionGenericaEnum.SI.getCodigo()); // la ultima version es la unica activa (que se va a visualizar)
                                        oc.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId());  //estado inicial "Activa" (publicada)
                                        oc.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                        oc.setIdTipoOrdenCompra(ocAnterior.getIdTipoOrdenCompra());
                                        logger.error(header2 + "  WRITING NEXT VERSION OC: " + oc.toString());
                                        oc = ordenCompraRepository.save(oc);
                                        Integer idOrdenCompra = oc.getId();
                                        Integer idTipoOrdenCompra = oc.getIdTipoOrdenCompra();

                                        ordenCompraTextoCabeceraSapList.stream()
                                                .filter(octc -> octc.getNumeroOrdenCompra().equals(numOrdenCompra))
                                                .forEach(octc -> {
                                                    octc.setIdOrdenCompra(idOrdenCompra);

                                                    logger.error(header2 + "  WRITING NEW OCTC: " + octc.toString());
                                                    ordenCompraTextoCabeceraRepository.save(octc);
                                                });
                                        final BigDecimal[] valorImpuesto = {BigDecimal.valueOf(0.0)};
                                        ordenCompraDetalleSapList.stream()
                                                .filter(ocd -> ocd.getNumeroOrdenCompra().equals(numOrdenCompra))
                                                .forEach(ocd -> {

                                                    valorImpuesto[0] = ocd.getValorImpuesto();

                                                    ocd.setIdOrdenCompra(idOrdenCompra);
                                                    ocd.setTipoPosicion(idTipoOrdenCompra == OrdenCompraTipoEnum.MATERIAL.getId() ? "M" : "S");

                                                    BigDecimal cantidadBase = ocd.getPrecioTotal();
                                                    BigDecimal precioUnitarioBase = ocd.getPrecioUnitario();
                                                    BigDecimal precioUnitario = precioUnitarioBase.divide(cantidadBase, 4, RoundingMode.HALF_UP);

                                                    ocd.setPrecioUnitario(precioUnitario);
                                                    ocd.setPrecioTotal(ocd.getCantidad().multiply(precioUnitario).setScale(4, RoundingMode.HALF_UP));

                                                    if (ocd.getCodigoSapBienServicio() != null && !ocd.getCodigoSapBienServicio().isEmpty()) {
                                                        ocd.setCodigoSapBienServicio(String.valueOf(Integer.parseInt(ocd.getCodigoSapBienServicio())));
                                                    }

                                                    logger.error(header2 + "  WRITING NEXT VERSION OCD: " + ocd.toString());
                                                    ocd = ordenCompraDetalleRepository.save(ocd);
                                                    Integer idOrdenCompraDetalle = ocd.getId();
                                                    String posicion = ocd.getPosicion();

                                                    ordenCompraDetalleTextoPosicionSapList.stream()
                                                            .filter(ocdt -> ocdt.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdt.getPosicion().equals(posicion))
                                                            .forEach(ocdt -> {
                                                                ocdt.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                                logger.error(header2 + "  WRITING NEW OCDT: " + ocdt.toString());
                                                                ordenCompraDetalleTextoRepository.save(ocdt);
                                                            });

                                                    ordenCompraDetalleTextoRegistroInfoSapList.stream()
                                                            .filter(ocdtri -> ocdtri.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtri.getPosicion().equals(posicion))
                                                            .forEach(ocdtri -> {
                                                                ocdtri.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                                logger.error(header2 + "  WRITING NEW OCDTRI: " + ocdtri.toString());
                                                                ordenCompraDetalleTextoRegistroInfoRepository.save(ocdtri);
                                                            });

                                                    ordenCompraDetalleTextoMaterialAmpliadoSapList.stream()
                                                            .filter(ocdtma -> ocdtma.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtma.getPosicion().equals(posicion))
                                                            .forEach(ocdtma -> {
                                                                ocdtma.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                                logger.error(header2 + "  WRITING NEW OCDTMA: " + ocdtma.toString());
                                                                ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ocdtma);
                                                            });
                                                });

                                        oc.setValorImpuesto(valorImpuesto[0]);
                                        oc = ordenCompraRepository.save(oc);

                                        if (enviarCorreoPublicacion) {
                                            /*Enviando Correo*/
                                            Usuario comprador = usuarioRepository.findByCodigoSap(oc.getCompradorUsuarioSap());
                                            String respuesta = "";
                                            if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())


                                                respuesta = contactoPublicadaOCNotificacion.enviar(oc, null, comprador, null);
                                            LogTransaccion logTransaccion = new LogTransaccion();
                                            logTransaccion.setEnvioTrama("contactoPublicadaOCNotificacion");
                                            logTransaccion.setRespuestaCodigo(respuesta);
                                            logTransaccion.setTipoRegistro("extraerOrdenCompraListRFC");
                                            this.logTransaccionRepository.save(logTransaccion);

                                            Proveedor proveedor = proveedorService.getProveedorByRuc(oc.getProveedorRuc());
                                            Usuario proveedorUsuario = null;
                                            if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                                                proveedorUsuario = new Usuario();
                                                proveedorUsuario.setEmail(proveedor.getEmail());
                                                proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                            } else {
                                                List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(oc.getProveedorRuc());
                                                if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                                    proveedorUsuario = posibleProveedorList.get(0);
                                            }
                                            String respuesta1 = "";
                                            if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty())
                                                respuesta = contactoPublicadaOCNotificacion.enviar(oc, proveedorUsuario, null, null);
                                            LogTransaccion logTransaccion1 = new LogTransaccion();
                                            logTransaccion1.setEnvioTrama("contactoPublicadaOCNotificacion");
                                            logTransaccion1.setRespuestaCodigo(respuesta1);
                                            logTransaccion1.setTipoRegistro("extraerOrdenCompraListRFC");
                                            this.logTransaccionRepository.save(logTransaccion);
                                        }
                                    }
                                } else { // llega registro OC bloqueada o anulada
                                    ocAnterior.setEstadoSap(oc.getEstadoSap());
                                    ocAnterior.setFechaModificacion(fechaModNueva);
                                    ocAnterior.setHoraModificacion(horaModNueva);

                                    if (oc.getEstadoSap().equalsIgnoreCase(OrdenCompraEstadoSapEnum.ANULADA.getCodigo())) {
                                        ocAnterior.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ANULADA.getId());
                                    }

                                    logger.error(header2 + "  MOD CURRENT VERSION OC: " + ocAnterior.toString());
                                    ordenCompraRepository.save(ocAnterior);
                                }
                            }
                        }
                    });
                    logger.error(header1 + "FINISHED");

                    consultaOrdenCompra.setOrdenCompraSapList(ordenCompraSapList);
                    consultaOrdenCompra.setOrdenCompraTextoCabeceraSapList(ordenCompraTextoCabeceraSapList);
                    consultaOrdenCompra.setOrdenCompraDetalleSapList(ordenCompraDetalleSapList);
                    consultaOrdenCompra.setOrdenCompraDetalleTextoRegistroInfoSapList(ordenCompraDetalleTextoRegistroInfoSapList);
                    consultaOrdenCompra.setOrdenCompraDetalleTextoMaterialAmpliadoSapList(ordenCompraDetalleTextoMaterialAmpliadoSapList);
                    consultaOrdenCompra.setOrdenCompraDetalleTextoPosicionSapList(ordenCompraDetalleTextoPosicionSapList);

                }


                String FUNCION_RFC = "ZPE_MM_COMPRAS_DETAIL";
//--------------------------------------------------------------------------------------------------------


                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
            } catch (Exception e) {
                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
                logger.error(e.getMessage(), e.getCause());
                throw new Exception(e);
            }
        } else {
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + "  OrdenCompra Extraction esta procesando");
        }
        return consultaOrdenCompra;
    }

    @Transactional
    public ConsultaOrdenCompra extraerOrdenCompraListRFC(String fechaInicio, String fechaFin, boolean enviarCorreoPublicacion) throws Exception {

        ConsultaOrdenCompra consultaOrdenCompra = new ConsultaOrdenCompra();
        if (!ocProcessing.get()) {
            ocProcessing.set(!ocProcessing.get());
            try {

                List<CondicionPago> condiciones = condicionPagoService.getListAll();
                List<Parametro> Impuestos = parametroService.getByModuloandTipo("INDICADOR_IMPUESTO", "INDICADOR_IMPUESTO");
                List<Parametro> AreasSolicitante = parametroService.getByModuloandTipo("AREA_SOLICITANTE", "AREA_SOLICITANTE");
                List<CentroAlmacen> centroAlmacens = centroAlmacenRepository.findAll();

                /* */
                String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ORDENCOMPRA_CDS/YY1_OrdenCompra?$filter=LastChangeDateTime ge datetimeoffset'" + fechaInicio + "Z' and LastChangeDateTime le datetimeoffset'" + fechaFin + "Z' and CompanyCode eq 'P100'";
                //String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ORDENCOMPRA_CDS/YY1_OrdenCompra?$filter=PurchaseOrderDate ge datetime'" + fechaInicio+ "' and PurchaseOrderDate le datetime'" + fechaFin + "' and PurchasingProcessingStatus eq '05' and CompanyCode eq 'P100'";
                //String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ORDENCOMPRA_CDS/YY1_OrdenCompra?$filter=LastChangeDateTime ge datetimeoffset'2025-01-14T12:00:00Z'";

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
                            String purchaseOrder = result.getString("PurchaseOrder");

                            // Obtener la lista existente o crear una nueva si no existe
                            groupedData.computeIfAbsent(purchaseOrder, k -> new ArrayList<>()).add(result);
                        }

                        logger.error("Rango de Fechas : " + fechaInicio + " - " + fechaFin);
                        logger.error("CANTIDAD DE OC ENCONTRADOS: " + groupedData.size());
                        logger.error("CANTIDAD DE OCD ENCONTRADOS: " + resultsArray.length());

                        // Iterar sobre el mapa para procesar las cabeceras y detalles
                        for (Map.Entry<String, List<JSONObject>> entry : groupedData.entrySet()) {
                            String purchaseOrder = entry.getKey();
                            List<JSONObject> details = entry.getValue();

                            System.out.println("Procesando Orden de Compra: " + purchaseOrder);

                            //if(!purchaseOrder.equals("4500000409")){
                            //    continue;
                            //}

                            //Optional<OrdenCompra> optionalOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero(purchaseOrder);
                            OrdenCompra optionalOrdenCompra = ordenCompraRepository.findTopByNumeroOrdenCompraAndIsActive(purchaseOrder, "1");
                            if (optionalOrdenCompra == null) {
                                OrdenCompra oc = new OrdenCompra();

                                oc.setVersion(1); // porque OC es publicada por 1ra vez
                                oc.setIsActive(OpcionGenericaEnum.SI.getCodigo());  //OC es activa porque es la 1ra y unica version
                                oc.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId());  //estado inicial publicada
                                oc.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                oc.setNumeroOrdenCompra(purchaseOrder);
                                oc.setProveedorCodigoSap(details.get(0).getString("Supplier"));
                                oc.setCondicionPago(details.get(0).getString("PaymentTerms"));

                                /*Lugar de entrega :D */


                                String lugar_entrega = centroAlmacens.stream()
                                        .filter(centroAlmacen -> centroAlmacen.getCodigoSap().equals(details.get(0).getString("Plant")))
                                        .<String>map(centroAlmacen -> String.format("%s - %s - %s - %s",
                                                Objects.toString(centroAlmacen.getDireccion(), ""),
                                                Objects.toString(centroAlmacen.getDistrito(), ""),
                                                Objects.toString(centroAlmacen.getDepartamento(), ""),
                                                Objects.toString(centroAlmacen.getPais(), "")))
                                        .findFirst()
                                        .orElse("");

                                oc.setLugarEntrega(lugar_entrega);

                                /*Buscamos en la tabla Area */
                                String AreaSolicitante = AreasSolicitante.stream()
                                        .filter(area -> area.getCodigo().equals(details.get(0).getString("AreaSolicitante_PDH")))
                                        .map(Parametro::getDescripcion)
                                        .findFirst()
                                        .orElse("");

                                oc.setSedeSolicitud(AreaSolicitante);

                                /*Buscamos a condicion de pago */
                                String descripcion = condiciones.stream()
                                        .filter(condicion -> condicion.getCodigoSap().equals(details.get(0).getString("PaymentTerms")))
                                        .map(CondicionPago::getDescripcion)
                                        .findFirst()
                                        .orElse("");

                                oc.setCondicionPagoDescripcion(descripcion);

                                oc.setCodigoMondeda(details.get(0).getString("DocumentCurrency"));
                                oc.setCompradorUsuarioSap(details.get(0).getString("CreatedByUser"));


                                Optional<Parametro> impuestoOptional = Impuestos.stream()
                                        .filter(impuesto -> impuesto.getCodigo().equals(details.get(0).getString("TaxCode")))
                                        .findFirst();

                                String descripcionImpuesto = impuestoOptional.map(Parametro::getDescripcion).orElse("");
                                double valorImpuesto = impuestoOptional
                                        .map(Parametro::getValor) // Assuming this returns a String
                                        .map(Double::parseDouble) // Convert the String to double
                                        .orElse(0.0);

                                /*String descripcionImpuesto = Impuestos.stream()
                                .filter(impuesto -> impuesto.getCodigo().equals(details.get(0).getString("TaxCode")))
                                .map(Parametro::getDescripcion)
                                .findFirst()
                                .orElse("");*/

                                oc.setIndicadorImpuesto(descripcionImpuesto);

                                Integer tipoOC = 3;
                                String creationDate = details.get(0).getString("Supplier");
                                long timestamp = Long.parseLong(creationDate.replaceAll("[^0-9]", ""));
                                Date fechaEntrega = new Date(timestamp);

                                oc.setFechaEntrega(fechaEntrega);
                                oc.setSociedad(details.get(0).getString("CompanyCode"));
                                oc.setCompradorUsuarioSap(details.get(0).getString("CreatedByUser"));
                                oc.setUltimoLiberadorUsuarioSap(details.get(0).getString("CreatedByUser_PurchaseReq"));

                                Double total = 0.00;
                                //Double total = Double.parseDouble(details.get(0).getString("Supplier"));
                                //Double valor_impuesto = total / 1.18 * 0.18;
                                //Double subtotal = total - valor_impuesto;

                                //oc.setSubTotal(BigDecimal.valueOf(total));
                                //oc.setValorImpuesto(BigDecimal.valueOf(valor_impuesto));
                                //oc.setTotal(BigDecimal.valueOf(subtotal));

                                /*Buscamos proveedor por  */
                                ProveedorDto proveedor = proveedorService.getProveedorByAcreedorCodigoSap(details.get(0).getString("Supplier"));

                                if (proveedor.getIdProveedor() != null) {
                                    oc.setProveedorRazonSocial(proveedor.getRazonSocial());
                                    oc.setProveedorRuc(proveedor.getRuc());
                                }else{
                                    //CONSULTAR EL API DE SAP PARA TRAER EL BP
                                    JSONObject supplier = this.consultarSupplier(details.get(0).getString("Supplier"));
                                    if(supplier !=null){
                                        oc.setProveedorRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                        oc.setProveedorRuc(supplier.getString("BPTaxNumber"));

                                        //Validar si Existe correo
                                        if(!supplier.getString("EmailAddress").isEmpty()){
                                            proveedor.setEmail(supplier.getString("EmailAddress"));
                                            proveedor.setRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                            proveedor.setRuc(supplier.getString("BPTaxNumber"));
                                        }
                                    }
                                }

                                long timestampMillisPublicacion = Long.parseLong(details.get(0).getString("PurchaseOrderDate").replaceAll("[^0-9]", ""));
                                Timestamp timestampPublicacion = new Timestamp(timestampMillisPublicacion);

                                long timestampMillisEntrega = Long.parseLong(details.get(0).getString("ScheduleLineDeliveryDate").replaceAll("[^0-9]", ""));
                                Date timestampEntrega2 = new Date(timestampMillisEntrega);
                                long cincoHorasEnMs = 5L * 60 * 60 * 1000;
                                Date timestampEntrega = new Date(timestampEntrega2.getTime() + cincoHorasEnMs);

                                oc.setFechaPublicacion(timestampPublicacion);
                                oc.setEstadoSap("05");

                                //TipoOrdenCompra tipoOrdenCompra = new TipoOrdenCompra();
                                //tipoOrdenCompra.setId(3);
                                oc.setIdTipoOrdenCompra(tipoOC);

                                long timestampMillisRegistro = Long.parseLong(details.get(0).getString("CreationDate").replaceAll("[^0-9]", ""));
                                Date dateRegistro = new Date(timestampMillisRegistro);
                                oc.setFechaRegistro(dateRegistro);
                                String comprador_nombre = "";
                                String comprador_email = "";
                                String comprador_telefono = "";
                                //fecha modificacion

                                String dateString = details.get(0).getString("LastChangeDateTime");
                                long timestampMillisModificacion = 0;
                                Pattern pattern = Pattern.compile("/Date\\((\\d+)([+-]\\d{4})?\\)/");
                                Matcher matcher = pattern.matcher(dateString);

                                if (matcher.matches()) {
                                    try {
                                        timestampMillisModificacion = Long.parseLong(matcher.group(1));

                                    } catch (NumberFormatException e) {
                                        System.err.println("Error al convertir el timestamp: " + e.getMessage());
                                    }
                                } else {

                                    System.err.println("El formato de fecha no es el esperado: " + dateString);
                                }
                                Date dateModificacion = new Date(timestampMillisModificacion);
                                oc.setFechaModificacion(dateModificacion);

                                /*Obtener datos del comprado */

                                String url_sap_comprado = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                OkHttpClient client_comprador = new OkHttpClient().newBuilder().build();

                                Request postRequest_pk_comprador = new Request.Builder()
                                        .url(url_sap_comprado)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Basic " + encodedAuth)
                                        .get()
                                        .build();

                                Response postResponse_pk_comprador = client_comprador.newCall(postRequest_pk_comprador).execute();

                                if (postResponse_pk_comprador.isSuccessful()) {

                                    String responseBody_comprador = postResponse_pk_comprador.body().string();
                                    JSONObject jsonObject_comprador = new JSONObject(responseBody_comprador);
                                    JSONObject dObject_comprador = jsonObject_comprador.getJSONObject("d");
                                    JSONArray resultsArray_comprador = dObject_comprador.getJSONArray("results");

                                    if (resultsArray_comprador.length() > 0) {
                                        JSONObject firstResult = resultsArray_comprador.getJSONObject(0); // Tomar el primer elemento
                                        comprador_nombre = firstResult.getString("PersonFullName");
                                        comprador_email = firstResult.getString("EmailAddress");
                                        comprador_telefono = firstResult.getString("PhoneNumber");
                                        // Imprimir el valor o utilizarlo como necesites
                                        System.out.println("PersonFullName: " + comprador_nombre);
                                    } else {
                                        String urlSapCompradoMasivo = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CorrespncInternalReference") + "'";
                                        OkHttpClient clientCompradorMasivo = new OkHttpClient().newBuilder().build();

                                        Request postRequest_pk_compradorMasivo = new Request.Builder()
                                                .url(urlSapCompradoMasivo)
                                                .addHeader("Content-Type", "application/json")
                                                .addHeader("Accept", "application/json")
                                                .addHeader("Authorization", "Basic " + encodedAuth)
                                                .get()
                                                .build();

                                        Response postResponse_pk_compradorMasivo = clientCompradorMasivo.newCall(postRequest_pk_compradorMasivo).execute();
                                        if (postResponse_pk_comprador.isSuccessful()) {

                                            String responseBody_compradorMasivo = postResponse_pk_compradorMasivo.body().string();
                                            JSONObject jsonObject_compradorMasivo = new JSONObject(responseBody_compradorMasivo);
                                            JSONObject dObject_compradorMasivo = jsonObject_compradorMasivo.getJSONObject("d");
                                            JSONArray resultsArray_compradorMasivo = dObject_compradorMasivo.getJSONArray("results");
                                            if (resultsArray_compradorMasivo.length() > 0) {
                                                JSONObject firstResultMasivo = resultsArray_compradorMasivo.getJSONObject(0); // Tomar el primer elemento
                                                comprador_nombre = firstResultMasivo.getString("PersonFullName");
                                                comprador_email = firstResultMasivo.getString("EmailAddress");
                                                comprador_telefono = firstResultMasivo.getString("PhoneNumber");
                                                // Imprimir el valor o utilizarlo como necesites
                                                System.out.println("PersonFullName: " + comprador_nombre);
                                            }else {
                                                System.out.println("No hay resultados disponibles.");
                                            }
                                        }

                                        System.out.println("No hay resultados disponibles.");
                                    }
                                }

                                oc.setCompradorNombre(comprador_nombre);
                                oc.setCompradorEmail(comprador_email);
                                oc.setCompradorTelefono(comprador_telefono);

                                String solicitante_nombre = "";
                                /*Obtener datos del comprado */
                                String url_sap_solicitante = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                OkHttpClient client_solicitante = new OkHttpClient().newBuilder().build();

                                Request postRequest_pk_solicitante = new Request.Builder()
                                        .url(url_sap_solicitante)
                                        .addHeader("Content-Type", "application/json")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Basic " + encodedAuth)
                                        .get()
                                        .build();

                                Response postResponse_pk_solicitante = client_solicitante.newCall(postRequest_pk_solicitante).execute();

                                if (postResponse_pk_solicitante.isSuccessful()) {

                                    String responseBody_solicitante = postResponse_pk_solicitante.body().string();
                                    JSONObject jsonObject_solicitante = new JSONObject(responseBody_solicitante);
                                    JSONObject dObject_solicitante = jsonObject_solicitante.getJSONObject("d");
                                    JSONArray resultsArray_solicitante = dObject_solicitante.getJSONArray("results");

                                    if (resultsArray_solicitante.length() > 0) {
                                        JSONObject firstResult = resultsArray_solicitante.getJSONObject(0); // Tomar el primer elemento
                                        solicitante_nombre = firstResult.getString("PersonFullName");

                                        // Imprimir el valor o utilizarlo como necesites
                                        System.out.println("PersonFullName: " + solicitante_nombre);
                                    } else {
                                        System.out.println("No hay resultados disponibles.");
                                    }
                                }

                                oc.setUltimoLiberadorUsuarioNombre(solicitante_nombre);

                                oc = ordenCompraRepository.save(oc);

                                Integer idOrdenCompra = oc.getId();
                                //Integer idTipoOrdenCompra = oc.getIdTipoOrdenCompra();

                                List<OrdenCompraDetalle> ordenCompraDetalles = new ArrayList<>();

                                // Procesar los detalles asociados a esta cabecera
                                for (JSONObject detail : details) {
                                    if (!detail.getString("PurchasingDocumentDeletionCode").equals("L") && !detail.getString("PurchasingDocumentDeletionCode").equals("S")) {
                                        OrdenCompraDetalle ocd = new OrdenCompraDetalle();
                                        ocd.setIdOrdenCompra(idOrdenCompra);
                                        ocd.setTipoPosicion("M");
                                        ocd.setPrecioTotal(BigDecimal.valueOf(Double.parseDouble(detail.getString("NetAmount"))).setScale(4, RoundingMode.HALF_UP));
                                        ocd.setPrecioUnitario(BigDecimal.valueOf(Double.parseDouble(detail.getString("NetPriceAmount"))).setScale(4, RoundingMode.HALF_UP));
                                        ocd.setCantidad(BigDecimal.valueOf(Double.parseDouble(detail.getString("OrderQuantity"))).setScale(4, RoundingMode.HALF_UP));
                                        ocd.setSociedad(detail.getString("PurchasingOrganization"));
                                        ocd.setCodigoSapAlmacen(detail.getString("Plant"));
                                        ocd.setCondicionPago(detail.getString("PaymentTerms"));
                                        ocd.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));
                                        ocd.setPosicion(detail.getString("PurchaseOrderItem"));
                                        ocd.setPosicionOc(detail.getString("PurchaseOrderItem"));
                                        ocd.setCodigoSapCentro(detail.getString("Plant"));
                                        ocd.setNumeroSolped(detail.getString("PurchaseRequisition"));
                                        //lugar de entrega -> nueva logica.

                                        String lugarEntregaDetalle = centroAlmacens.stream()
                                                .filter(centroAlmacen -> centroAlmacen.getCodigoSap().equals(detail.getString("Plant")))
                                                .<String>map(centroAlmacen -> String.format("%s - %s - %s - %s",
                                                        Objects.toString(centroAlmacen.getDireccion(), ""),
                                                        Objects.toString(centroAlmacen.getDistrito(), ""),
                                                        Objects.toString(centroAlmacen.getDepartamento(), ""),
                                                        Objects.toString(centroAlmacen.getPais(), "")))
                                                .findFirst()
                                                .orElse("");
                                        ocd.setLugarEntregaDetalle(lugarEntregaDetalle);

                                        /*Buscamos descripcion de centro / almace */
                                        CentroAlmacen centroAlmacen = centroAlmacenRepository.getByCodigoSap(detail.getString("Plant"));

                                        if (centroAlmacen != null) {
                                            ocd.setDenominacionAlmacen(centroAlmacen.getDenominacion());
                                            ocd.setDenominacionCentro(centroAlmacen.getDenominacion());
                                        }

                                        ocd.setFechaEntrega(timestampEntrega);

                                        ocd.setCodigoSapBienServicio(detail.getString("Material"));

                                        total = total + Double.parseDouble(detail.getString("NetAmount"));

                                        /*Buscamos el bien */
                                        List<BienServicio> bienServicioRpta = this.bienServicioRepository.findByCodigoSap(detail.getString("Material"));
                                        if (bienServicioRpta.size() > 0) {
                                            ocd.setDescripcionBienServicio(bienServicioRpta.get(0).getDescripcion());
                                            ocd.setUnidadMedidaBienServicio(bienServicioRpta.get(0).getUnidadMedida().getDescripcion());
                                            ocd.setUnidadMedidaBienServicio(bienServicioRpta.get(0).getUnidadMedida().getCodigoSap());
                                            if (bienServicioRpta.get(0).getTipoItem().equals("SERVICIO")) {
                                                tipoOC = 2;
                                            } else {
                                                tipoOC = 1;
                                            }
                                        }

                                        ocd = ordenCompraDetalleRepository.save(ocd);
                                        Integer idOrdenCompraDetalle = ocd.getId();

                                        ordenCompraDetalles.add(ocd);
                                        OrdenCompraDetalleTexto compraDetalleTexto = new OrdenCompraDetalleTexto();
                                        compraDetalleTexto.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                        compraDetalleTexto.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                        compraDetalleTexto.setPosicion(detail.getString("PurchaseOrderItem"));
                                        compraDetalleTexto.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                        compraDetalleTexto = ordenCompraDetalleTextoRepository.save(compraDetalleTexto);

                                        OrdenCompraDetalleTextoRegistroInfo compraDetalleTextoRegistroInfo = new OrdenCompraDetalleTextoRegistroInfo();
                                        compraDetalleTextoRegistroInfo.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                        compraDetalleTextoRegistroInfo.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                        compraDetalleTextoRegistroInfo.setPosicion(detail.getString("PurchaseOrderItem"));
                                        compraDetalleTextoRegistroInfo.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                        compraDetalleTextoRegistroInfo = ordenCompraDetalleTextoRegistroInfoRepository.save(compraDetalleTextoRegistroInfo);

                                        OrdenCompraDetalleTextoMaterialAmpliado ampliado = new OrdenCompraDetalleTextoMaterialAmpliado();
                                        ampliado.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                        ampliado.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                        ampliado.setPosicion(detail.getString("PurchaseOrderItem"));
                                        ampliado.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                        ampliado = ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ampliado);
                                    }
                                    /*
                                     * ordenCompraDetalleTextoPosicionSapList.stream()
                                                    .filter(ocdt -> ocdt.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdt.getPosicion().equals(posicion))
                                                    .forEach(ocdt -> {
                                                        ocdt.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDT: " + ocdt.toString());
                                                        ordenCompraDetalleTextoRepository.save(ocdt);
                                                    });

                                            ordenCompraDetalleTextoRegistroInfoSapList.stream()
                                                    .filter(ocdtri -> ocdtri.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtri.getPosicion().equals(posicion))
                                                    .forEach(ocdtri -> {
                                                        ocdtri.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDTRI: " + ocdtri.toString());
                                                        ordenCompraDetalleTextoRegistroInfoRepository.save(ocdtri);
                                                    });

                                            ordenCompraDetalleTextoMaterialAmpliadoSapList.stream()
                                                    .filter(ocdtma -> ocdtma.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtma.getPosicion().equals(posicion))
                                                    .forEach(ocdtma -> {
                                                        ocdtma.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDTMA: " + ocdtma.toString());
                                                        ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ocdtma);
                                                    });
                                     * 
                                     */

                                }

                                oc.setSubtotal(BigDecimal.valueOf(total));

                                //Double valor_impuesto = total / 1.18 * 0.18;
                                //Double subtotal = total - valor_impuesto;

                                //oc.setSubTotal(BigDecimal.valueOf(total));
                                //oc.setValorImpuesto(BigDecimal.valueOf(valor_impuesto));
                                //oc.setTotal(BigDecimal.valueOf(subtotal));

                                /*Calculamos impuesto y subtotal */
                                if (valorImpuesto > 0) {
                                    Double montoImpuesto = total * (valorImpuesto / 100);
                                    Double montoSubTotal = total + montoImpuesto;
                                    oc.setValorImpuesto(BigDecimal.valueOf(montoImpuesto));
                                    oc.setTotal(BigDecimal.valueOf(montoSubTotal));
                                } else {
                                    oc.setValorImpuesto(BigDecimal.valueOf(0));
                                    oc.setTotal(BigDecimal.valueOf(total));
                                }

                                oc = ordenCompraRepository.save(oc);
                                logger.info("OC guardada: " + oc.getNumeroOrdenCompra());

                                if (enviarCorreoPublicacion) {
                                    /*Enviando Correo*/
                                    String respuesta = "";
                                    Usuario comprador = usuarioRepository.findByCodigoSap(oc.getCompradorUsuarioSap());
                                    if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                        respuesta = contactoPublicadaOCNotificacion.enviar(oc, null, comprador, ordenCompraDetalles);
                                    LogTransaccion logTransaccion = new LogTransaccion();
                                    logTransaccion.setEnvioTrama("contactoPublicadaOCNotificacion");
                                    logTransaccion.setRespuestaCodigo(respuesta);
                                    logTransaccion.setTipoRegistro("extraerOrdenCompraListRFC");
                                    this.logTransaccionRepository.save(logTransaccion);

                                    //Proveedor proveedor = proveedorService.getProveedorByRuc(oc.getProveedorRuc());
                                    Usuario proveedorUsuario = null;
                                    if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                                        proveedorUsuario = new Usuario();
                                        proveedorUsuario.setEmail(proveedor.getEmail());
                                        proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                    } else {
                                        List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(oc.getProveedorRuc());
                                        if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                            proveedorUsuario = posibleProveedorList.get(0);
                                    }
                                    String respuesta1 = "";
                                    if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty()) {
                                        respuesta1 = contactoPublicadaOCNotificacion.enviar(oc, proveedorUsuario, null, ordenCompraDetalles);
                                    }

                                    LogTransaccion logTransaccion1 = new LogTransaccion();
                                    logTransaccion1.setEnvioTrama("contactoPublicadaOCNotificacion");
                                    logTransaccion1.setRespuestaCodigo(respuesta);
                                    logTransaccion1.setTipoRegistro("extraerOrdenCompraListRFC");
                                    this.logTransaccionRepository.save(logTransaccion);
                                }

                            } else if (optionalOrdenCompra != null) {
                                /*QUE HACER SI YA EXISTE???*/
                                String dateString = details.get(0).getString("LastChangeDateTime");
                                long timestampMillisModificacion = 0;
                                Pattern pattern = Pattern.compile("/Date\\((\\d+)([+-]\\d{4})?\\)/");
                                Matcher matcher = pattern.matcher(dateString);

                                if (matcher.matches()) {
                                    try {
                                        timestampMillisModificacion = Long.parseLong(matcher.group(1));

                                    } catch (NumberFormatException e) {
                                        System.err.println("Error al convertir el timestamp: " + e.getMessage());
                                    }
                                } else {

                                    System.err.println("El formato de fecha no es el esperado: " + dateString);
                                }

                                Date dateModificacion = new Date(timestampMillisModificacion);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                if (optionalOrdenCompra.getFechaModificacion() == null || !format.format(optionalOrdenCompra.getFechaModificacion()).equals(format.format(dateModificacion))) {
                                    optionalOrdenCompra.setVersion(1); // porque OC es publicada por 1ra vez
                                    optionalOrdenCompra.setIsActive(OpcionGenericaEnum.SI.getCodigo());  //OC es activa porque es la 1ra y unica version
                                    optionalOrdenCompra.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId());  //estado inicial publicada
                                    optionalOrdenCompra.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                                    optionalOrdenCompra.setNumeroOrdenCompra(purchaseOrder);
                                    optionalOrdenCompra.setProveedorCodigoSap(details.get(0).getString("Supplier"));
                                    optionalOrdenCompra.setCondicionPago(details.get(0).getString("PaymentTerms"));

                                    /*Lugar de entrega :D */


                                    String lugar_entrega = centroAlmacens.stream()
                                            .filter(centroAlmacen -> centroAlmacen.getCodigoSap().equals(details.get(0).getString("Plant")))
                                            .<String>map(centroAlmacen -> String.format("%s - %s - %s - %s",
                                                    Objects.toString(centroAlmacen.getDireccion(), ""),
                                                    Objects.toString(centroAlmacen.getDistrito(), ""),
                                                    Objects.toString(centroAlmacen.getDepartamento(), ""),
                                                    Objects.toString(centroAlmacen.getPais(), "")))
                                            .findFirst()
                                            .orElse("");

                                    optionalOrdenCompra.setLugarEntrega(lugar_entrega);

                                    /*Buscamos en la tabla Area */
                                    String AreaSolicitante = AreasSolicitante.stream()
                                            .filter(area -> area.getCodigo().equals(details.get(0).getString("AreaSolicitante_PDH")))
                                            .map(Parametro::getDescripcion)
                                            .findFirst()
                                            .orElse("");

                                    optionalOrdenCompra.setSedeSolicitud(AreaSolicitante);

                                    /*Buscamos a condicion de pago */
                                    String descripcion = condiciones.stream()
                                            .filter(condicion -> condicion.getCodigoSap().equals(details.get(0).getString("PaymentTerms")))
                                            .map(CondicionPago::getDescripcion)
                                            .findFirst()
                                            .orElse("");

                                    optionalOrdenCompra.setCondicionPagoDescripcion(descripcion);

                                    optionalOrdenCompra.setCodigoMondeda(details.get(0).getString("DocumentCurrency"));
                                    optionalOrdenCompra.setCompradorUsuarioSap(details.get(0).getString("CreatedByUser"));


                                    Optional<Parametro> impuestoOptional = Impuestos.stream()
                                            .filter(impuesto -> impuesto.getCodigo().equals(details.get(0).getString("TaxCode")))
                                            .findFirst();

                                    String descripcionImpuesto = impuestoOptional.map(Parametro::getDescripcion).orElse("");
                                    double valorImpuesto = impuestoOptional
                                            .map(Parametro::getValor) // Assuming this returns a String
                                            .map(Double::parseDouble) // Convert the String to double
                                            .orElse(0.0);


                                    optionalOrdenCompra.setIndicadorImpuesto(descripcionImpuesto);

                                    Integer tipoOC = 3;
                                    String creationDate = details.get(0).getString("Supplier");
                                    long timestamp = Long.parseLong(creationDate.replaceAll("[^0-9]", ""));
                                    Date fechaEntrega = new Date(timestamp);

                                    optionalOrdenCompra.setFechaEntrega(fechaEntrega);
                                    optionalOrdenCompra.setSociedad(details.get(0).getString("CompanyCode"));
                                    optionalOrdenCompra.setCompradorUsuarioSap(details.get(0).getString("CreatedByUser"));
                                    optionalOrdenCompra.setUltimoLiberadorUsuarioSap(details.get(0).getString("CreatedByUser_PurchaseReq"));

                                    Double total = 0.00;

                                    ProveedorDto proveedor = proveedorService.getProveedorByAcreedorCodigoSap(details.get(0).getString("Supplier"));

                                    if (proveedor.getIdProveedor() != null) {
                                        optionalOrdenCompra.setProveedorRazonSocial(proveedor.getRazonSocial());
                                        optionalOrdenCompra.setProveedorRuc(proveedor.getRuc());
                                    }else{
                                        //CONSULTAR EL API DE SAP PARA TRAER EL BP
                                        JSONObject supplier = this.consultarSupplier(details.get(0).getString("Supplier"));
                                        if(supplier !=null){
                                            optionalOrdenCompra.setProveedorRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                            optionalOrdenCompra.setProveedorRuc(supplier.getString("BPTaxNumber"));

                                            //Validar si Existe correo
                                            if(!supplier.getString("EmailAddress").isEmpty()){
                                                proveedor.setEmail(supplier.getString("EmailAddress"));
                                                proveedor.setRazonSocial(supplier.getString("BusinessPartnerFullName"));
                                                proveedor.setRuc(supplier.getString("BPTaxNumber"));
                                            }
                                        }
                                    }

                                    long timestampMillisPublicacion = Long.parseLong(details.get(0).getString("PurchaseOrderDate").replaceAll("[^0-9]", ""));
                                    Timestamp timestampPublicacion = new Timestamp(timestampMillisPublicacion);

                                    long timestampMillisEntrega = Long.parseLong(details.get(0).getString("ScheduleLineDeliveryDate").replaceAll("[^0-9]", ""));
                                    Date timestampEntrega2 = new Date(timestampMillisEntrega);
                                    long cincoHorasEnMs = 5L * 60 * 60 * 1000;
                                    Date timestampEntrega = new Date(timestampEntrega2.getTime() + cincoHorasEnMs);


                                    optionalOrdenCompra.setFechaPublicacion(timestampPublicacion);
                                    optionalOrdenCompra.setEstadoSap("05");

                                    //TipoOrdenCompra tipoOrdenCompra = new TipoOrdenCompra();
                                    //tipoOrdenCompra.setId(3);
                                    optionalOrdenCompra.setIdTipoOrdenCompra(tipoOC);

                                    long timestampMillisRegistro = Long.parseLong(details.get(0).getString("CreationDate").replaceAll("[^0-9]", ""));
                                    Date dateRegistro = new Date(timestampMillisRegistro);
                                    optionalOrdenCompra.setFechaRegistro(dateRegistro);
                                    String comprador_nombre = "";
                                    String comprador_email = "";
                                    String comprador_telefono = "";
                                    //fecha modificacion

                                    optionalOrdenCompra.setFechaModificacion(dateModificacion);

                                    /*Obtener datos del comprado */
                                    /*String url_sap_comprado = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                    OkHttpClient client_comprador = new OkHttpClient().newBuilder().build();

                                    Request postRequest_pk_comprador = new Request.Builder()
                                            .url(url_sap_comprado)
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("Accept", "application/json")
                                            .addHeader("Authorization", "Basic " + encodedAuth)
                                            .get()
                                            .build();

                                    Response postResponse_pk_comprador = client_comprador.newCall(postRequest_pk_comprador).execute();

                                    if (postResponse_pk_comprador.isSuccessful()) {

                                        String responseBody_comprador = postResponse_pk_comprador.body().string();
                                        JSONObject jsonObject_comprador = new JSONObject(responseBody_comprador);
                                        JSONObject dObject_comprador = jsonObject_comprador.getJSONObject("d");
                                        JSONArray resultsArray_comprador = dObject_comprador.getJSONArray("results");

                                        if (resultsArray_comprador.length() > 0) {
                                            JSONObject firstResult = resultsArray_comprador.getJSONObject(0); // Tomar el primer elemento
                                            comprador_nombre = firstResult.getString("PersonFullName");
                                            comprador_email = firstResult.getString("EmailAddress");
                                            comprador_telefono = firstResult.getString("PhoneNumber");
                                            // Imprimir el valor o utilizarlo como necesites
                                            System.out.println("PersonFullName: " + comprador_nombre);
                                        } else {
                                            System.out.println("No hay resultados disponibles.");
                                        }
                                    }

                                    oc.setCompradorNombre(comprador_nombre);
                                    oc.setCompradorEmail(comprador_email);
                                    oc.setCompradorTelefono(comprador_telefono);

                                    String solicitante_nombre = "";
                                    *//*Obtener datos del comprado *//*
                                    String url_sap_solicitante = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                                    OkHttpClient client_solicitante = new OkHttpClient().newBuilder().build();

                                    Request postRequest_pk_solicitante = new Request.Builder()
                                            .url(url_sap_solicitante)
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("Accept", "application/json")
                                            .addHeader("Authorization", "Basic " + encodedAuth)
                                            .get()
                                            .build();

                                    Response postResponse_pk_solicitante = client_solicitante.newCall(postRequest_pk_solicitante).execute();

                                    if (postResponse_pk_solicitante.isSuccessful()) {

                                        String responseBody_solicitante = postResponse_pk_solicitante.body().string();
                                        JSONObject jsonObject_solicitante = new JSONObject(responseBody_solicitante);
                                        JSONObject dObject_solicitante = jsonObject_solicitante.getJSONObject("d");
                                        JSONArray resultsArray_solicitante = dObject_solicitante.getJSONArray("results");

                                        if (resultsArray_solicitante.length() > 0) {
                                            JSONObject firstResult = resultsArray_solicitante.getJSONObject(0); // Tomar el primer elemento
                                            solicitante_nombre = firstResult.getString("PersonFullName");

                                            // Imprimir el valor o utilizarlo como necesites
                                            System.out.println("PersonFullName: " + solicitante_nombre);
                                        } else {
                                            System.out.println("No hay resultados disponibles.");
                                        }
                                    }

                                    oc.setUltimoLiberadorUsuarioNombre(solicitante_nombre);*/

                                    optionalOrdenCompra = ordenCompraRepository.save(optionalOrdenCompra);

                                    Integer idOrdenCompra = optionalOrdenCompra.getId();
                                    //Integer idTipoOrdenCompra = oc.getIdTipoOrdenCompra();

                                    List<OrdenCompraDetalle> ordenCompraDetalles = new ArrayList<>();

                                    // Procesar los detalles asociados a esta cabecera

                                    List<OrdenCompraDetalle> ocdList = ordenCompraDetalleRepository.getAllByIdOrdenCompra(idOrdenCompra);

                                    for (OrdenCompraDetalle ocd : ocdList) {

                                        Integer idOrdenCompraDetalle = ocd.getId();
                                        OrdenCompraDetalleTexto compraDetalleTexto = ordenCompraDetalleTextoRepository.findByIdOrdenCompraDetalleAndPosicion(idOrdenCompraDetalle, ocd.getPosicion()).orElseThrow(() -> new RuntimeException("id no encontrada"));
                                        ordenCompraDetalleTextoRepository.delete(compraDetalleTexto);

                                        OrdenCompraDetalleTextoRegistroInfo compraDetalleTextoRegistroInfo = ordenCompraDetalleTextoRegistroInfoRepository.findByIdOrdenCompraDetalleAndPosicion(idOrdenCompraDetalle, ocd.getPosicion()).orElseThrow(() -> new RuntimeException("id no encontrada"));
                                        ordenCompraDetalleTextoRegistroInfoRepository.delete(compraDetalleTextoRegistroInfo);

                                        OrdenCompraDetalleTextoMaterialAmpliado ampliado = ordenCompraDetalleTextoMaterialAmpliadoRepository.findByIdOrdenCompraDetalleAndPosicion(idOrdenCompraDetalle, ocd.getPosicion()).orElseThrow(() -> new RuntimeException("id no encontrada"));

                                        ordenCompraDetalleTextoMaterialAmpliadoRepository.delete(ampliado);
                                        ordenCompraDetalleRepository.delete(ocd);
                                    }

                                    for (JSONObject detail : details) {
                                        if (!detail.getString("PurchasingDocumentDeletionCode").equals("L") && !detail.getString("PurchasingDocumentDeletionCode").equals("S")) {
                                            OrdenCompraDetalle ocd = new OrdenCompraDetalle();
                                            ocd.setIdOrdenCompra(idOrdenCompra);
                                            ocd.setTipoPosicion("M");
                                            ocd.setPrecioTotal(BigDecimal.valueOf(Double.parseDouble(detail.getString("NetAmount"))).setScale(4, RoundingMode.HALF_UP));
                                            ocd.setPrecioUnitario(BigDecimal.valueOf(Double.parseDouble(detail.getString("NetPriceAmount"))).setScale(4, RoundingMode.HALF_UP));
                                            ocd.setCantidad(BigDecimal.valueOf(Double.parseDouble(detail.getString("OrderQuantity"))).setScale(4, RoundingMode.HALF_UP));
                                            ocd.setSociedad(detail.getString("PurchasingOrganization"));
                                            ocd.setCodigoSapAlmacen(detail.getString("Plant"));
                                            ocd.setCondicionPago(detail.getString("PaymentTerms"));
                                            ocd.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));
                                            ocd.setPosicion(detail.getString("PurchaseOrderItem"));
                                            ocd.setPosicionOc(detail.getString("PurchaseOrderItem"));
                                            ocd.setCodigoSapCentro(detail.getString("Plant"));
                                            ocd.setNumeroSolped(detail.getString("PurchaseRequisition"));
                                            //lugar de entrega -> nueva logica.

                                            String lugarEntregaDetalle = centroAlmacens.stream()
                                                    .filter(centroAlmacen -> centroAlmacen.getCodigoSap().equals(detail.getString("Plant")))
                                                    .<String>map(centroAlmacen -> String.format("%s - %s - %s - %s",
                                                            Objects.toString(centroAlmacen.getDireccion(), ""),
                                                            Objects.toString(centroAlmacen.getDistrito(), ""),
                                                            Objects.toString(centroAlmacen.getDepartamento(), ""),
                                                            Objects.toString(centroAlmacen.getPais(), "")))
                                                    .findFirst()
                                                    .orElse("");
                                            ocd.setLugarEntregaDetalle(lugarEntregaDetalle);

                                            /*Buscamos descripcion de centro / almace */
                                            CentroAlmacen centroAlmacen = centroAlmacenRepository.getByCodigoSap(detail.getString("Plant"));

                                            if (centroAlmacen != null) {
                                                ocd.setDenominacionAlmacen(centroAlmacen.getDenominacion());
                                                ocd.setDenominacionCentro(centroAlmacen.getDenominacion());
                                            }

                                            ocd.setFechaEntrega(timestampEntrega);

                                            ocd.setCodigoSapBienServicio(detail.getString("Material"));

                                            total = total + Double.parseDouble(detail.getString("NetAmount"));

                                            /*Buscamos el bien */
                                            List<BienServicio> bienServicioRpta = this.bienServicioRepository.findByCodigoSap(detail.getString("Material"));
                                            if (bienServicioRpta.size() > 0) {
                                                ocd.setDescripcionBienServicio(bienServicioRpta.get(0).getDescripcion());
                                                ocd.setUnidadMedidaBienServicio(bienServicioRpta.get(0).getUnidadMedida().getDescripcion());
                                                ocd.setUnidadMedidaBienServicio(bienServicioRpta.get(0).getUnidadMedida().getCodigoSap());
                                                if (bienServicioRpta.get(0).getTipoItem().equals("SERVICIO")) {
                                                    tipoOC = 2;
                                                } else {
                                                    tipoOC = 1;
                                                }
                                            }

                                            ocd = ordenCompraDetalleRepository.save(ocd);
                                            Integer idOrdenCompraDetalle = ocd.getId();

                                            ordenCompraDetalles.add(ocd);
                                            OrdenCompraDetalleTexto compraDetalleTexto = new OrdenCompraDetalleTexto();
                                            compraDetalleTexto.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                            compraDetalleTexto.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                            compraDetalleTexto.setPosicion(detail.getString("PurchaseOrderItem"));
                                            compraDetalleTexto.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                            compraDetalleTexto = ordenCompraDetalleTextoRepository.save(compraDetalleTexto);

                                            OrdenCompraDetalleTextoRegistroInfo compraDetalleTextoRegistroInfo = new OrdenCompraDetalleTextoRegistroInfo();
                                            compraDetalleTextoRegistroInfo.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                            compraDetalleTextoRegistroInfo.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                            compraDetalleTextoRegistroInfo.setPosicion(detail.getString("PurchaseOrderItem"));
                                            compraDetalleTextoRegistroInfo.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                            compraDetalleTextoRegistroInfo = ordenCompraDetalleTextoRegistroInfoRepository.save(compraDetalleTextoRegistroInfo);

                                            OrdenCompraDetalleTextoMaterialAmpliado ampliado = new OrdenCompraDetalleTextoMaterialAmpliado();
                                            ampliado.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                            ampliado.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                            ampliado.setPosicion(detail.getString("PurchaseOrderItem"));
                                            ampliado.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                            ampliado = ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ampliado);

                                        }
                                    }
                                    optionalOrdenCompra.setSubtotal(BigDecimal.valueOf(total));

                                    //Double valor_impuesto = total / 1.18 * 0.18;
                                    //Double subtotal = total - valor_impuesto;

                                    //oc.setSubTotal(BigDecimal.valueOf(total));
                                    //oc.setValorImpuesto(BigDecimal.valueOf(valor_impuesto));
                                    //oc.setTotal(BigDecimal.valueOf(subtotal));

                                    /*Calculamos impuesto y subtotal */
                                    if (valorImpuesto > 0) {
                                        Double montoImpuesto = total * (valorImpuesto / 100);
                                        Double montoSubTotal = total + montoImpuesto;
                                        optionalOrdenCompra.setValorImpuesto(BigDecimal.valueOf(montoImpuesto));
                                        optionalOrdenCompra.setTotal(BigDecimal.valueOf(montoSubTotal));
                                    } else {
                                        optionalOrdenCompra.setValorImpuesto(BigDecimal.valueOf(0));
                                        optionalOrdenCompra.setTotal(BigDecimal.valueOf(total));
                                    }

                                    optionalOrdenCompra = ordenCompraRepository.save(optionalOrdenCompra);
                                    logger.info("OC guardada: " + optionalOrdenCompra.getNumeroOrdenCompra());

                                    if (enviarCorreoPublicacion) {
                                        /*Enviando Correo*/
                                        String respuesta = "";
                                        Usuario comprador = usuarioRepository.findByCodigoSap(optionalOrdenCompra.getCompradorUsuarioSap());
                                        if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                            respuesta = contactoPublicadaOCNotificacion.enviarOcModificada(optionalOrdenCompra, null, comprador, ordenCompraDetalles);
                                        LogTransaccion logTransaccion = new LogTransaccion();
                                        logTransaccion.setEnvioTrama("contactoPublicadaOCNotificacion");
                                        logTransaccion.setRespuestaCodigo(respuesta);
                                        logTransaccion.setTipoRegistro("extraerOrdenCompraListRFC");
                                        this.logTransaccionRepository.save(logTransaccion);

                                        //Proveedor proveedor = proveedorService.getProveedorByRuc(oc.getProveedorRuc());
                                        Usuario proveedorUsuario = null;
                                        if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                                            proveedorUsuario = new Usuario();
                                            proveedorUsuario.setEmail(proveedor.getEmail());
                                            proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                        } else {
                                            List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(optionalOrdenCompra.getProveedorRuc());
                                            if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                                proveedorUsuario = posibleProveedorList.get(0);
                                        }
                                        String respuesta1 = "";
                                        if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty()) {
                                            respuesta1 = contactoPublicadaOCNotificacion.enviarOcModificada(optionalOrdenCompra, proveedorUsuario, null, ordenCompraDetalles);
                                        }

                                        LogTransaccion logTransaccion1 = new LogTransaccion();
                                        logTransaccion1.setEnvioTrama("contactoPublicadaOCNotificacion");
                                        logTransaccion1.setRespuestaCodigo(respuesta);
                                        logTransaccion1.setTipoRegistro("extraerOrdenCompraListRFC");
                                        this.logTransaccionRepository.save(logTransaccion);
                                    }

                                }
                                //query en caso de que exista la OC.


                            }


                        }
                    } catch (JSONException e) {
                        LogTransaccion logTransaccion2 = new LogTransaccion();
                        logTransaccion2.setEnvioTrama(e.getMessage());
                        logTransaccion2.setRespuestaCodigo(e.getMessage());
                        logTransaccion2.setTipoRegistro("ExcepcionLogsOC3");
                        this.logTransaccionRepository.save(logTransaccion2);
                        System.err.println("Error al procesar el JSON: " + e.getMessage());
                    }
                }
                
                /*consultaOrdenCompra.setOrdenCompraSapList(ordenCompraSapList);
                consultaOrdenCompra.setOrdenCompraTextoCabeceraSapList(ordenCompraTextoCabeceraSapList);
                consultaOrdenCompra.setOrdenCompraDetalleSapList(ordenCompraDetalleSapList);
                consultaOrdenCompra.setOrdenCompraDetalleTextoRegistroInfoSapList(ordenCompraDetalleTextoRegistroInfoSapList);
                consultaOrdenCompra.setOrdenCompraDetalleTextoMaterialAmpliadoSapList(ordenCompraDetalleTextoMaterialAmpliadoSapList);
                consultaOrdenCompra.setOrdenCompraDetalleTextoPosicionSapList(ordenCompraDetalleTextoPosicionSapList);*/


                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
            } catch (Exception e) {
                if (ocProcessing.get())
                    ocProcessing.set(!ocProcessing.get());
                logger.error(e.getMessage(), e.getCause());

                LogTransaccion logTransaccion3 = new LogTransaccion();
                logTransaccion3.setEnvioTrama(e.getMessage());
                logTransaccion3.setRespuestaCodigo(e.getMessage());
                logTransaccion3.setTipoRegistro("ExcepcionLogsOC3");
                this.logTransaccionRepository.save(logTransaccion3);

                throw new Exception(e);
            }
        } else {
            logger.error("INI: " + DateUtils.getCurrentTimestamp().toString() + "  OrdenCompra Extraction esta procesando");
        }
        return consultaOrdenCompra;
    }

    @Override
    public ConsultaOrdenCompra extraerOrdenCompraListRFCporOC(boolean enviarCorreoPublicacion, String numeroOc) throws Exception {
        ConsultaOrdenCompra consultaOrdenCompra = new ConsultaOrdenCompra();

        try {

            List<CondicionPago> condiciones = condicionPagoService.getListAll();
            List<Parametro> Impuestos = parametroService.getByModuloandTipo("INDICADOR_IMPUESTO", "INDICADOR_IMPUESTO");
            List<Parametro> AreasSolicitante = parametroService.getByModuloandTipo("AREA_SOLICITANTE", "AREA_SOLICITANTE");
            List<CentroAlmacen> centroAlmacens = centroAlmacenRepository.findAll();

            /* */
            String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ORDENCOMPRA_CDS/YY1_OrdenCompra?$filter=PurchaseOrder eq '" + numeroOc + "' and CompanyCode eq 'P100'";
            //String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ORDENCOMPRA_CDS/YY1_OrdenCompra?$filter=PurchaseOrderDate ge datetime'" + fechaInicio+ "' and PurchaseOrderDate le datetime'" + fechaFin + "' and PurchasingProcessingStatus eq '05' and CompanyCode eq 'P100'";
            //String url_sap = urlSap + "/sap/opu/odata/sap/YY1_ORDENCOMPRA_CDS/YY1_OrdenCompra?$filter=LastChangeDateTime ge datetimeoffset'2025-01-14T12:00:00Z'";

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
                        String purchaseOrder = result.getString("PurchaseOrder");

                        // Obtener la lista existente o crear una nueva si no existe
                        groupedData.computeIfAbsent(purchaseOrder, k -> new ArrayList<>()).add(result);
                    }

                    // logger.error("Rango de Fechas : " + fechaInicio + " - " + fechaFin);
                    logger.error("CANTIDAD DE OC ENCONTRADOS: " + groupedData.size());
                    logger.error("CANTIDAD DE OCD ENCONTRADOS: " + resultsArray.length());

                    // Iterar sobre el mapa para procesar las cabeceras y detalles
                    for (Map.Entry<String, List<JSONObject>> entry : groupedData.entrySet()) {
                        String purchaseOrder = entry.getKey();
                        List<JSONObject> details = entry.getValue();

                        System.out.println("Procesando Orden de Compra: " + purchaseOrder);

                        //if(!purchaseOrder.equals("4500000409")){
                        //    continue;
                        //}

                        OrdenCompra optionalOrdenCompra = ordenCompraRepository.findTopByNumeroOrdenCompraAndIsActive(purchaseOrder, "1");

                        if (optionalOrdenCompra == null) {
                            OrdenCompra oc = new OrdenCompra();

                            oc.setVersion(1); // porque OC es publicada por 1ra vez
                            oc.setIsActive(OpcionGenericaEnum.SI.getCodigo());  //OC es activa porque es la 1ra y unica version
                            oc.setIdEstadoOrdenCompra(OrdenCompraEstadoEnum.ACTIVA.getId());  //estado inicial publicada
                            oc.setFechaPublicacion(DateUtils.getCurrentTimestamp());
                            oc.setNumeroOrdenCompra(purchaseOrder);
                            oc.setProveedorCodigoSap(details.get(0).getString("Supplier"));
                            oc.setCondicionPago(details.get(0).getString("PaymentTerms"));

                            /*Lugar de entrega :D */


                            String lugar_entrega = centroAlmacens.stream()
                                    .filter(centroAlmacen -> centroAlmacen.getCodigoSap().equals(details.get(0).getString("Plant")))
                                    .<String>map(centroAlmacen -> String.format("%s - %s - %s - %s",
                                            Objects.toString(centroAlmacen.getDireccion(), ""),
                                            Objects.toString(centroAlmacen.getDistrito(), ""),
                                            Objects.toString(centroAlmacen.getDepartamento(), ""),
                                            Objects.toString(centroAlmacen.getPais(), "")))
                                    .findFirst()
                                    .orElse("");

                            oc.setLugarEntrega(lugar_entrega);

                            /*Buscamos en la tabla Area */
                            String AreaSolicitante = AreasSolicitante.stream()
                                    .filter(area -> area.getCodigo().equals(details.get(0).getString("AreaSolicitante_PDH")))
                                    .map(Parametro::getDescripcion)
                                    .findFirst()
                                    .orElse("");

                            oc.setSedeSolicitud(AreaSolicitante);

                            /*Buscamos a condicion de pago */
                            String descripcion = condiciones.stream()
                                    .filter(condicion -> condicion.getCodigoSap().equals(details.get(0).getString("PaymentTerms")))
                                    .map(CondicionPago::getDescripcion)
                                    .findFirst()
                                    .orElse("");

                            oc.setCondicionPagoDescripcion(descripcion);

                            oc.setCodigoMondeda(details.get(0).getString("DocumentCurrency"));
                            oc.setCompradorUsuarioSap(details.get(0).getString("CreatedByUser"));


                            Optional<Parametro> impuestoOptional = Impuestos.stream()
                                    .filter(impuesto -> impuesto.getCodigo().equals(details.get(0).getString("TaxCode")))
                                    .findFirst();

                            String descripcionImpuesto = impuestoOptional.map(Parametro::getDescripcion).orElse("");
                            double valorImpuesto = impuestoOptional
                                    .map(Parametro::getValor) // Assuming this returns a String
                                    .map(Double::parseDouble) // Convert the String to double
                                    .orElse(0.0);

                                /*String descripcionImpuesto = Impuestos.stream()
                                .filter(impuesto -> impuesto.getCodigo().equals(details.get(0).getString("TaxCode")))
                                .map(Parametro::getDescripcion)
                                .findFirst()
                                .orElse("");*/

                            oc.setIndicadorImpuesto(descripcionImpuesto);

                            Integer tipoOC = 3;
                            String creationDate = details.get(0).getString("Supplier");
                            long timestamp = Long.parseLong(creationDate.replaceAll("[^0-9]", ""));
                            Date fechaEntrega = new Date(timestamp);

                            oc.setFechaEntrega(fechaEntrega);
                            oc.setSociedad(details.get(0).getString("CompanyCode"));
                            oc.setCompradorUsuarioSap(details.get(0).getString("CreatedByUser"));
                            oc.setUltimoLiberadorUsuarioSap(details.get(0).getString("CreatedByUser_PurchaseReq"));

                            Double total = 0.00;
                            //Double total = Double.parseDouble(details.get(0).getString("Supplier"));
                            //Double valor_impuesto = total / 1.18 * 0.18;
                            //Double subtotal = total - valor_impuesto;

                            //oc.setSubTotal(BigDecimal.valueOf(total));
                            //oc.setValorImpuesto(BigDecimal.valueOf(valor_impuesto));
                            //oc.setTotal(BigDecimal.valueOf(subtotal));

                            /*Buscamos proveedor por  */
                            ProveedorDto proveedor = proveedorService.getProveedorByAcreedorCodigoSap(details.get(0).getString("Supplier"));

                            if (proveedor != null) {
                                oc.setProveedorRazonSocial(proveedor.getRazonSocial());
                                oc.setProveedorRuc(proveedor.getRuc());
                            }

                            long timestampMillisPublicacion = Long.parseLong(details.get(0).getString("PurchaseOrderDate").replaceAll("[^0-9]", ""));
                            Timestamp timestampPublicacion = new Timestamp(timestampMillisPublicacion);

                            long timestampMillisEntrega = Long.parseLong(details.get(0).getString("ScheduleLineDeliveryDate").replaceAll("[^0-9]", ""));
                            Date timestampEntrega2 = new Date(timestampMillisEntrega);
                            long cincoHorasEnMs = 5L * 60 * 60 * 1000;
                            Date timestampEntrega = new Date(timestampEntrega2.getTime() + cincoHorasEnMs);

                            oc.setFechaPublicacion(timestampPublicacion);
                            oc.setEstadoSap("05");

                            //TipoOrdenCompra tipoOrdenCompra = new TipoOrdenCompra();
                            //tipoOrdenCompra.setId(3);
                            oc.setIdTipoOrdenCompra(tipoOC);

                            long timestampMillisRegistro = Long.parseLong(details.get(0).getString("CreationDate").replaceAll("[^0-9]", ""));
                            Date dateRegistro = new Date(timestampMillisRegistro);
                            oc.setFechaRegistro(dateRegistro);
                            String comprador_nombre = "";
                            String comprador_email = "";
                            String comprador_telefono = "";

                            /*Obtener datos del comprado */
                            String url_sap_comprado = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                            OkHttpClient client_comprador = new OkHttpClient().newBuilder().build();

                            Request postRequest_pk_comprador = new Request.Builder()
                                    .url(url_sap_comprado)
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", "Basic " + encodedAuth)
                                    .get()
                                    .build();

                            Response postResponse_pk_comprador = client_comprador.newCall(postRequest_pk_comprador).execute();

                            if (postResponse_pk_comprador.isSuccessful()) {

                                String responseBody_comprador = postResponse_pk_comprador.body().string();
                                JSONObject jsonObject_comprador = new JSONObject(responseBody_comprador);
                                JSONObject dObject_comprador = jsonObject_comprador.getJSONObject("d");
                                JSONArray resultsArray_comprador = dObject_comprador.getJSONArray("results");

                                if (resultsArray_comprador.length() > 0) {
                                    JSONObject firstResult = resultsArray_comprador.getJSONObject(0); // Tomar el primer elemento
                                    comprador_nombre = firstResult.getString("PersonFullName");
                                    comprador_email = firstResult.getString("EmailAddress");
                                    comprador_telefono = firstResult.getString("PhoneNumber");
                                    // Imprimir el valor o utilizarlo como necesites
                                    System.out.println("PersonFullName: " + comprador_nombre);
                                } else {
                                    System.out.println("No hay resultados disponibles.");
                                }
                            }

                            oc.setCompradorNombre(comprador_nombre);
                            oc.setCompradorEmail(comprador_email);
                            oc.setCompradorTelefono(comprador_telefono);

                            String solicitante_nombre = "";
                            /*Obtener datos del comprado */
                            String url_sap_solicitante = urlSap + "/sap/opu/odata/sap/YY1_USER_DETAILS_CDS/YY1_User_Details?$filter=UserID eq '" + details.get(0).getString("CreatedByUser") + "'";
                            OkHttpClient client_solicitante = new OkHttpClient().newBuilder().build();

                            Request postRequest_pk_solicitante = new Request.Builder()
                                    .url(url_sap_solicitante)
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Accept", "application/json")
                                    .addHeader("Authorization", "Basic " + encodedAuth)
                                    .get()
                                    .build();

                            Response postResponse_pk_solicitante = client_solicitante.newCall(postRequest_pk_solicitante).execute();

                            if (postResponse_pk_solicitante.isSuccessful()) {

                                String responseBody_solicitante = postResponse_pk_solicitante.body().string();
                                JSONObject jsonObject_solicitante = new JSONObject(responseBody_solicitante);
                                JSONObject dObject_solicitante = jsonObject_solicitante.getJSONObject("d");
                                JSONArray resultsArray_solicitante = dObject_solicitante.getJSONArray("results");

                                if (resultsArray_solicitante.length() > 0) {
                                    JSONObject firstResult = resultsArray_solicitante.getJSONObject(0); // Tomar el primer elemento
                                    solicitante_nombre = firstResult.getString("PersonFullName");

                                    // Imprimir el valor o utilizarlo como necesites
                                    System.out.println("PersonFullName: " + solicitante_nombre);
                                } else {
                                    System.out.println("No hay resultados disponibles.");
                                }
                            }

                            oc.setUltimoLiberadorUsuarioNombre(solicitante_nombre);

                            oc = ordenCompraRepository.save(oc);

                            Integer idOrdenCompra = oc.getId();
                            //Integer idTipoOrdenCompra = oc.getIdTipoOrdenCompra();


                            // Procesar los detalles asociados a esta cabecera
                            for (JSONObject detail : details) {

                                OrdenCompraDetalle ocd = new OrdenCompraDetalle();
                                ocd.setIdOrdenCompra(idOrdenCompra);
                                ocd.setTipoPosicion("M");
                                ocd.setPrecioTotal(BigDecimal.valueOf(Double.parseDouble(detail.getString("NetAmount"))).setScale(4, RoundingMode.HALF_UP));
                                ocd.setPrecioUnitario(BigDecimal.valueOf(Double.parseDouble(detail.getString("NetPriceAmount"))).setScale(4, RoundingMode.HALF_UP));
                                ocd.setCantidad(BigDecimal.valueOf(Double.parseDouble(detail.getString("OrderQuantity"))).setScale(4, RoundingMode.HALF_UP));
                                ocd.setSociedad(detail.getString("PurchasingOrganization"));
                                ocd.setCodigoSapAlmacen(detail.getString("Plant"));
                                ocd.setCondicionPago(detail.getString("PaymentTerms"));
                                ocd.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));
                                ocd.setPosicion(detail.getString("PurchaseOrderItem"));
                                ocd.setPosicionOc(detail.getString("PurchaseOrderItem"));
                                ocd.setCodigoSapCentro(detail.getString("Plant"));
                                ocd.setNumeroSolped(detail.getString("PurchaseRequisition"));
                                /*Buscamos descripcion de centro / almace */
                                CentroAlmacen centroAlmacen = centroAlmacenRepository.getByCodigoSap(detail.getString("Plant"));

                                if (centroAlmacen != null) {
                                    ocd.setDenominacionAlmacen(centroAlmacen.getDenominacion());
                                    ocd.setDenominacionCentro(centroAlmacen.getDenominacion());
                                }

                                ocd.setFechaEntrega(timestampEntrega);

                                ocd.setCodigoSapBienServicio(detail.getString("Material"));

                                total = total + Double.parseDouble(detail.getString("NetAmount"));

                                /*Buscamos el bien */
                                List<BienServicio> bienServicioRpta = this.bienServicioRepository.findByCodigoSap(detail.getString("Material"));
                                if (bienServicioRpta.size() > 0) {
                                    ocd.setDescripcionBienServicio(bienServicioRpta.get(0).getDescripcion());
                                    ocd.setUnidadMedidaBienServicio(bienServicioRpta.get(0).getUnidadMedida().getDescripcion());
                                    ocd.setUnidadMedidaBienServicio(bienServicioRpta.get(0).getUnidadMedida().getCodigoSap());
                                    if (bienServicioRpta.get(0).getTipoItem().equals("SERVICIO")) {
                                        tipoOC = 2;
                                    } else {
                                        tipoOC = 1;
                                    }
                                }

                                ocd = ordenCompraDetalleRepository.save(ocd);
                                Integer idOrdenCompraDetalle = ocd.getId();


                                OrdenCompraDetalleTexto compraDetalleTexto = new OrdenCompraDetalleTexto();
                                compraDetalleTexto.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                compraDetalleTexto.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                compraDetalleTexto.setPosicion(detail.getString("PurchaseOrderItem"));
                                compraDetalleTexto.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                compraDetalleTexto = ordenCompraDetalleTextoRepository.save(compraDetalleTexto);

                                OrdenCompraDetalleTextoRegistroInfo compraDetalleTextoRegistroInfo = new OrdenCompraDetalleTextoRegistroInfo();
                                compraDetalleTextoRegistroInfo.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                compraDetalleTextoRegistroInfo.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                compraDetalleTextoRegistroInfo.setPosicion(detail.getString("PurchaseOrderItem"));
                                compraDetalleTextoRegistroInfo.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                compraDetalleTextoRegistroInfo = ordenCompraDetalleTextoRegistroInfoRepository.save(compraDetalleTextoRegistroInfo);

                                OrdenCompraDetalleTextoMaterialAmpliado ampliado = new OrdenCompraDetalleTextoMaterialAmpliado();
                                ampliado.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                ampliado.setLinea(String.valueOf(Integer.parseInt(detail.getString("PurchaseOrderItem"))));
                                ampliado.setPosicion(detail.getString("PurchaseOrderItem"));
                                ampliado.setNumeroOrdenCompra(detail.getString("PurchaseOrder"));

                                ampliado = ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ampliado);

                                    /*
                                     * ordenCompraDetalleTextoPosicionSapList.stream()
                                                    .filter(ocdt -> ocdt.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdt.getPosicion().equals(posicion))
                                                    .forEach(ocdt -> {
                                                        ocdt.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDT: " + ocdt.toString());
                                                        ordenCompraDetalleTextoRepository.save(ocdt);
                                                    });

                                            ordenCompraDetalleTextoRegistroInfoSapList.stream()
                                                    .filter(ocdtri -> ocdtri.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtri.getPosicion().equals(posicion))
                                                    .forEach(ocdtri -> {
                                                        ocdtri.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDTRI: " + ocdtri.toString());
                                                        ordenCompraDetalleTextoRegistroInfoRepository.save(ocdtri);
                                                    });

                                            ordenCompraDetalleTextoMaterialAmpliadoSapList.stream()
                                                    .filter(ocdtma -> ocdtma.getNumeroOrdenCompra().equals(numOrdenCompra) && ocdtma.getPosicion().equals(posicion))
                                                    .forEach(ocdtma -> {
                                                        ocdtma.setIdOrdenCompraDetalle(idOrdenCompraDetalle);
                                                        logger.error(header2 + "  WRITING NEW OCDTMA: " + ocdtma.toString());
                                                        ordenCompraDetalleTextoMaterialAmpliadoRepository.save(ocdtma);
                                                    });
                                     *
                                     */

                            }

                            oc.setSubtotal(BigDecimal.valueOf(total));

                            //Double valor_impuesto = total / 1.18 * 0.18;
                            //Double subtotal = total - valor_impuesto;

                            //oc.setSubTotal(BigDecimal.valueOf(total));
                            //oc.setValorImpuesto(BigDecimal.valueOf(valor_impuesto));
                            //oc.setTotal(BigDecimal.valueOf(subtotal));

                            /*Calculamos impuesto y subtotal */
                            if (valorImpuesto > 0) {
                                Double montoImpuesto = total * (valorImpuesto / 100);
                                Double montoSubTotal = total + montoImpuesto;
                                oc.setValorImpuesto(BigDecimal.valueOf(montoImpuesto));
                                oc.setTotal(BigDecimal.valueOf(montoSubTotal));
                            } else {
                                oc.setValorImpuesto(BigDecimal.valueOf(0));
                                oc.setTotal(BigDecimal.valueOf(total));
                            }

                            oc = ordenCompraRepository.save(oc);
                            List<OrdenCompra> ordenescompraList = new ArrayList<>();
                            ordenescompraList.add(oc);
                            consultaOrdenCompra.setOrdenCompraSapList(ordenescompraList);

                            if (enviarCorreoPublicacion) {
                                /*Enviando Correo*/
                                String respuesta = "";
                                Usuario comprador = usuarioRepository.findByCodigoSap(oc.getCompradorUsuarioSap());
                                if (comprador != null && comprador.getEmail() != null && !comprador.getEmail().isEmpty())
                                    respuesta = contactoPublicadaOCNotificacion.enviar(oc, null, comprador, null);
                                LogTransaccion logTransaccion = new LogTransaccion();
                                logTransaccion.setEnvioTrama("contactoPublicadaOCNotificacion");
                                logTransaccion.setRespuestaCodigo(respuesta);
                                logTransaccion.setTipoRegistro("extraerOrdenCompraListRFC");
                                this.logTransaccionRepository.save(logTransaccion);

                                //Proveedor proveedor = proveedorService.getProveedorByRuc(oc.getProveedorRuc());
                                Usuario proveedorUsuario = null;
                                if (proveedor != null && proveedor.getEmail() != null && !proveedor.getEmail().isEmpty()) {
                                    proveedorUsuario = new Usuario();
                                    proveedorUsuario.setEmail(proveedor.getEmail());
                                    proveedorUsuario.setApellido(proveedor.getRazonSocial());
                                } else {
                                    List<Usuario> posibleProveedorList = usuarioRepository.findByCodigoUsuarioIdp(oc.getProveedorRuc());
                                    if (posibleProveedorList != null && !posibleProveedorList.isEmpty() && posibleProveedorList.size() == 1)
                                        proveedorUsuario = posibleProveedorList.get(0);
                                }
                                String respuesta1 = "";
                                if (proveedorUsuario != null && proveedorUsuario.getEmail() != null && !proveedorUsuario.getEmail().isEmpty()) {
                                    respuesta1 = contactoPublicadaOCNotificacion.enviar(oc, proveedorUsuario, null, null);
                                }

                                LogTransaccion logTransaccion1 = new LogTransaccion();
                                logTransaccion1.setEnvioTrama("contactoPublicadaOCNotificacion");
                                logTransaccion1.setRespuestaCodigo(respuesta);
                                logTransaccion1.setTipoRegistro("extraerOrdenCompraListRFC");
                                this.logTransaccionRepository.save(logTransaccion);
                            }

                        } else {
                            /*QUE HACER SI YA EXISTE???*/

                        }


                    }
                } catch (JSONException e) {
                    System.err.println("Error al procesar el JSON: " + e.getMessage());
                }
            }

                /*consultaOrdenCompra.setOrdenCompraSapList(ordenCompraSapList);
                consultaOrdenCompra.setOrdenCompraTextoCabeceraSapList(ordenCompraTextoCabeceraSapList);
                consultaOrdenCompra.setOrdenCompraDetalleSapList(ordenCompraDetalleSapList);
                consultaOrdenCompra.setOrdenCompraDetalleTextoRegistroInfoSapList(ordenCompraDetalleTextoRegistroInfoSapList);
                consultaOrdenCompra.setOrdenCompraDetalleTextoMaterialAmpliadoSapList(ordenCompraDetalleTextoMaterialAmpliadoSapList);
                consultaOrdenCompra.setOrdenCompraDetalleTextoPosicionSapList(ordenCompraDetalleTextoPosicionSapList);*/


            if (ocProcessing.get())
                ocProcessing.set(!ocProcessing.get());
        } catch (Exception e) {
            if (ocProcessing.get())
                ocProcessing.set(!ocProcessing.get());
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
        return consultaOrdenCompra;
    }


//    private void mapFilters(JCoFunction function, String fechaInicio, String fechaFin) {
//        JCoParameterList paramList = function.getImportParameterList();
//
//        if (fechaInicio != null && !fechaInicio.isEmpty())
//            paramList.setValue("I_AEDATI", fechaInicio); // parametro fecha desde la cual se extraera las OC
//
//        if (fechaFin != null && !fechaFin.isEmpty())
//            paramList.setValue("I_AEDATF", fechaFin);  //parametro fecha hasta la cual se extraera las OC
//    }

    private String armarTrama(String fechaInicio, String fechaFinal) {


        String trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_COMPRAS_DETAIL>\n" +
                "         <!--Optional:-->\n" +
                "         <I_AEDATF>" + Optional.ofNullable(fechaInicio).orElse("") + "</I_AEDATF>\n" +
                "         <!--Optional:-->\n" +
                "         <I_AEDATI>" + Optional.ofNullable(fechaFinal).orElse("") + "</I_AEDATI>\n" +
                "         <!--Optional:-->\n" +
                "         <I_EBELN>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </I_EBELN>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_HEADER>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_HEADER>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_ITEMS_AUX>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_ITEMS_AUX>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_ITEM_SERVICES_AUX>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_ITEM_SERVICES_AUX>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_TEXT>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_TEXT>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_TEXTO_AMPL_MAT>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_TEXTO_AMPL_MAT>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_TEXTO_CABECERA>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_TEXTO_CABECERA>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_TEXTO_POS>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_TEXTO_POS>\n" +
                "         <!--Optional:-->\n" +
                "         <PO_TEXTO_REG_POS>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "         </PO_TEXTO_REG_POS>\n" +
                "      </urn:ZMM_COMPRAS_DETAIL>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return trama;

    }

    public boolean toggleOrdenCompraExtractionProcessingState() {
        ocProcessing.set(!ocProcessing.get());
        return ocProcessing.get();
    }


    public boolean currentOrdenCompraExtractionProcessingState() {
        return ocProcessing.get();
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
