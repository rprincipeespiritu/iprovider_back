package com.incloud.hcp.jco.proveedor.service.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incloud.hcp.config.util.AppUtil;
import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.domain.Parametro;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.domain.ProveedorCuentaBancaria;
import com.incloud.hcp.dto.ProveedorDto;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.proveedor.dto.ProveedorHomologacionRFCResponseDto;
import com.incloud.hcp.jco.proveedor.dto.ProveedorRFCResponseDto;
import com.incloud.hcp.jco.proveedor.dto.ProveedorResponseRFC;
import com.incloud.hcp.jco.proveedor.service.JCOProveedorService;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.BancoRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.ParametroRepository;
import com.incloud.hcp.repository.ProveedorCuentaBancoRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.service.notificacion.ProveedorHomologacionVencidaNotificacion;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.StrUtils;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.util.constant.WebServiceConstant;
//import com.sap.conn.jco.*;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;

import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONArray;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOProveedorServiceImpl implements JCOProveedorService {
    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Value("${API_URL_SAP}")
    private String urlSap;
    @Value("${USERNAME_SAP}")
    private String userSap;
    @Value("${PASSWORD_SAP}")
    private String passwordSap;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final static String ESCAPE_CHARS = "<>&\"\'";
    public final static List<String> ESCAPE_STRINGS = Collections.unmodifiableList(Arrays.asList(new String[] {
            "&lt;"
            , "&gt;"
            , "&amp;"
            , "&quot;"
            , "&apos;"
    }));

    private static String UNICODE_NULL = "" + ((char)0x00); //null
    private static String UNICODE_LOW =  "" + ((char)0x20); //space
    private static String UNICODE_HIGH = "" + ((char)0x7f);
    private final int NRO_EJECUCIONES_RFC = 10;
//    private final String FUNCION_RFC = "ZMM_CREA_PROVEEDOR";
    private final String FUNCION_RFC = "ZPE_MM_CREA_PROVEEDOR";
    private final String NOMBRE_TABLA_RFC = "OT_MENSAJES";
    //    private static final String I_SOCIEDAD = "SFER";
    private static final String I_SOCIEDAD = "1200";
    //    private static final String I_ORG_COMPRAS = "1000";
    private static final String I_ORG_COMPRAS = "1100"; // PE21 para servicios
    //    private static final String RAMO = "Y002";
    private static final String RAMO = "Y002";
    private static final String VALOR_DEFAULT="X";
    //    private static final String VIAS_PAGO_N="M"; // vias_pago nacional
//    private static final String VIAS_PAGO_N="T";
    private static final String VIAS_PAGO_N="1CEILQRTU";
    //    private static final String VIAS_PAGO_E="";// vias_pago extranjero
    private static final String VIAS_PAGO_E="1CEILQRTU";
    //    private static final String CCI="CC";
    private static final String CCI="CC";
    //    private static final String CASOCIADA_FACTURA="42120001"; // cuenta asociada factura
//    private static final String CASOCIADA_FACTURA="48820033 "; // 1910348820033 (13 DIGITOS), clave banco 002
    private static final String CASOCIADA_FACTURA="42120001";
    private static final String CASOCIADA_FACTURA_E="42120002";
    //    private static final String CASOCIADA_RH="42400001"; // cuenta asociada recibo por honorarios
//    private static final String CASOCIADA_RH="48820033"; // 1910348820033 (13 DIGITOS), clave banco 002
    private static final String CASOCIADA_RH="42120001";

    private ParametroMapper parametroMapper;

    private ProveedorHomologacionVencidaNotificacion proveedorHomologacionVencidaNotificacion;

    //should only be used for the content of an attribute or tag
    public static String toEscaped(String content) {
        String result = content;

        if ((content != null) && (content.length() > 0)) {
            boolean modified = false;
            StringBuilder stringBuilder = new StringBuilder(content.length());
            for (int i = 0, count = content.length(); i < count; ++i) {
                String character = content.substring(i, i + 1);
                int pos = ESCAPE_CHARS.indexOf(character);
                if (pos > -1) {
                    stringBuilder.append(ESCAPE_STRINGS.get(pos));
                    modified = true;
                }
                else {
                    if (    (character.compareTo(UNICODE_LOW) > -1)
                            && (character.compareTo(UNICODE_HIGH) < 1)
                    ) {
                        stringBuilder.append(character);
                    }
                    else {
                        //Per URL reference below, Unicode null character is always restricted from XML
                        //URL: https://en.wikipedia.org/wiki/Valid_characters_in_XML
                        if (character.compareTo(UNICODE_NULL) != 0) {
                            stringBuilder.append("&#" + ((int)character.charAt(0)) + ";");
                        }
                        modified = true;
                    }
                }
            }
            if (modified) {
                result = stringBuilder.toString();
            }
        }
        return result;
    }

    @Autowired
    public void setParametroMapper(ParametroMapper parametroMapper) {
        this.parametroMapper = parametroMapper;
    }

    @Autowired
    public void setProveedorHomologacionVencidaNotificacion(ProveedorHomologacionVencidaNotificacion proveedorHomologacionVencidaNotificacion) {
        this.proveedorHomologacionVencidaNotificacion = proveedorHomologacionVencidaNotificacion;
    }

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired
    private ProveedorCuentaBancoRepository proveedorCuentaBancoRepository;

    @Override
    public ProveedorRFCResponseDto grabarProveedor_old(Integer idProveedor,String usuarioSap) throws Exception {
        ProveedorRFCResponseDto proveedorRFCResponseDto = new ProveedorRFCResponseDto();
        LogTransaccion logTransaccion = new LogTransaccion();
        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");

        Proveedor proveedorParam =  this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        List<ProveedorCuentaBancaria> listaCuentaBancaria= this.
                proveedorCuentaBancoRepository.getListCuentaBancariaByIdProveedor(idProveedor);

//I

        String tramaXML = armarTrama( proveedorParam, listaCuentaBancaria,usuarioSap);
        logger.info("RFC: Trama" + tramaXML);

        logTransaccion.setEnvioTrama(tramaXML);
        logTransaccion.setTipoRegistro("CrearPrv");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());

        logger.error("parametro ingresado Proveedor" + proveedorParam.toString());
        logger.error("parametro ingresado Cuenta Bancaria" + listaCuentaBancaria.toString());


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
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_CREA_PROVEEDOR?sap-client=400&wsdl=1.1&mode=sap_wsdl";

        logger.info("RFC: URL " + url);


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

        logger.info(soap.toString());
        //HttpEntity strEntity = new StringEntity(soap.toString());
        //HttpEntity strEntity = new StringEntity(soap.toString(), ContentType.TEXT_XML);

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_CREA_PROVEEDOR/ZMM_CREA_PROVEEDORRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_CREA_PROVEEDOR/ZMM_CREA_PROVEEDORRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        logger.info("HTTP POS" + post.toString());

        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);

        logger.info("RESULT:" +result);
        logTransaccion.setTipoRegistro("CrearPrv");
        logTransaccion.setLogFecha(DateUtils.getCurrentTimestamp());
        logTransaccion.setRespuestaCodigo(result);


//F
//INICIO
/*
                String tramaXML = armarTrama( proveedorParam, listaCuentaBancaria,usuarioSap);

                 List<SapLog> listSapLog = new ArrayList<>();

                HttpParams httpParameters = new BasicHttpParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 15000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 35000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
                String encoding = Base64.getEncoder().encodeToString(("CMENDEZ"+":"+"Iprovider2022+").getBytes(StandardCharsets.UTF_8));

                HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_CREA_PROVEEDOR?sap-client=400");
                httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_CREA_PROVEEDOR/ZMM_CREA_PROVEEDORRequest");
                httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
                httppost.setHeader("Authorization", "Basic " + encoding);
                System.out.println("executing request" + httppost.getRequestLine());
                //now create a soap request message as follows:
                final StringBuffer soap = new StringBuffer();
                soap.append("\n");
                soap.append("");
                // this is a sample data..you have create your own required data  BEGIN
                soap.append(" \n");
                soap.append(" \n");
                soap.append("" + tramaXML);
                soap.append(" \n");
                soap.append(" \n");


                HttpEntity entity = new StringEntity(soap.toString(), HTTP.UTF_8);
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);// calling server
                HttpEntity r_entity = response.getEntity();  //get response

                String result = EntityUtils.toString(r_entity);

*/
//FIN

        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));

        logger.info("RFC: Resultado :" + result);
       /* SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigo);
        sapLog.setMesaj(message);*/


        String tipoerror = "";
        String claseerror = "";
        String numeroerror = "";
        String textoerror = "";

        NodeList mensajes = doc.getElementsByTagName("E_MENSAJES").item(0).getChildNodes();
        for(int i = 0; i < mensajes.getLength(); i++) {

            Node mensaje = mensajes.item(i);
            Element elemento = (Element) mensaje;

            tipoerror = Utils.getValueNodo(elemento, "TIPO");
            claseerror = Utils.getValueNodo(elemento, "CLASE");
            numeroerror = Utils.getValueNodo(elemento, "NUMERO");
            textoerror = Utils.getValueNodo(elemento, "TEXTO");
            if(tipoerror.equals("E"))
            {
                throw new PortalException(textoerror);
            }
        }

        String codigoAcrededor = doc.getElementsByTagName("E_COD_PROVEEDOR").item(0).getChildNodes().item(0).getNodeValue();

        if (Objects.equals(codigoAcrededor, "Error")) {
            throw new PortalException(doc.getElementsByTagName("TEXTO").item(0).getChildNodes().item(0).getNodeValue());
        }

        ProveedorDto proveedorDto = new ProveedorDto();

        proveedorParam.setAcreedorCodigoSap(codigoAcrededor);
        proveedorRFCResponseDto.setNroAcreedor(codigoAcrededor);
        proveedorRFCResponseDto.setProveedorSap(proveedorParam);
        proveedorRFCResponseDto.setListasapLog(listSapLog);
        proveedorRFCResponseDto.setProveedorDto(proveedorDto);
        return proveedorRFCResponseDto;
    }

    @Override
    public ProveedorRFCResponseDto actualizarProveedor_old(Integer idProveedor,String usuarioSap) throws Exception {
        ProveedorRFCResponseDto proveedorRFCResponseDto = new ProveedorRFCResponseDto();

        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");


        ///Obtener Bean Proveedor


        Proveedor proveedorParam =  this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        List<ProveedorCuentaBancaria> listaCuentaBancaria= this.
                proveedorCuentaBancoRepository.getListCuentaBancariaByIdProveedor(idProveedor);
// I

        String tramaXMLactualizar = armarTramaActualizar2( proveedorParam, listaCuentaBancaria,usuarioSap);
        logger.info("RFC: Trama" + tramaXMLactualizar);


        logger.error("parametro ingresado Proveedor" + tramaXMLactualizar);
        logger.error("parametro ingresado Proveedor" + proveedorParam.toString());
        logger.error("parametro ingresado Cuenta Bancaria" + listaCuentaBancaria.toString());

        /* Obteniendo los valores obtenidos del RFC */
        logger.error("02 - GET PROVEEDOR - FIN RFC");
//        JCoParameterList result = jCoFunction.getExportParameterList();

        List<SapLog> listSapLog = new ArrayList<>();


        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));


        logger.info(String.valueOf(client));
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
//        http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_ACTUALIZA_PROVEEDOR?sap-client=400&wsdl=1.1&mode=sap_wsdl
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_ACTUALIZA_PROVEEDOR?sap-client=400&wsdl=1.1&mode=sap_wsdl";

        logger.info("RFC: URL " + url);


        //String tramaXML = armarTrama2();

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
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_ACTUALIZA_PROVEEDOR/ZMM_ACTUALIZA_PROVEEDORRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_ACTUALIZA_PROVEEDOR/ZMM_ACTUALIZA_PROVEEDORRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        logger.info("HTTP POS" + post.toString());

        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);
        logger.info("RESULT:" +result);


//F
//INICIO

/*
        String tramaXML = armarTramaActualizar2( proveedorParam, listaCuentaBancaria,usuarioSap);

        List<SapLog> listSapLog = new ArrayList<>();

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 35000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        String encoding = Base64.getEncoder().encodeToString(("CMENDEZ"+":"+"Iprovider2022+").getBytes(StandardCharsets.UTF_8));

        HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_ACTUALIZA_PROVEEDOR?sap-client=400");
        httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_ACTUALIZA_PROVEEDOR/ZMM_ACTUALIZA_PROVEEDORRequest");
        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
        httppost.setHeader("Authorization", "Basic " + encoding);
        System.out.println("executing request" + httppost.getRequestLine());
        //now create a soap request message as follows:
        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaXML);
        soap.append(" \n");
        soap.append(" \n");


        HttpEntity entity = new StringEntity(soap.toString(), HTTP.UTF_8);
        httppost.setEntity(entity);
        HttpResponse response = httpclient.execute(httppost);// calling server
        HttpEntity r_entity = response.getEntity();  //get response

        String result = EntityUtils.toString(r_entity);
*/

