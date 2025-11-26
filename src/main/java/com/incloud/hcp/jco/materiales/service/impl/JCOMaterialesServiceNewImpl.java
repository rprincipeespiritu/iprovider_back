package com.incloud.hcp.jco.materiales.service.impl;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.RubroBien;
import com.incloud.hcp.domain.TempBienServicio;
import com.incloud.hcp.domain.UnidadMedida;
import com.incloud.hcp.jco.materiales.dto.MaterialesRFCParameterBuilder;
import com.incloud.hcp.jco.materiales.dto.MaterialesRFCResponseDto;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesServiceNew;
import com.incloud.hcp.repository.BienServicioRepository;
import com.incloud.hcp.repository.RubroBienRepository;
import com.incloud.hcp.repository.TempBienServicioRepository;
import com.incloud.hcp.repository.UnidadMedidaRepository;
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
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
//@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class) // EAAR temproal
public class JCOMaterialesServiceNewImpl implements JCOMaterialesServiceNew {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    private final int NIVEL = 1;
    //    private final String FUNCION_RFC = "ZMMRFC_CONSULTA_MATERIALES";
    private final String FUNCION_RFC = "ZPE_MM_CONSULTA_MATERIALES";
    private final String NOMBRE_TABLA_RFC = "TO_MATNR";

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
    private TempBienServicioRepository tempBienServicioRepository;

    @Autowired
    private BienServicioRepository bienServicioRepository;

    @Autowired
    private RubroBienRepository rubroBienRepository;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;


    public MaterialesRFCResponseDto getListMaterialesRFC(String fechaInicio, String fechaFin) throws Exception {
        MaterialesRFCResponseDto materialesRFCResponseDto = new MaterialesRFCResponseDto();

        //String url_sap = urlSap + "/sap/opu/odata/sap/YY1_MATERIALSSERVICES_CDS/YY1_MaterialsServices?$filter=Language%20eq%20'ES'%20and%20ProductType%20eq%20'HAWA'%20and%20CreationDate%20ge%20datetime'"+fechaInicio+"'%20and%20CreationDate%20le%20datetime'"+fechaFin+"'";
        String url_sap = urlSap + "/sap/opu/odata/sap/YY1_MATERIALSSERVICES_CDS/YY1_MaterialsServices?$filter=Language eq 'ES' and ProductType eq 'HAWA' and LastChangeDate ge datetime'"+fechaInicio+"' and LastChangeDate ge datetime'"+fechaFin+"'";
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
        List<BienServicio> listTempBienServicio = new ArrayList<BienServicio>();

        if (postResponse_pk.isSuccessful()) {

            String responseBody = postResponse_pk.body().string();

            try {
                // Convertir la respuesta en un objeto JSON
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject dObject = jsonObject.getJSONObject("d");
                JSONArray resultsArray = dObject.getJSONArray("results");

                logger.error("Materiales: " + String.valueOf(resultsArray.length()));

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject result = resultsArray.getJSONObject(i);

                    logger.error("Materiales procesado: " + String.valueOf(i));
                    logger.error("Materiales :" + result.getString("Product"));

                    // Obtener los valores que necesitas
                    String product = result.getString("Product");
                    String productDescription = result.getString("ProductDescription");
                    /*String plant = result.getString("Plant");
                    String creationDate = result.getString("CreationDate");
                    String productType = result.getString("ProductType");
                    String productGroup = result.getString("ProductGroup");
                    String lastChangeDate = result.getString("LastChangeDate");
                    String language = result.getString("Language");*/

                    List<BienServicio> bienServicioRpta = this.bienServicioRepository.findByCodigoSap(product);

                    if (bienServicioRpta.size() == 0) {
                        BienServicio material = new BienServicio();
                        material.setCodigoSap(product);
                        material.setNro_pieza(result.getString("ProductManufacturerNumber"));

                        material.setDescripcion(productDescription);
                        material.setNumeroParte(String.valueOf(i + 1));
                        material.setTipoItem("M");
        
                        List<UnidadMedida> unidadMedidas = unidadMedidaRepository.listByCodigoSap("UN");
    
                        //MATKL
                        if (unidadMedidas.size()>0) {
        
                            List<RubroBien> rubroBienes = rubroBienRepository.listByCodigoSap("L001");
                            Integer idBienServicio = bienServicioRepository.findByUltimoRegistro();
                            RubroBien rubroBien = null;
        
                            logger.info("UM : " + rubroBien);
        
                            //rubroBien.setIdRubro(1);
                            if (rubroBienes.size()==0) {
                                rubroBien = new RubroBien();
                                rubroBien.setIdRubro(1);
                            } else {
                                rubroBien = rubroBienes.get(0);
                            }
        
                            material.setRubroBien(rubroBien);
                            material.setUnidadMedida(unidadMedidas.get(0));
                            logger.info(String.valueOf(material));
                            this.bienServicioRepository.save(material);
        
                            listTempBienServicio.add(material);
                        } else {
                            logger.info("Aqui se no se encontro UM : ");
                        }
                    }else{

                        logger.error("Actualizando Material :" + result.getString("Product"));

                        BienServicio material = bienServicioRpta.get(0);
                        List<RubroBien> rubroBienes = rubroBienRepository.listByCodigoSap("L001");
                        //rubroBien.setIdRubro(1);
                        RubroBien rubroBien = null;
                        if (rubroBienes.size()==0) {
                            rubroBien = new RubroBien();
                            rubroBien.setIdRubro(1);
                        } else {
                            rubroBien = rubroBienes.get(0);
                        }
                        material.setDescripcion(productDescription);
                        material.setRubroBien(rubroBien);
                        material.setNro_pieza(result.getString("ProductManufacturerNumber"));
                        this.bienServicioRepository.save(material);
        
        
                    }


                }

            }catch (Exception e) {
                logger.error("Error: " + e.getMessage());
            }
        }else{

        }
       
