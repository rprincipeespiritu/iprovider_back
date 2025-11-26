package com.incloud.hcp.jco.banco.service.impl;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.RubroBien;
import com.incloud.hcp.domain.UnidadMedida;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.banco.service.JCOBancoService;
import com.incloud.hcp.myibatis.mapper.BancoMapper;
import com.incloud.hcp.repository.BancoRepository;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOBancoServiceImpl implements JCOBancoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${destination.rfc.profit}")
    private String destinationProfit;
    @Value("${API_URL_SAP}")
    private String urlSap;
    @Value("${USERNAME_SAP}")
    private String userSap;
    @Value("${PASSWORD_SAP}")
    private String passwordSap;

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired
    private BancoMapper bancoMapper;
    // List<Banco>
    @Override
    public void extraerBancosRFC_old(String fechaInicio, String fechaFin) throws Exception {

        logger.error("RFC BANCO", "JCO DESTINATION Exe");
        String tramaXML = armarTrama(fechaInicio, fechaFin);
        logger.error("tramaBancos ",tramaXML);
        /* Obteniendo valores del RFC*/
        List<SapLog> listSapLog = new ArrayList<>();
        logger.info("RFC: DESTINATION - " + destinationProfit);
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

        logger.info(String.valueOf(client));
        String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
        Integer portx = 20003;

        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_EXTRACTOR_BANCOS?sap-client=400&wsdl=1.1";

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

        HttpPost post = new HttpPost(url);
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_EXTRACTOR_BANCOS/ZMM_EXTRACTOR_BANCOSRequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_EXTRACTOR_BANCOS/ZMM_EXTRACTOR_BANCOSRequest");
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

        logger.info("RFC: Resultado :");
        logger.info(result);

        String codigoRetorno = doc.getElementsByTagName("PO_CODE").item(0).getChildNodes().item(0).getNodeValue();

        if (codigoRetorno.equals("1")) {
            String message = doc.getElementsByTagName("PO_MESSAGE").item(0).getChildNodes().item(0).getNodeValue();

            throw new PortalException(message);
        }

        NodeList nodeitemList = doc.getElementsByTagName("PO_DATOS_BANCOS").item(0).getChildNodes();

        Integer validarId = bancoMapper.getIdSequence();

        logger.error("valor validarId "+validarId.toString());
        List<Banco> bancoList = Optional.ofNullable(BancoExtactorMapper.getBancoList(nodeitemList,validarId)).orElse(new ArrayList<>());

        String header1 = "INI: " + DateUtils.getCurrentTimestamp().toString() + " -- EXTR OC -- RANGO: " + fechaInicio + " - " + fechaFin + "  ";
        logger.error(header1 + "Rango de Fechas : " + fechaInicio + " - " + fechaFin);
        logger.error(header1 + "CANTIDAD DE BANCOS ENCONTRADOS: " + bancoList.size());

        for (int i=0;i< bancoList.size();i++){
            logger.error("bancos "+bancoList.get(i).toString());//.getIdBanco()+"-"+bancoList.get(i).getDescripcion());
            //bancoList.get(i).setIdBanco(0);
            Integer cantBanco=bancoMapper.existeBanco(bancoList.get(i).getClaveBanco());
            if (cantBanco == 0){
                bancoMapper.insertBancos(bancoList.get(i));
            }

            //bancoRepository.save(bancoList.get(i));
        }

        //insertar Banco
       // List<Banco> insertBankList = bancoRepository.saveAll(bancoList);

        //return insertBankList;
    }

    @Override
    public void extraerBancosRFC(String fechaInicio, String fechaFin) throws Exception {

         
        String url_sap = urlSap + "/sap/opu/odata/sap/YY1_BANK_CDS/YY1_Bank";
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

                Integer validarId = bancoMapper.getIdSequence();

                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject result = resultsArray.getJSONObject(i);

                    // Crear objeto Banco a partir de los datos obtenidos
                    Banco banco = new Banco();
                    banco.setIdBanco(validarId);
                    banco.setClaveBanco(result.getString("BankInternalID"));
                    banco.setDescripcion(result.getString("BankName"));
                    banco.setEjemploFormatoCta("99999999999999");
                    banco.setExtensionCci(23);
                    banco.setExtensionCta(18);
                    banco.setFormatoCta("^[0-9]{11}$");
                    banco.setExtensionCtaMin(10);
                    banco.setTipoBanco("N");
                    
                    Integer cantBanco=bancoMapper.existeBanco(banco.getClaveBanco());
                    if (cantBanco == 0){
                        bancoMapper.insertBancos(banco);
                    }

                }

            }catch (Exception e) {
                logger.error("Error: " + e.getMessage());
            }
        }else{

        }
    }

    private String armarTrama(String fechaInicio,String fechaFinal){

        String trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_EXTRACTOR_BANCOS>\n" +
                "         <PI_ERDAT>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <SIGN>I</SIGN>\n" +
                "               <OPTION>BT</OPTION>\n" +
                "               <LOW>"+Optional.ofNullable(fechaInicio).orElse("")+"</LOW>\n" +
                "               <HIGH>"+Optional.ofNullable(fechaFinal).orElse("")+"</HIGH>\n" +
                "            </item>\n" +
                "         </PI_ERDAT>\n" +
                "      </urn:ZMM_EXTRACTOR_BANCOS>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return trama;
    }
}