//FIN
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));

        logger.info("RFC: Resultado :" + result);
       /* SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigo);
        sapLog.setMesaj(message);*/

        String tipoerror = "";
        String claseerror = "";
        String numeroerror = "";
        String textoerror = "";

        NodeList mensajes = doc.getElementsByTagName("E_MENSAJES").item(0).getChildNodes();
        for(int i = 0; i < mensajes.getLength(); i++) {

            Node mensaje = mensajes.item(i);
            Element elemento = (Element) mensaje;

            tipoerror = Utils.getValueNodo(elemento, "TIPO");
            claseerror = Utils.getValueNodo(elemento, "CLASE");
            numeroerror = Utils.getValueNodo(elemento, "NUMERO");
            textoerror = Utils.getValueNodo(elemento, "TEXTO");
            if(tipoerror.equals("E"))
            {
                throw new PortalException(textoerror);
            }
        }

        String codigoAcrededor = doc.getElementsByTagName("E_COD_PROVEEDOR").item(0).getChildNodes().item(0).getNodeValue();

        if (Objects.equals(codigoAcrededor, "Error")) {
            throw new PortalException(doc.getElementsByTagName("TEXTO").item(0).getChildNodes().item(0).getNodeValue());
        }

        ProveedorDto proveedorDto = new ProveedorDto();

        proveedorParam.setAcreedorCodigoSap(proveedorParam.getAcreedorCodigoSap());
        proveedorRFCResponseDto.setNroAcreedor(proveedorParam.getAcreedorCodigoSap());
        proveedorRFCResponseDto.setProveedorSap(proveedorParam);
        proveedorRFCResponseDto.setListasapLog(listSapLog);
        proveedorRFCResponseDto.setProveedorDto(proveedorDto);
        return proveedorRFCResponseDto;
    }


    @Override
    public ProveedorRFCResponseDto actualizarHomologacion(String codigoAcrededor, String fechaIni, String fechaFin) throws Exception {
        ProveedorRFCResponseDto proveedorRFCResponseDto = new ProveedorRFCResponseDto();

        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");

        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_ACTUALIZA_HOMOLOGACION>\n" +
                "         <I_FECHA_FIN>"+fechaFin+"</I_FECHA_FIN>\n" +
                "         <I_FECHA_INICIO>"+ fechaIni+"</I_FECHA_INICIO>\n" +
                "         <I_LIFNR>"+ codigoAcrededor +"</I_LIFNR>\n" +
                "      </urn:ZMM_ACTUALIZA_HOMOLOGACION>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        /* Obteniendo los valores obtenidos del RFC */
        logger.error("02 - GET PROVEEDOR - FIN RFC");
        List<SapLog> listSapLog = new ArrayList<>();


        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

        logger.info(String.valueOf(client));

        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_actualiza_homologacion/100/zws_actualiza_homologacion/zws_actualiza_homologacion";

        logger.info("RFC: URL " + url);

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

        logger.info(soap.toString());
        //HttpEntity strEntity = new StringEntity(soap.toString());
        //HttpEntity strEntity = new StringEntity(soap.toString(), ContentType.TEXT_XML);

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_ACTUALIZA_HOMOLOGACION:ZMM_ACTUALIZA_HOMOLOGACIONRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_ACTUALIZA_HOMOLOGACION:ZMM_ACTUALIZA_HOMOLOGACIONRequest");
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

        logger.info("RFC: Resultado :" + result);


        String codigo = doc.getElementsByTagName("O_CODE").item(0).getChildNodes().item(0).getNodeValue();

        if(!codigo.equals("0"))
        {
            throw new PortalException(doc.getElementsByTagName("O_MENSAJE").item(0).getChildNodes().item(0).getNodeValue());
        }



        ProveedorDto proveedorDto = new ProveedorDto();
        proveedorRFCResponseDto.setNroAcreedor(codigoAcrededor);
        proveedorRFCResponseDto.setListasapLog(listSapLog);
        proveedorRFCResponseDto.setProveedorDto(proveedorDto);
        return proveedorRFCResponseDto;
    }

    @Override
    public void notificacionHomologacion() throws Exception
    {
        /*Obtenemos todos los clientes*/
         List<Proveedor> proveedorList = proveedorRepository.findAll();

        for (Proveedor bean: proveedorList) {

            /*Prueba local*/
            if(bean.getAcreedorCodigoSap() == null)
            {
                continue;
            }

//            if(!bean.getAcreedorCodigoSap().equals("0001010351"))
//            {
//                continue;
//            }

            ProveedorHomologacionRFCResponseDto proveedorHomologacionRFCResponseDto = new ProveedorHomologacionRFCResponseDto();

            try
            {
                 proveedorHomologacionRFCResponseDto = this.obtenerFechaHomologacion(bean.getAcreedorCodigoSap());
            }
            catch (Exception ex)
            {
                logger.error("Error al obtener datos de homologacion " + bean.getRuc()  + " - " + ex.getMessage() );
            }


//            proveedorHomologacionRFCResponseDto.setFechaIniHomInterna(DateUtils.sumarRestarDias(DateUtils.obtenerFechaHoraActual(), -10));
//            proveedorHomologacionRFCResponseDto.setFechaFinHomInterna(DateUtils.sumarRestarDias(DateUtils.obtenerFechaHoraActual(), -1));
//            proveedorHomologacionRFCResponseDto.setFechaIniHomExterna(DateUtils.sumarRestarDias(DateUtils.obtenerFechaHoraActual(), -10));
//            proveedorHomologacionRFCResponseDto.setFechaFinHomExterna(DateUtils.sumarRestarDias(DateUtils.obtenerFechaHoraActual(), 10));


            logger.info("datos obtenidos - " +  proveedorHomologacionRFCResponseDto.toString());

            if(proveedorHomologacionRFCResponseDto.getFechaIniHomInterna() != null)
            {
                logger.info("Entro uno ");
                Integer dias = DateUtils.daysDiff(DateUtils.obtenerFechaHoraActual(), proveedorHomologacionRFCResponseDto.getFechaFinHomInterna());

                if(dias < 60)
                {
                    /*Enviar correo*/
                    logger.info("Entro 1.5");
                     this.proveedorHomologacionVencidaNotificacion.enviar(this.parametroMapper.getMailSetting(), bean);
                }
            }

            if(proveedorHomologacionRFCResponseDto.getFechaIniHomExterna() != null)
            {
                logger.info("Entro doss ");
                Integer dias = DateUtils.daysDiff(DateUtils.obtenerFechaHoraActual(), proveedorHomologacionRFCResponseDto.getFechaFinHomExterna());

                if(dias < 60)
                {
                    /*Enviar correo*/
                    logger.info("Entro 2.5");
                    String respuesta = "";
                    respuesta =  this.proveedorHomologacionVencidaNotificacion.enviar(this.parametroMapper.getMailSetting(), bean);
                    LogTransaccion logTransaccion = new LogTransaccion();
                    logTransaccion.setEnvioTrama("proveedorHomologacionVencidaNotificacion");
                    logTransaccion.setRespuestaCodigo(respuesta);
                    logTransaccion.setTipoRegistro("Correo proveedorHomologacionVencidaNotificacion");
                    this.logTransaccionRepository.save(logTransaccion);

                }
            }

        }

    }

    public String homologarProveedor(Integer idProveedor,String usuarioSap) throws Exception {
        ProveedorRFCResponseDto proveedorRFCResponseDto = new ProveedorRFCResponseDto();

        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");


        ///Obtener Bean Proveedor


        Proveedor proveedorParam =  this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        List<ProveedorCuentaBancaria> listaCuentaBancaria= this.
                proveedorCuentaBancoRepository.getListCuentaBancariaByIdProveedor(idProveedor);



        logger.error("parametro ingresado Proveedor" + proveedorParam.toString());
        logger.error("parametro ingresado Cuenta Bancaria" + listaCuentaBancaria.toString());

        /* Obteniendo los valores obtenidos del RFC */
        logger.error("02 - GET PROVEEDOR - FIN RFC");
//        JCoParameterList result = jCoFunction.getExportParameterList();

        List<SapLog> listSapLog = new ArrayList<>();


        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));


        logger.info(String.valueOf(client));

        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_homologacion/100/zws_homologacion/zws_homologacion";

        logger.info("RFC: URL " + url);

        String tramaXMLhomologar = armarTramaHomologar( proveedorParam,usuarioSap);
        //String tramaXML = armarTrama2();

        logger.info("RFC: Trama" + tramaXMLhomologar);

        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaXMLhomologar);
        soap.append(" \n");
        soap.append(" \n");

        /* soap.append(body); */
        // END of MEssage Body
        soap.append("");

        HttpEntity strEntity = new StringEntity(tramaXMLhomologar, "text/xml", "UTF-8");

        logger.info(soap.toString());

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_HOMOLOGACION:ZMM_HOMOLOGACION_1Request");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_HOMOLOGACION:ZMM_HOMOLOGACION_1Request");
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

        logger.info("RFC: Resultado :" + result);
       /* SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigo);
        sapLog.setMesaj(message);*/
        String respuesta ="";
        String homologacionProveedor = "";
        if(result.equals(null) ){
             respuesta ="";
        }else{
            NodeList nodes  = doc.getElementsByTagName("ZZSTAT_HOMO_EXT").item(0).getChildNodes();
            Node node = (Node) nodes.item(0);
            if(node != null){
                respuesta = node.getNodeValue();
            }else{
                respuesta = "";
            }
        }


        /*ProveedorDto proveedorDto = new ProveedorDto();

        proveedorParam.setAcreedorCodigoSap(proveedorParam.getAcreedorCodigoSap());
        proveedorRFCResponseDto.setNroAcreedor(proveedorParam.getAcreedorCodigoSap());
        proveedorRFCResponseDto.setProveedorSap(proveedorParam);
        proveedorRFCResponseDto.setListasapLog(listSapLog);
        proveedorRFCResponseDto.setProveedorDto(proveedorDto);*/
        return respuesta;
    }

    @Override
    public ProveedorHomologacionRFCResponseDto obtenerFechaHomologacion(String usuarioSap) throws Exception {
        ProveedorHomologacionRFCResponseDto proveedorHomologacionRFCResponseDto = new ProveedorHomologacionRFCResponseDto();

        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");

        /* Obteniendo los valores obtenidos del RFC */
        logger.error("02 - GET PROVEEDOR - FIN RFC");
//        JCoParameterList result = jCoFunction.getExportParameterList();

        List<SapLog> listSapLog = new ArrayList<>();

        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));


        logger.info(String.valueOf(client));

        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_homologacion/100/zws_homologacion/zws_homologacion";

        logger.info("RFC: URL " + url);

        String tramaXMLhomologar = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_HOMOLOGACION_1>\n" +
                "         <!--Optional:-->\n" +
                "         <IT_HOMOLOG_DATA>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <LIFNR></LIFNR>\n" +
                "               <LAND1></LAND1>\n" +
                "               <NAME1></NAME1>\n" +
                "               <NAME2></NAME2>\n" +
                "               <NAME3></NAME3>\n" +
                "               <NAME4></NAME4>\n" +
                "               <KTOKK></KTOKK>\n" +
                "               <LOEVM></LOEVM>\n" +
                "               <SPERR></SPERR>\n" +
                "               <SPERM></SPERM>\n" +
                "               <STCD1></STCD1>\n" +
                "               <SPERQ></SPERQ>\n" +
                "               <ZZSTAT_HOMO_INT></ZZSTAT_HOMO_INT>\n" +
                "               <ZZTIPO_HOMO_INT></ZZTIPO_HOMO_INT>\n" +
                "               <ZZFECHA_INI_INT></ZZFECHA_INI_INT>\n" +
                "               <ZZFECHA_FIN_INT></ZZFECHA_FIN_INT>\n" +
                "               <ZZSTAT_HOMO_EXT></ZZSTAT_HOMO_EXT>\n" +
                "               <ZZTIPO_HOMO_EXT></ZZTIPO_HOMO_EXT>\n" +
                "               <ZZFECHA_INI_EXT></ZZFECHA_INI_EXT>\n" +
                "               <ZZFECHA_FIN_EXT></ZZFECHA_FIN_EXT>\n" +
                "            </item>\n" +
                "         </IT_HOMOLOG_DATA>\n" +
                "         <PI_LIFNR>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <LIFNR>"+ usuarioSap +"</LIFNR>\n" +
                "            </item>\n" +
                "         </PI_LIFNR>\n" +
                "      </urn:ZMM_HOMOLOGACION_1>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        //String tramaXML = armarTrama2();

        logger.info("RFC: Trama" + tramaXMLhomologar);

        final StringBuffer soap = new StringBuffer();
        soap.append("\n");
        soap.append("");
        // this is a sample data..you have create your own required data  BEGIN
        soap.append(" \n");
        soap.append(" \n");
        soap.append("" + tramaXMLhomologar);
        soap.append(" \n");
        soap.append(" \n");

        soap.append("");

        HttpEntity strEntity = new StringEntity(tramaXMLhomologar, "text/xml", "UTF-8");

        logger.info(soap.toString());

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_HOMOLOGACION:ZMM_HOMOLOGACION_1Request");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_HOMOLOGACION:ZMM_HOMOLOGACION_1Request");
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

        logger.info("RFC: Resultado :" + result);
        SapLog sapLog = new SapLog();
        String codigo = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();


        sapLog.setCode(codigo);
        sapLog.setMesaj(message);

        proveedorHomologacionRFCResponseDto.setSapLog(sapLog);

        NodeList nodes  = doc.getElementsByTagName("IT_HOMOLOG_DATA").item(0).getChildNodes();
        logger.info("JCO 1 :" + result);
        for(int i = 0; i < nodes.getLength(); i++) {

            logger.info("JCO 2 :" + result);
            Node posicion = nodes.item(i);
            Element elemento = (Element) posicion;

            String fechaIniHomInterna = Utils.getValueNodo(elemento, "ZZFECHA_INI_INT");
            String fechaFinHomInterna = Utils.getValueNodo(elemento, "ZZFECHA_FIN_INT");
            String fechaIniHomExterna = Utils.getValueNodo(elemento, "ZZFECHA_INI_EXT");
            String fechaFinHomExterna = Utils.getValueNodo(elemento, "ZZFECHA_FIN_EXT");

            if(!fechaIniHomInterna.equals("0000-00-00"))
            {
                logger.info("JCO 3 :" + result);
                proveedorHomologacionRFCResponseDto.setFechaIniHomInterna(DateUtils.convertStringToDate("yyyy-MM-dd",fechaIniHomInterna));
            }

            if(!fechaFinHomInterna.equals("0000-00-00"))
            {
                logger.info("JCO 4 :" + result);
                proveedorHomologacionRFCResponseDto.setFechaFinHomInterna(DateUtils.convertStringToDate("yyyy-MM-dd",fechaFinHomInterna));
            }

            if(!fechaIniHomExterna.equals("0000-00-00"))
            {
                logger.info("JCO 5 :" + result);
                proveedorHomologacionRFCResponseDto.setFechaIniHomExterna(DateUtils.convertStringToDate("yyyy-MM-dd",fechaIniHomExterna));
            }

            if(!fechaFinHomExterna.equals("0000-00-00"))
            {
                logger.info("JCO 6 :" + result);
                proveedorHomologacionRFCResponseDto.setFechaFinHomExterna(DateUtils.convertStringToDate("yyyy-MM-dd",fechaFinHomExterna));
            }

        }
        logger.info("JCO 7 :" + proveedorHomologacionRFCResponseDto.toString());
        return proveedorHomologacionRFCResponseDto;
    }

    @Autowired
    private ParametroRepository parametroRepository;

    private String armarTrama(Proveedor beanProveedor, List<ProveedorCuentaBancaria> listaCuentaBancaria,String usuarioSap)
    {

        String NOMB_CONTACTO = "";
        String NOMB_PILA = "";
        String TELEFONO_CONTACTO = "";
        String TELEFONO_CELULAR = "";
        String MAIL_CONTACTO = "";
        String MAIL_CONTACTO_P0 = "";
        String MAIL_CONTACTO_P1 = "";
        String MAIL_CONTACTO_P2 = "";
        String  DNI_CONTACTO = "";
        String DEP_CONTACTO = "";
        String FUNC_CONTACTO = "";

        String NOMB_PILA2 = "";
        String NOMB_CONTACTO2 = "";
        String TELEFONO_CONTACTO2 = "";
        String TELEFONO_CELULAR2 = "";
        String MAIL_CONTACTO2 = "";
        String MAIL_CONTACTO2_P0 = "";
        String MAIL_CONTACTO2_P1 = "";
        String MAIL_CONTACTO2_P2 = "";
        String DNI_CONTACTO2 = "";
        String DEP_CONTACTO2 =  "";
        String FUNC_CONTACTO2 =  "";

        String NOMB_PILA3 = "";
        String NOMB_CONTACTO3 = "";
        String TELEFONO_CONTACTO3 = "";
        String TELEFONO_CELULAR3 = "";
        String MAIL_CONTACTO3 = "";
        String MAIL_CONTACTO3_P0 = "";
        String MAIL_CONTACTO3_P1 = "";
        String MAIL_CONTACTO3_P2 = "";
        String DNI_CONTACTO3 = "";
        String DEP_CONTACTO3 =  "";
        String FUNC_CONTACTO3 =  "";

        String NOMB_PILA4 = "";
        String NOMB_CONTACTO4 = "";
        String TELEFONO_CONTACTO4 = "";
        String TELEFONO_CELULAR4 = "";
        String MAIL_CONTACTO4 = "";
        String MAIL_CONTACTO4_P0 = "";
        String MAIL_CONTACTO4_P1 = "";
        String MAIL_CONTACTO4_P2 = "";
        String DNI_CONTACTO4 = "";
        String DEP_CONTACTO4 =  "";
        String FUNC_CONTACTO4 =  "";

        String DIRECCION = "";
        String DIRECCION2 = "";
        String DIRECCION3 = "";
        String DIRECCION4 = "";
        String TELEFONO ="";
        String EMAIL ="";
        String NUM_IDENT_FISC ="";
        String NUM_IDENT_FISCD="";
        String NIF3 = "";
        String TIPO_NIF ="";
        String FUNC_INTER ="";
        String CLASE_IMPUESTO ="";
        String RECARGO_EQUIV ="";
        String PERSONA_FISICA ="";
        String RAMO ="010";
        String COD_INTER = "";
        String TRATAMIENTO = "EMPRESA";
        String NOMBRE3 = "";
        String NOMBRE4 = "";
//--------------IT_RETENCIONES-----------
        String TIPO_RETEN = "";
        String INDIC_RETEN = "";
        String WT_SUBJCT =  "";
        String PADRON = "";
        String NUM_DNI = "";

        String COD_POSTAL = "";
        String I_SOCIEDAD1 = "1100";
        String I_GRUPO_CTAS = "";


        String pais = Optional.ofNullable(beanProveedor.getPais())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();
        String region = Optional.ofNullable(beanProveedor.getRegion())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();
        logger.error("REGION"+region.toString());

        String provincia = "";

        if(beanProveedor.getProvincia() != null)
        {
            provincia=beanProveedor.getProvincia().getDescripcion();
        }else{
            provincia = beanProveedor.getPais().getDescripcion();
        }

        //Optional.ofNullable(beanProveedor.getProvincia())
        //.map(tp -> Optional.ofNullable(tp.getDescripcion()).orElse("")).get();
        //.map(c -> c.trim()).get();
        String distrito = "";
        if(beanProveedor.getDistrito() != null)
        {
            distrito=beanProveedor.getDistrito().getDescripcion();
        }
        //Optional.ofNullable(beanProveedor.getDistrito())
        //.map(tp -> Optional.ofNullable(tp.getDescripcion()).orElse("")).get();
        //.map(c -> c.trim()).get();


        String razonSocial = beanProveedor.getRazonSocial().trim().toUpperCase();
        razonSocial = AppUtil.reemplazarCaracteresEspeciales(razonSocial);
        String NOMBRE = "";
        String NOMBRE2 = "";

        if (razonSocial.length() <= 35) {
            NOMBRE = razonSocial.toUpperCase();
        }
        else {

            String[] palabras = razonSocial.toUpperCase().split(" ");
            Integer cant = 0;
            Integer cant2 = 0;
            Integer cant3 = 0;
            for (String palabra:palabras) {

                if(cant + palabra.length() + 1 <= 35)
                {
                    cant += palabra.length() + 1;
                    NOMBRE = NOMBRE + palabra + " ";
                    continue;
                }

                if(cant + palabra.length() + 1 > 35 && cant + palabra.length() + 1 <= 70 && cant2 + palabra.length() + 1 <= 35)
                {
                    cant += palabra.length() + 1;
                    cant2 += palabra.length() + 1;
                    NOMBRE2 = NOMBRE2 + palabra + " ";
                    continue;
                }

                if(cant + palabra.length() + 1 > 35 && cant + palabra.length() + 1 <= 70 && cant3 + palabra.length() + 1 <= 35)
                {
                    cant += palabra.length() + 1;
                    cant3+= palabra.length() + 1;
                    NOMBRE3 = NOMBRE3 + palabra + " ";
                    continue;
                }
            }


//            if (razonSocial.length() > 35 && razonSocial.length() <= 70) {
//                String razonSocial01 = razonSocial.substring(0, 35);
//                String razonSocial02 = razonSocial.substring(36, razonSocial.length());
//                NOMBRE = razonSocial01.toUpperCase();
//                NOMBRE2 = razonSocial02.toUpperCase();
//
//           } else {
//                String razonSocial01 = razonSocial.substring(0, 35);
//                String razonSocial02 = razonSocial.substring(36, 70);
//                NOMBRE = razonSocial01.toUpperCase();
//                NOMBRE2 = razonSocial02.toUpperCase();
//            }
        }


        String datosPersonaNatural=separarCadenas(razonSocial,"/");
        String nombres=datosPersonaNatural.split(",")[0];
        String apellidos=datosPersonaNatural.split(",")[1];

        Boolean indAgenteRetencion = beanProveedor.getIndAgenteRetencion() != null && beanProveedor.getIndAgenteRetencion().trim().equals("1");

        String direccion = "";
        String direccion1= "";
        String direccion2= "";
        String direccion3= "";
        String direccion4= "";
        if (Optional.ofNullable(beanProveedor.getDireccionFiscal()).isPresent()) {
            direccion = beanProveedor.getDireccionFiscal().toUpperCase();
            direccion = AppUtil.reemplazarCaracteresEspeciales(direccion);
            int longitud = direccion.length();
            if(longitud<=60){
                direccion1 =direccion.substring(0,direccion.length());
            }
            else if (longitud > 60 && longitud <= 120) {
                logger.error("DIRECCION 2");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,direccion.length());
            }else if(longitud > 120 && longitud <= 180){
                logger.error("DIRECCION 3");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,120);
                direccion3=direccion.substring(120,direccion.length());
            }else{
                logger.error("DIRECCION 4");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,120);
                direccion3=direccion.substring(120,180);
                direccion4=direccion.substring(180,direccion.length());
            }
        }

        logger.error("DIRECION: " + direccion);
        logger.error("DIRECION: " + direccion2);
        String grupoEsquemaProveedor="";
        String viasPago="";
        String tipoNif="";
        String claseImpuesto="01";
        String ctaAsociada="";


        String tipoProveedor=beanProveedor.getTipoProveedor().getCodigoSap().toUpperCase();
        if(tipoProveedor.equalsIgnoreCase("ZNAC")) {
            viasPago=VIAS_PAGO_N;
//            grupoEsquemaProveedor="Z1";
            grupoEsquemaProveedor="01";
//            tipoNif="P6";
            tipoNif="92";
            claseImpuesto=Optional.ofNullable(beanProveedor.getTipoPersona().toUpperCase())
                    .map(codigo -> {
                        switch (beanProveedor.getTipoPersona()) {
                            case "J":
                                return "PJ";
                            case "N":
                                return "PN";
                            default:
                                return "";
                        }
                    }).orElse("");

            if(claseImpuesto=="PJ"){
                if (beanProveedor.getTipoProveedor().equals("Extranjero"))  {
                    ctaAsociada=CASOCIADA_FACTURA_E;
                } else {
                    ctaAsociada=CASOCIADA_FACTURA;

                }

                TRATAMIENTO = "EMPRESA";
            }else{
                NUM_DNI = beanProveedor.getRuc().substring(2,10);
                TRATAMIENTO = "SENOR";
                NOMBRE3 = apellidos;
                NOMBRE4 = nombres;
                if(beanProveedor.getTipoComprobante().getCodigoTipoComprobante().equalsIgnoreCase("FA")){
                    ctaAsociada=CASOCIADA_FACTURA;
                }else{
                    ctaAsociada=CASOCIADA_RH;
                }

            }

            /*String datosContacto1=separarCadenas(beanProveedor.getNombreRepresentanteLegal()," ");
            String nombresContacto1=datosContacto1.split(",")[0];
            String apellidosContacto1=datosContacto1.split(",")[1];

            String datosContacto2=separarCadenas(beanProveedor.getNombrePersonaCreditoCobranza()," ");
            String nombresContacto2=datosContacto2.split(",")[0];
            String apellidosContacto2=datosContacto2.split(",")[1];*/

            NOMB_CONTACTO = beanProveedor.getNombreRepresentanteLegal();
            NOMB_PILA = beanProveedor.getNombreRepresentanteLegal();
            TELEFONO_CONTACTO = "";
            TELEFONO_CELULAR = beanProveedor.getCelularRepresentanteLegal().trim();
            MAIL_CONTACTO = beanProveedor.getEmailRepresentanteLegal().trim();
            MAIL_CONTACTO_P0 = beanProveedor.getEmailRepresentanteLegal2().trim();
            MAIL_CONTACTO_P1 = beanProveedor.getEmailRepresentanteLegal3().trim();
            MAIL_CONTACTO_P2 = beanProveedor.getEmailRepresentanteLegal4().trim();
            DNI_CONTACTO = beanProveedor.getNroDocumRepresentanteLegal().trim();
            DEP_CONTACTO = "0004";
            FUNC_CONTACTO = "04";

            NOMB_PILA2 = beanProveedor.getNombrePersonaCreditoCobranza();
            NOMB_CONTACTO2 = beanProveedor.getNombrePersonaCreditoCobranza();
            TELEFONO_CONTACTO2 = "";
            TELEFONO_CELULAR2 = beanProveedor.getCelularPersonaCreditoCobranza().trim();
            MAIL_CONTACTO2 = beanProveedor.getEmailPersonaCreditoCobranza().trim();
            MAIL_CONTACTO2_P0 = beanProveedor.getEmailPersonaCreditoCobranza2().trim();
            MAIL_CONTACTO2_P1 = beanProveedor.getEmailPersonaCreditoCobranza3().trim();
            MAIL_CONTACTO2_P2 = beanProveedor.getEmailPersonaCreditoCobranza4().trim();
            DNI_CONTACTO2 = beanProveedor.getNroDocumPersonaCreditoCobranza().trim();
            DEP_CONTACTO2 =  "0009";
            FUNC_CONTACTO2 =  "09";

            NOMB_PILA3 = beanProveedor.getNombrePersonaTesoreria();
            NOMB_CONTACTO3 = beanProveedor.getNombrePersonaTesoreria();
            TELEFONO_CONTACTO3 = "";
            TELEFONO_CELULAR3 = beanProveedor.getCelularTesoreria().trim();
            MAIL_CONTACTO3 = beanProveedor.getEmailPersonaTesoreria().trim();
            MAIL_CONTACTO3_P0 = beanProveedor.getEmailPersonaTesoreria2().trim();
            MAIL_CONTACTO3_P1 = beanProveedor.getEmailPersonaTesoreria3().trim();
            MAIL_CONTACTO3_P2 = beanProveedor.getEmailPersonaTesoreria4().trim();
            DNI_CONTACTO3 = beanProveedor.getNroDocumPersonaTesoreria().trim();
            DEP_CONTACTO3 =  "0009";
            FUNC_CONTACTO3 =  "09";

            NOMB_PILA4 = beanProveedor.getNombrePersonaCompra();
            NOMB_CONTACTO4 = beanProveedor.getNombrePersonaCompra();
            TELEFONO_CONTACTO4 = "";
            TELEFONO_CELULAR4 = beanProveedor.getCelularPersonaCompra().trim();
            MAIL_CONTACTO4 = beanProveedor.getEmailPersonaCompra().trim();
            MAIL_CONTACTO4_P0 = beanProveedor.getEmailPersonaCompra2().trim();
            MAIL_CONTACTO4_P1 = beanProveedor.getEmailPersonaCompra3().trim();
            MAIL_CONTACTO4_P2 = beanProveedor.getEmailPersonaCompra4().trim();
            DNI_CONTACTO4 = beanProveedor.getNroDocumPersonaCompra().trim();
            DEP_CONTACTO4 =  "0009";
            FUNC_CONTACTO4 =  "09";
        }
        else {
//            grupoEsquemaProveedor="Z4";
            grupoEsquemaProveedor="01";
            viasPago=VIAS_PAGO_E;
//            tipoNif="P0";
            Integer tipoProveedorNacExt=beanProveedor.getTipoProveedor().getIdTipoProveedor();
            if (tipoProveedorNacExt==2) {
                tipoNif = "0";
            } else {
                tipoNif = "97";
            }

            claseImpuesto="01";
            if(beanProveedor.getTipoComprobante().getCodigoTipoComprobante().equalsIgnoreCase("FA")){
                ctaAsociada=CASOCIADA_FACTURA;
            }else{
                ctaAsociada=CASOCIADA_RH;
            }

            NOMB_CONTACTO = beanProveedor.getNombreRepresentanteLegal();
            NOMB_PILA = beanProveedor.getNombreRepresentanteLegal();
            TELEFONO_CONTACTO = "";
            MAIL_CONTACTO = beanProveedor.getEmailRepresentanteLegal().trim();
            MAIL_CONTACTO_P0 = beanProveedor.getEmailRepresentanteLegal2() == null ? "":beanProveedor.getEmailRepresentanteLegal2().trim();
            MAIL_CONTACTO_P1 = beanProveedor.getEmailRepresentanteLegal3() == null ? "":beanProveedor.getEmailRepresentanteLegal3().trim();
            MAIL_CONTACTO_P2 = beanProveedor.getEmailRepresentanteLegal4() == null ? "":beanProveedor.getEmailRepresentanteLegal4().trim();
            DNI_CONTACTO = beanProveedor.getNroDocumRepresentanteLegal().trim();
            DEP_CONTACTO = "0004";
            FUNC_CONTACTO = "04";

            NOMB_PILA2 = beanProveedor.getNombrePersonaCreditoCobranza();
            NOMB_CONTACTO2 = beanProveedor.getNombrePersonaCreditoCobranza();
            TELEFONO_CONTACTO2 = "";
            MAIL_CONTACTO2 = beanProveedor.getEmailPersonaCreditoCobranza().trim();
            MAIL_CONTACTO2_P0 = beanProveedor.getEmailPersonaCreditoCobranza2() == null ? "": beanProveedor.getEmailPersonaCreditoCobranza2().trim();
            MAIL_CONTACTO2_P1 = beanProveedor.getEmailPersonaCreditoCobranza3() == null ? "": beanProveedor.getEmailPersonaCreditoCobranza3().trim();
            MAIL_CONTACTO2_P2 = beanProveedor.getEmailPersonaCreditoCobranza4() == null ? "": beanProveedor.getEmailPersonaCreditoCobranza4().trim();
            DNI_CONTACTO2 = beanProveedor.getNroDocumPersonaCreditoCobranza().trim();
            DEP_CONTACTO2 =  "0009";
            FUNC_CONTACTO2 =  "09";

            NOMB_PILA3 = beanProveedor.getNombrePersonaTesoreria();
            NOMB_CONTACTO3 = beanProveedor.getNombrePersonaTesoreria();
            TELEFONO_CONTACTO3 = "";
            MAIL_CONTACTO3 = beanProveedor.getEmailPersonaTesoreria().trim();
            MAIL_CONTACTO3_P0 = beanProveedor.getEmailPersonaTesoreria2() == null ? "": beanProveedor.getEmailPersonaTesoreria2().trim();
            MAIL_CONTACTO3_P1 = beanProveedor.getEmailPersonaTesoreria3() == null ? "": beanProveedor.getEmailPersonaTesoreria3().trim();
            MAIL_CONTACTO3_P2 = beanProveedor.getEmailPersonaTesoreria4() == null ? "": beanProveedor.getEmailPersonaTesoreria4().trim();
            DNI_CONTACTO3 = beanProveedor.getNroDocumPersonaTesoreria().trim();
            DEP_CONTACTO3 =  "0009";
            FUNC_CONTACTO3 =  "09";

            NOMB_PILA4 = beanProveedor.getNombrePersonaCompra();
            NOMB_CONTACTO4 = beanProveedor.getNombrePersonaCompra();
            TELEFONO_CONTACTO4 = "";
            MAIL_CONTACTO4 = beanProveedor.getEmailPersonaCompra().trim();
            MAIL_CONTACTO4_P0 = beanProveedor.getEmailPersonaCompra2() == null ? "": beanProveedor.getEmailPersonaCompra2().trim();
            MAIL_CONTACTO4_P1 = beanProveedor.getEmailPersonaCompra3() == null ? "": beanProveedor.getEmailPersonaCompra3().trim();
            MAIL_CONTACTO4_P2 = beanProveedor.getEmailPersonaCompra4() == null ? "": beanProveedor.getEmailPersonaCompra4().trim();
            DNI_CONTACTO4 = beanProveedor.getNroDocumPersonaCompra() !=null ? beanProveedor.getNroDocumPersonaCompra().trim(): "";
            DEP_CONTACTO4 =  "0009";
            FUNC_CONTACTO4 =  "09";
        }


        DIRECCION = direccion1.toUpperCase();
        DIRECCION2 = direccion2.toUpperCase();
        DIRECCION3 = direccion3.toUpperCase();
        DIRECCION4 = direccion4.toUpperCase();
        TELEFONO =  beanProveedor.getTelefono().toUpperCase();
        EMAIL = beanProveedor.getEmail().trim().toUpperCase();
        NUM_IDENT_FISC =  beanProveedor.getRuc().toUpperCase();
        Integer longitudNIF = NUM_IDENT_FISC.length();
        if (longitudNIF > 16) {
            NUM_IDENT_FISCD = NUM_IDENT_FISC.substring(0,16);
            NIF3 = NUM_IDENT_FISC.substring(16, longitudNIF); // +1
            NUM_IDENT_FISC = NUM_IDENT_FISCD;
        }
        TIPO_NIF =  tipoNif;


        ///Datos de Intelocutor CodigoSap Usuario 1100000045
        FUNC_INTER = "";
        COD_INTER = usuarioSap.trim();
        //////fin//////
        CLASE_IMPUESTO = claseImpuesto;
        RECARGO_EQUIV =  "";
        PERSONA_FISICA =  "";
        RAMO = "";
        if(tipoProveedor.equalsIgnoreCase("ZNAC")) {

            if(claseImpuesto=="PJ"){
                TIPO_RETEN = "RE";
                INDIC_RETEN =  "R1";

                if(!indAgenteRetencion){
                    WT_SUBJCT =  VALOR_DEFAULT;
                    logger.error("PADRON" + beanProveedor.getCodigoPadron());
                }else{
                    WT_SUBJCT = "";
                }
                TIPO_RETEN =  "DE";
                INDIC_RETEN = "D1";
                WT_SUBJCT = VALOR_DEFAULT;
            }else{

                /*Denisse Indico que cuando se el proveedor es nacional debe de nacer con RE y R1*/

                TIPO_RETEN =  "RE";
                INDIC_RETEN = "R1";
                if(!indAgenteRetencion){
                    WT_SUBJCT = VALOR_DEFAULT;
                    logger.error("PADRON" + beanProveedor.getCodigoPadron());
                }else{
                    WT_SUBJCT = "";
                }

            }
        }else{

            TIPO_RETEN = "DE";
            INDIC_RETEN = "37";
            WT_SUBJCT = VALOR_DEFAULT;
        }

        String CONCEP_BUSQUED = Optional.ofNullable(beanProveedor.getRuc()).orElse("");
        RECARGO_EQUIV = "X";
        String GRUPO_TESOR = "K-LOC-TERC";
        Parametro indDetraccion;

        if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
            indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));
        }
        String itemretencion ="";
        boolean emiteRecibo = beanProveedor.getIndEmiteRecibo() == null ? false : beanProveedor.getIndEmiteRecibo();
        if(emiteRecibo){
            TIPO_NIF= "0";
            CLASE_IMPUESTO= "";
            RECARGO_EQUIV = "";
            ctaAsociada = "42120001";
            viasPago = "CIT";
            TIPO_RETEN= "Q3";
            INDIC_RETEN = "A1";
            String WT_SUBJCTx = "X";
            itemretencion = "    <item>\n" +
                    "              <TIPO_RETEN>"+Optional.ofNullable(TIPO_RETEN).orElse("")+"</TIPO_RETEN>\n" +
                    "               <INDIC_RETEN>"+Optional.ofNullable(INDIC_RETEN).orElse("")+"</INDIC_RETEN>\n" +
                    "               <WT_SUBJCT>"+Optional.ofNullable(WT_SUBJCTx).orElse("")+"</WT_SUBJCT>\n" +
                    "            </item>\n";


        }else {
            if(beanProveedor.getOperacionesAfectas().equals("04")){ //no gravado
                if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero"))
                {
                    TIPO_NIF= "0";
                    ctaAsociada = CASOCIADA_FACTURA_E;
                }
                itemretencion ="<item>\n" +
                        "            \t<TIPO_RETEN></TIPO_RETEN>\n" +
                        "            \t<INDIC_RETEN></INDIC_RETEN>\n" +
                        "            \t<WT_SUBJCT>X</WT_SUBJCT>\n" +
                        "     \t</item>";
            }else{

                if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
                    indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));
                    TIPO_RETEN = indDetraccion.getValor();
                    INDIC_RETEN= indDetraccion.getCodigo();
                }
                if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero"))
                {
                    TIPO_NIF= "0";
                    CLASE_IMPUESTO= "";
                    RECARGO_EQUIV = "";
                    ctaAsociada = CASOCIADA_FACTURA_E;

                    viasPago = "1CEILQRTU";


                    if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
                        indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));
                            if(indDetraccion.getValor().equals("RC")){
                                itemretencion =  "            <item>\n" +
                                        "              <TIPO_RETEN>" + "RC" + "</TIPO_RETEN>\n" +
                                        "               <INDIC_RETEN>" + Optional.ofNullable(INDIC_RETEN).orElse("") +"</INDIC_RETEN>\n" +
                                        "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                        "            </item>\n";
                            }else{
                                itemretencion = "<item>\n" +
                                        "              <TIPO_RETEN>" + Optional.ofNullable(TIPO_RETEN).orElse("") + "</TIPO_RETEN>\n" +
                                        "               <INDIC_RETEN>" + Optional.ofNullable(INDIC_RETEN).orElse("") + "</INDIC_RETEN>\n" +
                                        "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                        "       </item>\n" ;    //
                            }

                    }else{
                        itemretencion ="            <item>\n" +
                                "              <TIPO_RETEN></TIPO_RETEN>\n" +
                                "               <INDIC_RETEN></INDIC_RETEN>\n" +
                                "               <WT_SUBJCT></WT_SUBJCT>\n" +
                                "            </item>\n";
                    }
                }
                else
                {
                    I_SOCIEDAD1 ="1100";
                    if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
                        indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));

                        TIPO_RETEN = indDetraccion.getValor();
                        INDIC_RETEN= indDetraccion.getCodigo();

                        if(indDetraccion.getValor().equals("RC")){
                            itemretencion =  "            <item>\n" +
                                    "              <TIPO_RETEN>" + "RC" + "</TIPO_RETEN>\n" +
                                    "               <INDIC_RETEN>" + Optional.ofNullable(INDIC_RETEN).orElse("") + "</INDIC_RETEN>\n" +
                                    "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                    "            </item>\n";
                        }else{
                            itemretencion = "<item>\n" +
                                    "              <TIPO_RETEN>" + Optional.ofNullable(TIPO_RETEN).orElse("") + "</TIPO_RETEN>\n" +
                                    "               <INDIC_RETEN>" + Optional.ofNullable(INDIC_RETEN).orElse("") + "</INDIC_RETEN>\n" +
                                    "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                    "       </item>\n" ;

                        }
                    }else{
                        itemretencion ="            <item>\n" +
                                "              <TIPO_RETEN>RC</TIPO_RETEN>\n" +
                                "               <INDIC_RETEN></INDIC_RETEN>\n" +
                                "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                "            </item>\n";
                    }

                }
            }
        }
        String I_ORG_COMPRAS_X = "1100";

        if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero")) {
            I_SOCIEDAD1 ="1100";
            GRUPO_TESOR = "K-EXT-TERC";
            grupoEsquemaProveedor = "I1";
            I_GRUPO_CTAS = "PEXT";
            //1200
            I_ORG_COMPRAS_X = "1100";

        }else{
            I_SOCIEDAD1 ="1100";
            GRUPO_TESOR = "K-LOC-TERC";
            boolean opAfectas = beanProveedor.getOperacionesAfectas().equals("04");
            if (emiteRecibo || opAfectas){ // mizalo 27-02-2023
                grupoEsquemaProveedor = "N2";
            } else {
                grupoEsquemaProveedor = "N1";
            }

            I_GRUPO_CTAS = "PLOC";
            I_ORG_COMPRAS_X = "1100";
        }

        String CtasBancarias = "";
        String Moneda = "";
        String nombreTitular = "";
        String cci = "";
        for ( int i = 0; i < listaCuentaBancaria.size(); i++)
        {

            if (listaCuentaBancaria.get(i).getBanco().getClaveBanco().equals("08") && listaCuentaBancaria.get(i).getClaveControlBanco().equals("03")) {
                Moneda = "PEN";
            }
            else {
                Moneda = listaCuentaBancaria.get(i).getMoneda().getCodigoMoneda();
            }

            if(listaCuentaBancaria.get(i).getNumeroCuentaCci() != null)
                nombreTitular = listaCuentaBancaria.get(i).getContacto();

            if(nombreTitular == null)
            {
                nombreTitular = "";
            }

            cci = listaCuentaBancaria.get(i).getNumeroCuentaCci();
            String cci_dup;
            if(cci == null)
            {
                cci = "";
            } else{
                cci_dup=cci.replace("-","");
                cci= cci_dup.replaceAll("\\s", "");
            }

            String nrocuenta = listaCuentaBancaria.get(i).getNumeroCuenta().replace("-","");
            if(nrocuenta.length() > 18)
            {
                nrocuenta = nrocuenta.substring(0,18);
            }

            String tipocta = "AH";

            if(listaCuentaBancaria.get(i).getIndCuentaDetraccion().equals("SI") || listaCuentaBancaria.get(i).getIndCuentaDetraccion().equals("CC"))
            {
                tipocta = "CC";
            }


            CtasBancarias += "<item>\n" +
                    "               <PAIS>" + listaCuentaBancaria.get(i).getProveedor().getPais().getCodigoUbigeoSap() + "</PAIS>\n" +
                    "               <ENTIDAD_BANCARIA>"+listaCuentaBancaria.get(i).getBanco().getClaveBanco()+"</ENTIDAD_BANCARIA>\n" +
                    "               <TIPO_CUENTA>"+tipocta+"</TIPO_CUENTA>\n" +
                    "               <NRO_CUENTA>"+ nrocuenta +"</NRO_CUENTA>\n" +
                    "               <MONEDA>"+ Moneda +"</MONEDA>\n" +
                    "               <CCI>"+cci.trim()+"</CCI>\n" +
                    "               <REFERENCIA>"+""+"</REFERENCIA>\n" +
                    "               <NOMBRE_TITULAR>"+ nombreTitular.trim() +"</NOMBRE_TITULAR>\n" +
                    "               <DIRECC_TITULAR>"+""+"</DIRECC_TITULAR>\n" +
                    "               <NOMBRE_BANCO>"+""+"</NOMBRE_BANCO>\n" +
                    "               <DIRECC_BANCO>"+""+"</DIRECC_BANCO>\n" +
                    "               <CODIGO_ABA>"+""+"</CODIGO_ABA>\n" +
                    "               <CODIGO_SWIFT>"+""+"</CODIGO_SWIFT>\n" +
                    "               <CODIGO_IBAN>"+""+"</CODIGO_IBAN>\n" +
                    "            </item>\n";

        }

        String CtasBancariasExt = "";
        if(beanProveedor.getCuentaBeneficiario() != null && !beanProveedor.getCuentaBeneficiario().equals(""))
        {
            String nrocuenta = beanProveedor.getCuentaBeneficiario();
            Banco bank = bancoRepository.getClaveBanco(beanProveedor.getIdBanco().getIdBanco());
            if(nrocuenta.length() > 18)
            {
                nrocuenta = nrocuenta.substring(0,18);
            }

            CtasBancariasExt = "<I_CUENTA_BANCO_EXTRAN>\n" +
                    "<item>\n" +
                    "               <BANKS>"+ beanProveedor.getPaisBancoExtranjero().getCodigoUbigeoSap() +"</BANKS>\n" +
                    "               <BANKL>"+ bank.getClaveBanco() +"</BANKL>\n" +
                    "               <CLAVE_REGULAR>"+ nrocuenta +"</CLAVE_REGULAR>\n" +
                    "               <BANKA>"+ beanProveedor.getBancoExtranjero() +"</BANKA>\n" +
                    "               <PROVZ>"+ beanProveedor.getEstadoBancoExtranjero().getCodigoUbigeoSap() +"</PROVZ>\n" +
                    "               <ORT01>"+ beanProveedor.getCiudadBancoCtaExtranjero() +"</ORT01>\n" +
                    "               <STRAS>" + beanProveedor.getDireccionBancoCtaExtranjero() + "</STRAS>\n" +
                    "               <SWIFT_ABA>"+ beanProveedor.getCodigoBancoCtaExtranjero() +"</SWIFT_ABA>\n" +
                    "               <KOINH>"+ beanProveedor.getNombreBeneficiario() +"</KOINH>\n" +
                    "               <BKREF>"+ beanProveedor.getReferenciaBeneficiario() +"</BKREF>\n" +
                    "               <IBAN></IBAN>\n" +
                    "            </item>\n" +
                    "            </I_CUENTA_BANCO_EXTRAN>\n";
        }else
        {
            CtasBancariasExt = "<I_CUENTA_BANCO_EXTRAN/>";
        }

        //CtasBancariasExt = "<I_CUENTA_BANCO_EXTRAN/>";
        String bancoLocal = "";

        if (CtasBancarias.isEmpty()) {
            bancoLocal = "<I_CUENTA_BANCARIA/>";
        } else {
            bancoLocal = "         <I_CUENTA_BANCARIA> \n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    CtasBancarias +
                    "         </I_CUENTA_BANCARIA>\n";
        }

        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_CREA_PROVEEDOR>\n" +
                bancoLocal +
                //"          \n" +
                //"            <!--Zero or more repetitions:-->\n" +
                CtasBancariasExt +
                // "         </I_CUENTA_BANCO_EXTRAN>\n" +
                "         <I_DATOS_GENERALES>\n" +
                "            <TRATAMIENTO>"+Optional.ofNullable(TRATAMIENTO).orElse("")+"</TRATAMIENTO>\n" +
                "            <NOMBRE>"+toEscaped(Optional.ofNullable(NOMBRE).orElse(""))+"</NOMBRE>\n" +
