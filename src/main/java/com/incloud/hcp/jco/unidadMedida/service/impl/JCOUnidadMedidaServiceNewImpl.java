package com.incloud.hcp.jco.unidadMedida.service.impl;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.UnidadMedida;
import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCDTO;
import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCParameterBuilder;
import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCResponseDto;
import com.incloud.hcp.jco.unidadMedida.service.JCOUnidadMedidaServiceNew;
import com.incloud.hcp.myibatis.mapper.BancoMapper;
import com.incloud.hcp.myibatis.mapper.UnidadMedidaMapper;
import com.incloud.hcp.repository.UnidadMedidaRepository;
import com.incloud.hcp.sap.SapLog;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOUnidadMedidaServiceNewImpl implements JCOUnidadMedidaServiceNew {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;

//    private final String FUNCION_RFC = "ZMMRFC_LISTA_UM";
    private final String FUNCION_RFC = "ZPE_MM_LISTA_UM";
    private final String NOMBRE_TABLA_RFC = "T_LIST_UM";

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
    private UnidadMedidaMapper unidadMedidaMapper;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @PersistenceContext
    private EntityManager entityManager
    ;

    @Override
    public UnidadMedidaRFCResponseDto actualizarUnidadMedida_old() throws Exception {
        UnidadMedidaRFCResponseDto unidadMedidaRFCResponseDto = new UnidadMedidaRFCResponseDto();

        String tramaXML = armarTrama("", "");
        /* Ejecucion invocacion a RFC */
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 35000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        String encoding = Base64.getEncoder().encodeToString(("CMENDEZ:Csti2022").getBytes(StandardCharsets.UTF_8));

        /*
         * httpclient.getCredentialsProvider().setCredentials( new
         * AuthScope("os.icloud.com", 80, null, "Digest"), new
         * UsernamePasswordCredentials(username, password));
         */

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

        /* soap.append(body); */
        // END of MEssage Body
        soap.append("");

        HttpEntity strEntity = new StringEntity(tramaXML, "text/xml", "UTF-8");
        HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_LISTA_UM?sap-client=400");
        httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_LISTA_UM/ZMM_LIST_UMRequest");
        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
        httppost.setHeader("Authorization", "Basic " + encoding);
        System.out.println("executing request" + httppost.getRequestLine());
        httppost.setEntity(strEntity);
        logger.info(soap.toString());
        HttpResponse response4 = httpclient.execute(httppost);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder
                .parse(new InputSource(new StringReader(result)));
        SapLog sapLog = new SapLog();
        String codigoSap = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();
        String message = doc.getElementsByTagName("PO_MSJE").item(0).getChildNodes().item(0).getNodeValue();
        sapLog.setCode(codigoSap);
        sapLog.setMesaj(message);
        unidadMedidaRFCResponseDto.setSapLog(sapLog);
        logger.error("02b - getListaServicios - sapLog: " + sapLog.toString());

        /* Recorriendo valores obtenidos del RFC */

        List<UnidadMedidaRFCDTO> listaUnidadMedida = new ArrayList<UnidadMedidaRFCDTO>();
        //lista de monedas actual
        //List<Moneda> listMoneda = this.monedaRepository.findAll();
        NodeList table = doc.getElementsByTagName("T_LIST_UM").item(0).getChildNodes();
        if (table != null ) {

            for(int i = 0; i < table.getLength(); i++) {

                Node posicion = table.item(i);
                Element elemento = (Element) posicion;
                UnidadMedidaRFCDTO unidadMedidaRFC = new UnidadMedidaRFCDTO();
                unidadMedidaRFC.setCodigo(Utils.getValueNodo(elemento, "MSEHI"));
                unidadMedidaRFC.setDescripcion(Utils.getValueNodo(elemento, "MSEHL"));
        
                UnidadMedida unidadMedidaEncontrada=unidadMedidaRepository.getByCodigoSap(unidadMedidaRFC.getCodigo());
                if (!Optional.ofNullable(unidadMedidaEncontrada).isPresent()) {
                    UnidadMedida unidadMedidanew = new UnidadMedida();
                    unidadMedidanew.setCodigoSap(unidadMedidaRFC.getCodigo());
                    unidadMedidanew.setDescripcion(unidadMedidaRFC.getDescripcion());
                    unidadMedidanew.setTextoUm(unidadMedidaRFC.getCodigo());
                    this.unidadMedidaRepository.save(unidadMedidanew);
                }else{
                    unidadMedidaEncontrada.setTextoUm(unidadMedidaRFC.getCodigo());
                    unidadMedidaEncontrada.setDescripcion(unidadMedidaRFC.getDescripcion());
                    unidadMedidaEncontrada.setCodigoSap(unidadMedidaRFC.getCodigo());
                    this.unidadMedidaRepository.save(unidadMedidaEncontrada);
                }

                listaUnidadMedida.add(unidadMedidaRFC);


                //logger.error("bean" + tempRubroBien);
            }
        }


        unidadMedidaRFCResponseDto.setListaUnidadMedida(listaUnidadMedida);
        logger.error("Lista Unidad Medida" +listaUnidadMedida.toString() );
        return unidadMedidaRFCResponseDto;
    }

    @Override
    public UnidadMedidaRFCResponseDto actualizarUnidadMedida() throws Exception {

        String url_sap = urlSap + "/sap/opu/odata/sap/YY1_UNITOFMEASURE_CDS/YY1_UnitOfMeasure?$filter=Language eq 'ES'";
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
        List<UnidadMedidaRFCDTO> listaUnidadMedida = new ArrayList<UnidadMedidaRFCDTO>();
        UnidadMedidaRFCResponseDto unidadMedidaRFCResponseDto = new UnidadMedidaRFCResponseDto();

        if (postResponse_pk.isSuccessful()) {

            String responseBody = postResponse_pk.body().string();

            try {
                // Convertir la respuesta en un objeto JSON
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONObject dObject = jsonObject.getJSONObject("d");
                JSONArray resultsArray = dObject.getJSONArray("results");

                //Integer validarId = bancoMapper.getIdSequence();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject result = resultsArray.getJSONObject(i);

                    UnidadMedidaRFCDTO unidadMedidaRFC = new UnidadMedidaRFCDTO();
                    unidadMedidaRFC.setCodigo(result.getString("UnitOfMeasure"));
                    unidadMedidaRFC.setDescripcion(result.getString("UnitOfMeasureLongName"));

                    UnidadMedida unidadMedidaEncontrada=unidadMedidaRepository.getByCodigoSap(unidadMedidaRFC.getCodigo());
                    if (!Optional.ofNullable(unidadMedidaEncontrada).isPresent()) {
                        UnidadMedida unidadMedidanew = new UnidadMedida();
                        //unidadMedidanew.setIdUnidadMedida(unidadMedidaMapper.getIdSequence());
                        unidadMedidanew.setCodigoSap(unidadMedidaRFC.getCodigo());
                        unidadMedidanew.setDescripcion(unidadMedidaRFC.getDescripcion());
                        unidadMedidanew.setTextoUm(unidadMedidaRFC.getCodigo());
                        this.unidadMedidaRepository.save(unidadMedidanew);
                        //this.unidadMedidaRepository.save(unidadMedidanew);
                    }else{
                        unidadMedidaEncontrada.setTextoUm(unidadMedidaRFC.getCodigo());
                        unidadMedidaEncontrada.setDescripcion(unidadMedidaRFC.getDescripcion());
                        unidadMedidaEncontrada.setCodigoSap(unidadMedidaRFC.getCodigo());
                        this.unidadMedidaRepository.save(unidadMedidaEncontrada);
                    }

                    listaUnidadMedida.add(unidadMedidaRFC);

                    }

            }catch (Exception e) {
                logger.error("Error: " + e.getMessage());
            }
        }else{

        }

        unidadMedidaRFCResponseDto.setListaUnidadMedida(listaUnidadMedida);
        logger.error("Lista Unidad Medida" +listaUnidadMedida.toString() );
        return unidadMedidaRFCResponseDto;
    }


    private String armarTrama(String fechaInicio, String fechaFin)
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_LIST_UM>\n" +
                "         <T_LIST_UM>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MSEHI></MSEHI>\n" +
                "               <MSEHL></MSEHL>\n" +
                "            </item>\n" +
                "         </T_LIST_UM>\n" +
                "      </urn:ZMM_LIST_UM>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }
}
