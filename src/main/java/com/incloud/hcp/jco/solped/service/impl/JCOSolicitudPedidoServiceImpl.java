package com.incloud.hcp.jco.solped.service.impl;

import com.incloud.hcp.bean.LicitacionResponse;
import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.jco.solped.dto.SolicitudPedidoRFCParameterBuilder;
import com.incloud.hcp.jco.solped.dto.SolicitudPedidoRFCResponseDto;
import com.incloud.hcp.jco.solped.service.JCOSolicitudPedidoService;
import com.incloud.hcp.myibatis.mapper.SolicitudPedidoMapper;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.util.constant.SolicitudPedidoConstant;
//import com.sap.conn.jco.*;
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOSolicitudPedidoServiceImpl implements JCOSolicitudPedidoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    private final String FUNCION_RFC = "ZMMRFC_VP_CONSULTA_SOLPED";
    private final String NOMBRE_TABLA_RFC = "T_SOLPED";

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Value("${API_URL_SAP}")
    private String urlSap;
    @Value("${USERNAME_SAP}")
    private String userSap;
    @Value("${PASSWORD_SAP}")
    private String passwordSap;


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ClaseDocumentoRepository claseDocumentoRepository;

    @Autowired
    private MonedaRepository monedaRepository;

    @Autowired
    private RubroBienRepository rubroBienRepository;

    @Autowired
    private BienServicioRepository bienServicioRepository;

    @Autowired
    private CentroAlmacenRepository centroAlmacenRepository;

    @Autowired
    private SolicitudPedidoMapper solicitudPedidoMapper;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    private void registerBienNotExists(String codigoSap, String unidadMedidaSap, String rubroBienSap, String tipoItem, String txz01Description) throws Exception {

        String tipo = tipoItem.equals("9") ? "S" : "M";
        BienServicio bs = new BienServicio();

        if (!codigoSap.isEmpty()) {
            BienServicio bienServicio = this.getBienServicio(codigoSap);
            if (bienServicio == null) {

                UnidadMedida unidadMedida = this.unidadMedidaRepository.findByCodigoSap(unidadMedidaSap);
                RubroBien rubroBien = rubroBienRepository.findByCodigoSap(rubroBienSap);

                if(unidadMedida == null) {
                    throw new Exception("No se encontró Unidad Medida: " + unidadMedidaSap + ". Se solicita registrar previamente dicha Unidad Medida en el SCP");
                }

                if(rubroBien == null) {
                    throw new Exception("No se encontró Rubro : " + rubroBienSap + ". Se solicita registrar previamente dicho Rubro en el SCP");
                }

                bs.setCodigoSap(codigoSap);
                bs.setUnidadMedida(unidadMedida);
                bs.setDescripcionLarga(txz01Description);
                bs.setDescripcion(txz01Description);
                bs.setRubroBien(rubroBien);
                bs.setTipoItem(tipo);

                bienServicioRepository.save(bs);
            }
        }
    }

    public SolicitudPedidoRFCResponseDto getSolpedResponseByCodigo_old(String numeroSolicitud) throws Exception {

        SolicitudPedidoRFCResponseDto solicitudPedidoRFCResponseDto = new SolicitudPedidoRFCResponseDto();

        String tramaXML = armarTrama(numeroSolicitud);
        logger.info("RFC: Trama" + tramaXML);

        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaXML);
        soap.append(" \n");
        soap.append(" \n");
        soap.append("");

        //region jco
        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
        logger.info(String.valueOf(client));

        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        //String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_CONSULTA_SOLPED?sap-client=400";
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_CONSULTA_SOLPED1?sap-client=400";


        logger.info("RFC: URL" + url);

        HttpEntity strEntity = new StringEntity(soap.toString());
        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_SOLPED1/ZMM_CONSULTA_SOLPED1Request");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_SOLPED1/ZMM_CONSULTA_SOLPED1Request");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");
        //endregion