        //logger.error("02e - getGrupoArticulo - sapLog: " + sapLog.toString());
        materialesRFCResponseDto.setListaBienServicio(listTempBienServicio);
        //logger.error("02f - getGrupoArticulo - sapLog: " + sapLog.toString());
        if (listTempBienServicio != null & listTempBienServicio.size() > 0) {
            //logger.error("Lista Materiales listTempBienServicio " +listTempBienServicio.size() );
            materialesRFCResponseDto.setContador(listTempBienServicio.size());
            logger.error("Lista Materiales size: " + listTempBienServicio.size());
        }

        logger.info("RFC 4");
        //logger.error("02f - getGrupoArticulo - FIN");
        return materialesRFCResponseDto;
    }
    
    public MaterialesRFCResponseDto getListMaterialesRFC_old(String fechaInicio, String fechaFin) throws Exception {
        MaterialesRFCResponseDto materialesRFCResponseDto = new MaterialesRFCResponseDto();

        String tramaXML = armarTrama(fechaInicio, fechaFin);
        //String tramaXML = armarTrama2();

        //region enable scp
//
        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

        logger.info(String.valueOf(client));
        String urlbase = String.valueOf(destination2.get().asHttp().getUri());

        //http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_CONSULTA_MATERIAL?sap-client=400&wsdl=1.1&mode=sap_wsdl
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_CONSULTA_MATERIALES?sap-client=400";

        logger.info("RFC: URL" + url);
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
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_MATERIALES/ZMM_CONSULTA_MATERIALESRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_MATERIALES/ZMM_CONSULTA_MATERIALESRequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);
        logger.info("RFC: Trama result ZWS_CONSULTA_MATERIALES" + result);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));

        /* Obteniendo los valores obtenidos del RFC */
        //logger.error("02 - GetMateriales02 - FIN RFC");
        //JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
        //JCoParameterList result = jCoFunction.getExportParameterList();
        SapLog sapLog = new SapLog();
        String codigoSap = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();
        sapLog.setCode(codigoSap);
        sapLog.setMesaj(message);

        //endregion

        //region local - vpn

