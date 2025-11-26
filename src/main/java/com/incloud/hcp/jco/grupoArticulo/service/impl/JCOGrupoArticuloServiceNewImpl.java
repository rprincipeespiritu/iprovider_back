package com.incloud.hcp.jco.grupoArticulo.service.impl;

import com.incloud.hcp.domain.TempRubroBien;
import com.incloud.hcp.jco.grupoArticulo.dto.GrupoArticuloRFCParameterBuilder;
import com.incloud.hcp.jco.grupoArticulo.dto.GrupoArticuloRFCResponseDto;
import com.incloud.hcp.jco.grupoArticulo.service.JCOGrupoArticuloServiceNew;
import com.incloud.hcp.repository.TempRubroBienRepository;
import com.incloud.hcp.sap.SapLog;
//import com.sap.conn.jco.*;
import com.incloud.hcp.util.Utils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class JCOGrupoArticuloServiceNewImpl implements JCOGrupoArticuloServiceNew {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int NRO_EJECUCIONES_RFC = 10;
    private final int NIVEL = 1;
//    private final String FUNCION_RFC = "ZMMRFC_CONSULTA_GR_ARTICULOS";
    private final String FUNCION_RFC = "ZPE_MM_CONSULTA_GR_ARTICULOS";
    private final String NOMBRE_TABLA_RFC = "TO_GR_ARTD";

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TempRubroBienRepository tempRubroBienRepository;


    @Override
    public GrupoArticuloRFCResponseDto getGrupoArticulo(String codigo) throws Exception {

        GrupoArticuloRFCResponseDto grupoArticuloRFCResponseDto = new GrupoArticuloRFCResponseDto();
        tempRubroBienRepository.deleteALLL();
        String tramaXML = armarTrama("", "");
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
        HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_CONSULTA_GR_ARTICULOS?sap-client=400");
        httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_CONSULTA_GR_ARTICULOS/ZMM_CONSULTA_GR_ARTICULOSRequest");
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
        grupoArticuloRFCResponseDto.setSapLog(sapLog);
        logger.error("02b - getGrupoArticulo - sapLog: " + sapLog.toString());

        /* Recorriendo valores obtenidos del RFC */

        List<TempRubroBien> rubroBienList = new ArrayList<TempRubroBien>();
        NodeList table = doc.getElementsByTagName("T_GR_ARTD").item(0).getChildNodes();
        if (table != null) {

            for(int i = 0; i < table.getLength(); i++) {
                Node posicion = table.item(i);
                Element elemento = (Element) posicion;
                String matkl= Utils.getValueNodo(elemento, "MATKL");
                if(!matkl.equals("")){

                //logger.error("A - getDevuelveValores TABLE JCO: " + table.toString());
                TempRubroBien tempRubroBien = new TempRubroBien();
                tempRubroBien.setCodigoSap( Utils.getValueNodo(elemento, "MATKL"));
                String wgbez = Utils.getValueNodo(elemento, "WGBEZ");
                tempRubroBien.setDescripcion( wgbez != "" ? Utils.getValueNodo(elemento, "WGBEZ") : "vacio" );
                tempRubroBien.setNivel(NIVEL);
                rubroBienList.add(tempRubroBien);
                }
                //logger.error("bean" + tempRubroBien);
            }
        }

        List<TempRubroBien> rubroBienListFinal = tempRubroBienRepository.saveAll(rubroBienList);
        logger.error("Lista Final " + rubroBienListFinal.toString());
        grupoArticuloRFCResponseDto.setListaGrupoArticulo(rubroBienList);

        logger.error("04 - getSolpedResponseByCodigo solicitudPedidoRFCResponseDto: " + grupoArticuloRFCResponseDto.toString());
        return grupoArticuloRFCResponseDto;
    }


    private String armarTrama(String fechaInicio, String fechaFin)
    {
        String tramaXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_CONSULTA_GR_ARTICULOS>\n" +
                "         <!--Optional:-->\n" +
                "         <PI_MATKL></PI_MATKL>\n" +
                "         <T_GR_ARTD>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <MATKL></MATKL>\n" +
                "               <SPRAS></SPRAS>\n" +
                "               <WGBEZ></WGBEZ>\n" +
                "            </item>\n" +
                "         </T_GR_ARTD>\n" +
                "      </urn:ZMM_CONSULTA_GR_ARTICULOS>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return tramaXML;

    }
}