//        //region local
//        HttpParams httpParameters = new BasicHttpParams();
//        // Set the timeout in milliseconds until a connection is established.
//        int timeoutConnection = 15000;
//        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT)
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 35000;
//        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//
//        DefaultHttpClient client = new DefaultHttpClient(httpParameters);
//        String encoding = Base64.getEncoder().encodeToString(("CMENDEZ:Csti2022").getBytes(StandardCharsets.UTF_8));
//
//        HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
//        HttpPost post = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_CONSULTA_SOLPED1?sap-client=400");
//        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_SOLPED1/ZMM_CONSULTA_SOLPED1Request");
//        post.setHeader("SOAPAction", "uurn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_SOLPED1/ZMM_CONSULTA_SOLPED1Request");
//        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
//        post.setHeader("Accept-Encoding", "gzip,deflate");
//        //endregion


        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);


        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);


        logger.info("RESULT" + result);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(result)));

        SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigo);
        sapLog.setMesaj(message);
        solicitudPedidoRFCResponseDto.setSapLog(sapLog);
        //logger.error("02b - getSolpedResponseByCodigo - sapLog: " + sapLog.toString());

        /* Recorriendo valores obtenidos del RFC */
        List<SolicitudPedido> solicitudPedidoList = new ArrayList<SolicitudPedido>();
        NodeList posicionesSolped = doc.getElementsByTagName("T_SOLPED").item(0).getChildNodes();

        //JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC);

        for (int i = 0; i < posicionesSolped.getLength(); i++) {

            Node posicion = posicionesSolped.item(i);
            Element elemento = (Element) posicion;
            String sapclasedocumento = Utils.getValueNodo(elemento, "BSART");

            if (sapclasedocumento.equals("")) {
                continue;
            }

            //logger.error("A - getDevuelveValores TABLE JCO: " + table.toString());
            SolicitudPedido bean = new SolicitudPedido();
            bean.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
            bean.setSapClaseDocumento(Utils.getValueNodo(elemento, "BSART"));


            String nodoCodigoSap = Utils.getValueNodo(elemento, "MATNR");
            if (nodoCodigoSap.isEmpty()) {
                bean.setSapBienServicio("");
            } else {
                bean.setSapBienServicio(nodoCodigoSap);
            }

            String nodoUnidadMedida = Utils.getValueNodo(elemento, "MEINS");
            if (nodoUnidadMedida.isEmpty()) {
                bean.setSapUnidadMedida("N/A");
            } else {
                bean.setSapUnidadMedida(nodoUnidadMedida);
            }

            String nodoRubroBien = Utils.getValueNodo(elemento, "MATKL");
            if (nodoRubroBien.isEmpty()) {
                bean.setSapRubroBien("N/A");
            } else {
                bean.setSapRubroBien(nodoRubroBien);
            }

            String txz01Description = Utils.getValueNodo(elemento, "TXZ01");
            String tipoItemPSTYP = Utils.getValueNodo(elemento, "PSTYP");


            //this.registerBienNotExists(bean.getSapBienServicio(), bean.getSapUnidadMedida(), bean.getSapRubroBien(), tipoItemPSTYP, txz01Description);


            bean.setSapCentro(Utils.getValueNodo(elemento, "WERKS"));
            bean.setSapAlmacen(Utils.getValueNodo(elemento, "LGORT"));
            bean.setPosicion(Utils.getValueNodo(elemento, "BNFPO").toString());
            bean.setSolicitudPedido(Utils.getValueNodo(elemento, "BANFN"));
            bean.setCantidad(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "MENGE")));
            bean.setNroParte(Utils.getValueNodo(elemento, "MFRPN"));
            bean.setSapfechaEntrega(Utils.getValueNodoDate(elemento, "LFDAT"));
            bean.setPrecio(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "RLWRT")));
            bean.setMoneda(Utils.getValueNodo(elemento, "WAERS"));
            bean.setEstadoSolped(Utils.getValueNodo(elemento, "FRGKZ"));
            logger.error("B - getDevuelveValores bean: " + bean);

            ClaseDocumento claseDocumento = this.getClaseDocumento(bean.getSapClaseDocumento());
            if (Optional.ofNullable(claseDocumento).isPresent()) {
                //logger.error("C - getDevuelveValores claseDocumento: " + claseDocumento.toString());
                bean.setDescripcionClaseDocumento(claseDocumento.getDescripcion());
                bean.setIdClaseDocumento(claseDocumento.getIdClaseDocumento());
            } else {
                throw new Exception("No se encontró Clase Documento: " + bean.getSapClaseDocumento() + " Se solicita registrar previamente dicha Clase Documento en el SCP");
            }

            Moneda moneda = this.getMoneda(bean.getMoneda());
            if (Optional.ofNullable(moneda).isPresent()) {
                //logger.error("C - getDevuelveValores claseDocumento: " + claseDocumento.toString());
                bean.setMoneda(moneda.getCodigoMoneda());
                bean.setIdMoneda(moneda.getIdMoneda());
            } else {
                throw new Exception("No se encontró Clase Documento: " + bean.getSapClaseDocumento() + " Se solicita registrar previamente dicha Clase Documento en el SCP");
            }

            CentroAlmacen centro = this.getCentroAlmacen(bean.getSapCentro(), 1);
            if (Optional.ofNullable(centro).isPresent()) {
                //logger.error("D - getDevuelveValores centro: " + centro.toString());
                bean.setCentro(centro);
            } else {
                throw new Exception("No se encontró Centro: " + bean.getSapCentro() + " Se solicita registrar previamente dicho Centro en el SCP");
            }

            CentroAlmacen almacen = this.getAlmacen(centro, bean.getSapAlmacen(), 2);
            if (Optional.ofNullable(almacen).isPresent()) {
                bean.setAlmacen(almacen);
            }

            BienServicio bienServicio = this.getBienServicio(bean.getSapBienServicio());

            if (!Optional.ofNullable(bienServicio).isPresent()) {
                bienServicio = new BienServicio();

                bienServicio.setCodigoSap("N/A");
                bienServicio.setDescripcion(txz01Description);
                bienServicio.setDescripcionLarga(txz01Description);

                //throw new Exception("No se encontró Bien Servicio: " + bean.getSapBienServicio() + " Se solicita registrar previamente dicho Bien Servicio en el SCP");
                //continue;
            }else{
                bean.setSapRubroBien(bienServicio.getRubroBien().getCodigoSap());
            }

            if (bean.getSapBienServicio().isEmpty()) {
                bienServicio.setCodigoSap("N/A");
                bienServicio.setDescripcion(txz01Description);
                bienServicio.setDescripcionLarga(txz01Description);
            }

            UnidadMedida um = this.unidadMedidaRepository.findByCodigoSap(bean.getSapUnidadMedida());
            if (!Optional.ofNullable(um).isPresent()) {
                um = new UnidadMedida();

                um.setCodigoSap("N/A");
                um.setDescripcion("N/A");
                throw new Exception("No se encontró Unidad Medida: " + bean.getSapUnidadMedida() + " Se solicita registrar previamente dicha Unidad Medida en el SCP");
                //continue;
            }

            RubroBien rubroBien = rubroBienRepository.findByCodigoSap(nodoRubroBien);
            bienServicio.setRubroBien(rubroBien);


            if (rubroBien != null) {
                if (bienServicio.getRubroBien().getCodigoSap().equals(1)
                        ||bienServicio.getRubroBien().getCodigoSap().equals(null)
                            || bienServicio.getRubroBien().getCodigoSap().equals(2)){
                    bienServicio.setRubroBien(rubroBien);
                    bienServicioRepository.save(bienServicio);
                }
                bean.setGrupoArticulo(rubroBien.getDescripcion());
            } else {
                bean.setGrupoArticulo("N/A");

            }

            bienServicio.setUnidadMedida(um);

            bean.setBienServicio(bienServicio);
            bean.setIdUnidadMedida(um.getIdUnidadMedida());
            bean.setUnidadMedida(um);

            if (bienServicio.getIdBienServicio() != null) {
                String estadoPosicion = this.getEstadoPosicion(numeroSolicitud, bean.getPosicion(), bienServicio.getIdBienServicio());
                bean.setEstado(estadoPosicion);
            }else{
                String estadoPosicion= this.getEstadoPosicionSinBienServicio(numeroSolicitud, bean.getPosicion());
                bean.setEstado(estadoPosicion);
            }


            solicitudPedidoList.add(bean);
        }

        solicitudPedidoRFCResponseDto.setListaSolped(solicitudPedidoList);
        if (solicitudPedidoList != null && solicitudPedidoList.size() > 0) {
            CentroAlmacen almacen = null;
            CentroAlmacen centro = solicitudPedidoList.get(0).getCentro();
            if (Optional.ofNullable(solicitudPedidoList.get(0).getAlmacen()).isPresent()) {
                almacen = solicitudPedidoList.get(0).getAlmacen();
            }
            Date fechaEntrega = solicitudPedidoList.get(0).getSapfechaEntrega();
            Integer idClaseDocumento = solicitudPedidoList.get(0).getIdClaseDocumento();
            Integer idMoneda = solicitudPedidoList.get(0).getIdMoneda();
            solicitudPedidoRFCResponseDto.setFechaEntrega(fechaEntrega);
            solicitudPedidoRFCResponseDto.setIdCentro(centro.getIdCentroAlmacen());
            solicitudPedidoRFCResponseDto.setIdClaseDocumento(idClaseDocumento);
            solicitudPedidoRFCResponseDto.setIdMoneda(idMoneda);
            if (Optional.ofNullable(almacen).isPresent()) {
                solicitudPedidoRFCResponseDto.setIdAlmacen(almacen.getIdCentroAlmacen());
            }
            BigDecimal precio = solicitudPedidoList.get(0).getPrecio();
            if (Optional.ofNullable(precio).isPresent()) {
                solicitudPedidoRFCResponseDto.setPrecioSolped(precio);
            }

            String estado = solicitudPedidoList.get(0).getEstadoSolped();
            if (Optional.ofNullable(estado).isPresent()) {
                solicitudPedidoRFCResponseDto.setEstadoSolped(estado);
            }
        }
        //logger.error("04 - getSolpedResponseByCodigo solicitudPedidoRFCResponseDto: " + solicitudPedidoRFCResponseDto.toString());
        return solicitudPedidoRFCResponseDto;

    }

    public SolicitudPedidoRFCResponseDto getSolpedResponseByCodigo(String numeroSolicitud) throws Exception {

        SolicitudPedidoRFCResponseDto solicitudPedidoRFCResponseDto = new SolicitudPedidoRFCResponseDto();

        String url_sap = urlSap + "/sap/opu/odata/sap/YY1_PURCHASEREQUISITION_CDS/YY1_PurchaseRequisition?$filter=PurchaseRequisition eq" + "'" + numeroSolicitud + "'";
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
        List<SolicitudPedido> solicitudPedidoList = new ArrayList<SolicitudPedido>();

        if (postResponse_pk.isSuccessful()) {

            String responseBody = postResponse_pk.body().string();

            try {
                // Convertir la respuesta en un objeto JSON
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject dObject = jsonObject.getJSONObject("d");
                JSONArray resultsArray = dObject.getJSONArray("results");

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject result = resultsArray.getJSONObject(i);
                    String sapclasedocumento = result.getString("PurchaseRequisitionType"); //Utils.getValueNodo(elemento, "BSART");
        
                    if (sapclasedocumento.equals("")) {
                        continue;
                    }
        
                    SolicitudPedido bean = new SolicitudPedido();
                    //bean.setSociedad(Utils.getValueNodo(elemento, "BUKRS"));
                    bean.setSapClaseDocumento(result.getString("PurchaseRequisitionType"));//bean.setSapClaseDocumento(Utils.getValueNodo(elemento, "BSART"));
        
        
                    String nodoCodigoSap = result.getString("Material");//String nodoCodigoSap = Utils.getValueNodo(elemento, "MATNR");
                    if (nodoCodigoSap.isEmpty()) {
                        bean.setSapBienServicio("");
                    } else {
                        bean.setSapBienServicio(nodoCodigoSap);
                    }
        
                    String nodoUnidadMedida = result.getString("BaseUnit");//Utils.getValueNodo(elemento, "MEINS");
                    if (nodoUnidadMedida.isEmpty()) {
                        bean.setSapUnidadMedida("N/A");
                    } else {
                        bean.setSapUnidadMedida(nodoUnidadMedida);
                    }
        
                    /*String nodoRubroBien = Utils.getValueNodo(elemento, "MATKL");
                    if (nodoRubroBien.isEmpty()) {
                        bean.setSapRubroBien("N/A");
                    } else {
                        bean.setSapRubroBien(nodoRubroBien);
                    }*/
                    String nodoRubroBien = "N/A";
                    bean.setSapRubroBien(nodoRubroBien);

                    String txz01Description = "";//Utils.getValueNodo(elemento, "TXZ01");
                    String tipoItemPSTYP = "";//Utils.getValueNodo(elemento, "PSTYP");
        
        
                    //this.registerBienNotExists(bean.getSapBienServicio(), bean.getSapUnidadMedida(), bean.getSapRubroBien(), tipoItemPSTYP, txz01Description);
        

                    bean.setSapCentro(result.getString("Plant"));
                    bean.setSapAlmacen(result.getString("StorageLocation"));
                    bean.setPosicion(result.getString("PurchaseRequisitionItem"));
                    bean.setSolicitudPedido(result.getString("PurchaseRequisition"));
                    bean.setAreaSolicitante(result.getString("YY1_AreaSolicitante_PRI"));
                    bean.setMoneda(result.getString("PurReqnItemCurrency"));
                    bean.setEstadoSolped(result.getString("PurchaseRequisitionStatus"));

                    //precio
                    String precioStr = result.getString("PurchaseRequisitionPrice");

                    BigDecimal precioDecimal = new BigDecimal(precioStr);
                    bean.setPrecio(precioDecimal);

                    String cantidad = result.getString("RequestedQuantity");
                    BigDecimal cantidadDecimal = new BigDecimal(cantidad);
                    bean.setCantidad(cantidadDecimal);
                    //bean.setCantidad(BigDecimal.valueOf(Double.valueOf(result.getString("RequestedQuantity"))));

                    bean.setNroParte(result.getString("ProductManufacturerNumber"));

                    String fechaDeliveryString = result.getString("DeliveryDate");

                    // Extraer el timestamp entre los paréntesis
                    String timestamp = fechaDeliveryString.replaceAll("[^0-9]", ""); // Extraer solo los dígitos
                    long epochMillis = Long.parseLong(timestamp);

                    // Convertir el timestamp a LocalDate
                    Instant instant = Instant.ofEpochMilli(epochMillis);
                    LocalDate fechaDelivery = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                    // Formatear la fecha a una cadena en formato yyyy-MM-dd si es necesario
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String fechaFormateada = fechaDelivery.format(formatter);

                    // Si necesitas java.sql.Date, convertir de nuevo
                    Date fechaDeliveryDate = java.sql.Date.valueOf(fechaDelivery);

                    // Asignar al bean
                    bean.setSapfechaEntrega(fechaDeliveryDate);

                    // Imprimir la fecha formateada como yyyy-MM-dd (opcional)
                    System.out.println("Fecha Delivery Formateada: " + fechaFormateada);
                    //bean.setSapfechaEntrega(Utils.getValueNodoDate(elemento, "LFDAT"));
                    
                    
                    //bean.setPrecio(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "RLWRT")));
                    //bean.setMoneda(Utils.getValueNodo(elemento, "WAERS"));
                    //bean.setEstadoSolped(Utils.getValueNodo(elemento, "FRGKZ"));
                    logger.error("B - getDevuelveValores bean: " + bean);


                    //aqui esta el inconveniente ->
                    ClaseDocumento claseDocumento = this.getClaseDocumento(bean.getSapClaseDocumento());
                    if (Optional.ofNullable(claseDocumento).isPresent()) {
                        //logger.error("C - getDevuelveValores claseDocumento: " + claseDocumento.toString());
                        bean.setDescripcionClaseDocumento(claseDocumento.getDescripcion());
                        bean.setIdClaseDocumento(claseDocumento.getIdClaseDocumento());
                    } else {
                        throw new Exception("No se encontró Clase Documento: " + bean.getSapClaseDocumento() + " Se solicita registrar previamente dicha Clase Documento en el SCP");
                    }
        
                    Moneda moneda = this.getMoneda(bean.getMoneda());
                    if (Optional.ofNullable(moneda).isPresent()) {
                        //logger.error("C - getDevuelveValores claseDocumento: " + claseDocumento.toString());
                        bean.setMoneda(moneda.getCodigoMoneda());
                        bean.setIdMoneda(moneda.getIdMoneda());
                    } else {
                        throw new Exception("No se encontró Clase Documento: " + bean.getSapClaseDocumento() + " Se solicita registrar previamente dicha Clase Documento en el SCP");
                    }
        
                    CentroAlmacen centro = this.getCentroAlmacen(bean.getSapCentro(), 1);
                    if (Optional.ofNullable(centro).isPresent()) {
                        //logger.error("D - getDevuelveValores centro: " + centro.toString());
                        bean.setCentro(centro);
                    } else {
                        throw new Exception("No se encontró Centro: " + bean.getSapCentro() + " Se solicita registrar previamente dicho Centro en el SCP");
                    }
        
                    CentroAlmacen almacen = this.getAlmacen(centro, bean.getSapAlmacen(), 2);
                    if (Optional.ofNullable(almacen).isPresent()) {
                        bean.setAlmacen(almacen);
                    }
        
                    BienServicio bienServicio = this.getBienServicio(bean.getSapBienServicio());
        
                    if (!Optional.ofNullable(bienServicio).isPresent()) {
                        bienServicio = new BienServicio();
        
                        bienServicio.setCodigoSap("N/A");
                        bienServicio.setDescripcion(txz01Description);
                        bienServicio.setDescripcionLarga(txz01Description);
        
                        //throw new Exception("No se encontró Bien Servicio: " + bean.getSapBienServicio() + " Se solicita registrar previamente dicho Bien Servicio en el SCP");
                        //continue;
                    }else{
                        bean.setSapRubroBien(bienServicio.getRubroBien().getCodigoSap());
                    }
        
                    if (bean.getSapBienServicio().isEmpty()) {
                        bienServicio.setCodigoSap("N/A");
                        bienServicio.setDescripcion(txz01Description);
                        bienServicio.setDescripcionLarga(txz01Description);
                    }
        
                    UnidadMedida um = this.unidadMedidaRepository.findByCodigoSap(bean.getSapUnidadMedida());
                    if (!Optional.ofNullable(um).isPresent()) {
                        um = new UnidadMedida();
        
                        um.setCodigoSap("N/A");
                        um.setDescripcion("N/A");
                        throw new Exception("No se encontró Unidad Medida: " + bean.getSapUnidadMedida() + " Se solicita registrar previamente dicha Unidad Medida en el SCP");
                        //continue;
                    }
        
                    /*RubroBien rubroBien = rubroBienRepository.findByCodigoSap(nodoRubroBien);
                    bienServicio.setRubroBien(rubroBien);
        
        
                    if (rubroBien != null) {
                        if (bienServicio.getRubroBien().getCodigoSap().equals(1)
                                ||bienServicio.getRubroBien().getCodigoSap().equals(null)
                                    || bienServicio.getRubroBien().getCodigoSap().equals(2)){
                            bienServicio.setRubroBien(rubroBien);
                            bienServicioRepository.save(bienServicio);
                        }
                        bean.setGrupoArticulo(rubroBien.getDescripcion());
                    } else {
                        bean.setGrupoArticulo("N/A");
        
                    }*/
                    RubroBien rubro = this.grupoArticuloSAP(bienServicio.getCodigoSap());
                    if(rubro.getCodigoSap() != null){    bienServicio.setRubroBien(rubro);
                        bienServicioRepository.save(bienServicio);}else {    bean.setGrupoArticulo("N/A");
                    }



                    bienServicio.setUnidadMedida(um);
        
                    bean.setBienServicio(bienServicio);
                    bean.setIdUnidadMedida(um.getIdUnidadMedida());
                    bean.setUnidadMedida(um);
        
                    if (bienServicio.getIdBienServicio() != null) {
                        String estadoPosicion = this.getEstadoPosicion(numeroSolicitud, bean.getPosicion(), bienServicio.getIdBienServicio());
                        bean.setEstado(estadoPosicion);
                    }else{
                        String estadoPosicion= this.getEstadoPosicionSinBienServicio(numeroSolicitud, bean.getPosicion());
                        bean.setEstado(estadoPosicion);
                    }
        
        
                    solicitudPedidoList.add(bean);

                   
                }

                solicitudPedidoRFCResponseDto.setListaSolped(solicitudPedidoList);
                if (solicitudPedidoList != null && solicitudPedidoList.size() > 0) {
                    CentroAlmacen almacen = null;
                    CentroAlmacen centro = solicitudPedidoList.get(0).getCentro();
                    if (Optional.ofNullable(solicitudPedidoList.get(0).getAlmacen()).isPresent()) {
                        almacen = solicitudPedidoList.get(0).getAlmacen();
                    }
                    Date fechaEntrega = solicitudPedidoList.get(0).getSapfechaEntrega();
                    Integer idClaseDocumento = solicitudPedidoList.get(0).getIdClaseDocumento();
                    Integer idMoneda = solicitudPedidoList.get(0).getIdMoneda();
                    solicitudPedidoRFCResponseDto.setFechaEntrega(fechaEntrega);
                    solicitudPedidoRFCResponseDto.setIdCentro(centro.getIdCentroAlmacen());
                    solicitudPedidoRFCResponseDto.setIdClaseDocumento(idClaseDocumento);
                    solicitudPedidoRFCResponseDto.setIdMoneda(idMoneda);
                    if (Optional.ofNullable(almacen).isPresent()) {
                        solicitudPedidoRFCResponseDto.setIdAlmacen(almacen.getIdCentroAlmacen());
                    }
                    BigDecimal precio = solicitudPedidoList.get(0).getPrecio();
                    if (Optional.ofNullable(precio).isPresent()) {
                        solicitudPedidoRFCResponseDto.setPrecioSolped(precio);
                    }

                    String estado = solicitudPedidoList.get(0).getEstadoSolped();
                    if (Optional.ofNullable(estado).isPresent()) {
                        solicitudPedidoRFCResponseDto.setEstadoSolped(estado);
                    }
                    SapLog sapLog = new SapLog();
                    String codigo = "0";
                    String message = "Solped encontrada con exito!";

                    sapLog.setCode(codigo);
                    sapLog.setMesaj(message);
                    solicitudPedidoRFCResponseDto.setSapLog(sapLog);
                }


            }catch (Exception e) {
                SapLog sapLog = new SapLog();
                String codigo = "400";
                String message = "Solped no encontrada!";

                sapLog.setCode(codigo);
                sapLog.setMesaj(message);
                solicitudPedidoRFCResponseDto.setSapLog(sapLog);
                logger.error("Error: " + e.getMessage());
            }
        }else{
        }


        //logger.error("04 - getSolpedResponseByCodigo solicitudPedidoRFCResponseDto: " + solicitudPedidoRFCResponseDto.toString());
        return solicitudPedidoRFCResponseDto;

    }

    public String modificarSolped(Integer codigoSolicitud, String posicion, BigDecimal cantidad, BigDecimal precioOc) throws Exception {

        SolicitudPedidoRFCResponseDto solicitudPedidoRFCResponseDto = new SolicitudPedidoRFCResponseDto();

        String tramaXML = armarTramaModificar(codigoSolicitud, posicion, cantidad, precioOc);
        SolicitudPedidoRFCResponseDto solicitudPedidoRFCResponseDto1 = this.getSolpedResponseByCodigo(codigoSolicitud.toString());

        SolicitudPedido solicitudPedido1 = solicitudPedidoRFCResponseDto1.getListaSolped().get(0);

        logger.info("SOLICITUD- " + solicitudPedido1);


        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

        logger.info(String.valueOf(client));
        //Uri x = new Uri("http://connectivityproxy.internal.cf.us10.hana.ondemand.com:20003");
        /*Uri x = new Uri(destination2.get().asHttp().getProxyConfiguration().get().getUri().getPath());
        logger.info(x.getHost());
        logger.info(String.valueOf(x.getPort()));
        */
        String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
        Integer portx = 20003;

        /*DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory() {
            @Override
            protected RequestConfig.Builder getRequestConfigBuilder(HttpDestinationProperties destination) {
                return super.getRequestConfigBuilder(destination)
                        .setProxy(new HttpHost(hostx, portx, "http"));
            }

            @Override
            protected HttpClientBuilder getHttpClientBuilder(HttpDestinationProperties destination) {
                return super.getHttpClientBuilder(destination)
                        .setUserAgent("SDK");
            }
        };

        HttpClientAccessor.setHttpClientFactory(customFactory);*/
        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_modifica_solped/100/zws_modifica_solped/zws_modifica_solped";

        logger.info("RFC: URL" + url);

        //String tramaXML = armarTrama2();

        logger.info("RFC: Trama" + tramaXML);

        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaXML);
        soap.append(" \n");
        soap.append(" \n");

        /* soap.append(body); */
        // END of MEssage Body
        soap.append("");

        HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
        //HttpEntity strEntity = new StringEntity(soap.toString());

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_MODIFICA_SOLPED:ZPE_MM_MODIFICA_SOLPEDRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_MODIFICA_SOLPED:ZPE_MM_MODIFICA_SOLPEDRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        logger.info("HTTP POS" + post.toString());

        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);


        DocumentBuilderFactory domFactory = DocumentBuilderFactory
            .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
            .parse(new InputSource(new StringReader(result)));

        solicitudPedido1.setPosicion(posicion);
        solicitudPedido1.setPrecio(precioOc);

        logger.info("SOLICITUD ACTUALIZADA :" + solicitudPedido1);
        logger.info("RFC: Resultado :" + result);
        String respuesta = "";
        if (result.equals(null)) {
            respuesta = "Solped NO actualizada";
        } else {
            respuesta = "Solped actualizada";

        }
        //logger.error("04 - getSolpedResponseByCodigo solicitudPedidoRFCResponseDto: " + solicitudPedidoRFCResponseDto.toString());
        return respuesta;

    }

    private ClaseDocumento getClaseDocumento(String codigoClaseDocumento) throws Exception {
        if (Optional.ofNullable(codigoClaseDocumento).isPresent()) {
            ClaseDocumento claseDoc = this.claseDocumentoRepository.
                findByCodigoClaseDocumentoAndNivel(codigoClaseDocumento, 1);
            return claseDoc;
        } else {
            return null;
        }
    }

    private Moneda getMoneda(String codigoMoneda) throws Exception {
        if (Optional.ofNullable(codigoMoneda).isPresent()) {
            Moneda claseDoc = this.monedaRepository.getByCodigoMoneda(codigoMoneda);
            return claseDoc;
        } else {
            return null;
        }
    }

    private BienServicio getBienServicio(String codigoSap) throws Exception {
        if (Optional.ofNullable(codigoSap).isPresent()) {
            List<BienServicio> bienServ = this.bienServicioRepository.findByCodigoSap(codigoSap);
            if(bienServ.size()> 0){
                return bienServ.get(0);
            }else {
                return null;
            }
        } else {
            return null;
        }
    }

    private CentroAlmacen getCentroAlmacen(String codigoSap, int nivel) throws Exception {
        if (Optional.ofNullable(codigoSap).isPresent()) {
            CentroAlmacen obj = this.centroAlmacenRepository.findByCodigoSapAndNivel(codigoSap, nivel);
            return obj;
        } else {
            return null;
        }

    }

    private CentroAlmacen getAlmacen(CentroAlmacen centroAlmacen, String codigoSap, int nivel) throws Exception {
        if (Optional.ofNullable(codigoSap).isPresent()) {
            CentroAlmacen obj = this.centroAlmacenRepository.findByIdPadreAndCodigoSapAndNivel(
                centroAlmacen.getIdCentroAlmacen(),
                codigoSap,
                nivel);
            return obj;
        } else {
            return null;
        }

    }

    /**
     * @param codSolped       = Codigo Solped
     * @param codBienServicio = Codigo de Material o Servicio
     * @return idLicitacion si la consulta devuelve algo, de lo contrario retorna null;
     */
    private String getEstadoPosicion(String codSolped, int codBienServicio) throws Exception {
        List<LicitacionResponse> listaLicitacion = this.solicitudPedidoMapper.getLicitacionBySolpedAndBien(codSolped, codBienServicio);
        String estado = "";
        if (listaLicitacion.size() > 0) {
            String estadoObj = listaLicitacion.get(0).getEstadoLicitacion();

            switch (estadoObj) {
                case "GE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "PE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "EV":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "AD":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                case "AN":
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
                    break;
                case "ES":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                default:
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
            }
        } else {
            estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
        }
        return estado;
    }


    private String getEstadoPosicion(String codSolped, String posicion, int codBienServicio) throws Exception {
        List<LicitacionResponse> listaLicitacion = this.solicitudPedidoMapper.getLicitacionBySolpedPosicionAndBien(codSolped, posicion, codBienServicio);
        String estado = "";
        if (listaLicitacion.size() > 0) {
            String estadoObj = listaLicitacion.get(0).getEstadoLicitacion();

            switch (estadoObj) {
                case "GE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "PE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "EV":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "AD":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                case "AN":
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
                    break;
                case "ES":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                default:
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
            }
        } else {
            estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
        }
        return estado;
    }

    @Autowired
    private LicitacionDetalleRepository  licitacionDetalleRepository;

    private String getEstadoPosicionSinBienServicio(String codSolped, String posicion) throws Exception {
        List<LicitacionDetalle> listaLicitacion = licitacionDetalleRepository.findBySolicitudPedidoAndPosicionSolicitudPedido(codSolped,posicion);
        String estado = "";
        if (listaLicitacion.size() > 0) {
            String estadoObj = listaLicitacion.get(0).getLicitacion().getEstadoLicitacion();

            switch (estadoObj) {
                case "GE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "PE":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "EV":
                    estado = SolicitudPedidoConstant.ESTADO_EN_PROCESO;
                    break;
                case "AD":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                case "AN":
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
                    break;
                case "ES":
                    estado = SolicitudPedidoConstant.ESTADO_ADJUDICADO;
                    break;
                default:
                    estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
            }
        } else {
            estado = SolicitudPedidoConstant.ESTADO_POR_LICITAR;
        }
        return estado;
    }

    public RubroBien grupoArticuloSAP(String producto) throws IOException {
        String url_sap = urlSap + "/sap/opu/odata/sap/YY1_MATERIALGROUP_CDS/YY1_MaterialGroup?$filter=Language eq 'ES' and Product eq '"+producto+"'";
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
        List<SolicitudPedido> solicitudPedidoList = new ArrayList<SolicitudPedido>();
        RubroBien rubroBien = new RubroBien();
        if (postResponse_pk.isSuccessful()) {
            String responseBody = postResponse_pk.body().string();

            try {
                // Convertir la respuesta en un objeto JSON
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject dObject = jsonObject.getJSONObject("d");
                JSONArray resultsArray = dObject.getJSONArray("results");

                if(resultsArray.length() > 0){
                    JSONObject result = resultsArray.getJSONObject(0);
                    String materialGroupName = result.getString("MaterialGroupName");
                    String materialGroup =   result.getString("MaterialGroup");

                    rubroBien.setCodigoSap(materialGroup);
                    rubroBien.setDescripcion(materialGroupName);
                    rubroBien.setNivel(1);

                    RubroBien rb = this.rubroBienRepository.findByCodigoSap(materialGroup);
                    if (rb != null) {
                        return rb;
                    }else{
                        return this.rubroBienRepository.save(rubroBien);
                    }

                }

                return rubroBien;
            }catch (Exception e) {
                System.out.println("error");
            }
        }else{
            return rubroBien;
        }
        return rubroBien;
    }



    private String armarTrama(String numeroSolicitud) {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_CONSULTA_SOLPED1>\n" +
                "         <PI_BANFN>"+numeroSolicitud+"</PI_BANFN>\n" +
                "         <T_SOLPED>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT></MANDT>\n" +
                "               <BANFN></BANFN>\n" +
                "               <BNFPO></BNFPO>\n" +
                "               <BSART></BSART>\n" +
                "               <BUKRS></BUKRS>\n" +
                "               <WERKS></WERKS>\n" +
                "               <LGORT></LGORT>\n" +
                "               <MATNR></MATNR>\n" +
                "               <MENGE></MENGE>\n" +
                "               <PREIS></PREIS>\n" +
                "               <MEINS></MEINS>\n" +
                "               <LFDAT></LFDAT>\n" +
                "               <MFRPN></MFRPN>\n" +
                "               <LOEKZ></LOEKZ>\n" +
                "               <RLWRT></RLWRT>\n" +
                "               <WAERS></WAERS>\n" +
                "               <FRGKZ></FRGKZ>\n" +
                "               <KNTTP></KNTTP>\n" +
                "               <MATKL></MATKL>\n" +
                "               <TXZ01></TXZ01>\n" +
                "            </item>\n" +
                "         </T_SOLPED>\n" +
                "      </urn:ZMM_CONSULTA_SOLPED1>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }

    private String armarTramaModificar(Integer codigoSolicitud, String posicion, BigDecimal cantidad, BigDecimal precioOc) {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <urn:ZPE_MM_MODIFICA_SOLPED>\n" +
            "         <P_NRO_SOLPED>" + codigoSolicitud + "</P_NRO_SOLPED>\n" +
            "         <RETURN>\n" +
            "            <!--Zero or more repetitions:-->\n" +
            "            <item>\n" +
            "               <TYPE></TYPE>\n" +
            "               <CODE></CODE>\n" +
            "               <MESSAGE></MESSAGE>\n" +
            "               <LOG_NO></LOG_NO>\n" +
            "               <LOG_MSG_NO></LOG_MSG_NO>\n" +
            "               <MESSAGE_V1></MESSAGE_V1>\n" +
            "               <MESSAGE_V2></MESSAGE_V2>\n" +
            "               <MESSAGE_V3></MESSAGE_V3>\n" +
            "               <MESSAGE_V4></MESSAGE_V4>\n" +
            "            </item>\n" +
            "         </RETURN>\n" +
            "         <TO_ITEM_SOLPED>\n" +
            "            <!--Zero or more repetitions:-->\n" +
            "            <item>\n" +
            "               <POSICION>" + posicion + "</POSICION>\n" +
            "               <CANTIDAD>" + cantidad + "</CANTIDAD>\n" +
            "               <PRECIO>" + precioOc + "</PRECIO>\n" +
            "            </item>\n" +
            "         </TO_ITEM_SOLPED>\n" +
            "      </urn:ZPE_MM_MODIFICA_SOLPED>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

        return tramaXML;

    }
}