//        HttpParams httpParameters = new BasicHttpParams();
//        // Set the timeout in milliseconds until a connection is established.
//        int timeoutConnection = 15000;
//        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT)
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 35000;
//        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//
//        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
//        String encoding = Base64.getEncoder().encodeToString(("CMENDEZ:Csti2022").getBytes(StandardCharsets.UTF_8));
//
//        /*
//         * httpclient.getCredentialsProvider().setCredentials( new
//         * AuthScope("os.icloud.com", 80, null, "Digest"), new
//         * UsernamePasswordCredentials(username, password));
//         */
//
//        //now create a soap request message as follows:
//        final StringBuffer soap = new StringBuffer();
//        soap.append("\n");
//        soap.append("");
//        // this is a sample data..you have create your own required data  BEGIN
//        soap.append(" \n");
//        soap.append(" \n");
//        soap.append("" + tramaXML);
//        soap.append(" \n");
//        soap.append(" \n");
//
//        /* soap.append(body); */
//        // END of MEssage Body
//        soap.append("");
//
//        HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
//        HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_CONSULTA_MATERIALES?sap-client=400");
//        httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_MATERIALES/ZMM_CONSULTA_MATERIALESRequest");
//        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
//        httppost.setHeader("Authorization", "Basic " + encoding);
//        System.out.println("executing request" + httppost.getRequestLine());
//        httppost.setEntity(strEntity);
//        logger.info(soap.toString());
//        HttpResponse response4 = httpclient.execute(httppost);
//        HttpEntity respEntity = response4.getEntity();
//        String result = EntityUtils.toString(respEntity);
//
//        DocumentBuilderFactory domFactory = DocumentBuilderFactory
//            .newInstance();
//        domFactory.setNamespaceAware(true);
//        DocumentBuilder builder = domFactory.newDocumentBuilder();
//        Document doc = builder
//            .parse(new InputSource(new StringReader(result)));
//
//        SapLog sapLog = new SapLog();
//        String codigoSap = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
//        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();
//        sapLog.setCode(codigoSap);
//        sapLog.setMesaj(message);
//
//        //endregion


        materialesRFCResponseDto.setSapLog(sapLog);
        //logger.error("02b - getGrupoArticulo - sapLog: " + sapLog.toString());

        /* Recorriendo valores obtenidos del RFC */
        logger.info("RFC 2");
        List<BienServicio> listTempBienServicio = new ArrayList<BienServicio>();
        //JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC);
        NodeList materiales = doc.getElementsByTagName("T_MATNR").item(0).getChildNodes();
        int materialesLength = materiales.getLength();
        for (int i = 0; i < materialesLength; i++) {
            logger.info(String.valueOf(i));
            Node posicion = materiales.item(i);
            Element elemento = (Element) posicion;

            String codigoSAPx = Utils.getValueNodo(elemento, "MATNR");
            /*validamos si existe*/
            List<BienServicio> bienServicioRpta = this.bienServicioRepository.findByCodigoSap(codigoSAPx);

            if (bienServicioRpta.size() == 0) {
                BienServicio material = new BienServicio();
                material.setCodigoSap(Utils.getValueNodo(elemento, "MATNR"));
                logger.info("Material : " + Utils.getValueNodo(elemento, "MATNR"));

                material.setDescripcion(Utils.getValueNodo(elemento, "MAKTX"));
                material.setNumeroParte(String.valueOf(i + 1));
                material.setTipoItem("M");

                List<UnidadMedida> unidadMedidas = unidadMedidaRepository.listByCodigoSap(Utils.getValueNodo(elemento, "MEINS"));

                logger.info("UM : " + unidadMedidas);
                //MATKL
                if (unidadMedidas.size()>0) {

                    List<RubroBien> rubroBienes = rubroBienRepository.listByCodigoSap(Utils.getValueNodo(elemento, "MATKL"));
                    Integer idBienServicio = bienServicioRepository.findByUltimoRegistro();
                    RubroBien rubroBien = null;

                    logger.info("UM : " + rubroBien);

                    //rubroBien.setIdRubro(1);
                    if (rubroBienes.size()==0) {
                        rubroBien = new RubroBien();
                        rubroBien.setIdRubro(1);
                    } else {
                        rubroBien = rubroBienes.get(0);
                    }

                    material.setRubroBien(rubroBien);
                    material.setUnidadMedida(unidadMedidas.get(0));
                    logger.info(String.valueOf(material));
                    this.bienServicioRepository.save(material);

                    listTempBienServicio.add(material);
                } else {
                    logger.info("Aqui se no se encontro UM : " + Utils.getValueNodo(elemento, "MATNR"));
                }
            }else{
                BienServicio material = bienServicioRpta.get(0);
                List<RubroBien> rubroBienes = rubroBienRepository.listByCodigoSap(Utils.getValueNodo(elemento, "MATKL"));
                //rubroBien.setIdRubro(1);
                RubroBien rubroBien = null;
                if (rubroBienes.size()==0) {
                    rubroBien = new RubroBien();
                    rubroBien.setIdRubro(1);
                } else {
                    rubroBien = rubroBienes.get(0);
                }

                material.setRubroBien(rubroBien);
                this.bienServicioRepository.save(material);


            }
            //logger.error("bean" + tempRubroBien);
        }

        logger.info("RFC 3");
        //logger.error("02c - getGrupoArticulo - sapLog: " + sapLog.toString());
        //this.bienServicioRepository.deleteAll();
        //logger.error("02d - getGrupoArticulo - sapLog: " + sapLog.toString());
        //this.bienServicioRepository.saveAll(listTempBienServicio);  // EAAR temporal

        //logger.error("02e - getGrupoArticulo - sapLog: " + sapLog.toString());
        materialesRFCResponseDto.setListaBienServicio(listTempBienServicio);
        //logger.error("02f - getGrupoArticulo - sapLog: " + sapLog.toString());
        if (listTempBienServicio != null & listTempBienServicio.size() > 0) {
            //logger.error("Lista Materiales listTempBienServicio " +listTempBienServicio.size() );
            materialesRFCResponseDto.setContador(listTempBienServicio.size());
            logger.error("Lista Materiales size: " + listTempBienServicio.size());
        }

        logger.info("RFC 4");
        //logger.error("02f - getGrupoArticulo - FIN");
        return materialesRFCResponseDto;
    }

    //@Override
    //public MaterialesRFCResponseDto getListMaterialesRFC(String fechaInicio, String fechaFin) throws Exception {