//                "            <NOMBRE><![CDATA["+ Optional.ofNullable(NOMBRE).orElse("") +"]]></NOMBRE>\n" +
                "            <NOMBRE2>"+ toEscaped(Optional.ofNullable(NOMBRE2).orElse("") )+"</NOMBRE2>\n" +
                "            <NOMBRE3>"+ toEscaped(Optional.ofNullable(NOMBRE3).orElse("")) +"</NOMBRE3>\n" +
                "            <NOMBRE4>"+toEscaped(Optional.ofNullable(NOMBRE4).orElse(""))+"</NOMBRE4>\n" +
               // "            <CONCEP_BUSQUED>"+Optional.ofNullable(beanProveedor.getRuc()).orElse("")+"</CONCEP_BUSQUED>\n" +
                "            <CONCEP_BUSQUED>"+NUM_IDENT_FISC+"</CONCEP_BUSQUED>\n" +
                "            <COD_POSTAL>" + Optional.ofNullable(beanProveedor.getCodigoPostal()).orElse("123456789").toUpperCase().trim() +"</COD_POSTAL>\n" +
                "            <PAIS>"+ Optional.ofNullable(pais.toUpperCase()).orElse("")+"</PAIS>\n" +
                "            <REGION>"+ Optional.ofNullable(region.toUpperCase()).orElse("")+"</REGION>\n" +
                "            <POBLACION>"+ Optional.ofNullable(provincia.toUpperCase()).orElse("")+"</POBLACION>\n" +
                "            <NUM_IDENT_FISC>"+NUM_IDENT_FISC+"</NUM_IDENT_FISC>\n" +
                //agregar validacion si es que es menor de 16 no envie nada.
                "            <NIF3>"+NIF3+"</NIF3>\n" +
                "            <NUM_DNI>"+Optional.ofNullable(NUM_DNI).orElse("")+"</NUM_DNI>\n" +
                "            <TIPO_NIF>"+Optional.ofNullable(TIPO_NIF).orElse("")+"</TIPO_NIF>\n" +
                "            <CLASE_IMPUESTO>"+Optional.ofNullable(CLASE_IMPUESTO).orElse("")+"</CLASE_IMPUESTO>\n" +
                "            <RECARGO_EQUIV>"+RECARGO_EQUIV+"</RECARGO_EQUIV>\n" +
                "            <PERSONA_FISICA>"+toEscaped(Optional.ofNullable(PERSONA_FISICA).orElse(""))+"</PERSONA_FISICA>\n" +
                "            <RAMO>"+Optional.ofNullable("010").orElse("010")+"</RAMO>\n" +
                "            <TELEFONO>"+Optional.ofNullable(TELEFONO).orElse("")+"</TELEFONO>\n" +
                "            <TELEMOV>"+""+"</TELEMOV>\n" +
                "            <DIRECCION>"+toEscaped(Optional.ofNullable(DIRECCION).orElse(""))+"</DIRECCION>\n" +
                "            <DIRECCION2>"+toEscaped(Optional.ofNullable(DIRECCION2).orElse(""))+"</DIRECCION2>\n" +
                "            <DIRECCION3>"+toEscaped(Optional.ofNullable(DIRECCION3).orElse(""))+"</DIRECCION3>\n" +
                "            <DIRECCION4>"+toEscaped(Optional.ofNullable(DIRECCION4).orElse(""))+"</DIRECCION4>\n" +
                "            <NOMB_PILA>"+toEscaped(Optional.ofNullable(NOMB_PILA).orElse(""))+"</NOMB_PILA>\n" +
                "            <NOMB_CONTACTO>"+toEscaped(Optional.ofNullable(NOMB_CONTACTO).orElse(""))+"</NOMB_CONTACTO>\n" +
                "            <TELEFONO_CONTACTO>"+Optional.ofNullable(TELEFONO_CONTACTO).orElse("")+"</TELEFONO_CONTACTO>\n" +
                "            <TELEFONO_CELULAR>"+Optional.ofNullable(beanProveedor.getCelularRepresentanteLegal() != null ? beanProveedor.getCelularRepresentanteLegal().trim():"").orElse("")+"</TELEFONO_CELULAR>\n" +
                "            <DEP_CONTACTO>"+Optional.ofNullable(DEP_CONTACTO).orElse("")+"</DEP_CONTACTO>\n" +
                "            <FUNC_CONTACTO>"+Optional.ofNullable(FUNC_CONTACTO).orElse("")+"</FUNC_CONTACTO>\n" +
                "            <MAIL_CONTACTO>" +
                "<item>\n" +
                "    <CORREO>"+Optional.ofNullable(MAIL_CONTACTO).orElse("")+"</CORREO>\n" +
                "</item>" ;
        if(MAIL_CONTACTO_P0 != "" || MAIL_CONTACTO_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO_P1 != "" || MAIL_CONTACTO_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO_P2 != "" || MAIL_CONTACTO_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        tramaXML += "</MAIL_CONTACTO>\n" +
                "            <DNI_CONTACTO>"+toEscaped(Optional.ofNullable(DNI_CONTACTO).orElse(""))+"</DNI_CONTACTO>\n" +
                "            <NOMB_PILA2>"+toEscaped(Optional.ofNullable(NOMB_PILA2).orElse(""))+"</NOMB_PILA2>\n" +
                "            <NOMB_CONTACTO2>"+toEscaped(Optional.ofNullable(NOMB_CONTACTO2).orElse(""))+"</NOMB_CONTACTO2>\n" +
                "            <TELEFONO_CONTACTO2>"+Optional.ofNullable(TELEFONO_CONTACTO2).orElse("")+"</TELEFONO_CONTACTO2>\n" +
                "            <TELEFONO_CELULAR2>"+Optional.ofNullable(beanProveedor.getCelularPersonaCreditoCobranza() != null ? beanProveedor.getCelularPersonaCreditoCobranza().trim(): "").orElse("")+"</TELEFONO_CELULAR2>\n" +
                "            <DEP_CONTACTO2>"+Optional.ofNullable(DEP_CONTACTO2).orElse("")+"</DEP_CONTACTO2>\n" +
                "            <FUNC_CONTACTO2>"+Optional.ofNullable(FUNC_CONTACTO2).orElse("")+"</FUNC_CONTACTO2>\n" +
                "            <MAIL_CONTACTO2>" +
                "<item>\n" +
                "                  <CORREO>"+Optional.ofNullable(MAIL_CONTACTO2).orElse("")+"</CORREO>\n" +

                "               </item>";
        if(MAIL_CONTACTO2_P0 != "" || MAIL_CONTACTO2_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO2_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO2_P1 != "" || MAIL_CONTACTO2_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO2_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO2_P2 != "" || MAIL_CONTACTO2_P2 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO2_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        tramaXML +=  "</MAIL_CONTACTO2>\n" +
                "            <DNI_CONTACTO2>"+toEscaped(Optional.ofNullable(DNI_CONTACTO2).orElse(""))+"</DNI_CONTACTO2>\n" +

                "            <NOMB_PILA3>"+toEscaped(Optional.ofNullable(NOMB_PILA3).orElse(""))+"</NOMB_PILA3>\n" +
                "            <NOMB_CONTACTO3>"+toEscaped(Optional.ofNullable(NOMB_CONTACTO3).orElse(""))+"</NOMB_CONTACTO3>\n" +
                "            <TELEFONO_CONTACTO3>"+Optional.ofNullable(TELEFONO_CONTACTO3).orElse("")+"</TELEFONO_CONTACTO3>\n" +
                "            <TELEFONO_CELULAR3>"+Optional.ofNullable(beanProveedor.getCelularTesoreria() !=null ? beanProveedor.getCelularTesoreria().trim():"").orElse("")+"</TELEFONO_CELULAR3>\n" +
                "            <DEP_CONTACTO3>"+Optional.ofNullable(DEP_CONTACTO3).orElse("")+"</DEP_CONTACTO3>\n" +
                "            <FUNC_CONTACTO3>"+Optional.ofNullable(FUNC_CONTACTO3).orElse("")+"</FUNC_CONTACTO3>\n" +
                "            <MAIL_CONTACTO3>" +
                "<item>\n" +
                "                  <CORREO>"+Optional.ofNullable(MAIL_CONTACTO3).orElse("")+"</CORREO>\n" +
                "               </item>" ;
        if(MAIL_CONTACTO3_P0 != "" || MAIL_CONTACTO3_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO3_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO3_P1 != "" || MAIL_CONTACTO3_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO3_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO3_P2 != "" || MAIL_CONTACTO3_P2 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO3_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        tramaXML += "</MAIL_CONTACTO3>\n" +
                "            <DNI_CONTACTO3>"+toEscaped(Optional.ofNullable(DNI_CONTACTO3).orElse(""))+"</DNI_CONTACTO3>\n" +

                "            <NOMB_PILA4>"+toEscaped(Optional.ofNullable(NOMB_PILA4).orElse(""))+"</NOMB_PILA4>\n" +
                "            <NOMB_CONTACTO4>"+toEscaped(Optional.ofNullable(NOMB_CONTACTO4).orElse(""))+"</NOMB_CONTACTO4>\n" +
                "            <TELEFONO_CONTACTO4>"+Optional.ofNullable(TELEFONO_CONTACTO4).orElse("")+"</TELEFONO_CONTACTO4>\n" +
                "            <TELEFONO_CELULAR4>"+Optional.ofNullable(beanProveedor.getCelularPersonaCompra() != null ? beanProveedor.getCelularPersonaCompra().trim():"").orElse("")+"</TELEFONO_CELULAR4>\n" +
                "            <DEP_CONTACTO4>"+Optional.ofNullable(DEP_CONTACTO4).orElse("")+"</DEP_CONTACTO4>\n" +
                "            <FUNC_CONTACTO4>"+Optional.ofNullable(FUNC_CONTACTO4).orElse("")+"</FUNC_CONTACTO4>\n" +
                "            <MAIL_CONTACTO4>" +
                "<item>\n" +
                "                  <CORREO>"+Optional.ofNullable(MAIL_CONTACTO4).orElse("")+"</CORREO>\n" +
                "               </item>" ;
        if(MAIL_CONTACTO4_P0 != "" || MAIL_CONTACTO4_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO4_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO4_P1 != "" || MAIL_CONTACTO4_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO4_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO4_P2 != "" || MAIL_CONTACTO4_P2 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO4_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }

        if(distrito == "" || distrito == null){
            distrito = beanProveedor.getRegion().getDescripcion();
        }
        String retencion;
        if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero")) {
            retencion = "<I_RETENCIONES/>";
        } else {
            retencion = "<I_RETENCIONES>\n" +
                        "<!--Zero or more repetitions:-->\n" +
                            itemretencion +
                        "</I_RETENCIONES>\n";
        }


            tramaXML +=                    "</MAIL_CONTACTO4>\n" +
                "            <DNI_CONTACTO4>"+toEscaped(Optional.ofNullable(DNI_CONTACTO2).orElse(""))+"</DNI_CONTACTO4>\n" +

                "            <FORMA_COMUNICACION>"+""+"</FORMA_COMUNICACION>\n" +
                "            <EMAIL>"+Optional.ofNullable(EMAIL).orElse("")+"</EMAIL>\n" +
                "            <CORREOS_ADIC>"+""+"</CORREOS_ADIC>\n" +
                "            <DISTRITO>"+ Optional.ofNullable(distrito.toUpperCase()).orElse("")+"</DISTRITO>\n" +
                "            <FECNAC>"+""+"</FECNAC>\n" +
                "            <LUGNAC>"+""+"</LUGNAC>\n" +
                "            <SEXO>"+""+"</SEXO>\n" +
                "            <PROFESION>"+""+"</PROFESION>\n" +
                "            <FUNC_INTER>"+Optional.ofNullable(FUNC_INTER).orElse("")+"</FUNC_INTER>\n" +
                "            <COD_INTER>"+Optional.ofNullable(COD_INTER).orElse("")+"</COD_INTER>\n" +
                "         </I_DATOS_GENERALES>\n" +
                "         <I_DATOS_ORG_COMP>\n" +
                "            <MONEDA_PEDIDO>"+Optional.ofNullable(beanProveedor.getMoneda().getCodigoMoneda().trim()).orElse("")+"</MONEDA_PEDIDO>\n" +
                "            <COND_PAGO>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</COND_PAGO>\n" +
                "            <GRP_ESQUEMA_PROV>"+grupoEsquemaProveedor+"</GRP_ESQUEMA_PROV>\n" +
                "            <VERIF_FACT_EM>"+VALOR_DEFAULT+"</VERIF_FACT_EM>\n" +
                "            <PED_AUTOMATICO>"+"X"+"</PED_AUTOMATICO>\n" +
                "            <VER_FACT_REL_SER>"+""+"</VER_FACT_REL_SER>\n" +
                "            <CONC_BONIF_ESPE>"+""+"</CONC_BONIF_ESPE>\n" +
                "            <CONT_CONFIRMA>"+""+"</CONT_CONFIRMA>\n" +
                "            <LIQ_PPOST>"+""+"</LIQ_PPOST>\n" +
                "            <LIQ_EPOST>"+""+"</LIQ_EPOST>\n" +
                "            <GRUPO_COMPRA></GRUPO_COMPRA>\n" +
                "         </I_DATOS_ORG_COMP>\n" +
                "         <I_DATOS_SOCIEDAD>\n" +
                "             <CTA_ASOCIADA>"+ctaAsociada+"</CTA_ASOCIADA>\n" +
                "            <GRUPO_TESOR>"+GRUPO_TESOR+"</GRUPO_TESOR>\n" +
                "            <COND_PAGO>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</COND_PAGO>\n" +
                "            <GRUPO_TOLER>"+""+"</GRUPO_TOLER>\n" +
                "            <VERIF_FRA_DOB>"+"X"+"</VERIF_FRA_DOB>\n" +
                "            <VIA_PAGO>"+Optional.ofNullable(viasPago).orElse("")+"</VIA_PAGO>\n" +
                "         </I_DATOS_SOCIEDAD>\n" +
                "         <I_ESTADO_PROVEEDOR>\n" +
                "            <INICIO_BLOQUEO>"+""+"</INICIO_BLOQUEO>\n" +
                "            <FIN_BLOQUEO>"+""+"</FIN_BLOQUEO>\n" +
                "            <HOMOLOGACION>"+""+"</HOMOLOGACION>\n" +
                "            <CHECK01>"+""+"</CHECK01>\n" +
                "            <CHECK02>"+""+"</CHECK02>\n" +
                "            <CHECK03>"+""+"</CHECK03>\n" +
                "            <CHECK04>"+""+"</CHECK04>\n" +
                "            <CHECK05>"+""+"</CHECK05>\n" +
                "            <CHECK06>"+""+"</CHECK06>\n" +
                "            <CHECK07>"+""+"</CHECK07>\n" +
                "            <CHECK08>"+""+"</CHECK08>\n" +
                "            <CHECK09>"+""+"</CHECK09>\n" +
                "            <CHECK10>"+""+"</CHECK10>\n" +
                "            <SENSIBLE>"+""+"</SENSIBLE>\n" +
                "         </I_ESTADO_PROVEEDOR>\n" +
                "           <I_GRUPO_CTAS>"+I_GRUPO_CTAS +"</I_GRUPO_CTAS>\n" +
                "         <I_ORG_COMPRAS>"+I_ORG_COMPRAS_X+"</I_ORG_COMPRAS>\n" +
                            retencion +
                "        <I_SOCIEDAD>"+I_SOCIEDAD1+"</I_SOCIEDAD>\n" +
                "      </urn:ZMM_CREA_PROVEEDOR>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }

    private String armarTramaActualizar2(Proveedor beanProveedor, List<ProveedorCuentaBancaria> listaCuentaBancaria,String usuarioSap)
    {

        String NOMB_CONTACTO = "";
        String NOMB_PILA = "";
        String TELEFONO_CONTACTO = "";
        String TELEFONO_CELULAR = "";
        String MAIL_CONTACTO = "";
        String MAIL_CONTACTO_P0 = "";
        String MAIL_CONTACTO_P1 = "";
        String MAIL_CONTACTO_P2 = "";
        String  DNI_CONTACTO = "";
        String DEP_CONTACTO = "";
        String FUNC_CONTACTO = "";

        String NOMB_PILA2 = "";
        String NOMB_CONTACTO2 = "";
        String TELEFONO_CONTACTO2 = "";
        String TELEFONO_CELULAR2 = "";
        String MAIL_CONTACTO2 = "";
        String MAIL_CONTACTO2_P0 = "";
        String MAIL_CONTACTO2_P1 = "";
        String MAIL_CONTACTO2_P2 = "";
        String DNI_CONTACTO2 = "";
        String DEP_CONTACTO2 =  "";
        String FUNC_CONTACTO2 =  "";

        String NOMB_PILA3 = "";
        String NOMB_CONTACTO3 = "";
        String TELEFONO_CONTACTO3 = "";
        String TELEFONO_CELULAR3 = "";
        String MAIL_CONTACTO3 = "";
        String MAIL_CONTACTO3_P0 = "";
        String MAIL_CONTACTO3_P1 = "";
        String MAIL_CONTACTO3_P2 = "";
        String DNI_CONTACTO3 = "";
        String DEP_CONTACTO3 =  "";
        String FUNC_CONTACTO3 =  "";

        String NOMB_PILA4 = "";
        String NOMB_CONTACTO4 = "";
        String TELEFONO_CONTACTO4 = "";
        String TELEFONO_CELULAR4 = "";
        String MAIL_CONTACTO4 = "";
        String MAIL_CONTACTO4_P0 = "";
        String MAIL_CONTACTO4_P1 = "";
        String MAIL_CONTACTO4_P2 = "";
        String DNI_CONTACTO4 = "";
        String DEP_CONTACTO4 =  "";
        String FUNC_CONTACTO4 =  "";

        String DIRECCION = "";
        String DIRECCION2 = "";
        String DIRECCION3 = "";
        String DIRECCION4 = "";
        String TELEFONO ="";
        String EMAIL ="";
        String NUM_IDENT_FISC ="";
        String TIPO_NIF ="";
        String FUNC_INTER ="";
        String CLASE_IMPUESTO ="";
        String RECARGO_EQUIV ="";
        String PERSONA_FISICA ="";
        String RAMO ="010";
        String COD_INTER = "";
        String TRATAMIENTO = "EMPRESA";
        String NOMBRE3 = "";
        String NOMBRE4 = "";
//--------------IT_RETENCIONES-----------
        String TIPO_RETEN = "";
        String INDIC_RETEN = "";
        String WT_SUBJCT =  "";
        String PADRON = "";
        String NUM_DNI = "";

        String COD_POSTAL = "";
                            //1200
        String I_SOCIEDAD1 = "1100";
        String I_GRUPO_CTAS = "";


        String pais = Optional.ofNullable(beanProveedor.getPais())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();
        String region = Optional.ofNullable(beanProveedor.getRegion())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
                .map(c -> c.trim()).get();
        logger.error("REGION"+region.toString());

        String provincia = "";

        if(beanProveedor.getProvincia() != null)
        {
            provincia=beanProveedor.getProvincia().getDescripcion();
        }else{
            provincia = beanProveedor.getPais().getDescripcion();
        }

        //Optional.ofNullable(beanProveedor.getProvincia())
        //.map(tp -> Optional.ofNullable(tp.getDescripcion()).orElse("")).get();
        //.map(c -> c.trim()).get();
        String distrito = "";
        if(beanProveedor.getDistrito() != null)
        {
            distrito=beanProveedor.getDistrito().getDescripcion();
        }
        //Optional.ofNullable(beanProveedor.getDistrito())
        //.map(tp -> Optional.ofNullable(tp.getDescripcion()).orElse("")).get();
        //.map(c -> c.trim()).get();


        String razonSocial = beanProveedor.getRazonSocial().trim().toUpperCase();
        razonSocial = AppUtil.reemplazarCaracteresEspeciales(razonSocial);
        String NOMBRE = "";
        String NOMBRE2 = "";

        if (razonSocial.length() <= 35) {
            NOMBRE = razonSocial.toUpperCase();
        }
        else {

            String[] palabras = razonSocial.toUpperCase().split(" ");
            Integer cant = 0;
            Integer cant2 = 0;
            Integer cant3 = 0;
            for (String palabra:palabras) {

                if(cant + palabra.length() + 1 <= 35)
                {
                    cant += palabra.length() + 1;
                    NOMBRE = NOMBRE + palabra + " ";
                    continue;
                }

                if(cant + palabra.length() + 1 > 35 && cant + palabra.length() + 1 <= 70 && cant2 + palabra.length() + 1 <= 35)
                {
                    cant += palabra.length() + 1;
                    cant2 += palabra.length() + 1;
                    NOMBRE2 = NOMBRE2 + palabra + " ";
                    continue;
                }

                if(cant + palabra.length() + 1 > 35 && cant + palabra.length() + 1 <= 70 && cant3 + palabra.length() + 1 <= 35)
                {
                    cant += palabra.length() + 1;
                    cant3+= palabra.length() + 1;
                    NOMBRE3 = NOMBRE3 + palabra + " ";
                    continue;
                }
            }


//            if (razonSocial.length() > 35 && razonSocial.length() <= 70) {
//                String razonSocial01 = razonSocial.substring(0, 35);
//                String razonSocial02 = razonSocial.substring(36, razonSocial.length());
//                NOMBRE = razonSocial01.toUpperCase();
//                NOMBRE2 = razonSocial02.toUpperCase();
//
//           } else {
//                String razonSocial01 = razonSocial.substring(0, 35);
//                String razonSocial02 = razonSocial.substring(36, 70);
//                NOMBRE = razonSocial01.toUpperCase();
//                NOMBRE2 = razonSocial02.toUpperCase();
//            }
        }


        String datosPersonaNatural=separarCadenas(razonSocial,"/");
        String nombres=datosPersonaNatural.split(",")[0];
        String apellidos=datosPersonaNatural.split(",")[1];

        Boolean indAgenteRetencion = beanProveedor.getIndAgenteRetencion() != null && beanProveedor.getIndAgenteRetencion().trim().equals("1");

        String direccion = "";
        String direccion1= "";
        String direccion2= "";
        String direccion3= "";
        String direccion4= "";
        if (Optional.ofNullable(beanProveedor.getDireccionFiscal()).isPresent()) {
            direccion = beanProveedor.getDireccionFiscal().toUpperCase();
            direccion = AppUtil.reemplazarCaracteresEspeciales(direccion);
            int longitud = direccion.length();
            if(longitud<=60){
                direccion1 =direccion.substring(0,direccion.length());
            }
            else if (longitud > 60 && longitud <= 120) {
                logger.error("DIRECCION 2");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,direccion.length());
            }else if(longitud > 120 && longitud <= 180){
                logger.error("DIRECCION 3");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,120);
                direccion3=direccion.substring(120,direccion.length());
            }else{
                logger.error("DIRECCION 4");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,120);
                direccion3=direccion.substring(120,180);
                direccion4=direccion.substring(180,direccion.length());
            }
        }

        logger.error("DIRECION: " + direccion);
        logger.error("DIRECION: " + direccion2);
        String grupoEsquemaProveedor="";
        String viasPago="";
        String tipoNif="";
        String claseImpuesto="01";
        String ctaAsociada="";


        String tipoProveedor=beanProveedor.getTipoProveedor().getCodigoSap().toUpperCase();
        if(tipoProveedor.equalsIgnoreCase("ZNAC")) {
            viasPago=VIAS_PAGO_N;
//            grupoEsquemaProveedor="Z1";
            grupoEsquemaProveedor="01";
//            tipoNif="P6";
            tipoNif="92";
            claseImpuesto=Optional.ofNullable(beanProveedor.getTipoPersona().toUpperCase())
                    .map(codigo -> {
                        switch (beanProveedor.getTipoPersona()) {
                            case "J":
                                return "PJ";
                            case "N":
                                return "PN";
                            default:
                                return "";
                        }
                    }).orElse("");

            if(claseImpuesto=="PJ"){

                ctaAsociada=CASOCIADA_FACTURA;
                TRATAMIENTO = "EMPRESA";
            }else{
                NUM_DNI = beanProveedor.getRuc().substring(2,10);
                TRATAMIENTO = "SENOR";
                NOMBRE3 = apellidos;
                NOMBRE4 = nombres;
                if(beanProveedor.getTipoComprobante().getCodigoTipoComprobante().equalsIgnoreCase("FA")){
                    ctaAsociada=CASOCIADA_FACTURA;
                }else{
                    ctaAsociada=CASOCIADA_RH;
                }

            }

            /*String datosContacto1=separarCadenas(beanProveedor.getNombreRepresentanteLegal()," ");
            String nombresContacto1=datosContacto1.split(",")[0];
            String apellidosContacto1=datosContacto1.split(",")[1];

            String datosContacto2=separarCadenas(beanProveedor.getNombrePersonaCreditoCobranza()," ");
            String nombresContacto2=datosContacto2.split(",")[0];
            String apellidosContacto2=datosContacto2.split(",")[1];*/

            NOMB_CONTACTO = beanProveedor.getNombreRepresentanteLegal();
            NOMB_PILA = beanProveedor.getNombreRepresentanteLegal();
            TELEFONO_CONTACTO = "";
            TELEFONO_CELULAR = beanProveedor.getCelularRepresentanteLegal().trim();
            MAIL_CONTACTO = beanProveedor.getEmailRepresentanteLegal().trim();
            MAIL_CONTACTO_P0 = beanProveedor.getEmailRepresentanteLegal2().trim();
            MAIL_CONTACTO_P1 = beanProveedor.getEmailRepresentanteLegal3().trim();
            MAIL_CONTACTO_P2 = beanProveedor.getEmailRepresentanteLegal4().trim();
            DNI_CONTACTO = beanProveedor.getNroDocumRepresentanteLegal().trim();
            DEP_CONTACTO = "0004";
            FUNC_CONTACTO = "04";

            NOMB_PILA2 = beanProveedor.getNombrePersonaCreditoCobranza();
            NOMB_CONTACTO2 = beanProveedor.getNombrePersonaCreditoCobranza();
            TELEFONO_CONTACTO2 = "";
            TELEFONO_CELULAR2 = beanProveedor.getCelularPersonaCreditoCobranza().trim();
            MAIL_CONTACTO2 = beanProveedor.getEmailPersonaCreditoCobranza().trim();
            MAIL_CONTACTO2_P0 = beanProveedor.getEmailPersonaCreditoCobranza2().trim();
            MAIL_CONTACTO2_P1 = beanProveedor.getEmailPersonaCreditoCobranza3().trim();
            MAIL_CONTACTO2_P2 = beanProveedor.getEmailPersonaCreditoCobranza4().trim();
            DNI_CONTACTO2 = beanProveedor.getNroDocumPersonaCreditoCobranza().trim();
            DEP_CONTACTO2 =  "0009";
            FUNC_CONTACTO2 =  "09";

            NOMB_PILA3 = beanProveedor.getNombrePersonaTesoreria();
            NOMB_CONTACTO3 = beanProveedor.getNombrePersonaTesoreria();
            TELEFONO_CONTACTO3 = "";
            TELEFONO_CELULAR3 = beanProveedor.getCelularTesoreria().trim();
            MAIL_CONTACTO3 = beanProveedor.getEmailPersonaTesoreria().trim();
            MAIL_CONTACTO3_P0 = beanProveedor.getEmailPersonaTesoreria2().trim();
            MAIL_CONTACTO3_P1 = beanProveedor.getEmailPersonaTesoreria3().trim();
            MAIL_CONTACTO3_P2 = beanProveedor.getEmailPersonaTesoreria4().trim();
            DNI_CONTACTO3 = beanProveedor.getNroDocumPersonaTesoreria().trim();
            DEP_CONTACTO3 =  "0009";
            FUNC_CONTACTO3 =  "09";

            NOMB_PILA4 = beanProveedor.getNombrePersonaCompra();
            NOMB_CONTACTO4 = beanProveedor.getNombrePersonaCompra();
            TELEFONO_CONTACTO4 = "";
            TELEFONO_CELULAR4 = beanProveedor.getCelularPersonaCompra().trim();
            MAIL_CONTACTO4 = beanProveedor.getEmailPersonaCompra().trim();
            MAIL_CONTACTO4_P0 = beanProveedor.getEmailPersonaCompra2().trim();
            MAIL_CONTACTO4_P1 = beanProveedor.getEmailPersonaCompra3().trim();
            MAIL_CONTACTO4_P2 = beanProveedor.getEmailPersonaCompra4().trim();
            DNI_CONTACTO4 = beanProveedor.getNroDocumPersonaCompra().trim();
            DEP_CONTACTO4 =  "0009";
            FUNC_CONTACTO4 =  "09";
        }
        else {
//            grupoEsquemaProveedor="Z4";
            grupoEsquemaProveedor="01";
            viasPago=VIAS_PAGO_E;
//            tipoNif="P0";
            tipoNif="97";
            claseImpuesto="01";
            if(beanProveedor.getTipoComprobante().getCodigoTipoComprobante().equalsIgnoreCase("FA")){
                ctaAsociada=CASOCIADA_FACTURA;
            }else{
                ctaAsociada=CASOCIADA_RH;
            }

            NOMB_CONTACTO = beanProveedor.getNombreRepresentanteLegal();
            NOMB_PILA = beanProveedor.getNombreRepresentanteLegal();
            TELEFONO_CONTACTO = "";
            MAIL_CONTACTO = beanProveedor.getEmailRepresentanteLegal().trim();
            MAIL_CONTACTO_P0 = beanProveedor.getEmailRepresentanteLegal2() == null ? "":beanProveedor.getEmailRepresentanteLegal2().trim();
            MAIL_CONTACTO_P1 = beanProveedor.getEmailRepresentanteLegal3() == null ? "":beanProveedor.getEmailRepresentanteLegal3().trim();
            MAIL_CONTACTO_P2 = beanProveedor.getEmailRepresentanteLegal4() == null ? "":beanProveedor.getEmailRepresentanteLegal4().trim();
            DNI_CONTACTO = beanProveedor.getNroDocumRepresentanteLegal().trim();
            DEP_CONTACTO = "0004";
            FUNC_CONTACTO = "04";

            NOMB_PILA2 = beanProveedor.getNombrePersonaCreditoCobranza();
            NOMB_CONTACTO2 = beanProveedor.getNombrePersonaCreditoCobranza();
            TELEFONO_CONTACTO2 = "";
            MAIL_CONTACTO2 = beanProveedor.getEmailPersonaCreditoCobranza().trim();
            MAIL_CONTACTO2_P0 = beanProveedor.getEmailPersonaCreditoCobranza2() == null ? "": beanProveedor.getEmailPersonaCreditoCobranza2().trim();
            MAIL_CONTACTO2_P1 = beanProveedor.getEmailPersonaCreditoCobranza3() == null ? "": beanProveedor.getEmailPersonaCreditoCobranza3().trim();
            MAIL_CONTACTO2_P2 = beanProveedor.getEmailPersonaCreditoCobranza4() == null ? "": beanProveedor.getEmailPersonaCreditoCobranza4().trim();
            DNI_CONTACTO2 = beanProveedor.getNroDocumPersonaCreditoCobranza().trim();
            DEP_CONTACTO2 =  "0009";
            FUNC_CONTACTO2 =  "09";

            NOMB_PILA3 = beanProveedor.getNombrePersonaTesoreria();
            NOMB_CONTACTO3 = beanProveedor.getNombrePersonaTesoreria();
            TELEFONO_CONTACTO3 = "";
            MAIL_CONTACTO3 = beanProveedor.getEmailPersonaTesoreria().trim();
            MAIL_CONTACTO3_P0 = beanProveedor.getEmailPersonaTesoreria2() == null ? "": beanProveedor.getEmailPersonaTesoreria2().trim();
            MAIL_CONTACTO3_P1 = beanProveedor.getEmailPersonaTesoreria3() == null ? "": beanProveedor.getEmailPersonaTesoreria3().trim();
            MAIL_CONTACTO3_P2 = beanProveedor.getEmailPersonaTesoreria4() == null ? "": beanProveedor.getEmailPersonaTesoreria4().trim();
            DNI_CONTACTO3 = beanProveedor.getNroDocumPersonaTesoreria().trim();
            DEP_CONTACTO3 =  "0009";
            FUNC_CONTACTO3 =  "09";

            NOMB_PILA4 = beanProveedor.getNombrePersonaCompra();
            NOMB_CONTACTO4 = beanProveedor.getNombrePersonaCompra();
            TELEFONO_CONTACTO4 = "";
            MAIL_CONTACTO4 = beanProveedor.getEmailPersonaCompra().trim();
            MAIL_CONTACTO4_P0 = beanProveedor.getEmailPersonaCompra2() == null ? "": beanProveedor.getEmailPersonaCompra2().trim();
            MAIL_CONTACTO4_P1 = beanProveedor.getEmailPersonaCompra3() == null ? "": beanProveedor.getEmailPersonaCompra3().trim();
            MAIL_CONTACTO4_P2 = beanProveedor.getEmailPersonaCompra4() == null ? "": beanProveedor.getEmailPersonaCompra4().trim();
            DNI_CONTACTO4 = beanProveedor.getNroDocumPersonaCompra() !=null ? beanProveedor.getNroDocumPersonaCompra().trim(): "";
            DEP_CONTACTO4 =  "0009";
            FUNC_CONTACTO4 =  "09";
        }


        DIRECCION = direccion1.toUpperCase();
        DIRECCION2 = direccion2.toUpperCase();
        DIRECCION3 = direccion3.toUpperCase();
        DIRECCION4 = direccion4.toUpperCase();
        TELEFONO =  beanProveedor.getTelefono().toUpperCase();
        EMAIL = beanProveedor.getEmail().trim().toUpperCase();
        NUM_IDENT_FISC =  beanProveedor.getRuc().toUpperCase();
        TIPO_NIF =  tipoNif;


        ///Datos de Intelocutor CodigoSap Usuario 1100000045
        FUNC_INTER = "";
        COD_INTER = usuarioSap.trim();
        //////fin//////
        CLASE_IMPUESTO = claseImpuesto;
        RECARGO_EQUIV =  "";
        PERSONA_FISICA =  "";
        RAMO = "";
        if(tipoProveedor.equalsIgnoreCase("ZNAC")) {

            if(claseImpuesto=="PJ"){
                TIPO_RETEN = "RE";
                INDIC_RETEN =  "R1";

                if(!indAgenteRetencion){
                    WT_SUBJCT =  VALOR_DEFAULT;
                    logger.error("PADRON" + beanProveedor.getCodigoPadron());
                }else{
                    WT_SUBJCT = "";
                }
                TIPO_RETEN =  "DE";
                INDIC_RETEN = "D1";
                WT_SUBJCT = VALOR_DEFAULT;
            }else{

                /*Denisse Indico que cuando se el proveedor es nacional debe de nacer con RE y R1*/

                TIPO_RETEN =  "RE";
                INDIC_RETEN = "R1";
                if(!indAgenteRetencion){
                    WT_SUBJCT = VALOR_DEFAULT;
                    logger.error("PADRON" + beanProveedor.getCodigoPadron());
                }else{
                    WT_SUBJCT = "";
                }

            }
        }else{

            TIPO_RETEN = "DE";
            INDIC_RETEN = "37";
            WT_SUBJCT = VALOR_DEFAULT;
        }

        String CONCEP_BUSQUED = Optional.ofNullable(beanProveedor.getRuc()).orElse("");
        RECARGO_EQUIV = "X";
        String GRUPO_TESOR = "K-LOC-TERC";
        Parametro indDetraccion;


        String itemretencion ="";
        if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
            indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));
        }
        boolean emiteRecibo = beanProveedor.getIndEmiteRecibo() == null ? false : beanProveedor.getIndEmiteRecibo();
        if(emiteRecibo){
            NUM_IDENT_FISC = "";
            TIPO_NIF= "0";
            CLASE_IMPUESTO= "";
            RECARGO_EQUIV = "";
            ctaAsociada = "42120001";
            viasPago = "CIT";
            TIPO_RETEN= "Q3";
            INDIC_RETEN = "A1";
            String WT_SUBJCTx = "X";
            itemretencion = "    <item>\n" +
                    "              <TIPO_RETEN>"+Optional.ofNullable(TIPO_RETEN).orElse("")+"</TIPO_RETEN>\n" +
                    "               <INDIC_RETEN>"+Optional.ofNullable(INDIC_RETEN).orElse("")+"</INDIC_RETEN>\n" +
                    "               <WT_SUBJCT>"+Optional.ofNullable(WT_SUBJCTx).orElse("")+"</WT_SUBJCT>\n" +
                    "            </item>\n";


        }else {
            if(beanProveedor.getOperacionesAfectas().equals("04")){
                String wt_sub = "";
                if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero")) {
                    TIPO_NIF= "0";
                    if (TIPO_RETEN.equals(null) || TIPO_RETEN.isEmpty()){
                        wt_sub = "";
                    } else {
                        wt_sub = "X";
                    }
                }

                itemretencion = "<item>\n" +
                        "            \t<TIPO_RETEN>"+Optional.ofNullable(TIPO_RETEN).orElse("")+"</TIPO_RETEN>\n" +
                        "            \t<INDIC_RETEN></INDIC_RETEN>\n" +
                        "            \t<WT_SUBJCT>"+wt_sub+"</WT_SUBJCT>\n" +
                        "       \t</item>";
            }else{
                if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
                    indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));
                    TIPO_RETEN = indDetraccion.getValor();
                    INDIC_RETEN= indDetraccion.getCodigo();
                }
                if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero"))
                {

                    NUM_IDENT_FISC = "";
                    TIPO_NIF= "0";
                    CLASE_IMPUESTO= "";
                    RECARGO_EQUIV = "";
                    ctaAsociada = CASOCIADA_FACTURA_E;

                    viasPago = "1CEILQRTU";


                    if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {


                        itemretencion = "<item>\n" +
                                "              <TIPO_RETEN>" + Optional.ofNullable(TIPO_RETEN).orElse("") + "</TIPO_RETEN>\n" +
                                "               <INDIC_RETEN>" + Optional.ofNullable(INDIC_RETEN).orElse("") + "</INDIC_RETEN>\n" +
                                "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                "       </item>\n" ;

                    }else{
                        itemretencion ="            <item>\n" +
                                "              <TIPO_RETEN>RC</TIPO_RETEN>\n" +
                                "               <INDIC_RETEN></INDIC_RETEN>\n" +
                                "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                "            </item>\n";
                    }
                }
                else
                {               //1200
                    I_SOCIEDAD1 ="1100";
                    if(beanProveedor.getIndDetraccion() != null && beanProveedor.getIndDetraccion() != "") {
                        indDetraccion = this.parametroRepository.getById(Integer.valueOf(beanProveedor.getIndDetraccion()));

                        TIPO_RETEN = indDetraccion.getValor();
                        INDIC_RETEN= indDetraccion.getCodigo();
                        itemretencion = "            <item>\n" +
                                "              <TIPO_RETEN>" + Optional.ofNullable(TIPO_RETEN).orElse("") + "</TIPO_RETEN>\n" +
                                "               <INDIC_RETEN>"+Optional.ofNullable(INDIC_RETEN).orElse("")+"</INDIC_RETEN>\n" +
                                "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                "            </item>\n" ;
                    }else{
                        itemretencion ="            <item>\n" +
                                "              <TIPO_RETEN>RC</TIPO_RETEN>\n" +
                                "               <INDIC_RETEN></INDIC_RETEN>\n" +
                                "               <WT_SUBJCT>" + Optional.ofNullable(WT_SUBJCT).orElse("") + "</WT_SUBJCT>\n" +
                                "            </item>\n";
                    }

                }
            }
        }


        if(beanProveedor.getTipoProveedor().getDescripcion().equals("Extranjero")) {
                        //1800
            I_SOCIEDAD1 ="1100";
            GRUPO_TESOR = "K-EXT-TERC";
            grupoEsquemaProveedor = "I1";
            I_GRUPO_CTAS = "PEXT";

        }else{
                        //1200
            I_SOCIEDAD1 ="1100";
            GRUPO_TESOR = "K-LOC-TERC";
            grupoEsquemaProveedor = "N1";
            I_GRUPO_CTAS = "PLOC";
        }

        String CtasBancarias = "";
        String Moneda = "";
        String nombreTitular = "";
        String cci = "";
        for ( int i = 0; i < listaCuentaBancaria.size(); i++)
        {

            if (listaCuentaBancaria.get(i).getBanco().getClaveBanco().equals("08") && listaCuentaBancaria.get(i).getClaveControlBanco().equals("03")) {
                Moneda = "PEN";
            }
            else {
                Moneda = listaCuentaBancaria.get(i).getMoneda().getCodigoMoneda();
            }

            if(listaCuentaBancaria.get(i).getNumeroCuentaCci() != null)
                nombreTitular = listaCuentaBancaria.get(i).getContacto();

            if(nombreTitular == null)
            {
                nombreTitular = "";
            }

            cci = listaCuentaBancaria.get(i).getNumeroCuentaCci();
            String cci_dup;
            if(cci == null)
            {
                cci = "";
            }else{
                cci_dup=cci.replace("-","");
                cci= cci_dup.replaceAll("\\s", "");
            }

            String nrocuenta = listaCuentaBancaria.get(i).getNumeroCuenta().replace("-","");
            if(nrocuenta.length() > 18)
            {
                nrocuenta = nrocuenta.substring(0,18);
            }

            String tipocta = "AH";

            if(listaCuentaBancaria.get(i).getIndCuentaDetraccion().equals("SI") || listaCuentaBancaria.get(i).getIndCuentaDetraccion().equals("CC"))
            {
                tipocta = "CC";
            }


            CtasBancarias += "<item>\n" +
                    "               <PAIS>" + listaCuentaBancaria.get(i).getProveedor().getPais().getCodigoUbigeoSap() + "</PAIS>\n" +
                    "               <ENTIDAD_BANCARIA>"+listaCuentaBancaria.get(i).getBanco().getClaveBanco()+"</ENTIDAD_BANCARIA>\n" +
                    "               <TIPO_CUENTA>"+tipocta+"</TIPO_CUENTA>\n" +
                    "               <NRO_CUENTA>"+ nrocuenta +"</NRO_CUENTA>\n" +
                    "               <MONEDA>"+ Moneda +"</MONEDA>\n" +
                    "               <CCI>"+cci.trim()+"</CCI>\n" +
                    "               <REFERENCIA>"+""+"</REFERENCIA>\n" +
                    "               <NOMBRE_TITULAR>"+ nombreTitular.trim() +"</NOMBRE_TITULAR>\n" +
                    "               <DIRECC_TITULAR>"+""+"</DIRECC_TITULAR>\n" +
                    "               <NOMBRE_BANCO>"+""+"</NOMBRE_BANCO>\n" +
                    "               <DIRECC_BANCO>"+""+"</DIRECC_BANCO>\n" +
                    "               <CODIGO_ABA>"+""+"</CODIGO_ABA>\n" +
                    "               <CODIGO_SWIFT>"+""+"</CODIGO_SWIFT>\n" +
                    "               <CODIGO_IBAN>"+""+"</CODIGO_IBAN>\n" +
                    "            </item>\n";

        }

        String CtasBancariasExt = "";
        if(beanProveedor.getCuentaBeneficiario() != null && !beanProveedor.getCuentaBeneficiario().equals(""))
        {
            String nrocuenta = beanProveedor.getCuentaBeneficiario();
            Banco bank = bancoRepository.getClaveBanco(beanProveedor.getIdBanco().getIdBanco());
            if(nrocuenta.length() > 18)
            {
                nrocuenta = nrocuenta.substring(0,18);
            }

            CtasBancariasExt = "<I_CUENTA_BANCO_EXTRAN>\n" +
                    "<item>\n" +
                    "               <BANKS>"+ beanProveedor.getPaisBancoExtranjero().getCodigoUbigeoSap() +"</BANKS>\n" +
                    "               <BANKL>"+ bank.getClaveBanco() +"</BANKL>\n" +
                    "               <CLAVE_REGULAR>"+ nrocuenta +"</CLAVE_REGULAR>\n" +
                    "               <BANKA>"+ beanProveedor.getBancoExtranjero() +"</BANKA>\n" +
                    "               <PROVZ>"+ beanProveedor.getEstadoBancoExtranjero().getCodigoUbigeoSap() +"</PROVZ>\n" +
                    "               <ORT01>"+ beanProveedor.getCiudadBancoCtaExtranjero() +"</ORT01>\n" +
                    "               <STRAS>" + beanProveedor.getDireccionBancoCtaExtranjero() + "</STRAS>\n" +
                    "               <SWIFT_ABA>"+ beanProveedor.getCodigoBancoCtaExtranjero() +"</SWIFT_ABA>\n" +
                    "               <KOINH>"+ beanProveedor.getNombreBeneficiario() +"</KOINH>\n" +
                    "               <BKREF>"+ beanProveedor.getReferenciaBeneficiario() +"</BKREF>\n" +
                    "               <IBAN></IBAN>\n" +
                    "            </item>\n" +
                    "            </I_CUENTA_BANCO_EXTRAN>\n";
        }else
        {
            CtasBancariasExt = "<I_CUENTA_BANCO_EXTRAN/>";
        }

        //CtasBancariasExt = "<I_CUENTA_BANCO_EXTRAN/>";
        String bancoLocal = "";

        if (CtasBancarias.isEmpty()) {
            bancoLocal = "<I_CUENTA_BANCARIA/>";
        } else {
            bancoLocal = "         <I_CUENTA_BANCARIA> \n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    CtasBancarias +
                    "         </I_CUENTA_BANCARIA>\n";
        }

        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_ACTUALIZA_PROVEEDOR>\n" +
                bancoLocal +
                //"          \n" +
                //"            <!--Zero or more repetitions:-->\n" +
                CtasBancariasExt +
                // "         </I_CUENTA_BANCO_EXTRAN>\n" +
                "         <I_DATOS_GENERALES>\n" +
                "            <TRATAMIENTO>"+Optional.ofNullable(TRATAMIENTO).orElse("")+"</TRATAMIENTO>\n" +
                "            <NOMBRE>"+toEscaped(Optional.ofNullable(NOMBRE).orElse(""))+"</NOMBRE>\n" +
