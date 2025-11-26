package com.incloud.hcp.jco.centroAlmacen.service.impl;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.domain.TempCentroAlmacen;
import com.incloud.hcp.jco.centro.dto.CentroRFCDto;
import com.incloud.hcp.jco.centroAlmacen.dto.CentroAlmacenRFCParameterBuilder;
import com.incloud.hcp.jco.centroAlmacen.dto.CentroAlmacenRFCResponseDto;
import com.incloud.hcp.jco.centroAlmacen.service.JCOCentroAlmacenServiceNew;
import com.incloud.hcp.repository.CentroAlmacenRepository;
import com.incloud.hcp.repository.TempCentroAlmacenRepository;
import com.incloud.hcp.sap.SapLog;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOCentroAlmacenServiceNewImpl implements JCOCentroAlmacenServiceNew {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    private final int NIVEL = 1;
//    private final String FUNCION_RFC = "ZMMRFC_LISTA_CNTRO_ALMCN";
    private final String FUNCION_RFC = "ZPE_MM_LISTA_CENTRO_ALMACEN";
    private final String NOMBRE_TABLA_RFC = "TO_CENTRO_ALMACEN";

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
    private CentroAlmacenRepository centroAlmacenRepository;

    @Autowired
    private TempCentroAlmacenRepository tempCentroAlmacenRepository;



    @Override
    public CentroAlmacenRFCResponseDto getListaCentroAlmacen_old(String centro) throws Exception {

        CentroAlmacenRFCResponseDto centroAlmacenRFCResponseDto = new CentroAlmacenRFCResponseDto();

        /* Ejecucion invocacion a RFC*/
//        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//        JCoRepository repo = destination.getRepository();
//        logger.error("01A - getCentroAlmacen");
//        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC);
//        logger.error("01B - getCentroAlmacen");
//
//        logger.error("parametro ingresado" + centro);
//        CentroAlmacenRFCParameterBuilder.build(
//                jCoFunction,
//                centro
//        );
//        logger.error("01C - getCentroAlmacen");
//        for(int contador=0; contador < NRO_EJECUCIONES_RFC; contador++) {
//            try {
//                jCoFunction.execute(destination);
//                break;
//            } catch (Exception e) {
//                if (contador == NRO_EJECUCIONES_RFC - 1 ) {
//                    logger.error("01Ca - getCentroAlmacen - INI RFC ERROR: "+ e.toString());
//                    throw new Exception(e);
//                }
//            }
//        }

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

       /* DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory() {
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
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_lista_centro_almacen/100/zws_lista_centro_almacen/zws_lista_centro_almacen";

        logger.info("RFC: URL" + url);

        String tramaXML = armarTrama();
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

        //HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
        HttpEntity strEntity = new StringEntity(soap.toString());

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_LISTA_CENTRO_ALMACEN:ZPE_MM_LISTA_CENTRO_ALMACENRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_LISTA_CENTRO_ALMACEN:ZPE_MM_LISTA_CENTRO_ALMACENRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);


        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);

        logger.info("response4 :" + response4);
        logger.info("Entity :" + respEntity);
        logger.info("Result :" + result);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));



        /* Obteniendo los valores obtenidos del RFC*/
//        logger.error("02 - getCentroAlmacen - FIN RFC");
//        JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
//        JCoParameterList result = jCoFunction.getExportParameterList();
//        SapLog sapLog = new SapLog();
//        String codigoSap = result.getString("PO_CODE");
//        String message = result.getString("PO_MSJE");

        SapLog sapLog = new SapLog();
        String codigoSap = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();

        sapLog.setCode(codigoSap);
        sapLog.setMesaj(message);
        centroAlmacenRFCResponseDto.setSapLog(sapLog);
        logger.error("02b - getCentroAlmacen - sapLog: " + sapLog.toString());

        /* Recorriendo valores obtenidos del RFC*/
        List<CentroAlmacen> centroAlmacenRFCDtoList = new ArrayList<CentroAlmacen>();
        //JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC);
        logger.error("02b 01 - getCentroAlmacen - sapLog: " + sapLog.toString());
        NodeList centros = doc.getElementsByTagName("TO_CENTRO_ALMACEN").item(0).getChildNodes();

        for(int i = 0; i < centros.getLength(); i++) {
            Node centroa = centros.item(i);
            Element elemento = (Element) centroa;
            String Centrox = Utils.getValueNodo(elemento, "WERKS");

            if (Centrox.equals("")) {
                continue;
            }

            //logger.error("02 bA - getDevuelveValores TABLE JCO: " + table.toString());
            CentroAlmacen bean = new CentroAlmacen();
            logger.info("Buscando padre : " + Utils.getValueNodo(elemento, "WERKS"));

            CentroAlmacen centroAlmacenPadre = this.centroAlmacenRepository.
                    getByCodigoSap(Utils.getValueNodo(elemento, "WERKS"));

            if(centroAlmacenPadre == null)
            {
                continue;
            }
            //logger.error("02b - centroAlmacenPadre - sapLog: " + centroAlmacenPadre.toString());
            logger.info("centroAlmacenPadre: " + centroAlmacenPadre.toString());

            bean.setIdPadre(centroAlmacenPadre.getIdCentroAlmacen());
            //bean.setCentro(Utils.getValueNodo(elemento, "WERKS"));
            bean.setPoblacion(Utils.getValueNodo(elemento, "ORT01"));
            bean.setDistrito(Utils.getValueNodo(elemento, "CITY2"));
            bean.setDireccion(Utils.getValueNodo(elemento, "STRAS"));

            //bean.setCodigoAlmacen(Utils.getValueNodo(elemento, "LGORT"));
            //bean.setDescripcionAlmacen(Utils.getValueNodo(elemento, "LGOBE"));
            bean.setCodigoSap(Utils.getValueNodo(elemento, "LGORT"));
            bean.setDescripcion(Utils.getValueNodo(elemento, "LGOBE"));
            bean.setDenominacion(Utils.getValueNodo(elemento, "LGOBE"));
            bean.setNivel(2);

            logger.info("centroAlmacen Hijo: " + bean.toString());

            //this.centroAlmacenRepository.save(bean);

            //logger.info("Pasando el save: " + bean.toString());

            centroAlmacenRFCDtoList.add(bean);
        }

        logger.error("02b 01 - getCentroAlmacen - fin recorrido list: " + sapLog.toString());

        this.centroAlmacenRepository.saveAll(centroAlmacenRFCDtoList);

        centroAlmacenRFCResponseDto.setListaCentroAlmacen2(centroAlmacenRFCDtoList);
        if (centroAlmacenRFCDtoList != null && centroAlmacenRFCDtoList.size() > 0) {
            centroAlmacenRFCResponseDto.setContador(centroAlmacenRFCDtoList.size());
            logger.error("02 getDevuelveValores centroAlmacenRFCDtoList: " + centroAlmacenRFCDtoList.size());
        }
        //this.centroAlmacenRepository.deleteAlll();
        this.centroAlmacenRepository.saveAll(centroAlmacenRFCDtoList);
        return centroAlmacenRFCResponseDto;
    }

    @Override
    public CentroAlmacenRFCResponseDto getListaCentroAlmacen(String centro) throws Exception {

        CentroAlmacenRFCResponseDto centroAlmacenRFCResponseDto = new CentroAlmacenRFCResponseDto();
        String url_sap = urlSap +  "/sap/opu/odata/sap/YY1_PLANT_CDS/YY1_Plant";
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
        List<CentroAlmacen> centroAlmacenRFCDtoList = new ArrayList<CentroAlmacen>();

        if (postResponse_pk.isSuccessful()) {

            String responseBody = postResponse_pk.body().string();

            try {
                // Convertir la respuesta en un objeto JSON
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject dObject = jsonObject.getJSONObject("d");
                JSONArray resultsArray = dObject.getJSONArray("results");

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject result = resultsArray.getJSONObject(i);

                    //logger.error("02 bA - getDevuelveValores TABLE JCO: " + table.toString());

                    //CentroAlmacen bean = new CentroAlmacen();        

                    CentroAlmacen bean = this.centroAlmacenRepository.getByCodigoSap(result.getString("StorageLocation"));

                    CentroAlmacen centroAlmacenPadre = this.centroAlmacenRepository.getByCodigoSap(result.getString("Plant"));

                    if(centroAlmacenPadre == null)
                    {
                        continue;
                    }
                    //logger.error("02b - centroAlmacenPadre - sapLog: " + centroAlmacenPadre.toString());
                    logger.info("centroAlmacenPadre: " + centroAlmacenPadre.toString());

                    bean.setIdPadre(centroAlmacenPadre.getIdCentroAlmacen());

                    //bean.setIdPadre(centroAlmacenPadre.getIdCentroAlmacen());
                    //bean.setCentro(result.getString("Plant"));
                    bean.setPoblacion(result.getString("CityName"));
                    bean.setDistrito(result.getString("DistrictName"));
                    bean.setDireccion(result.getString("StreetName"));
                    bean.setCodigoSap(result.getString("StorageLocation"));
                    bean.setDescripcion(result.getString("StorageLocationName"));
                    bean.setDenominacion(result.getString("StorageLocationName"));
                    bean.setNivel(2);
        
                    logger.info("centroAlmacen Hijo: " + bean.toString());        
        
                    centroAlmacenRFCDtoList.add(bean);
                }

                this.centroAlmacenRepository.saveAll(centroAlmacenRFCDtoList);

                centroAlmacenRFCResponseDto.setListaCentroAlmacen2(centroAlmacenRFCDtoList);
                if (centroAlmacenRFCDtoList != null && centroAlmacenRFCDtoList.size() > 0) {
                    centroAlmacenRFCResponseDto.setContador(centroAlmacenRFCDtoList.size());
                    logger.error("02 getDevuelveValores centroAlmacenRFCDtoList: " + centroAlmacenRFCDtoList.size());
                }
                //this.centroAlmacenRepository.deleteAlll();
                this.centroAlmacenRepository.saveAll(centroAlmacenRFCDtoList);
                return centroAlmacenRFCResponseDto;

            }catch (Exception e) {
                logger.error("Error: " + e.getMessage());
            }
        }else{
            return centroAlmacenRFCResponseDto;
        }
        return centroAlmacenRFCResponseDto;

        /*this.centroAlmacenRepository.saveAll(centroAlmacenRFCDtoList);

        centroAlmacenRFCResponseDto.setListaCentroAlmacen2(centroAlmacenRFCDtoList);
        if (centroAlmacenRFCDtoList != null && centroAlmacenRFCDtoList.size() > 0) {
            centroAlmacenRFCResponseDto.setContador(centroAlmacenRFCDtoList.size());
            logger.error("02 getDevuelveValores centroAlmacenRFCDtoList: " + centroAlmacenRFCDtoList.size());
        }
        //this.centroAlmacenRepository.deleteAlll();
        this.centroAlmacenRepository.saveAll(centroAlmacenRFCDtoList);
        return centroAlmacenRFCResponseDto;*/
    }


    private String armarTrama()
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_LISTA_CENTRO_ALMACEN>\n" +
                "         <PI_WERKS></PI_WERKS>\n" +
                "         <!--Optional:-->\n" +
                "         <TO_CENTRO_ALMACEN>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <WERKS></WERKS>\n" +
                "               <ORT01></ORT01>\n" +
                "               <CITY2></CITY2>\n" +
                "               <STRAS></STRAS>\n" +
                "               <LGORT></LGORT>\n" +
                "               <LGOBE></LGOBE>\n" +
                "            </item>\n" +
                "         </TO_CENTRO_ALMACEN>\n" +
                "      </urn:ZPE_MM_LISTA_CENTRO_ALMACEN>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }
}