//        MaterialesRFCResponseDto materialesRFCResponseDto = new MaterialesRFCResponseDto();

    //logger.error("delete");
    /* Ejecucion invocacion a RFC */
//        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//        JCoRepository repo = destination.getRepository();
//        //logger.error("01A - getListaMateriales");
//        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC);
//        //logger.error("01tB - GetMateriales02");
//
//        //logger.error("parametro ingresado");
//        MaterialesRFCParameterBuilder.build(
//                jCoFunction,
//                fechaInicio,
//                fechaFin
//        );
//        //logger.error("01C - GetMateriales02");
//        for(int contador=0; contador < NRO_EJECUCIONES_RFC; contador++) {
//            try {
//                jCoFunction.execute(destination);
//                break;
//            } catch (Exception e) {
//                if (contador == NRO_EJECUCIONES_RFC - 1 ) {
//                    logger.error("01Ca - GetMateriales02 - INI RFC ERROR: "+ e.toString());
//                    throw new Exception(e);
//                }
//            }
//        }
//
//        /* Obteniendo los valores obtenidos del RFC */
//        //logger.error("02 - GetMateriales02 - FIN RFC");
//        JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
//        JCoParameterList result = jCoFunction.getExportParameterList();
//        SapLog sapLog = new SapLog();
//        String codigoSap = result.getString("PO_CODE");
//        String message = result.getString("PO_MSJE");
//        sapLog.setCode(codigoSap);
//        sapLog.setMesaj(message);
//        materialesRFCResponseDto.setSapLog(sapLog);
//        //logger.error("02b - getGrupoArticulo - sapLog: " + sapLog.toString());
//
//        /* Recorriendo valores obtenidos del RFC */
//
//        List<TempBienServicio> listTempBienServicio = new ArrayList<TempBienServicio>();
//        JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC);
//        if (table != null && !table.isEmpty()) {
//
//            do {
//                //logger.error("A - getDevuelveValores TABLE JCO: " + table.toString());
//                TempBienServicio tempBienServicio = new TempBienServicio();
//                tempBienServicio.setCodigoSap(table.getString("MATNR"));
//                tempBienServicio.setDescripcion(table.getString("MAKTX"));
//                tempBienServicio.setDescripcionLarga(table.getString("MAKTX"));
//                tempBienServicio.setTipoItem("M");
//                tempBienServicio.setCodigoRubroSap(table.getString("MATKL"));
//                tempBienServicio.setCodigoUnidadMedidaSap(table.getString("MEINS"));
//                listTempBienServicio.add(tempBienServicio);
//                //logger.error("bean" + tempRubroBien);
//            } while (table.nextRow());
//        }
//
//        //logger.error("02c - getGrupoArticulo - sapLog: " + sapLog.toString());
//        this.tempBienServicioRepository.deleteAll();
//        //logger.error("02d - getGrupoArticulo - sapLog: " + sapLog.toString());
//        this.tempBienServicioRepository.saveAll(listTempBienServicio);
//        //logger.error("02e - getGrupoArticulo - sapLog: " + sapLog.toString());
//        materialesRFCResponseDto.setListaBienServicio(listTempBienServicio);
//        //logger.error("02f - getGrupoArticulo - sapLog: " + sapLog.toString());
//        if (listTempBienServicio != null & listTempBienServicio.size() > 0) {
//            //logger.error("Lista Materiales listTempBienServicio " +listTempBienServicio.size() );
//            materialesRFCResponseDto.setContador(listTempBienServicio.size());
//        }

    //logger.error("Lista Materiales" +listTempBienServicio.toString() );
    //logger.error("02f - getGrupoArticulo - FIN");