//                "            <NOMBRE><![CDATA["+ Optional.ofNullable(NOMBRE).orElse("") +"]]></NOMBRE>\n" +
                "            <NOMBRE2>"+ toEscaped(Optional.ofNullable(NOMBRE2).orElse("") )+"</NOMBRE2>\n" +
                "            <NOMBRE3>"+ toEscaped(Optional.ofNullable(NOMBRE3).orElse("")) +"</NOMBRE3>\n" +
                "            <NOMBRE4>"+toEscaped(Optional.ofNullable(NOMBRE4).orElse(""))+"</NOMBRE4>\n" +
                "            <CONCEP_BUSQUED>"+Optional.ofNullable(beanProveedor.getRuc()).orElse("")+"</CONCEP_BUSQUED>\n" +
                "            <COD_POSTAL>" + Optional.ofNullable(beanProveedor.getCodigoPostal()).orElse("123456789").toUpperCase().trim() +"</COD_POSTAL>\n" +
                "            <PAIS>"+ Optional.ofNullable(pais.toUpperCase()).orElse("")+"</PAIS>\n" +
                "            <REGION>"+ Optional.ofNullable(region.toUpperCase()).orElse("")+"</REGION>\n" +
                "            <POBLACION>"+ Optional.ofNullable(provincia.toUpperCase()).orElse("")+"</POBLACION>\n" +
                "            <NUM_IDENT_FISC>"+Optional.ofNullable(beanProveedor.getRuc()).orElse("")+"</NUM_IDENT_FISC>\n" +
                "            <NUM_DNI>"+Optional.ofNullable(NUM_DNI).orElse("")+"</NUM_DNI>\n" +
                "            <TIPO_NIF>"+Optional.ofNullable(TIPO_NIF).orElse("")+"</TIPO_NIF>\n" +
                "            <CLASE_IMPUESTO>"+Optional.ofNullable(CLASE_IMPUESTO).orElse("")+"</CLASE_IMPUESTO>\n" +
                "            <RECARGO_EQUIV>"+RECARGO_EQUIV+"</RECARGO_EQUIV>\n" +
                "            <PERSONA_FISICA>"+Optional.ofNullable(PERSONA_FISICA).orElse("")+"</PERSONA_FISICA>\n" +
                "            <RAMO>"+Optional.ofNullable("010").orElse("010")+"</RAMO>\n" +
                "            <TELEFONO>"+Optional.ofNullable(TELEFONO).orElse("")+"</TELEFONO>\n" +
                "            <TELEMOV>"+""+"</TELEMOV>\n" +
                "            <DIRECCION>"+Optional.ofNullable(DIRECCION).orElse("")+"</DIRECCION>\n" +
                "            <DIRECCION2>"+Optional.ofNullable(DIRECCION2).orElse("")+"</DIRECCION2>\n" +
                "            <DIRECCION3>"+Optional.ofNullable(DIRECCION3).orElse("")+"</DIRECCION3>\n" +
                "            <DIRECCION4>"+Optional.ofNullable(DIRECCION4).orElse("")+"</DIRECCION4>\n" +
                "            <NOMB_PILA>"+toEscaped(Optional.ofNullable(NOMB_PILA).orElse(""))+"</NOMB_PILA>\n" +
                "            <NOMB_CONTACTO>"+toEscaped(Optional.ofNullable(NOMB_CONTACTO).orElse(""))+"</NOMB_CONTACTO>\n" +
                "            <TELEFONO_CONTACTO>"+Optional.ofNullable(TELEFONO_CONTACTO).orElse("")+"</TELEFONO_CONTACTO>\n" +
                "            <TELEFONO_CELULAR>"+Optional.ofNullable(beanProveedor.getCelularRepresentanteLegal()).orElse("")+"</TELEFONO_CELULAR>\n" +
                "            <DEP_CONTACTO>"+Optional.ofNullable(DEP_CONTACTO).orElse("")+"</DEP_CONTACTO>\n" +
                "            <FUNC_CONTACTO>"+Optional.ofNullable(FUNC_CONTACTO).orElse("")+"</FUNC_CONTACTO>\n" +
                "            <MAIL_CONTACTO>" +
                "<item>\n" +
                "    <CORREO>"+Optional.ofNullable(MAIL_CONTACTO).orElse("")+"</CORREO>\n" +
                "</item>" ;
        if(MAIL_CONTACTO_P0 != "" || MAIL_CONTACTO_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO_P1 != "" || MAIL_CONTACTO_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO_P2 != "" || MAIL_CONTACTO_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        tramaXML += "</MAIL_CONTACTO>\n" +
                "            <DNI_CONTACTO>"+Optional.ofNullable(DNI_CONTACTO).orElse("")+"</DNI_CONTACTO>\n" +
                "            <NOMB_PILA2>"+toEscaped(Optional.ofNullable(NOMB_PILA2).orElse(""))+"</NOMB_PILA2>\n" +
                "            <NOMB_CONTACTO2>"+Optional.ofNullable(NOMB_CONTACTO2).orElse("")+"</NOMB_CONTACTO2>\n" +
                "            <TELEFONO_CONTACTO2>"+Optional.ofNullable(TELEFONO_CONTACTO2).orElse("")+"</TELEFONO_CONTACTO2>\n" +
                "            <TELEFONO_CELULAR2>"+Optional.ofNullable(beanProveedor.getCelularPersonaCreditoCobranza() != null ? beanProveedor.getCelularPersonaCreditoCobranza().trim(): "").orElse("")+"</TELEFONO_CELULAR2>\n" +
                "            <DEP_CONTACTO2>"+Optional.ofNullable(DEP_CONTACTO2).orElse("")+"</DEP_CONTACTO2>\n" +
                "            <FUNC_CONTACTO2>"+Optional.ofNullable(FUNC_CONTACTO2).orElse("")+"</FUNC_CONTACTO2>\n" +
                "            <MAIL_CONTACTO2>" +
                "<item>\n" +
                "                  <CORREO>"+Optional.ofNullable(MAIL_CONTACTO2).orElse("")+"</CORREO>\n" +

                "               </item>";
        if(MAIL_CONTACTO2_P0 != "" || MAIL_CONTACTO2_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO2_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO2_P1 != "" || MAIL_CONTACTO2_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO2_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO2_P2 != "" || MAIL_CONTACTO2_P2 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO2_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        tramaXML +=  "</MAIL_CONTACTO2>\n" +
                "            <DNI_CONTACTO2>"+Optional.ofNullable(DNI_CONTACTO2).orElse("")+"</DNI_CONTACTO2>\n" +

                "            <NOMB_PILA3>"+toEscaped(Optional.ofNullable(NOMB_PILA3).orElse(""))+"</NOMB_PILA3>\n" +
                "            <NOMB_CONTACTO3>"+Optional.ofNullable(NOMB_CONTACTO3).orElse("")+"</NOMB_CONTACTO3>\n" +
                "            <TELEFONO_CONTACTO3>"+Optional.ofNullable(TELEFONO_CONTACTO3).orElse("")+"</TELEFONO_CONTACTO3>\n" +
                "            <TELEFONO_CELULAR3>"+Optional.ofNullable(beanProveedor.getCelularTesoreria() !=null ? beanProveedor.getCelularTesoreria().trim():"").orElse("")+"</TELEFONO_CELULAR3>\n" +
                "            <DEP_CONTACTO3>"+Optional.ofNullable(DEP_CONTACTO3).orElse("")+"</DEP_CONTACTO3>\n" +
                "            <FUNC_CONTACTO3>"+Optional.ofNullable(FUNC_CONTACTO3).orElse("")+"</FUNC_CONTACTO3>\n" +
                "            <MAIL_CONTACTO3>" +
                "<item>\n" +
                "                  <CORREO>"+Optional.ofNullable(MAIL_CONTACTO3).orElse("")+"</CORREO>\n" +
                "               </item>" ;
        if(MAIL_CONTACTO3_P0 != "" || MAIL_CONTACTO3_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO3_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO3_P1 != "" || MAIL_CONTACTO3_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO3_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO3_P2 != "" || MAIL_CONTACTO3_P2 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO3_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        tramaXML += "</MAIL_CONTACTO3>\n" +
                "            <DNI_CONTACTO3>"+Optional.ofNullable(DNI_CONTACTO2).orElse("")+"</DNI_CONTACTO3>\n" +

                "            <NOMB_PILA4>"+Optional.ofNullable(NOMB_PILA4).orElse("")+"</NOMB_PILA4>\n" +
                "            <NOMB_CONTACTO4>"+Optional.ofNullable(NOMB_CONTACTO4).orElse("")+"</NOMB_CONTACTO4>\n" +
                "            <TELEFONO_CONTACTO4>"+Optional.ofNullable(TELEFONO_CONTACTO4).orElse("")+"</TELEFONO_CONTACTO4>\n" +
                "            <TELEFONO_CELULAR4>"+Optional.ofNullable(beanProveedor.getCelularPersonaCompra() != null ? beanProveedor.getCelularPersonaCompra().trim():"").orElse("")+"</TELEFONO_CELULAR4>\n" +
                "            <DEP_CONTACTO4>"+Optional.ofNullable(DEP_CONTACTO4).orElse("")+"</DEP_CONTACTO4>\n" +
                "            <FUNC_CONTACTO4>"+Optional.ofNullable(FUNC_CONTACTO4).orElse("")+"</FUNC_CONTACTO4>\n" +
                "            <MAIL_CONTACTO4>" +
                "<item>\n" +
                "                  <CORREO>"+Optional.ofNullable(MAIL_CONTACTO4).orElse("")+"</CORREO>\n" +
                "               </item>" ;
        if(MAIL_CONTACTO4_P0 != "" || MAIL_CONTACTO4_P0 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO4_P0).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO4_P1 != "" || MAIL_CONTACTO4_P1 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO4_P1).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }
        if(MAIL_CONTACTO4_P2 != "" || MAIL_CONTACTO4_P2 != null){
            tramaXML += "<item>\n" +
                    "<CORREO>"+Optional.ofNullable(MAIL_CONTACTO4_P2).orElse("")+"</CORREO>\n" +
                    "</item>" ;
        }

        if(distrito == "" || distrito == null){
            distrito = beanProveedor.getRegion().getDescripcion();
        }
        tramaXML +=                    "</MAIL_CONTACTO4>\n" +
                "            <DNI_CONTACTO4>"+Optional.ofNullable(DNI_CONTACTO2).orElse("")+"</DNI_CONTACTO4>\n" +

                "            <FORMA_COMUNICACION>"+""+"</FORMA_COMUNICACION>\n" +
                "            <EMAIL>"+Optional.ofNullable(EMAIL).orElse("")+"</EMAIL>\n" +
                "            <CORREOS_ADIC>"+""+"</CORREOS_ADIC>\n" +
                "            <DISTRITO>"+ Optional.ofNullable(distrito.toUpperCase()).orElse("")+"</DISTRITO>\n" +
                "            <FECNAC>"+""+"</FECNAC>\n" +
                "            <LUGNAC>"+""+"</LUGNAC>\n" +
                "            <SEXO>"+""+"</SEXO>\n" +
                "            <PROFESION>"+""+"</PROFESION>\n" +
                "            <FUNC_INTER>"+Optional.ofNullable(FUNC_INTER).orElse("")+"</FUNC_INTER>\n" +
                "            <COD_INTER>"+Optional.ofNullable(COD_INTER).orElse("")+"</COD_INTER>\n" +
                "         </I_DATOS_GENERALES>\n" +
                "         <I_DATOS_ORG_COMP>\n" +
                "            <MONEDA_PEDIDO>"+Optional.ofNullable(beanProveedor.getMoneda().getCodigoMoneda().trim()).orElse("")+"</MONEDA_PEDIDO>\n" +
                "            <COND_PAGO>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</COND_PAGO>\n" +
                "            <GRP_ESQUEMA_PROV>"+grupoEsquemaProveedor+"</GRP_ESQUEMA_PROV>\n" +
                "            <VERIF_FACT_EM>"+VALOR_DEFAULT+"</VERIF_FACT_EM>\n" +
                "            <PED_AUTOMATICO>"+"X"+"</PED_AUTOMATICO>\n" +
                "            <VER_FACT_REL_SER>"+""+"</VER_FACT_REL_SER>\n" +
                "            <CONC_BONIF_ESPE>"+""+"</CONC_BONIF_ESPE>\n" +
                "            <CONT_CONFIRMA>"+""+"</CONT_CONFIRMA>\n" +
                "            <LIQ_PPOST>"+""+"</LIQ_PPOST>\n" +
                "            <LIQ_EPOST>"+""+"</LIQ_EPOST>\n" +
                "            <GRUPO_COMPRA></GRUPO_COMPRA>\n" +
                "         </I_DATOS_ORG_COMP>\n" +
                "         <I_DATOS_SOCIEDAD>\n" +
                "             <CTA_ASOCIADA>"+ctaAsociada+"</CTA_ASOCIADA>\n" +
                "            <GRUPO_TESOR>"+GRUPO_TESOR+"</GRUPO_TESOR>\n" +
                "            <COND_PAGO>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</COND_PAGO>\n" +
                "            <GRUPO_TOLER>"+""+"</GRUPO_TOLER>\n" +
                "            <VERIF_FRA_DOB>"+"X"+"</VERIF_FRA_DOB>\n" +
                "            <VIA_PAGO>"+Optional.ofNullable(viasPago).orElse("")+"</VIA_PAGO>\n" +
                "         </I_DATOS_SOCIEDAD>\n" +
                "         <I_ESTADO_PROVEEDOR>\n" +
                "            <INICIO_BLOQUEO>"+""+"</INICIO_BLOQUEO>\n" +
                "            <FIN_BLOQUEO>"+""+"</FIN_BLOQUEO>\n" +
                "            <HOMOLOGACION>"+""+"</HOMOLOGACION>\n" +
                "            <CHECK01>"+""+"</CHECK01>\n" +
                "            <CHECK02>"+""+"</CHECK02>\n" +
                "            <CHECK03>"+""+"</CHECK03>\n" +
                "            <CHECK04>"+""+"</CHECK04>\n" +
                "            <CHECK05>"+""+"</CHECK05>\n" +
                "            <CHECK06>"+""+"</CHECK06>\n" +
                "            <CHECK07>"+""+"</CHECK07>\n" +
                "            <CHECK08>"+""+"</CHECK08>\n" +
                "            <CHECK09>"+""+"</CHECK09>\n" +
                "            <CHECK10>"+""+"</CHECK10>\n" +
                "            <SENSIBLE>"+""+"</SENSIBLE>\n" +
                "         </I_ESTADO_PROVEEDOR>\n" +
                "           <I_GRUPO_CTAS>"+I_GRUPO_CTAS +"</I_GRUPO_CTAS>\n" +
                "         <I_NRO_PROVEEEDOR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</I_NRO_PROVEEEDOR>" +
                "         <I_ORG_COMPRAS>1100</I_ORG_COMPRAS>\n" +
                "         <I_RETENCIONES>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                itemretencion +
                "         </I_RETENCIONES>\n" +
                "        <I_SOCIEDAD>"+I_SOCIEDAD1+"</I_SOCIEDAD>\n" +
                "       </urn:ZMM_ACTUALIZA_PROVEEDOR>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";



        return tramaXML;

    }


    private String armarTramaActualizar(Proveedor beanProveedor, List<ProveedorCuentaBancaria> listaCuentaBancaria,String usuarioSap)
    {

        String NOMB_CONTACTO = "";
        String NOMB_PILA = "";
        String TELEFONO_CONTACTO = "";
        String MAIL_CONTACTO = "";
        String  DNI_CONTACTO = "";
        String DEP_CONTACTO = "";
        String FUNC_CONTACTO = "";

        String NOMB_PILA2 = "";
        String NOMB_CONTACTO2 = "";
        String TELEFONO_CONTACTO2 = "";
        String MAIL_CONTACTO2 = "";
        String DNI_CONTACTO2 = "";
        String DEP_CONTACTO2 =  "";
        String FUNC_CONTACTO2 =  "";

        String DIRECCION = "";
        String DIRECCION2 = "";
        String DIRECCION3 = "";
        String DIRECCION4 = "";
        String TELEFONO ="";
        String EMAIL ="";
        String NUM_IDENT_FISC ="";
        String TIPO_NIF ="";
        String FUNC_INTER ="";
        String CLASE_IMPUESTO ="";
        String RECARGO_EQUIV ="";
        String PERSONA_FISICA ="";
        String RAMO ="";
        String COD_INTER = "";
        String TRATAMIENTO = "";
        String NOMBRE3 = "";
        String NOMBRE4 = "";
//--------------IT_RETENCIONES-----------
        String TIPO_RETEN = "";
        String INDIC_RETEN = "";
        String WT_SUBJCT =  "";
        String PADRON = "";
        String NUM_DNI = "";


        String pais = Optional.ofNullable(beanProveedor.getPais())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSapErp()).orElse(""))
                .map(c -> c.trim()).get();
        String region = Optional.ofNullable(beanProveedor.getRegion())
                .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSapErp()).orElse(""))
                .map(c -> c.trim()).get();
        logger.error("REGION"+region.toString());
        String provincia = Optional.ofNullable(beanProveedor.getProvincia())
                .map(tp -> Optional.ofNullable(tp.getDescripcion()).orElse(""))
                .map(c -> c.trim()).get();
        String distrito = Optional.ofNullable(beanProveedor.getDistrito())
                .map(tp -> Optional.ofNullable(tp.getDescripcion()).orElse(""))
                .map(c -> c.trim()).get();


        String razonSocial = beanProveedor.getRazonSocial().trim().toUpperCase();
        razonSocial = AppUtil.reemplazarCaracteresEspeciales(razonSocial);
        String NOMBRE = "";
        String NOMBRE2 = "";

        if (razonSocial.length() <= 40) {
            NOMBRE = razonSocial.toUpperCase();
        }
        else {
            if (razonSocial.length() > 40 && razonSocial.length() <= 70) {
                String razonSocial01 = razonSocial.substring(0, 39);
                String razonSocial02 = razonSocial.substring(40, razonSocial.length());
                NOMBRE = razonSocial01.toUpperCase();
                NOMBRE2 = razonSocial02.toUpperCase();

            } else {
                String razonSocial01 = razonSocial.substring(0, 39);
                String razonSocial02 = razonSocial.substring(40, 70);
                NOMBRE = razonSocial01.toUpperCase();
                NOMBRE2 = razonSocial02.toUpperCase();
            }
        }
        String datosPersonaNatural=separarCadenas(razonSocial,"/");
        String nombres=datosPersonaNatural.split(",")[0];
        String apellidos=datosPersonaNatural.split(",")[1];

        Boolean indAgenteRetencion = beanProveedor.getIndAgenteRetencion() != null && beanProveedor.getIndAgenteRetencion().trim().equals("1");

        String direccion = "";
        String direccion1= "";
        String direccion2= "";
        String direccion3= "";
        String direccion4= "";
        if (Optional.ofNullable(beanProveedor.getDireccionFiscal()).isPresent()) {
            direccion = beanProveedor.getDireccionFiscal().toUpperCase();
            direccion = AppUtil.reemplazarCaracteresEspeciales(direccion);
            int longitud = direccion.length();
            if(longitud<=60){
                direccion1 =direccion.substring(0,direccion.length());
            }
            else if (longitud > 60 && longitud <= 120) {
                logger.error("DIRECCION 2");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,direccion.length());
            }else if(longitud > 120 && longitud <= 180){
                logger.error("DIRECCION 3");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,120);
                direccion3=direccion.substring(120,direccion.length());
            }else{
                logger.error("DIRECCION 4");
                direccion1=direccion.substring(0,60);
                direccion2=direccion.substring(60,120);
                direccion3=direccion.substring(120,180);
                direccion4=direccion.substring(180,direccion.length());
            }
        }

        logger.error("DIRECION: " + direccion);
        logger.error("DIRECION: " + direccion2);
        String grupoEsquemaProveedor="";
        String viasPago="";
        String tipoNif="";
        String claseImpuesto="";
        String ctaAsociada="";


        String tipoProveedor=beanProveedor.getTipoProveedor().getCodigoSap().toUpperCase();
        if(tipoProveedor.equalsIgnoreCase("ZNAC")) {
            viasPago=VIAS_PAGO_N;
//            grupoEsquemaProveedor="Z1";
            grupoEsquemaProveedor="01";
//            tipoNif="P6";
            tipoNif="80";
            claseImpuesto=Optional.ofNullable(beanProveedor.getTipoPersona().toUpperCase())
                    .map(codigo -> {
                        switch (beanProveedor.getTipoPersona()) {
                            case "J":
                                return "PJ";
                            case "N":
                                return "PN";
                            default:
                                return "";
                        }
                    }).orElse("");

            if(claseImpuesto=="PJ"){

                ctaAsociada=CASOCIADA_FACTURA;
                TRATAMIENTO = "EMPRESA";
            }else{
                NUM_DNI = beanProveedor.getRuc().substring(2,10);
                TRATAMIENTO = "SENOR";
                NOMBRE3 = apellidos;
                NOMBRE4 = nombres;
                if(beanProveedor.getTipoComprobante().getCodigoTipoComprobante().equalsIgnoreCase("FA")){
                    ctaAsociada=CASOCIADA_FACTURA;
                }else{
                    ctaAsociada=CASOCIADA_RH;
                }

            }

            /*String datosContacto1=separarCadenas(beanProveedor.getNombreRepresentanteLegal()," ");
            String nombresContacto1=datosContacto1.split(",")[0];
            String apellidosContacto1=datosContacto1.split(",")[1];

            String datosContacto2=separarCadenas(beanProveedor.getNombrePersonaCreditoCobranza()," ");
            String nombresContacto2=datosContacto2.split(",")[0];
            String apellidosContacto2=datosContacto2.split(",")[1];*/

            NOMB_CONTACTO = beanProveedor.getNombreRepresentanteLegal();
            NOMB_PILA = beanProveedor.getNombreRepresentanteLegal();
            TELEFONO_CONTACTO = "";
            MAIL_CONTACTO = beanProveedor.getEmailRepresentanteLegal().trim();
            DNI_CONTACTO = beanProveedor.getNroDocumRepresentanteLegal().trim();
            DEP_CONTACTO = "0004";
            FUNC_CONTACTO = "RL";

            NOMB_PILA2 = beanProveedor.getNombrePersonaCreditoCobranza();
            NOMB_CONTACTO2 = beanProveedor.getNombrePersonaCreditoCobranza();
            TELEFONO_CONTACTO2 = "";
            MAIL_CONTACTO2 = beanProveedor.getEmailPersonaCreditoCobranza().trim();
            DNI_CONTACTO2 = beanProveedor.getNroDocumPersonaCreditoCobranza().trim();
            DEP_CONTACTO2 =  "0009";
            FUNC_CONTACTO2 =  "09";
        }
        else {
//            grupoEsquemaProveedor="Z4";
            grupoEsquemaProveedor="01";
            viasPago=VIAS_PAGO_E;
//            tipoNif="P0";
            tipoNif="80";
            claseImpuesto="PE";
            if(beanProveedor.getTipoComprobante().getCodigoTipoComprobante().equalsIgnoreCase("FA")){
                ctaAsociada=CASOCIADA_FACTURA;
            }else{
                ctaAsociada=CASOCIADA_RH;
            }
        }


        DIRECCION = direccion1.toUpperCase();
        DIRECCION2 = direccion2.toUpperCase();
        DIRECCION3 = direccion3.toUpperCase();
        DIRECCION4 = direccion4.toUpperCase();
        TELEFONO =  beanProveedor.getTelefono().toUpperCase();
        EMAIL = beanProveedor.getEmail().trim().toUpperCase();
        NUM_IDENT_FISC =  beanProveedor.getRuc().toUpperCase();
        TIPO_NIF =  tipoNif;


        ///Datos de Intelocutor CodigoSap Usuario 1100000045
        FUNC_INTER = "";
        COD_INTER = usuarioSap.trim();
        //////fin//////
        CLASE_IMPUESTO = claseImpuesto;
        RECARGO_EQUIV =  "X";
        PERSONA_FISICA =  "";
        RAMO = "";
        if(tipoProveedor.equalsIgnoreCase("ZNAC")) {

            if(claseImpuesto=="PJ"){
                TIPO_RETEN = "RE";
                INDIC_RETEN =  "R1";

                if(!indAgenteRetencion){
                    WT_SUBJCT =  VALOR_DEFAULT;
                    logger.error("PADRON" + beanProveedor.getCodigoPadron());
                }else{
                    WT_SUBJCT = "";
                }
                TIPO_RETEN =  "D1";
                INDIC_RETEN = "D1";
                WT_SUBJCT = VALOR_DEFAULT;
            }else{

                /*Denisse Indico que cuando se el proveedor es nacional debe de nacer con RE y R1*/

                TIPO_RETEN =  "RE";
                INDIC_RETEN = "R1";
                if(!indAgenteRetencion){
                    WT_SUBJCT = VALOR_DEFAULT;
                    logger.error("PADRON" + beanProveedor.getCodigoPadron());
                }else{
                    WT_SUBJCT = "";
                }

            }
        }else{

            TIPO_RETEN = "NI";
            INDIC_RETEN = "N0";
            WT_SUBJCT = VALOR_DEFAULT;
        }

        String CtasBancarias = "";
        String Moneda = "";
        String nombreTitular = "";
        for ( int i = 0; i < listaCuentaBancaria.size(); i++)
        {

            if (listaCuentaBancaria.get(i).getBanco().getClaveBanco().equals("018") && listaCuentaBancaria.get(i).getClaveControlBanco().equals("03")) {
                Moneda = "PEN1";
            }
            else {
                Moneda = listaCuentaBancaria.get(i).getMoneda().getCodigoMoneda();
            }

            if(listaCuentaBancaria.get(i).getNumeroCuentaCci() != null)
                nombreTitular = listaCuentaBancaria.get(i).getNumeroCuentaCci().trim();

            CtasBancarias +=
                    "<LIFNR>"+Optional.ofNullable(listaCuentaBancaria.get(i).getNumeroCuenta().replace("-",""))+"</LIFNR>\n" ;

        }
        String tramaXMLactualizar = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_ACTUALIZA_PROVEEDOR>\n" +
                "         <PI_LFA1>\n" +
                "            <MANDT>"+100+"</MANDT>\n" +
                "            <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</MANDT>\n" +
                "            <NAME1>"+ Optional.ofNullable(NOMBRE).orElse("") +"</NAME1>\n" +
                "            <NAME2>"+ Optional.ofNullable(NOMBRE2).orElse("") +"</NAME2>\n" +
                "            <NAME3>"+ Optional.ofNullable(NOMBRE2).orElse("") +"</NAME3>\n" +
                "            <SORTL>"+""+"</SORTL>\n" +
                "            <LAND1>"+ Optional.ofNullable(pais.toUpperCase()).orElse("")+"</LAND1>\n" +
                "            <ORT01>"+ Optional.ofNullable(provincia.toUpperCase()).orElse("")+"</ORT01>\n" +
                "            <ORT02>"+ Optional.ofNullable(distrito.toUpperCase()).orElse("")+"</ORT02>\n" +
                "            <PSTLZ>"+Optional.ofNullable(beanProveedor.getCodigoPostal()).orElse("").toUpperCase().trim()+"</PSTLZ>\n" +
                "            <STRAS>"+""+"</STRAS>\n" +
                "            <REGIO>"+ Optional.ofNullable(region.toUpperCase()).orElse("")+"</REGIO>\n" +
                "            <BRSCH>"+Optional.ofNullable(RAMO).orElse("")+"</BRSCH>\n" +
                "            <KTOKK>"+ Optional.ofNullable(beanProveedor.getTipoProveedor().getCodigoSap()).orElse("")+"</KTOKK>\n" +
                "            <SPRAS>"+""+"</SPRAS>\n" +
                "            <TELF1>"+Optional.ofNullable(TELEFONO).orElse("")+"</TELF1>\n" +
                "            <DLGRP>"+""+"</DLGRP>\n" +
                "            <STCD1>"+""+"</STCD1>\n" +
                "            <TAXTYPE>"+""+"</TAXTYPE>\n" +
                "            <ADRNR>"+Optional.ofNullable(DIRECCION).orElse("")+"</ADRNR>\n" +
                "         </PI_LFA1>\n" +
                "         <PI_LFB1>\n" +
                "            <MANDT>"+100+"</MANDT>\n" +
                "            <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</MANDT>\n" +
                "            <BUKRS>"+I_SOCIEDAD+"</BUKRS>\n" +
                "            <AKONT>"+""+"</AKONT>\n" +
                "            <ZWELS>"+Optional.ofNullable(viasPago).orElse("")+"</ZWELS>\n" +
                "            <ZTERM>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</ZTERM>\n" +
                "            <FDGRV>"+""+"</FDGRV>\n" +
                "            <REPRF>"+""+"</REPRF>\n" +
                "         </PI_LFB1>\n" +
                "         <PI_LFM1>\n" +
                "            <MANDT>"+100+"</MANDT>\n" +
                "            <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</MANDT>\n" +
                "            <EKORG>"+Optional.ofNullable(beanProveedor.getCodigoGrupoCompra()).orElse("")+"</EKORG>\n" +
                "            <WAERS>"+Optional.ofNullable(beanProveedor.getMoneda().getCodigoMoneda().trim()).orElse("")+"</WAERS>\n" +
                "            <WEBRE>"+""+"</WEBRE>\n" +
                "            <KALSK>"+Optional.ofNullable(grupoEsquemaProveedor).orElse("")+"</KALSK>\n" +
                "            <KZAUT>"+""+"</KZAUT>\n" +
                "            <EKGRP>"+Optional.ofNullable(beanProveedor.getCodigoGrupoCompra()).orElse("")+"</EKGRP>\n" +
                "            <PLIFZ>"+""+"</PLIFZ>\n" +
                "            <LEBRE>"+""+"</LEBRE>\n" +
                "         </PI_LFM1>\n" +
                "         <T_EMAIL>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT>"+100+"</MANDT>\n" +
                "               <ADDRNUMBER>"+""+"</ADDRNUMBER>\n" +
                "               <PERSNUMBER>"+""+"</PERSNUMBER>\n" +
                "               <CONSNUMBER>"+""+"</CONSNUMBER>\n" +
                "               <SMTP_ADDR>"+Optional.ofNullable(EMAIL).orElse("")+"</SMTP_ADDR>\n" +
                "               <REMARK>"+""+"</REMARK>\n" +
                "            </item>\n" +
                "         </T_EMAIL>\n" +
                "         <T_LFBK>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT>"+100+"</MANDT>\n" +
                "               <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</MANDT>\n" +
                "               <BANKS>"+""+"</BANKS>\n" +
                "               <BANKL>"+""+"</BANKL>\n" +
                "               <BANKN>"+""+"</BANKN>\n" +
                "               <BVTYP>"+""+"</BVTYP>\n" +
                "               <BKONT>"+""+"</BKONT>\n" +
                "               <BKREF>"+""+"</BKREF>\n" +
                "            </item>\n" +
                "         </T_LFBK>\n" +
                "         <T_LFBW>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MANDT>"+100+"</MANDT>\n" +
                "               <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</MANDT>\n" +
                "               <BUKRS>"+I_SOCIEDAD+"</BUKRS>\n" +
                "               <WITHT>"+""+"</WITHT>\n" +
                "               <WT_SUBJCT>"+""+"</WT_SUBJCT>\n" +
                "            </item>\n" +
                "         </T_LFBW>\n" +
                "      </urn:ZMM_ACTUALIZA_PROVEEDOR>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
     /*   String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_CREA_PROVEEDOR>\n" +
                "         <I_CUENTA_BANCARIA> \n" +
                "            <!--Zero or more repetitions:-->\n" +
                CtasBancarias +
                "         </I_CUENTA_BANCARIA>\n" +
                "         <I_DATOS_GENERALES>\n" +
                "            <TRATAMIENTO>"+Optional.ofNullable(TRATAMIENTO).orElse("")+"</TRATAMIENTO>\n" +
                "            <NOMBRE>"+ Optional.ofNullable(NOMBRE).orElse("") +"</NOMBRE>\n" +
                "            <NOMBRE2>"+ Optional.ofNullable(NOMBRE2).orElse("") +"</NOMBRE2>\n" +
                "            <NOMBRE3>"+ Optional.ofNullable(NOMBRE3).orElse("") +"</NOMBRE3>\n" +
                "            <NOMBRE4>"+Optional.ofNullable(NOMBRE4).orElse("")+"</NOMBRE4>\n" +
                "            <CONCEP_BUSQUED>"+Optional.ofNullable(beanProveedor.getRuc()).orElse("")+"</CONCEP_BUSQUED>\n" +
                "            <COD_POSTAL>"+Optional.ofNullable(beanProveedor.getCodigoPostal()).orElse("").toUpperCase().trim()+"</COD_POSTAL>\n" +
                "            <PAIS>"+ Optional.ofNullable(pais.toUpperCase()).orElse("")+"</PAIS>\n" +
                "            <REGION>"+ Optional.ofNullable(region.toUpperCase()).orElse("")+"</REGION>\n" +
                "            <POBLACION>"+ Optional.ofNullable(provincia.toUpperCase()).orElse("")+"</POBLACION>\n" +
                "            <NUM_IDENT_FISC>"+Optional.ofNullable(NUM_IDENT_FISC).orElse("")+"</NUM_IDENT_FISC>\n" +
                "            <NUM_DNI>"+Optional.ofNullable(NUM_DNI).orElse("")+"</NUM_DNI>\n" +
                "            <TIPO_NIF>"+Optional.ofNullable(TIPO_NIF).orElse("")+"</TIPO_NIF>\n" +
                "            <CLASE_IMPUESTO>"+Optional.ofNullable(CLASE_IMPUESTO).orElse("")+"</CLASE_IMPUESTO>\n" +
                "            <RECARGO_EQUIV>"+Optional.ofNullable(RECARGO_EQUIV).orElse("")+"</RECARGO_EQUIV>\n" +
                "            <PERSONA_FISICA>"+Optional.ofNullable(PERSONA_FISICA).orElse("")+"</PERSONA_FISICA>\n" +
                "            <RAMO>"+Optional.ofNullable(RAMO).orElse("")+"</RAMO>\n" +
                "            <TELEFONO>"+Optional.ofNullable(TELEFONO).orElse("")+"</TELEFONO>\n" +
                "            <TELEMOV>"+""+"</TELEMOV>\n" +
                "            <DIRECCION>"+Optional.ofNullable(DIRECCION).orElse("")+"</DIRECCION>\n" +
                "            <DIRECCION2>"+Optional.ofNullable(DIRECCION2).orElse("")+"</DIRECCION2>\n" +
                "            <DIRECCION3>"+Optional.ofNullable(DIRECCION3).orElse("")+"</DIRECCION3>\n" +
                "            <DIRECCION4>"+Optional.ofNullable(DIRECCION4).orElse("")+"</DIRECCION4>\n" +
                "            <NOMB_PILA>"+Optional.ofNullable(NOMB_PILA).orElse("")+"</NOMB_PILA>\n" +
                "            <NOMB_CONTACTO>"+Optional.ofNullable(NOMB_CONTACTO).orElse("")+"</NOMB_CONTACTO>\n" +
                "            <TELEFONO_CONTACTO>"+Optional.ofNullable(TELEFONO_CONTACTO).orElse("")+"</TELEFONO_CONTACTO>\n" +
                "            <DEP_CONTACTO>"+Optional.ofNullable(DEP_CONTACTO).orElse("")+"</DEP_CONTACTO>\n" +
                "            <FUNC_CONTACTO>"+Optional.ofNullable(FUNC_CONTACTO).orElse("")+"</FUNC_CONTACTO>\n" +
                "            <MAIL_CONTACTO>"+Optional.ofNullable(MAIL_CONTACTO).orElse("")+"</MAIL_CONTACTO>\n" +
                "            <DNI_CONTACTO>"+Optional.ofNullable(DNI_CONTACTO).orElse("")+"</DNI_CONTACTO>\n" +
                "            <NOMB_PILA2>"+Optional.ofNullable(NOMB_PILA2).orElse("")+"</NOMB_PILA2>\n" +
                "            <NOMB_CONTACTO2>"+Optional.ofNullable(NOMB_CONTACTO2).orElse("")+"</NOMB_CONTACTO2>\n" +
                "            <TELEFONO_CONTACTO2>"+Optional.ofNullable(TELEFONO_CONTACTO2).orElse("")+"</TELEFONO_CONTACTO2>\n" +
                "            <DEP_CONTACTO2>"+Optional.ofNullable(DEP_CONTACTO2).orElse("")+"</DEP_CONTACTO2>\n" +
                "            <FUNC_CONTACTO2>"+Optional.ofNullable(FUNC_CONTACTO2).orElse("")+"</FUNC_CONTACTO2>\n" +
                "            <MAIL_CONTACTO2>"+Optional.ofNullable(MAIL_CONTACTO2).orElse("")+"</MAIL_CONTACTO2>\n" +
                "            <DNI_CONTACTO2>"+Optional.ofNullable(DNI_CONTACTO2).orElse("")+"</DNI_CONTACTO2>\n" +
                "            <FORMA_COMUNICACION>"+""+"</FORMA_COMUNICACION>\n" +
                "            <EMAIL>"+Optional.ofNullable(EMAIL).orElse("")+"</EMAIL>\n" +
                "            <CORREOS_ADIC>"+""+"</CORREOS_ADIC>\n" +
                "            <DISTRITO>"+ Optional.ofNullable(distrito.toUpperCase()).orElse("")+"</DISTRITO>\n" +
                "            <FECNAC>"+""+"</FECNAC>\n" +
                "            <LUGNAC>"+""+"</LUGNAC>\n" +
                "            <SEXO>"+""+"</SEXO>\n" +
                "            <PROFESION>"+""+"</PROFESION>\n" +
                "            <FUNC_INTER>"+Optional.ofNullable(FUNC_INTER).orElse("")+"</FUNC_INTER>\n" +
                "            <COD_INTER>"+Optional.ofNullable(COD_INTER).orElse("")+"</COD_INTER>\n" +
                "         </I_DATOS_GENERALES>\n" +
                "         <I_DATOS_ORG_COMP>\n" +
                "            <MONEDA_PEDIDO>"+Optional.ofNullable(beanProveedor.getMoneda().getCodigoMoneda().trim()).orElse("")+"</MONEDA_PEDIDO>\n" +
                "            <COND_PAGO>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</COND_PAGO>\n" +
                "            <GRP_ESQUEMA_PROV>"+Optional.ofNullable(grupoEsquemaProveedor).orElse("")+"</GRP_ESQUEMA_PROV>\n" +
                "            <VERIF_FACT_EM>"+VALOR_DEFAULT+"</VERIF_FACT_EM>\n" +
                "            <PED_AUTOMATICO>"+""+"</PED_AUTOMATICO>\n" +
                "            <VER_FACT_REL_SER>"+VALOR_DEFAULT+"</VER_FACT_REL_SER>\n" +
                "            <CONC_BONIF_ESPE>"+""+"</CONC_BONIF_ESPE>\n" +
                "            <CONT_CONFIRMA>"+""+"</CONT_CONFIRMA>\n" +
                "            <LIQ_PPOST>"+""+"</LIQ_PPOST>\n" +
                "            <LIQ_EPOST>"+""+"</LIQ_EPOST>\n" +
                "            <GRUPO_COMPRA>"+Optional.ofNullable(beanProveedor.getCodigoGrupoCompra()).orElse("")+"</GRUPO_COMPRA>\n" +
                "         </I_DATOS_ORG_COMP>\n" +
                "         <I_DATOS_SOCIEDAD>\n" +
                "             <CTA_ASOCIADA>"+Optional.ofNullable(ctaAsociada).orElse("")+"</CTA_ASOCIADA>\n" +
                "            <GRUPO_TESOR>"+""+"</GRUPO_TESOR>\n" +
                "            <COND_PAGO>"+Optional.ofNullable(beanProveedor.getCondicionPago().getCodigoSap()).orElse("")+"</COND_PAGO>\n" +
                "            <GRUPO_TOLER>"+""+"</GRUPO_TOLER>\n" +
                "            <VERIF_FRA_DOB>"+VALOR_DEFAULT+"</VERIF_FRA_DOB>\n" +
                "            <VIA_PAGO>"+Optional.ofNullable(viasPago).orElse("")+"</VIA_PAGO>\n" +
                "         </I_DATOS_SOCIEDAD>\n" +
                "         <I_ESTADO_PROVEEDOR>\n" +
                "            <INICIO_BLOQUEO>"+""+"</INICIO_BLOQUEO>\n" +
                "            <FIN_BLOQUEO>"+""+"</FIN_BLOQUEO>\n" +
                "            <HOMOLOGACION>"+""+"</HOMOLOGACION>\n" +
                "            <CHECK01>"+""+"</CHECK01>\n" +
                "            <CHECK02>"+""+"</CHECK02>\n" +
                "            <CHECK03>"+""+"</CHECK03>\n" +
                "            <CHECK04>"+""+"</CHECK04>\n" +
                "            <CHECK05>"+""+"</CHECK05>\n" +
                "            <CHECK06>"+""+"</CHECK06>\n" +
                "            <CHECK07>"+""+"</CHECK07>\n" +
                "            <CHECK08>"+""+"</CHECK08>\n" +
                "            <CHECK09>"+""+"</CHECK09>\n" +
                "            <CHECK10>"+""+"</CHECK10>\n" +
                "            <SENSIBLE>"+""+"</SENSIBLE>\n" +
                "         </I_ESTADO_PROVEEDOR>\n" +
                "           <I_GRUPO_CTAS>"+ Optional.ofNullable(beanProveedor.getTipoProveedor().getCodigoSap()).orElse("")+"</I_GRUPO_CTAS>\n" +
                "         <I_ORG_COMPRAS>"+""+"</I_ORG_COMPRAS>\n" +
                "         <I_RETENCIONES>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "              <TIPO_RETEN>"+Optional.ofNullable(TIPO_RETEN).orElse("")+"</TIPO_RETEN>\n" +
                "               <INDIC_RETEN>"+Optional.ofNullable(INDIC_RETEN).orElse("")+"</INDIC_RETEN>\n" +
                "               <WT_SUBJCT>"+Optional.ofNullable(WT_SUBJCT).orElse("")+"</WT_SUBJCT>\n" +
                "            </item>\n" +
                "         </I_RETENCIONES>\n" +
                "        <I_SOCIEDAD>"+I_SOCIEDAD+"</I_SOCIEDAD>\n" +
                "      </urn:ZMM_CREA_PROVEEDOR>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";*/

        return tramaXMLactualizar;

    }

    private String armarTramaHomologar(Proveedor beanProveedor,String usuarioSap)
    {


        String tramaXMLactualizar = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_HOMOLOGACION_1>\n" +
                "         <!--Optional:-->\n" +
                "         <IT_HOMOLOG_DATA>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</LIFNR>\n" +
                "               <LAND1>"+""+"</LAND1>\n" +
                "               <NAME1>"+""+"</NAME1>\n" +
                "               <NAME2>"+""+"</NAME2>\n" +
                "               <NAME3>"+""+"</NAME3>\n" +
                "               <NAME4>"+""+"</NAME4>\n" +
                "               <KTOKK>"+""+"</KTOKK>\n" +
                "               <LOEVM>"+""+"</LOEVM>\n" +
                "               <SPERR>"+""+"</SPERR>\n" +
                "               <SPERM>"+""+"</SPERM>\n" +
                "               <STCD1>"+""+"</STCD1>\n" +
                "               <SPERQ>"+""+"</SPERQ>\n" +
                "               <ZZSTAT_HOMO_INT>"+""+"</ZZSTAT_HOMO_INT>\n" +
                "               <ZZTIPO_HOMO_INT>"+""+"</ZZTIPO_HOMO_INT>\n" +
                "               <ZZFECHA_INI_INT>"+""+"</ZZFECHA_INI_INT>\n" +
                "               <ZZFECHA_FIN_INT>"+""+"</ZZFECHA_FIN_INT>\n" +
                "               <ZZSTAT_HOMO_EXT>"+""+"</ZZSTAT_HOMO_EXT>\n" +
                "               <ZZTIPO_HOMO_EXT>"+""+"</ZZTIPO_HOMO_EXT>\n" +
                "               <ZZFECHA_INI_EXT>"+""+"</ZZFECHA_INI_EXT>\n" +
                "               <ZZFECHA_FIN_EXT>"+""+"</ZZFECHA_FIN_EXT>\n" +
                "            </item>\n" +
                "         </IT_HOMOLOG_DATA>\n" +
                "         <PI_LIFNR>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <LIFNR>"+ Optional.ofNullable(beanProveedor.getAcreedorCodigoSap()).orElse("") +"</LIFNR>\n" +
                "              \n" +
                "            </item>\n" +
                "           \n" +
                "         </PI_LIFNR>\n" +
                "              </urn:ZMM_HOMOLOGACION_1>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXMLactualizar;

    }

    public static String separarCadenas(String cadena,String aux){
        String [] razonPartida=cadena.split(" ");
        int longRazonSocial=razonPartida.length;
        String nombres="";
        String apellidos="";
        if(longRazonSocial<=2){
            nombres =razonPartida[0];
            apellidos=razonPartida[longRazonSocial-1];
        }else if(longRazonSocial==3){
            nombres =razonPartida[0];
            apellidos=razonPartida[1]+aux+razonPartida[2];
        }else{
            if(longRazonSocial==4){
                nombres =razonPartida[0]+aux+razonPartida[1];
                apellidos=razonPartida[2]+aux+razonPartida[3];
            }else{
                nombres =razonPartida[0]+aux+razonPartida[1]+aux+razonPartida[2];
                apellidos=razonPartida[3]+aux+razonPartida[4];
            }
        }
        return nombres+","+apellidos;
    }
    
    public static String longitudCadenaContactos(String cadena,int l){

        String cadenaReturn="";
        if(cadena.length()<=l){
            cadenaReturn=cadena;
        }else{
            if(cadena.length()>l){
                cadenaReturn=cadena.substring(0,34);
            }
        }


        return cadenaReturn;
    }

    @Override
    public ProveedorResponseRFC grabarListaProveedorSAP(List<Proveedor> listaProveedorPotencial,String usuarioSap) throws Exception {

        ProveedorResponseRFC beanRetorno = new ProveedorResponseRFC();
        beanRetorno.setTieneError(false);
        SapLog sapLogRetorno = new SapLog();
        List<ProveedorRFCResponseDto> listaProveedorSAPResult = new ArrayList<ProveedorRFCResponseDto>();

        /////GRABAR
        sapLogRetorno.setCode(WebServiceConstant.RESPUESTA_OK);
        sapLogRetorno.setMesaj(WebServiceConstant.MENSAJE_VACIO);
        for (Proveedor beanProveedor : listaProveedorPotencial) {

            Optional<String> oCodigoSap = Optional.ofNullable(beanProveedor.getAcreedorCodigoSap())
                    .filter(codigo -> !codigo.isEmpty());
            if (!oCodigoSap.isPresent()) {
                logger.error("INGRESANDO A CREAR");
                ProveedorRFCResponseDto proveedorRFCResponseDtoResult = new ProveedorRFCResponseDto();
                try {
                    proveedorRFCResponseDtoResult = this.grabarProveedor(beanProveedor.getIdProveedor(),usuarioSap);
                    logger.error("PROVEEDOR ACREEDOR"+proveedorRFCResponseDtoResult.getNroAcreedor());

                    /* Respuesta SAP */
                    if (StringUtils.isNotBlank(proveedorRFCResponseDtoResult.getNroAcreedor())) {
                        logger.error("CREANDO");
                        beanProveedor.setAcreedorCodigoSap(proveedorRFCResponseDtoResult.getNroAcreedor());
                          this.proveedorRepository.updateAcreedorCodigoSAP(
                                    proveedorRFCResponseDtoResult.getNroAcreedor(),
                                   beanProveedor.getIdProveedor());
                        logger.error("GrabarSAP PROVEEDOR EXITO CODIGO SAP: " + proveedorRFCResponseDtoResult.getNroAcreedor());
                    } else {
                        beanRetorno.setTieneError(true);
                        logger.error("GrabarSAP PROVEEDOR ERROR: "  );
                    }
                } catch (Exception e) {
                    logger.error("Ingresando grabarProveedorSAP  error: " );
                    logger.error("GrabarSAP PROVEEDOR Exception: " + e.getMessage());
                    beanRetorno.setTieneError(true);
                }
                logger.error("BEAN PROVEEDOR SAP" + beanProveedor.getAcreedorCodigoSap());
                proveedorRFCResponseDtoResult.setProveedorSap(beanProveedor);
                listaProveedorSAPResult.add(proveedorRFCResponseDtoResult);
            }
        }
        beanRetorno.setSapLog(sapLogRetorno);
        beanRetorno.setListaProveedorSAPResult(listaProveedorSAPResult);
        logger.error("Ingresando grabarProveedorSAP DEV FIN: ");
        return beanRetorno;
    }

    @Override
    public ProveedorRFCResponseDto grabarUnicoProveedorSAP(Proveedor proveedorPotencial,String usuarioSap) throws Exception {
        ProveedorRFCResponseDto proveedorRFCResponseDtoResult = new ProveedorRFCResponseDto();
        List<SapLog> sapLogList = new ArrayList<>();
        Optional<String> oCodigoSap = Optional.ofNullable(proveedorPotencial.getAcreedorCodigoSap())
                .filter(codigo -> !codigo.isEmpty());

        if (!oCodigoSap.isPresent()) {
            logger.error("RFC PROVEEDOR : INGRESANDO A CREAR");
            try {
                proveedorRFCResponseDtoResult = this.grabarProveedor(proveedorPotencial.getIdProveedor(),usuarioSap);
                logger.error("RFC PROVEEDOR :INGRESANDO TRY PROVEEDOR");
                String numeroAcreedor = Optional.ofNullable(proveedorRFCResponseDtoResult.getNroAcreedor()).orElse("");

                logger.error("RFC PROVEEDOR :PROVEEDOR ACREEDOR: " + (!numeroAcreedor.isEmpty() ? numeroAcreedor : "--"));

                    /* Respuesta SAP */
                if (StringUtils.isNotBlank(numeroAcreedor)) {
                    logger.error("RFC PROVEEDOR :ACTUALIZANDO");
                    proveedorPotencial.setAcreedorCodigoSap(numeroAcreedor);
                    this.proveedorRepository.updateAcreedorCodigoSAP(
                            proveedorRFCResponseDtoResult.getNroAcreedor(),
                            proveedorPotencial.getIdProveedor());
                    logger.error("RFC PROVEEDOR :GrabarSAP PROVEEDOR EXITO CODIGO SAP: " + numeroAcreedor);
                }
                else {
                    logger.error("RFC PROVEEDOR :GrabarSAP PROVEEDOR ERROR");
                }

                sapLogList = proveedorRFCResponseDtoResult.getListasapLog();
                logger.error("RFC PROVEEDOR","GrabarSAP LISTA RESPUESTA RFC: " + (sapLogList != null ? sapLogList.toString() : "--"));
            } catch (Exception e) {
                String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
                logger.error("RFC PROVEEDOR","GrabarSAP PROVEEDOR Exception: " + error);
                throw new Exception(error);
            }
            logger.error("RFC PROVEEDOR","BEAN PROVEEDOR SAP" + proveedorPotencial.getAcreedorCodigoSap());
//            proveedorRFCResponseDtoResult.setProveedorSap(proveedorPotencial);
        }
        else{
            String errorMessage = "Proveedor con RUC '" + proveedorPotencial.getRuc() + "' ya tiene numeroAcreedor: " + proveedorPotencial.getAcreedorCodigoSap();
            logger.error("RFC PROVEEDOR","ERROR PARA CREAR PROVEEDOR SAP: " + errorMessage);
            SapLog errorLog = new SapLog("ERROR", errorMessage);
            sapLogList.add(errorLog);
            proveedorRFCResponseDtoResult.setListasapLog(sapLogList);
        }

        return proveedorRFCResponseDtoResult;
    }

    /*SAP 4 HANA  Grabar Proveedor*/

    @Override
    public ProveedorRFCResponseDto grabarProveedor(Integer idProveedor,String usuarioSap) throws Exception {
        
        ProveedorRFCResponseDto proveedorRFCResponseDto = new ProveedorRFCResponseDto();
        LogTransaccion logTransaccion = new LogTransaccion();
        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");

        Proveedor proveedorParam =  this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        List<ProveedorCuentaBancaria> listaCuentaBancaria= this.
                proveedorCuentaBancoRepository.getListCuentaBancariaByIdProveedor(idProveedor);


        /*PASOS */
        // 1. BusinessPartner
        Map<String, String> resultadoBP = CrearBP_S4Public(proveedorParam, listaCuentaBancaria);
        String statusBP = resultadoBP.get("status");
        String msgBP = resultadoBP.get("msg");

        if(statusBP.equals("1")){
            String idaddres = resultadoBP.get("idaddres");
            String token = resultadoBP.get("token");

            // 2. to_SupplierCompany
            Map<String, String> resultadoSupplierCompanyBP = CrearSupplierCompanyBP_S4Public(proveedorParam, msgBP, token );
            String statusSupplierCompanyBP = resultadoSupplierCompanyBP.get("status");
            String msgSupplierCompanyBP = resultadoSupplierCompanyBP.get("msg");

            if(statusSupplierCompanyBP.equals("1")){
                
                // 3. to_BuPaIndustry
                Map<String, String> resultadoBuPaIndustryBP = CrearMobileEmailBP_S4Public(proveedorParam, msgBP, idaddres);
                String statusBuPaIndustryBP = resultadoBuPaIndustryBP.get("status");
                String msgEmailBP = resultadoBuPaIndustryBP.get("msg");

                if(statusBuPaIndustryBP.equals("1")){
                    
                    // 4. to_MobilePhoneNumber
                    Map<String, String> resultadoMobilePhoneNumberBP = CrearMobilePhoneNumberBP_S4Public(proveedorParam, msgBP, idaddres);
                    String statusMobilePhoneNumberBP = resultadoMobilePhoneNumberBP.get("status");
                    String msgMobilePhoneNumberBP = resultadoMobilePhoneNumberBP.get("msg");
                    
                    if(statusMobilePhoneNumberBP.equals("1")){

                      

                        
                        // 8. to_BusinessPartnerRole
                        Map<String, String> resultadoBusinessPartnerRoleBP = CrearBusinessPartnerRoleBP_S4Public(proveedorParam, msgBP, idaddres);
                        String statusBusinessPartnerRoleBP = resultadoBusinessPartnerRoleBP.get("status");
                        String msgBusinessPartnerRoleBP = resultadoBusinessPartnerRoleBP.get("msg");

                        if(statusBusinessPartnerRoleBP.equals("1")){
                            
                            // 9. to_SupplierPurchasingOrg
                            Map<String, String> resultadoSupplierPurchasingOrg = CrearBusinessSupplierPurchasingOrgBP_S4Public(proveedorParam, msgBP, idaddres);
                            String statusSupplierPurchasingOrg = resultadoSupplierPurchasingOrg.get("status");
                            String msgSupplierPurchasingOrg = resultadoSupplierPurchasingOrg.get("msg");

                            if(statusSupplierPurchasingOrg.equals("1")){

                                // 10. to_SupplierWithHoldingTax
                                
                                Map<String, String> resultadoSupplierWithHoldingTaxBP = CrearBusinessSupplierWithHoldingTaxBP_S4Public(proveedorParam, msgBP, idaddres);
                                String statusSupplierWithHoldingTaxBP = resultadoSupplierWithHoldingTaxBP.get("status");
                                String msgSupplierWithHoldingTaxBP = resultadoSupplierWithHoldingTaxBP.get("msg");

                                if(statusSupplierWithHoldingTaxBP.equals("1")){

                                    // 11. A_Supplier('1000003')
                                    Map<String, String> resultadoA_SupplierBP = CrearBusinessA_SupplierBP_S4Public(proveedorParam, msgBP, idaddres);
                                    String statusA_SupplierBP = resultadoA_SupplierBP.get("status");
                                    String msgA_SupplierBP = resultadoA_SupplierBP.get("msg");
    
                                    if(statusA_SupplierBP.equals("1")){


                                        Map<String, String> resultadoA_ContactoBP = CrearContacto_S4Public(proveedorParam, msgBP);
                                        String statusA_ContactoBP = resultadoA_ContactoBP.get("status");
                                        String msgA_ContactoBP = resultadoA_ContactoBP.get("msg");
                                        if(statusA_ContactoBP.equals("1")){

                                        }else{
                                            throw new PortalException(msgA_ContactoBP);
                                        } 
                                        // 12. A_BusinessPartner Pesona contacto
                                        // 5. to_BusinessPartnerContact
                                        // 6. to_BPRelationship
                                        // 7. BusinessPartnerPerson
                                        
                                        
                                        

                                    }else{
                                        throw new PortalException(msgA_SupplierBP);
                                    } 

                                }else{
                                    throw new PortalException("SupplierWithHoldingTaxBP : " +msgSupplierWithHoldingTaxBP);
                                }  

                            }else{
                                throw new PortalException("SupplierPurchasingOrg : " +msgSupplierPurchasingOrg);
                            }    


                        }else{
                            throw new PortalException("BusinessPartnerRoleBP : " +msgBusinessPartnerRoleBP);
                        }                        

                    }else{
                        throw new PortalException("MobilePhoneNumberBP : " +msgMobilePhoneNumberBP);
                    }

                }else{
                    throw new PortalException("EmailBP : " +msgEmailBP);
                }
            }
            else{
                throw new PortalException("SupplierCompanyBP BP : " + msgSupplierCompanyBP);
            }

        }else{

            /*Error*/
            throw new PortalException(msgBP);
        }
        
        String codigoAcrededor = msgBP;
        ProveedorDto proveedorDto = new ProveedorDto();
        proveedorParam.setAcreedorCodigoSap(codigoAcrededor);
        proveedorRFCResponseDto.setNroAcreedor(codigoAcrededor);
        proveedorRFCResponseDto.setProveedorSap(proveedorParam);
        proveedorRFCResponseDto.setListasapLog(null);
        proveedorRFCResponseDto.setProveedorDto(proveedorDto);
        return proveedorRFCResponseDto;
    }

    public String construirJsonBodyBP(Proveedor prov, List<ProveedorCuentaBancaria> listCtaBancaria) {
        
        String[] words = prov.getRazonSocial().split(" ");
        StringBuilder  firstPart = new StringBuilder();
        StringBuilder  secondPart = new StringBuilder();
        int maxLength = 40;

        for (String word : words) {
            // Verificar si la palabra cabe en la primera parte
            if (firstPart.length() + word.length() + 1 <= maxLength || firstPart.length() == 0) {
                if (firstPart.length() > 0) {
                    firstPart.append(" ");
                }
                firstPart.append(word);
            } else {
                // Si no cabe en la primera parte, verificar que la segunda parte no supere el límite
                if (secondPart.length() + word.length() + 1 <= maxLength || secondPart.length() == 0) {
                    if (secondPart.length() > 0) {
                        secondPart.append(" ");
                    }
                    secondPart.append(word);
                } else {
                    // Si la segunda parte ya alcanzó el límite, truncar y salir
                    break;
                }
            }
        }

        String TipoPersona = prov.getTipoPersona();
        String Ruc = prov.getRuc();
        String pais = Optional.ofNullable(prov.getPais())
            .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
            .map(String::trim).orElse("");

        String region = Optional.ofNullable(prov.getRegion())
            .map(tp -> Optional.ofNullable(tp.getCodigoUbigeoSap()).orElse(""))
            .map(String::trim).orElse("");

        int IsNaturalPerson = "J".equals(TipoPersona) ? 0 : "N".equals(TipoPersona) ? 1 : -1;
        
        String direccionFiscal = prov.getDireccionFiscal();
        String direccionFinal = direccionFiscal.length() > 40 
            ? direccionFiscal.substring(0, 40) 
            : direccionFiscal;

        String json = 
            "{\n" +
            "   \"OrganizationBPName1\": \"" + firstPart.toString() + "\",\n" +
            "   \"OrganizationBPName2\": \"" + secondPart.toString() + "\",\n" +
            "   \"BusinessPartnerCategory\": \"" + 2 + "\",\n" +
            //"   \"IsNaturalPerson\": \"" + IsNaturalPerson + "\",\n" +
            "   \"SearchTerm1\": \"" + Ruc + "\",\n" +
            //"   \"NameCountry\": \"" + pais + "\",\n" +
            "   \"Language\": \"ES\",\n" +
            "   \"to_BusinessPartnerAddress\": [\n" +
            "      {\n" +
            "         \"StreetName\": \"" + direccionFinal + "\",\n" +
            //"         \"EmailAddress\": \"" + prov.getEmail() + "\",\n" +
            "         \"HouseNumber\": \"\",\n" +
            "         \"District\": \"" + prov.getDistrito().getDescripcion() + "\",\n" + //prov.getDistrito().getDescripcion()
            "         \"Country\": \"" + prov.getPais().getCodigoUbigeoSap() + "\",\n" + //prov.getPais().getCodigoUbigeoSap()
            "         \"Region\": \"" + prov.getRegion().getCodigoUbigeoSap() + "\",\n" + //prov.getRegion().getDescripcion()
            "         \"PostalCode\": \"" + "+51" + "\",\n" + 
            "         \"Language\": \"" + "ES" + "\",\n" + //Idioma Espanol
            "         \"CityName\": \"" + prov.getProvincia().getDescripcion()  + "\",\n" + // prov.getRegion().getDescripcion() 
            "         \"HouseNumberSupplementText\": \"" + prov.getDistrito().getCodigoUbigeoSap() + "\"\n" + /*CODIGO UBIGEO*/ //prov.getCodigoUbigeoSap
            //"         \"ValidityEndDate\": \"/Date(1882041600000)/\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"to_BusinessPartnerRole\": [\n" +
            "      {\n" +
            "         \"BusinessPartnerRole\": \"FLVN01\",\n" +
            "         \"ValidFrom\": \"/Date(1728516567000)/\"\n" +
            //"         \"ValidTo\": \"/Date(1748924150000)/\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"to_BusinessPartnerTax\": [\n" +
            "      {\n" +
            "         \"BPTaxType\": \"PE1\",\n" +
            "         \"BPTaxNumber\": \"" + prov.getRuc() + "\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"to_BusinessPartnerBank\": [\n";
    
            
        for (int i = 0; i < listCtaBancaria.size(); i++) {

            ProveedorCuentaBancaria cuenta = listCtaBancaria.get(i);
            String CCI = cuenta.getNumeroCuentaCci();

            if(CCI != null){
                CCI = CCI.replace("-", "");
            }else
            {
                CCI = ""; 
            }

            json += 
                "      {\n" +
                "         \"BankCountryKey\": \"PE\",\n" +
                "         \"BankIdentification\": \"" + String.valueOf(cuenta.getIdCuenta()) + "\",\n" +
                "         \"BankNumber\": \"" + String.valueOf(cuenta.getBanco().getClaveBanco()) + "\",\n" +
                "         \"BankAccount\": \"" + cuenta.getNumeroCuenta().replace("-", "") + "\",\n" +
                "         \"BankControlKey\": \"" + cuenta.getClaveControlBanco() + "\",\n" + //CS
                "         \"BankAccountReferenceText\": \"" + CCI + "\",\n" +
                "         \"BankAccountHolderName\": \"" + firstPart.toString() + "\",\n" +
                "         \"BankAccountName\":  \"" + listCtaBancaria.get(0).getMoneda().getCodigoMoneda()  + "\"\n" +
                "      }";
            if (i < listCtaBancaria.size() - 1) {
                json += ",";
            }
            json += "\n";
        }
    
        json += "   ]\n" +
                "}\n";
    
        return json;
    }
    
    private Map<String, String> CrearBP_S4Public(Proveedor prov, List<ProveedorCuentaBancaria> listCtaBancaria)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap +"/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            String body = construirJsonBodyBP(prov,listCtaBancaria);
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {

                String responseBody = postResponse_pk.body().string();
                // Analizar el JSON y extraer BusinessPartner
                JSONObject jsonObject = new JSONObject(responseBody);
                String businessPartner =  jsonObject.getJSONObject("d").getString("BusinessPartner");
                String idaddres =  "";

                // Analizar el JSON y extraer AddressID
                
                JSONArray resultsArray = jsonObject
                        .getJSONObject("d")
                        .getJSONObject("to_BusinessPartnerAddress")
                        .getJSONArray("results");

                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    idaddres = firstResult.getString("AddressID");
                }
                

                System.out.println("Creación Correcta");
                resultMap.put("idaddres", idaddres);
                resultMap.put("msg", businessPartner);
                resultMap.put("status", "1");
                resultMap.put("token", csrfToken_pk);
                return resultMap;

            } else {
                String responseBody = postResponse_pk.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
    
                // Navegar a través de las claves hasta obtener el valor del mensaje
                if (jsonObject.has("error")) {
                    JSONObject errorObject = jsonObject.getJSONObject("error");
                    if (errorObject.has("message")) {
                        JSONObject messageObject = errorObject.getJSONObject("message");
                        if (messageObject.has("value")) {
                            String mensaje = messageObject.getString("value");
                            System.out.println("Mensaje: " + mensaje);
                            resultMap.put("status", "0");
                            resultMap.put("msg", mensaje);
                            return resultMap;

                        } else {
                            
                            System.out.println("El campo 'value' no está presente en el mensaje.");
                            resultMap.put("status", "0");
                            resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner");
                            return resultMap;
                        }
                    } else {
                        System.out.println("El campo 'message' no está presente en el error.");
                        resultMap.put("status", "0");
                        resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner");
                        return resultMap;
                    }
                } else {
                    System.out.println("El campo 'error' no está presente en la respuesta.");
                    resultMap.put("status", "0");
                    resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner");
                    return resultMap;
                }

            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
    
    public String construirJsonBodySupplierCompanyBP(Proveedor prov) {
        String json = "{\n" +
            "   \"CompanyCode\": \"P100\",\n" +
            "   \"ReconciliationAccount\": \"21100100\",\n" +
            //"   \"CashPlanningGroup\": \"A4\",\n" +
            "   \"PaymentTerms\": \"" + String.valueOf(prov.getCondicionPago().getCodigoSap()) + "\",\n" +
            //"   \"APARToleranceGroup\": \"CUS1\",\n" +
            "   \"PaymentMethodsList\": \"T\",\n" +
            "   \"Currency\": \"PEN\"\n" +
            "}";

        return json;

    }

    private Map<String, String> CrearSupplierCompanyBP_S4Public(Proveedor prov, String codigoBP, String token)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier('" + codigoBP + "')/to_SupplierCompany";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodySupplierCompanyBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }

    public String construirJsonBodyBuPaIndustryBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n")
            .append("   \"IndustrySector\": \"").append("24").append("\",\n")
            .append("   \"IndustrySystemType\": \"").append("0001").append("\"\n")
            .append("}");

        return jsonBuilder.toString();
    }

    private Map<String, String> CrearBuPaIndustryBP_S4Public(Proveedor prov, String codigoBP)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap +  "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('" + codigoBP + "')/to_BuPaIndustry";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodyBuPaIndustryBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }

    public String construirJsonBodyMobilePhoneNumberBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n")
            .append("   \"PhoneNumber\": \"").append(prov.getCelular()).append("\"\n")
            .append("}");

        return jsonBuilder.toString();
    }

    private Map<String, String> CrearMobilePhoneNumberBP_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress(BusinessPartner='" + codigoBP + "',AddressID='" + idaddres + "')/to_MobilePhoneNumber";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodyMobilePhoneNumberBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }

    public String construirJsonBodyEmailBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n")
            .append("   \"EmailAddress\": \"").append(prov.getEmail()).append("\"\n")
            .append("}");

        return jsonBuilder.toString();
    }

    private Map<String, String> CrearMobileEmailBP_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress(BusinessPartner='" + codigoBP + "',AddressID='" + idaddres + "')/to_EmailAddress";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodyEmailBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
    
    private Map<String, String> ObtenerBPRelationship_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('" + codigoBP + "')/to_BPRelationship";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodyMobilePhoneNumberBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");

                String responseBody = postResponse_pk.body().string();
                // Analizar el JSON y extraer BusinessPartner
                JSONObject jsonObject = new JSONObject(responseBody);
                //String businessPartner =  jsonObject.getJSONObject("d").getString("BusinessPartner");

                String BusinessPartner2 = "";
                JSONArray resultsArray = jsonObject
                .getJSONObject("d")
                .getJSONObject("to_BusinessPartnerAddress")
                .getJSONArray("results");

                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    BusinessPartner2 = firstResult.getString("BusinessPartner2");
                }
                
                resultMap.put("BP2", BusinessPartner2);
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
    
    public String construirJsonBodyBusinessPartnerRoleBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n")
            .append("   \"BusinessPartnerRole\": \"").append("FLVN00").append("\",\n")
            .append("         \"ValidFrom\": \"/Date(").append("1728516567000").append(")/\",\n")
            .append("         \"ValidTo\": \"/Date(").append("1748924150000").append(")/\"\n")
            .append("}");

        return jsonBuilder.toString();
    }

    private Map<String, String> CrearBusinessPartnerRoleBP_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('" + codigoBP + "')/to_BusinessPartnerRole";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodyBusinessPartnerRoleBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
 
    public String construirJsonBodySupplierPurchasingOrgBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n")
            .append("   \"PurchasingOrganization\": \"").append("P100").append("\",\n")
            .append("   \"InvoiceIsGoodsReceiptBased\": ").append(true).append(",\n")
            .append("   \"PurOrdAutoGenerationIsAllowed\": ").append(false).append(",\n")
            .append("   \"PaymentTerms\": \"").append("NT60").append("\",\n")
            .append("   \"PurchaseOrderCurrency\": \"").append("PEN").append("\",\n")
            .append("   \"CalculationSchemaGroupCode\": \"").append("01").append("\"\n")
            .append("}");

        return jsonBuilder.toString();
    }

    private Map<String, String> CrearBusinessSupplierPurchasingOrgBP_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap +  "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier('" + codigoBP + "')/to_SupplierPurchasingOrg ";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodySupplierPurchasingOrgBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
 
    public String construirJsonBodySupplierWithHoldingTaxBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        String idParametroDetraccion = prov.getIndDetraccion();
        
        Parametro parametroDetraccion = parametroRepository.getById(Integer.parseInt(idParametroDetraccion));
        String tipo = parametroDetraccion.getValor();
        String codigo = parametroDetraccion.getCodigo();
        
        jsonBuilder.append("{\n")
            .append("   \"WithholdingTaxType\": \"").append(tipo).append("\",\n")
            .append("   \"IsWithholdingTaxSubject\": ").append(true).append(",\n")
            .append("   \"WithholdingTaxCode\": \"").append(codigo).append("\"\n")
            .append("}");

        return jsonBuilder.toString();
        
    }

    private Map<String, String> CrearBusinessSupplierWithHoldingTaxBP_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            if(prov.getIndDetraccion() == null){
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "No tiene asignado Detraccion");
                return resultMap;
            }

            if(prov.getIndDetraccion().equals("")){
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "No tiene asignado Detraccion");
                return resultMap;
            }

            String url_sap = urlSap +  "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_SupplierCompany(Supplier='" + codigoBP + "',CompanyCode='P100')/to_SupplierWithHoldingTax";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodySupplierWithHoldingTaxBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                
                String responseBody = postResponse_pk.body().string();
                //JSONObject jsonObject = new JSONObject(responseBody);

                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "OK");
                return resultMap;

            } else {

                String responseBody = postResponse_pk.body().string();
                //JSONObject jsonObject = new JSONObject(responseBody);

                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
 
    public String construirJsonBodyA_SupplierBP(Proveedor prov) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{\n")
            .append("   \"d\": {\n")
            .append("      \"ResponsibleType\": \"").append("PJ").append("\"\n")
            .append("   }\n")
            .append("}");

        return jsonBuilder.toString();
    }

    private Map<String, String> CrearBusinessA_SupplierBP_S4Public(Proveedor prov, String codigoBP, String idaddres)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_Supplier('" + codigoBP + "')";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, construirJsonBodyA_SupplierBP(prov));

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .patch(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {
                System.out.println("Creación Correcta");
                resultMap.put("status", "1");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;

            } else {
                System.out.println("Error al consumir servicio");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }

    /* Contacto */
    public String construirJsonBodyContactoBP(Proveedor prov) {
        
        String businessPartnerCategory = "1";
        String businessPartnerGrouping = "BP02";
        String firstName = "Bussiness";
        String lastName = "";
        String nameCountry = "PE";
        String language = "ES";
        String businessPartnerRole = "BUP001";
        String validFrom = "/Date(1728516567000)/";
        String validTo = "/Date(1748924150000)/";

        String nombreCompleto = prov.getNombrePersonaCompra();

        // Dividir el texto por espacios
        String[] palabras = nombreCompleto.split("\\s+", 2);

        // Asignar la primera palabra y el resto
        String primeraPalabra = palabras[0];
        String restoPalabras = palabras.length > 1 ? palabras[1] : primeraPalabra;

        // Construir el JSON como String
        String json = "{\n" +
            "    \"BusinessPartnerCategory\": \"" + businessPartnerCategory + "\",\n" +
            "    \"BusinessPartnerGrouping\": \"" + businessPartnerGrouping + "\",\n" +
            "    \"FirstName\": \"" + primeraPalabra + "\",\n" +
            "    \"LastName\": \"" + restoPalabras + "\",\n" +
            "    \"NameCountry\": \"" + nameCountry + "\",\n" +
            "    \"Language\": \"" + language + "\",\n" +
            "    \"to_BusinessPartnerRole\": [\n" +
            "        {\n" +
            "            \"BusinessPartnerRole\": \"" + businessPartnerRole + "\",\n" +
            "            \"ValidFrom\": \"" + validFrom + "\",\n" +
            "            \"ValidTo\": \"" + validTo + "\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    
        return json;
    }
    
    private Map<String, String> CrearContacto_S4Public(Proveedor prov, String codigoBP)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            String body = construirJsonBodyContactoBP(prov);
            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            /*Variable*/
            String businessPartnerContacto = "";

            if (postResponse_pk.isSuccessful()) {

                String responseBody = postResponse_pk.body().string();
                // Analizar el JSON y extraer BusinessPartner
                JSONObject jsonObject = new JSONObject(responseBody);
                String businessPartner =  jsonObject.getJSONObject("d").getString("BusinessPartner");
                String idaddres =  "";

                // Analizar el JSON y extraer AddressID
                
                /*JSONArray resultsArray = jsonObject
                        .getJSONObject("d")
                        .getJSONObject("to_BusinessPartnerAddress")
                        .getJSONArray("results");

                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    idaddres = firstResult.getString("AddressID");
                }*/
                
                businessPartnerContacto = businessPartner;

                /*System.out.println("Creación Correcta");
                resultMap.put("idaddres", idaddres);
                resultMap.put("msg", businessPartner);
                resultMap.put("status", "1");
                resultMap.put("token", csrfToken_pk);*/
                //return resultMap;

            } else {
                String responseBody = postResponse_pk.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
    
                // Navegar a través de las claves hasta obtener el valor del mensaje
                if (jsonObject.has("error")) {
                    JSONObject errorObject = jsonObject.getJSONObject("error");
                    if (errorObject.has("message")) {
                        JSONObject messageObject = errorObject.getJSONObject("message");
                        if (messageObject.has("value")) {
                            String mensaje = messageObject.getString("value");
                            System.out.println("Mensaje: " + mensaje);
                            resultMap.put("status", "0");
                            resultMap.put("msg", mensaje);
                            return resultMap;

                        } else {
                            
                            System.out.println("El campo 'value' no está presente en el mensaje.");
                            resultMap.put("status", "0");
                            resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner Contacto");
                            return resultMap;
                        }
                    } else {
                        System.out.println("El campo 'message' no está presente en el error.");
                        resultMap.put("status", "0");
                        resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner Contacto");
                        return resultMap;
                    }
                } else {
                    System.out.println("El campo 'error' no está presente en la respuesta.");
                    resultMap.put("status", "0");
                    resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner Contacto");
                    return resultMap;
                }

            }


            /*Llamamos al servicio para Asignar */
            String body_asig = "{\"BusinessPartnerPerson\": \"" + businessPartnerContacto + "\"}";
            String url_sap_asig = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('" + codigoBP + "')/to_BusinessPartnerContact";
            okhttp3.MediaType mediaType_pk_asig = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk_asig = okhttp3.RequestBody.create(mediaType_pk_asig, body_asig);

            Request postRequest_pk_asig = new Request.Builder()
                    .url(url_sap_asig)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .post(body_pk_asig)
                    .build();

            Response postResponse_pk_asig = client.newCall(postRequest_pk_asig).execute();

            if (!postResponse_pk_asig.isSuccessful()) {
                System.out.println("ERROR Servicio Asignación de Contacto");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner Contacto");
                return resultMap;
            }
            

            /*Obtenemos el ID del relación */

            String url_sap_consulta = urlSap +  "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('" + codigoBP + "')/to_BPRelationship";
            
            String relationshipNumber = "";

            Request postRequest_consulta = new Request.Builder()
                    .url(url_sap_consulta)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .get()
                    .build();

            Response postResponse_consulta = client.newCall(postRequest_consulta).execute();

            if (postResponse_consulta.isSuccessful()) {

                /*Obtenemos IdAddress */
                String responseBody = postResponse_consulta.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray results = jsonResponse.getJSONObject("d").getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    if (result.getString("BusinessPartner2").equals(businessPartnerContacto)) {
                        relationshipNumber = result.getString("RelationshipNumber");
                        break; // Terminar búsqueda al encontrar el valor
                    }
                }
            } else {
                System.out.println("No se pudo encontrar relationshipNumber.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner/to_BPRelationship");
                return resultMap;
            }


            /* Registramos datos de la persona de contacto*/

            String body_setDatos = "{\n" +
            "    \"d\": {\n" +
            "        \"ContactPersonFunction\": \"" + "0003" + "\",\n" +
            "        \"PhoneNumber\": \"" + prov.getCelularPersonaCompra() + "\",\n" +
            "        \"EmailAddress\": \"" + prov.getEmailPersonaCompra() + "\"\n" +
            "    }\n" +
            "}";

            String url_sap_setDatos = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BPContactToFuncAndDept(RelationshipNumber='" + relationshipNumber + "',BusinessPartnerCompany='"+  codigoBP +"',BusinessPartnerPerson='" + businessPartnerContacto + "',ValidityEndDate=datetime'9999-12-31T00%3A00')";
            okhttp3.MediaType mediaType_pk_setDatos = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk_setDatos = okhttp3.RequestBody.create(mediaType_pk_setDatos, body_setDatos);

            Request postRequest_pk_setDatos = new Request.Builder()
                    .url(url_sap_setDatos)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .patch(body_pk_setDatos)
                    .build();

            Response postResponse_pk_setDatos = client.newCall(postRequest_pk_setDatos).execute();

            if (!postResponse_pk_setDatos.isSuccessful()) {
                System.out.println("ERROR Servicio Seteo de datos de contacto");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner Contacto");
                return resultMap;
            }

            System.out.println("Creación Correcta de BP");
            resultMap.put("idaddres", "");
            resultMap.put("msg", businessPartnerContacto);
            resultMap.put("status", "1");
            resultMap.put("token", csrfToken_pk);
            return resultMap;

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP CONTACTO");
            resultMap.put("status", "0");
            return resultMap;
        }
    }


    @Override
    public ProveedorRFCResponseDto actualizarProveedor(Integer idProveedor,String usuarioSap) throws Exception {
        ProveedorRFCResponseDto proveedorRFCResponseDto = new ProveedorRFCResponseDto();

        logger.error("RFC PROVEEDOR","JCO DESTINATION MANAGER Exe");


        ///Obtener Bean Proveedor
        Proveedor proveedorParam =  this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        List<ProveedorCuentaBancaria> listaCuentaBancaria= this.
                proveedorCuentaBancoRepository.getListCuentaBancariaByIdProveedor(idProveedor);

        Map<String, String> resultadoBP = ActualizarBP_S4Public(proveedorParam, listaCuentaBancaria);
        String statusBP = resultadoBP.get("status");
        String msgBP = resultadoBP.get("msg");

        if(statusBP.equals("1")){

            Map<String, String> resultadoBPAddress = ActualizarBPAddress_S4Public(proveedorParam, listaCuentaBancaria);
            String statusBPAddress = resultadoBPAddress.get("status");
            String msgBPAddress = resultadoBPAddress.get("msg");

            if(statusBPAddress.equals("1")){

                Map<String, String> resultadoBPBank = ActualizarBPBank_S4Public(proveedorParam, listaCuentaBancaria);
                String statusBPBank = resultadoBPBank.get("status");
                String msgBPBank = resultadoBPBank.get("msg");

                if(statusBPBank.equals("1")){

                    
                }else{
                    throw new PortalException(msgBPBank);
                } 
            }else{
                throw new PortalException(msgBPAddress);
            }
        }
        else{
            throw new PortalException(msgBP);
        }


        ProveedorDto proveedorDto = new ProveedorDto();

        proveedorParam.setAcreedorCodigoSap(proveedorParam.getAcreedorCodigoSap());
        proveedorRFCResponseDto.setNroAcreedor(proveedorParam.getAcreedorCodigoSap());
        proveedorRFCResponseDto.setProveedorSap(proveedorParam);
        proveedorRFCResponseDto.setListasapLog(null);
        proveedorRFCResponseDto.setProveedorDto(proveedorDto);
        return proveedorRFCResponseDto;
    }

    private Map<String, String> ActualizarBP_S4Public(Proveedor prov, List<ProveedorCuentaBancaria> listCtaBancaria)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('"+ prov.getAcreedorCodigoSap() +"')";
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        
            String Nombre = prov.getRazonSocial();

            Nombre = Nombre.length() <= 25 ? Nombre : Nombre.substring(0, Math.min(25, Nombre.lastIndexOf(" ", 25) == -1 ? 25 : Nombre.lastIndexOf(" ", 25)));

            String Ruc = prov.getRuc();
            
            //String body = construirJsonBodyBP(prov,listCtaBancaria);
            String body =  "{\n" +
            "   \"d\": {\n" +
            "      \"OrganizationBPName1\": \"" + Nombre + "\",\n" +
            "      \"SearchTerm1\": \"" + Ruc + "\"\n" +
            "   }\n" +
            "}";

            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .put(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {

                System.out.println("Creación Correcta");
                resultMap.put("msg", "Creación Correcta");
                resultMap.put("status", "1");
                return resultMap;

            } else {
                
                System.out.println("El campo 'error' no está presente en la respuesta.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner");
                return resultMap;
                

            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
    
    private Map<String, String> ActualizarBPAddress_S4Public(Proveedor prov, List<ProveedorCuentaBancaria> listCtaBancaria)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            /*Consultamos un servicio para obtener el Addres para  */
            String url_sap_consulta = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('"+ prov.getAcreedorCodigoSap() +"')/to_BusinessPartnerAddress";
            
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            String idaddres =  "";

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request postRequest_consulta = new Request.Builder()
                    .url(url_sap_consulta)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .get()
                    .build();

            Response postResponse_consulta = client.newCall(postRequest_consulta).execute();

            if (postResponse_consulta.isSuccessful()) {

                /*Obtenemos IdAddress */
                String responseBody = postResponse_consulta.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                
                JSONArray resultsArray = jsonObject
                        .getJSONObject("d")
                        .getJSONArray("results");

                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    idaddres = firstResult.getString("AddressID");
                    //return resultMap;
                }

            } else {
                
                System.out.println("No se pudo encontrar IDAdrres.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner");
                return resultMap;
            }

            String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress(BusinessPartner='" + prov.getAcreedorCodigoSap()  + "',AddressID='" + idaddres + "')";

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }
        

            String direccionFiscal = prov.getDireccionFiscal();
            String direccionFinal = direccionFiscal.length() > 40 
                ? direccionFiscal.substring(0, 40) 
                : direccionFiscal;

            String body = "{\n" +
                      "   \"d\": {\n" +
                      "      \"StreetName\": \"" + direccionFinal  + "\",\n" +
                      "      \"Country\": \"" + prov.getPais().getCodigoUbigeoSap() + "\",\n" +
                      "      \"Region\": \"" + prov.getRegion().getCodigoUbigeoSap() + "\",\n" +
                      "      \"PostalCode\": \"" + "+51"  + "\",\n" +
                      "      \"HouseNumber\": \"" + ""  + "\",\n" +
                      "      \"District\":  \"" + prov.getDistrito().getDescripcion()  + "\",\n" +
                      "      \"HouseNumberSupplementText\": \"" + prov.getDistrito().getCodigoUbigeoSap()  + "\",\n" +
                      "      \"CityName\": \"" + prov.getProvincia().getDescripcion() + "\"\n" +
                      "   }\n" +
                      "}";

            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .patch(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {

                System.out.println("Creación Correcta");
                resultMap.put("msg", "Creación Correcta");
                resultMap.put("status", "1");
                return resultMap;

            } else {
                
                System.out.println("El campo 'error' no está presente en la respuesta.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }

    private Map<String, String> ActualizarBPBank_S4Public(Proveedor prov, List<ProveedorCuentaBancaria> listCtaBancaria)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {
            if(listCtaBancaria.size() == 0) {
                System.out.println("Bank");
                resultMap.put("msg", "No se encontró cuenta");
                resultMap.put("status", "1");
                return resultMap;
            }
            
            String usrSap = "RMJ-IPA";
            String pwdSap = "DzBpqubKhGleURSvZGVqNXoPt4mRCAseYehGus}t";
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            
            for (ProveedorCuentaBancaria proveedorCuentaBancaria : listCtaBancaria) {
                
                String idbank =  String.valueOf(proveedorCuentaBancaria.getIdCuenta());
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                String url_sap = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerBank(BusinessPartner='" + prov.getAcreedorCodigoSap() + "',BankIdentification='" + idbank + "')";

                Request getTokenRequest_pk = new Request.Builder()
                .url(url_sap)
                .method("GET", null)
                .addHeader("x-csrf-token", "fetch")
                .addHeader("Authorization", "Basic " + encodedAuth)
                .build();

                Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

                if (!getTokenResponse_pk.isSuccessful()) {

                    continue;
                    //System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                    //resultMap.put("status", "0");
                    //resultMap.put("msg", "Error al obtener el x-csrf-token");
                    //return resultMap;
                }

                // Obtener el valor del token y las cookies
                String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
                if (csrfToken_pk == null) {
                    System.out.println("No se pudo obtener el token CSRF.");
                    resultMap.put("status", "0");
                    resultMap.put("msg", "No se pudo obtener el token CSRF");
                    return resultMap;
                }
                
                // Obtener cookies de la respuesta
                String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
                if (cookies_pk == null) {
                    System.out.println("No se pudieron obtener las cookies.");
                    resultMap.put("status", "0");
                    resultMap.put("msg", "No se pudieron obtener las cookies");
                    return resultMap;
                }

                String Nombre = prov.getRazonSocial();
                Nombre = Nombre.length() <= 25 ? Nombre : Nombre.substring(0, Math.min(25, Nombre.lastIndexOf(" ", 25) == -1 ? 25 : Nombre.lastIndexOf(" ", 25)));


                String body = "{\n" +
                            "   \"d\": {\n" +
                            "      \"BankCountryKey\": \"PE\",\n" +
                            "      \"BankNumber\": \"002\",\n" +
                            "      \"BankAccount\": \"" + listCtaBancaria.get(0).getNumeroCuenta().replace("-", "") + "\",\n" +
                            "      \"BankControlKey\":  \"" + listCtaBancaria.get(0).getClaveControlBanco() + "\",\n" +
                            "      \"BankAccountReferenceText\": \"" + listCtaBancaria.get(0).getNumeroCuentaCci().replace("-", "") + "\",\n" +
                            "      \"BankAccountHolderName\": \"" + Nombre + "\",\n" +
                            "      \"BankAccountName\":  \"" + listCtaBancaria.get(0).getMoneda().getCodigoMoneda()  + "\"\n" +
                            "   }\n" +
                            "}";

                okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
                okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

                Request postRequest_pk = new Request.Builder()
                        .url(url_sap)
                        .addHeader("x-csrf-token", csrfToken_pk)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Basic " + encodedAuth)
                        .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                        .put(body_pk)
                        .build();

                Response postResponse_pk = client.newCall(postRequest_pk).execute();

                if (!postResponse_pk.isSuccessful()) {
                    String responseBody = postResponse_pk.body().string();
                    
                    System.out.println("El campo 'error' no está presente en la respuesta.");
                    resultMap.put("status", "0");
                    resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerBank");
                    return resultMap;
                }
            }

            
            System.out.println("Creación Correcta");
            resultMap.put("msg", "Creación Correcta");
            resultMap.put("status", "1");
            return resultMap;

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
    
    private Map<String, String> ActualizarBPBank_S4Public_old(Proveedor prov, List<ProveedorCuentaBancaria> listCtaBancaria)
    {
        Map<String, String> resultMap = new HashMap<>();
        try {

            if(listCtaBancaria.size() == 0) {
                System.out.println("Bank");
                resultMap.put("msg", "No se encontró cuenta");
                resultMap.put("status", "1");
                return resultMap;
            }

            /*Consultamos un servicio para obtener el Addres para  */
            String url_sap_consulta = urlSap + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('" + prov.getAcreedorCodigoSap() + "')/to_BusinessPartnerBank";
            
            String usrSap = userSap;
            String pwdSap = passwordSap;
            String authString = usrSap + ":" + pwdSap;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            String idbank =  "";

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            Request postRequest_consulta = new Request.Builder()
                    .url(url_sap_consulta)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .get()
                    .build();

            Response postResponse_consulta = client.newCall(postRequest_consulta).execute();

            if (postResponse_consulta.isSuccessful()) {

                /*Obtenemos IdAddress */
                String responseBody = postResponse_consulta.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                
                JSONArray resultsArray = jsonObject
                        .getJSONObject("d")
                        .getJSONArray("results");

                if (resultsArray.length() > 0) {
                    JSONObject firstResult = resultsArray.getJSONObject(0);
                    idbank = firstResult.getString("BankIdentification");
                }

            } else {
                
                System.out.println("No se pudo encontrar IDAdrres.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner");
                return resultMap;
            }

            String url_sap = urlSap +  "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerBank(BusinessPartner='" + prov.getAcreedorCodigoSap() + "',BankIdentification='" + "0001" + "')"; //idbank

            Request getTokenRequest_pk = new Request.Builder()
            .url(url_sap)
            .method("GET", null)
            .addHeader("x-csrf-token", "fetch")
            .addHeader("Authorization", "Basic " + encodedAuth)
            .build();

            Response getTokenResponse_pk = client.newCall(getTokenRequest_pk).execute();

            if (!getTokenResponse_pk.isSuccessful()) {
                System.out.println("Error al obtener el x-csrf-token. Código de respuesta: " + getTokenResponse_pk.code());
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al obtener el x-csrf-token");
                return resultMap;
            }

            // Obtener el valor del token y las cookies
            String csrfToken_pk = getTokenResponse_pk.header("x-csrf-token");
            if (csrfToken_pk == null) {
                System.out.println("No se pudo obtener el token CSRF.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudo obtener el token CSRF");
                return resultMap;
            }
            
            // Obtener cookies de la respuesta
            String cookies_pk = getTokenResponse_pk.header("Set-Cookie");
            if (cookies_pk == null) {
                System.out.println("No se pudieron obtener las cookies.");
                resultMap.put("status", "0");
                resultMap.put("msg", "No se pudieron obtener las cookies");
                return resultMap;
            }

        
            String body = "{\n" +
                        "   \"d\": {\n" +
                        "      \"BankCountryKey\": \"PE\",\n" +
                        "      \"BankNumber\": \"002\",\n" +
                        "      \"BankAccount\": \"" + listCtaBancaria.get(0).getNumeroCuenta().replace("-", "") + "\",\n" +
                        "      \"BankControlKey\":  \"" + listCtaBancaria.get(0).getClaveControlBanco() + "\",\n" +
                        "      \"BankAccountReferenceText\": \"" + listCtaBancaria.get(0).getNumeroCuentaCci().replace("-", "") + "\",\n" +
                        "      \"BankAccountHolderName\": \"" + prov.getRazonSocial() + "\",\n" +
                        "      \"BankAccountName\":  \"" + listCtaBancaria.get(0).getMoneda().getCodigoMoneda()  + "\"\n" +
                        "   }\n" +
                        "}";

            okhttp3.MediaType mediaType_pk = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body_pk = okhttp3.RequestBody.create(mediaType_pk, body);

            Request postRequest_pk = new Request.Builder()
                    .url(url_sap)
                    .addHeader("x-csrf-token", csrfToken_pk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic " + encodedAuth)
                    .addHeader("Cookie", cookies_pk) // Reutilizar las cookies de la solicitud GET
                    .put(body_pk)
                    .build();

            Response postResponse_pk = client.newCall(postRequest_pk).execute();

            if (postResponse_pk.isSuccessful()) {

                System.out.println("Creación Correcta");
                resultMap.put("msg", "Creación Correcta");
                resultMap.put("status", "1");
                return resultMap;

            } else {
                
                System.out.println("El campo 'error' no está presente en la respuesta.");
                resultMap.put("status", "0");
                resultMap.put("msg", "Error al consumir servicio /sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress");
                return resultMap;
            }

        } catch (Exception e) {
            System.out.println("Excepción al integrar con SAP BP");
            resultMap.put("status", "0");
            return resultMap;
        }
    }
    
}

