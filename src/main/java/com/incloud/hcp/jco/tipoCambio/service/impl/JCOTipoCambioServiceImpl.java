package com.incloud.hcp.jco.tipoCambio.service.impl;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.Moneda;
import com.incloud.hcp.domain.TasaCambio;
import com.incloud.hcp.domain.UnidadMedida;
import com.incloud.hcp.jco.tipoCambio.dto.TipoCambioRFCDTO;
import com.incloud.hcp.jco.tipoCambio.dto.TipoCambioRFCParameterBuilder;
import com.incloud.hcp.jco.tipoCambio.dto.TipoCambioRFCResponseDto;
import com.incloud.hcp.jco.tipoCambio.service.JCOTipoCambioService;
import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCDTO;
import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCResponseDto;
import com.incloud.hcp.repository.MonedaRepository;
import com.incloud.hcp.repository.TasaCambioRepository;
import com.incloud.hcp.sap.SapLog;
import com.incloud.hcp.util.DateUtils;
//import com.sap.conn.jco.*;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class JCOTipoCambioServiceImpl implements JCOTipoCambioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;

    //    private final String FUNCION_RFC = "ZMMRFC_CONSULTA_TIPO_CAMBIO";
    private final String FUNCION_RFC = "ZPE_MM_CONSULTA_TIPO_CAMBIO";
    private final String NOMBRE_TABLA_RFC = "TO_TIP_CAMBIO";

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
    private MonedaRepository monedaRepository;

    @Autowired
    private TasaCambioRepository tasaCambioRepository;

    @Override
    public TipoCambioRFCResponseDto actualizarTipoCambio_old(String fecha) throws Exception {
        TipoCambioRFCResponseDto tipoCambioRFCResponseDto = new TipoCambioRFCResponseDto();

        logger.info("RFC: DESTINATION - " + destinationProfit);

        //region jco
        Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

        logger.info(String.valueOf(destination2.get()));
        HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));

        logger.info(String.valueOf(client));

        String urlbase = String.valueOf(destination2.get().asHttp().getUri());
        //http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_TIPO_CAMBIO?sap-client=400&wsdl=1.1&mode=sap_wsdl
        String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_TIPO_CAMBIO?sap-client=400";

        logger.info("RFC: URL" + url);

        String tramaXML = armarTrama(fecha);
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
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_TIPO_CAMBIO/ZMM_CONSULTA_TIPO_CAMBIORequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_TIPO_CAMBIO/ZMM_CONSULTA_TIPO_CAMBIORequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        //endregion

        //region local

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
//        /*
//         * httpclient.getCredentialsProvider().setCredentials( new
//         * AuthScope("os.icloud.com", 80, null, "Digest"), new
//         * UsernamePasswordCredentials(username, password));
//         */
//
//        //now create a soap request message as follows:
//        String tramaXML = armarTrama(fecha);
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
//        HttpPost post = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_TIPO_CAMBIO?sap-client=400");
//        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions/ZWS_TIPO_CAMBIO/ZMM_CONSULTA_TIPO_CAMBIORequest");
//        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions/ZWS_TIPO_CAMBIO/ZMM_CONSULTA_TIPO_CAMBIORequest");
//        post.setHeader("Content-Type", "text/xml; charset=utf-8");
//        post.setHeader("Authorization", "Basic " + encoding);
        //endregion

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        HttpResponse response4 = client.execute(post);
        HttpEntity respEntity = response4.getEntity();
        String result = EntityUtils.toString(respEntity);
        logger.info("RESULT" + result);
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
        tipoCambioRFCResponseDto.setSapLog(sapLog);
        logger.error("02b - getTipoCambio - sapLog: " + sapLog.toString());

        /* Recorriendo valores obtenidos del RFC */

        List<TipoCambioRFCDTO> lisTasaCambio = new ArrayList<TipoCambioRFCDTO>();
        //lista de monedas actual
        List<Moneda> listMoneda = this.monedaRepository.findAll();

        //JCoTable table = tableParameterList.getTable(NOMBRE_TABLA_RFC);
        NodeList tiposcampo = doc.getElementsByTagName("T_TIP_CAMBIO").item(0).getChildNodes();

        for (int i = 0; i < tiposcampo.getLength(); i++) {

            Node tipo = tiposcampo.item(i);
            Element elemento = (Element) tipo;

            String fechax = Utils.getValueNodo(elemento, "PI_DATE");

            if (fechax.equals("")) {
                continue;
            }

            //logger.error("A - getDevuelveValores TABLE JCO: " + table.toString());
            TipoCambioRFCDTO tipoCambioRFCDTO = new TipoCambioRFCDTO();
            tipoCambioRFCDTO.setCodigoMonedaOrigen(Utils.getValueNodo(elemento, "LOCAL_CURRENCY"));
            tipoCambioRFCDTO.setCodigoMonedaDestino(Utils.getValueNodo(elemento, "FOREIGN_CURRENCY"));
            tipoCambioRFCDTO.setValor(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "EXCHANGE_RATE")));
            String fechaNodo = Utils.getValueNodo(elemento, "PI_DATE");
            tipoCambioRFCDTO.setFecha(fechaNodo);

            String monedaDestino = tipoCambioRFCDTO.getCodigoMonedaDestino();
            Moneda monedaSearch = this.monedaRepository.getByCodigoMoneda(monedaDestino);

            if (!Optional.ofNullable(monedaSearch).isPresent()) {
                Moneda newmoneda = new Moneda();
                newmoneda.setSigla(tipoCambioRFCDTO.getCodigoMonedaDestino());
                newmoneda.setCodigoMoneda(tipoCambioRFCDTO.getCodigoMonedaDestino());
                newmoneda.setTextoBreve(tipoCambioRFCDTO.getCodigoMonedaDestino());
                newmoneda.setTextoExplicativo(tipoCambioRFCDTO.getCodigoMonedaDestino());
                newmoneda.setTasaMoneda(new BigDecimal(0.0));
                logger.error(newmoneda.toString());
                this.monedaRepository.save(newmoneda);
            }

            lisTasaCambio.add(tipoCambioRFCDTO);
        }


        for (TipoCambioRFCDTO tipoCambioRFCDTO1 : lisTasaCambio) {
            Moneda monedaLocal = monedaRepository.getByCodigoMoneda(tipoCambioRFCDTO1.getCodigoMonedaOrigen());
            Moneda monedaDestino = monedaRepository.getByCodigoMoneda(tipoCambioRFCDTO1.getCodigoMonedaDestino());
            Date fechaTasa = DateUtils.convertStringToDate("yyyyMMdd", tipoCambioRFCDTO1.getFecha());

            if (monedaLocal != null && monedaDestino != null) {
                TasaCambio tasaCambioExistente = tasaCambioRepository.getByFechaTasaAndIdMonedaOrigenAndIdMonedaDestino(fechaTasa, monedaLocal, monedaDestino);

                if (!Optional.ofNullable(tasaCambioExistente).isPresent()) {
                    logger.error("Nueva tasa Cambio");
                    TasaCambio tasaCambio = new TasaCambio();
                    tasaCambio.setFechaTasa(fechaTasa);
                    tasaCambio.setValor(tipoCambioRFCDTO1.getValor().setScale(4, BigDecimal.ROUND_HALF_UP));
                    tasaCambio.setIdMonedaOrigen(monedaLocal);
                    tasaCambio.setIdMonedaDestino(monedaDestino);
                    logger.error("Nueva tasa Cambio tasaCambio: " + tasaCambio.toString());
                    tasaCambioRepository.save(tasaCambio);
                } else {
                    logger.error("ActualizandoValor");
                    tasaCambioExistente.setFechaTasa(fechaTasa);
                    tasaCambioExistente.setValor(tipoCambioRFCDTO1.getValor().setScale(4, BigDecimal.ROUND_HALF_UP));
                    logger.error("ActualizandoValor tasa Cambio tasaCambio: " + tasaCambioExistente.toString());
                    tasaCambioRepository.save(tasaCambioExistente);
                }
            }
        }

        tipoCambioRFCResponseDto.setListaTasaCambio(lisTasaCambio);
        logger.error("Lista Tasa Cambios" + lisTasaCambio.toString());
        return tipoCambioRFCResponseDto;
    }

    @Override
    public TipoCambioRFCResponseDto actualizarTipoCambio(String fecha) throws Exception {

        
        String url_sap = urlSap + "/sap/opu/odata/sap/YY1_EXCHANGERATE_CDS/YY1_ExchangeRate?$filter=TargetCurrency eq 'PEN'";
        //String url_sap = urlSap + "/sap/opu/odata/sap/YY1_EXCHANGERATE_CDS/YY1_ExchangeRate"
        //    + "?$filter=TargetCurrency eq 'PEN' and ExchangeRateEffectiveDate eq datetime'"
        //    + fecha + "T00:00:00'";

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
        TipoCambioRFCResponseDto tipoCambioRFCResponseDto = new TipoCambioRFCResponseDto();

        List<TipoCambioRFCDTO> lisTasaCambio = new ArrayList<TipoCambioRFCDTO>();
        //lista de monedas actual
        List<Moneda> listMoneda = this.monedaRepository.findAll();

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

                    /*TasaCambio tasaCambio = new TasaCambio();
                    Moneda moneda = new Moneda();
                    moneda.setCodigoMoneda("9");//PEN
                    Moneda monedaDestino = new Moneda();
                    monedaDestino.setCodigoMoneda("10");//USD

                    String fechaTasaString = result.getString("ExchangeRateEffectiveDate");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fechaTasa = LocalDate.parse(fechaTasaString, formatter);
                    Date fechaTasaDate = java.sql.Date.valueOf(fechaTasa);
                    tasaCambio.setFechaTasa(fechaTasaDate);

                    String exchangeRateString = result.getString("ExchangeRate");
                    BigDecimal exchangeRate = new BigDecimal(exchangeRateString);
                    tasaCambio.setValor(exchangeRate);

                    tasaCambio.setIdMonedaOrigen(moneda);
                    tasaCambio.setIdMonedaDestino(monedaDestino);*/

                    //logger.error("A - getDevuelveValores TABLE JCO: " + table.toString());
                    String exchangeRateString = result.getString("ExchangeRate");
                    BigDecimal exchangeRate = new BigDecimal(exchangeRateString);
                    
                    TipoCambioRFCDTO tipoCambioRFCDTO = new TipoCambioRFCDTO();
                    tipoCambioRFCDTO.setCodigoMonedaOrigen(result.getString("TargetCurrency"));
                    tipoCambioRFCDTO.setCodigoMonedaDestino(result.getString("SourceCurrency"));
                    tipoCambioRFCDTO.setValor(exchangeRate);
                    String fechaNodo = result.getString("ExchangeRateEffectiveDate");
                    // Convertir la fecha SAP al formato yyyyMMdd
                    String fechaFormateada = convertSapDateToString(fechaNodo);
                    tipoCambioRFCDTO.setFecha(fechaFormateada);
                    //Date fechaTasa = convertSapDate(fechaNodo);
                    //tipoCambioRFCDTO.setFecha(fechaTasa.toString());
                    Date fechaTasa = DateUtils.convertStringToDate("yyyyMMdd", tipoCambioRFCDTO.getFecha());

                    String monedaDestino = tipoCambioRFCDTO.getCodigoMonedaDestino();
                    Moneda monedaSearch = this.monedaRepository.getByCodigoMoneda(monedaDestino);

                    if (!Optional.ofNullable(monedaSearch).isPresent()) {
                        Moneda newmoneda = new Moneda();
                        newmoneda.setSigla(tipoCambioRFCDTO.getCodigoMonedaDestino());
                        newmoneda.setCodigoMoneda(tipoCambioRFCDTO.getCodigoMonedaDestino());
                        newmoneda.setTextoBreve(tipoCambioRFCDTO.getCodigoMonedaDestino());
                        newmoneda.setTextoExplicativo(tipoCambioRFCDTO.getCodigoMonedaDestino());
                        newmoneda.setTasaMoneda(new BigDecimal(0.0));
                        logger.error(newmoneda.toString());
                        this.monedaRepository.save(newmoneda);
                    }

                    lisTasaCambio.add(tipoCambioRFCDTO);
                }

                for (TipoCambioRFCDTO tipoCambioRFCDTO1 : lisTasaCambio) {
                    Moneda monedaLocal = monedaRepository.getByCodigoMoneda(tipoCambioRFCDTO1.getCodigoMonedaOrigen());
                    Moneda monedaDestino = monedaRepository.getByCodigoMoneda(tipoCambioRFCDTO1.getCodigoMonedaDestino());
                    Date fechaTasa = DateUtils.convertStringToDate("yyyyMMdd", tipoCambioRFCDTO1.getFecha());

                    if (monedaLocal != null && monedaDestino != null) {
                        TasaCambio tasaCambioExistente = tasaCambioRepository.getByFechaTasaAndIdMonedaOrigenAndIdMonedaDestino(fechaTasa, monedaLocal, monedaDestino);

                        if (!Optional.ofNullable(tasaCambioExistente).isPresent()) {
                            logger.error("Nueva tasa Cambio");
                            TasaCambio tasaCambio = new TasaCambio();
                            tasaCambio.setFechaTasa(fechaTasa);
                            tasaCambio.setValor(tipoCambioRFCDTO1.getValor().setScale(4, BigDecimal.ROUND_HALF_UP));
                            tasaCambio.setIdMonedaOrigen(monedaLocal);
                            tasaCambio.setIdMonedaDestino(monedaDestino);
                            logger.error("Nueva tasa Cambio tasaCambio: " + tasaCambio.toString());
                            tasaCambioRepository.save(tasaCambio);
                        } else {
                            logger.error("ActualizandoValor");
                            tasaCambioExistente.setFechaTasa(fechaTasa);
                            tasaCambioExistente.setValor(tipoCambioRFCDTO1.getValor().setScale(4, BigDecimal.ROUND_HALF_UP));
                            logger.error("ActualizandoValor tasa Cambio tasaCambio: " + tasaCambioExistente.toString());
                            tasaCambioRepository.save(tasaCambioExistente);
                        }
                    }
                }

            }catch (Exception e) {
                logger.error("Error: " + e.getMessage());
            }
        }else{

        }
        tipoCambioRFCResponseDto.setListaTasaCambio(lisTasaCambio);
        logger.error("Lista Tasa Cambios" + lisTasaCambio.toString());
        return tipoCambioRFCResponseDto;
    }

    public static String convertSapDateToString(String sapDate) {
        try {
            // Extraer los milisegundos del formato "/Date(-5427734400000)/"
            String timestamp = sapDate.replaceAll("/Date\\((-?\\d+)\\)/", "$1");
            long milliseconds = Long.parseLong(timestamp);

            // Convertir los milisegundos a un objeto Date
            Date date = new Date(milliseconds);

            // Formatear la fecha en el formato yyyyMMdd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(date);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir la fecha SAP: " + sapDate, e);
        }
    }


    private String armarTrama(String fecha) //20.01.2020
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
            "   <soapenv:Header/>\n" +
            "   <soapenv:Body>\n" +
            "      <urn:ZMM_CONSULTA_TIPO_CAMBIO>\n" +
            "         <!--Optional:-->\n" +
            "         <PI_DATE>" + fecha + "</PI_DATE>\n" +
            "         <T_TIP_CAMBIO>\n" +
            "            <!--Zero or more repetitions:-->\n" +
            "            <item>\n" +
            "               <PI_DATE></PI_DATE>\n" +
            "               <FOREIGN_AMOUNT></FOREIGN_AMOUNT>\n" +
            "               <FOREIGN_CURRENCY></FOREIGN_CURRENCY>\n" +
            "               <LOCAL_CURRENCY></LOCAL_CURRENCY>\n" +
            "               <EXCHANGE_RATE></EXCHANGE_RATE>\n" +
            "            </item>\n" +
            "         </T_TIP_CAMBIO>\n" +
            "      </urn:ZMM_CONSULTA_TIPO_CAMBIO>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

        return tramaXML;

    }

}
