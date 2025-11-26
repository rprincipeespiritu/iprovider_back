package com.incloud.hcp.jco.centro.service.impl;


import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.jco.centro.dto.CentroRFCDto;
import com.incloud.hcp.jco.centro.dto.CentroRFCParameterBuilder;
import com.incloud.hcp.jco.centro.dto.CentroRFCResponseDto;
import com.incloud.hcp.jco.centro.service.JCOCentroServiceNew;
import com.incloud.hcp.repository.CentroAlmacenRepository;
import com.incloud.hcp.sap.SapLog;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;
import com.sap.cloud.sdk.cloudplatform.connectivity.*;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOCentroServiceNewImpl implements JCOCentroServiceNew {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    private final int NIVEL = 1;
//    private final String FUNCION_RFC = "ZMMRFC_LISTA_CENTRO";
    private final String FUNCION_RFC = "ZPE_MM_LISTA_CENTRO";
    private final String NOMBRE_TABLA_RFC = "TO_CENTRO";

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CentroAlmacenRepository centroAlmacenRepository;

    @Override
    public CentroRFCResponseDto actualizarCentro(String sociedad) throws Exception {

        CentroRFCResponseDto centroRFCResponseDto = new CentroRFCResponseDto();

        /* Ejecucion invocacion a RFC*/
//        JCoDestination destination = JCoDestinationManager.getDestination(destinationProfit);
//        JCoRepository repo = destination.getRepository();
//        logger.error("01A - getCentroAlmacen");
//        JCoFunction jCoFunction = repo.getFunction(FUNCION_RFC);
//        logger.error("01B - getCentroAlmacen");
//
//        logger.error("parametro ingresado" + sociedad);
//        CentroRFCParameterBuilder.build(
//                jCoFunction,
//                sociedad
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
//
//
//        /* Obteniendo los valores obtenidos del RFC*/
//        logger.error("02 - getCentro - FIN RFC");
//        JCoParameterList tableParameterList = jCoFunction.getTableParameterList();
//        JCoParameterList result = jCoFunction.getExportParameterList();

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
        String url = urlbase + "/sap/bc/srt/rfc/sap/zws_lista_centro/100/zws_lista_centro/zws_lista_centro";

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
        post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_LISTA_CENTRO:ZPE_MM_LISTA_CENTRORequest");
        post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_LISTA_CENTRO:ZPE_MM_LISTA_CENTRORequest");
        post.setHeader("Content-Type", "text/xml;charset=UTF-8");
        post.setHeader("Accept-Encoding", "gzip,deflate");

        post.setEntity(strEntity);

        logger.info("RFC: Trama Entity" + strEntity);

        HttpResponse response4 = client.execute(post);
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
        centroRFCResponseDto.setSapLog(sapLog);
        logger.error("02b - getCentro - sapLog: " + sapLog.toString());


        /* Recorriendo valores obtenidos del RFC*/

        List<CentroRFCDto> centroAlmacenRFCDtoList = new ArrayList<CentroRFCDto>();
        NodeList centros = doc.getElementsByTagName("TO_CENTRO").item(0).getChildNodes();

        for(int i = 0; i < centros.getLength(); i++) {

            Node centro = centros.item(i);
            Element elemento = (Element) centro;
            String Centrox = Utils.getValueNodo(elemento, "WERKS");

            if(Centrox.equals(""))
            {
                continue;
            }

            CentroRFCDto centroAlmacenRFCDto = new CentroRFCDto();
            centroAlmacenRFCDto.setCentro(Utils.getValueNodo(elemento, "WERKS"));
            centroAlmacenRFCDto.setPoblacion(Utils.getValueNodo(elemento, "ORT01"));
            centroAlmacenRFCDto.setDistrito(Utils.getValueNodo(elemento, "CITY2"));
            centroAlmacenRFCDto.setDireccion(Utils.getValueNodo(elemento, "STRAS"));
            centroAlmacenRFCDto.setDescripcion(Utils.getValueNodo(elemento, "NAME1"));

            CentroAlmacen centroAlmacen = this.centroAlmacenRepository.findByCodigoSapAndNivel(
                    centroAlmacenRFCDto.getCentro(), 1
            );
            if (!Optional.ofNullable(centroAlmacen).isPresent()) {
                centroAlmacen = new CentroAlmacen();
            }
            centroAlmacen.setCodigoSap(centroAlmacenRFCDto.getCentro());
            centroAlmacen.setNivel(1);
            centroAlmacen.setDenominacion(centroAlmacenRFCDto.getDescripcion());
            centroAlmacen.setDireccion(centroAlmacenRFCDto.getDireccion());
            centroAlmacen.setPoblacion(centroAlmacenRFCDto.getPoblacion());
            centroAlmacen.setDistrito(centroAlmacenRFCDto.getDistrito());
            this.centroAlmacenRepository.saveAndFlush(centroAlmacen);

            centroAlmacenRFCDtoList.add(centroAlmacenRFCDto);
        }

        centroRFCResponseDto.setListaCentro(centroAlmacenRFCDtoList);
        if (centroAlmacenRFCDtoList != null && centroAlmacenRFCDtoList.size() > 0) {
            centroRFCResponseDto.setContador(centroAlmacenRFCDtoList.size());
        }

        //logger.error("04 - getSolpedResponseByCodigo solicitudPedidoRFCResponseDto: " + grupoArticuloRFCResponseDto.toString());
        return centroRFCResponseDto;
    }

    private String armarTrama()
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZPE_MM_LISTA_CENTRO>\n" +
                "         <PI_BUKRS></PI_BUKRS>\n" +
                "         <TO_CENTRO>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <WERKS></WERKS>\n" +
                "               <NAME1></NAME1>\n" +
                "               <STRAS></STRAS>\n" +
                "               <CITY2></CITY2>\n" +
                "               <ORT01></ORT01>\n" +
                "            </item>\n" +
                "         </TO_CENTRO>\n" +
                "      </urn:ZPE_MM_LISTA_CENTRO>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }
}