//        return materialesRFCResponseDto;
//    }

    private String armarTrama(String fechaInicio, String fechaFin) {

        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <urn:ZMM_CONSULTA_MATERIALES>\n" +
            "         <!--Optional:-->\n" +
            "         <TI_ERSDA>\n" +
            "            <LOW>" + fechaInicio + "</LOW>\n" +
            "            <HIGH>" + fechaFin + "</HIGH>\n" +
            "         </TI_ERSDA>\n" +
            "         <T_MATNR>\n" +
            "            <!--Zero or more repetitions:-->\n" +
            "            <item>\n" +
            "               <MATNR></MATNR>\n" +
            "               <MTART></MTART>\n" +
            "               <MATKL></MATKL>\n" +
            "               <MEINS></MEINS>\n" +
            "               <MAKTX></MAKTX>\n" +
            "            </item>\n" +
            "         </T_MATNR>\n" +
            "      </urn:ZMM_CONSULTA_MATERIALES>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
//        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
//                "   <soapenv:Header/>\n" +
//                "   <soapenv:Body>\n" +
//                "      <urn:ZPE_MM_CONSULTA_MATERIALES>\n" +
//                "         <!--Optional:-->\n" +
//                "         <TI_ERSDA>\n" +
//                "            <LOW>" + fechaInicio + "</LOW>\n" +
//                "            <HIGH>" + fechaFin + "</HIGH>\n" +
//                "         </TI_ERSDA>\n" +
//                "         <T_MATNR>\n" +
//                "            <!--Zero or more repetitions:-->\n" +
//                "            <item>\n" +
//                "               <MATNR>?</MATNR>\n" +
//                "               <MTART>?</MTART>\n" +
//                "               <MATKL>?</MATKL>\n" +
//                "               <MEINS>?</MEINS>\n" +
//                "               <MAKTX>?</MAKTX>\n" +
//                "            </item>\n" +
//                "         </T_MATNR>\n" +
//                "      </urn:ZPE_MM_CONSULTA_MATERIALES>\n" +
//                "   </soapenv:Body>\n" +
//                "</soapenv:Envelope>";

        return tramaXML;

    }
}
