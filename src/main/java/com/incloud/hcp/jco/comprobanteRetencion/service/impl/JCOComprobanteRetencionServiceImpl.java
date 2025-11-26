package com.incloud.hcp.jco.comprobanteRetencion.service.impl;

import com.incloud.hcp.domain.Sociedad;
import com.incloud.hcp.enums.ComprobanteRetencionEstadoEnum;
import com.incloud.hcp.enums.ComprobanteTipoEnum;
import com.incloud.hcp.jco.comprobanteRetencion.dto.*;
import com.incloud.hcp.jco.comprobanteRetencion.service.JCOComprobanteRetencionService;
import com.incloud.hcp.repository.SociedadRepository;
//import com.sap.conn.jco.*;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import io.vavr.control.Try;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class JCOComprobanteRetencionServiceImpl implements JCOComprobanteRetencionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    private SociedadRepository sociedadRepository;


    @Override
    public List<ComprobanteRetencionDto> extraerComprobanteRetencionListRFC(String fechaInicio, String fechaFin, String codSociedad, String ruc, String numeroDocumentoErp, String yearEjercicio) throws Exception {
        try {
            String FUNCION_RFC = "ZPE_MM_RETENCIONES";
            List<ComprobanteRetencionDto> comprobanteRetencionDtoList = new ArrayList<>();
//INI
            String trama = this.tramaFilters1(fechaInicio, fechaFin, codSociedad, ruc, numeroDocumentoErp, yearEjercicio);

            logger.info("RFC: DESTINATION - " + destinationProfit);
            Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

            logger.info(String.valueOf(destination2.get()));
            HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
            logger.info(String.valueOf(client));

            String urlbase = String.valueOf(destination2.get().asHttp().getUri());
            logger.info("URL BASE: " + urlbase);
            String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_COMPROBANTE_RETENCION?sap-client=400";
            logger.info("RFC: URL " + url);


            logger.info("RFC: Trama: " + trama);

            final StringBuffer soap = new StringBuffer();
            soap.append("\n");
            soap.append("");
            // this is a sample data..you have create your own required data  BEGIN
            soap.append(" \n");
            soap.append(" \n");
            soap.append("" + trama);
            soap.append(" \n");
            soap.append(" \n");

            /* soap.append(body); */
            // END of MEssage Body
            soap.append("");

            logger.info(soap.toString());

            //HttpEntity strEntity = new StringEntity(soap.toString());
            HttpEntity strEntity = new StringEntity(trama, "text/xml", "UTF-8");

            HttpPost post = new HttpPost(url);
            post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_COMPROBANTE_RETENCION:ZMM_COMPROBANTE_RETENCIONRequest");
            post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_COMPROBANTE_RETENCION:ZMM_COMPROBANTE_RETENCIONRequest");
            post.setHeader("Content-Type", "text/xml;charset=UTF-8");
            post.setHeader("Accept-Encoding", "gzip,deflate");

            post.setEntity(strEntity);

            logger.info("RFC: Trama Entity: " + strEntity);

            logger.info("HTTP POST: " + post.toString());

            HttpResponse response4 = client.execute(post);
            HttpEntity respEntity = response4.getEntity();
            String result = EntityUtils.toString(respEntity);

            logger.info("Respuesta SOAP: " + result);
//INICIO
//                String body = this.tramaFilters1(fechaInicio, fechaFin, codSociedad, ruc, numeroDocumentoErp, yearEjercicio);
//
//                HttpParams httpParameters = new BasicHttpParams();
//                // Set the timeout in milliseconds until a connection is established.
//                int timeoutConnection = 15000;
//                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//                // Set the default socket timeout (SO_TIMEOUT)
//                // in milliseconds which is the timeout for waiting for data.
//                int timeoutSocket = 35000;
//                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//
//                DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
//                String encoding = Base64.getEncoder().encodeToString(("CMENDEZ"+":"+"Iprovider2022+").getBytes(StandardCharsets.   UTF_8));
//
//                HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_COMPROBANTE_RETENCION?sap-client=400");
//                httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions/ZWS_COMPROBANTE_RETENCION/ZMM_COMPROBANTE_RETENCIONRequest");
//                httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
//                httppost.setHeader("Authorization", "Basic " + encoding);
//                System.out.println("executing request" + httppost.getRequestLine());
//                //now create a soap request message as follows:
//                final StringBuffer soap = new StringBuffer();
//                soap.append("\n");
//                soap.append("");
//                // this is a sample data..you have create your own required data  BEGIN
//                soap.append(" \n");
//                soap.append(" \n");
//                soap.append("" + body);
//                soap.append(" \n");
//                soap.append(" \n");
//
//
//                HttpEntity entity = new StringEntity(soap.toString(), HTTP.UTF_8);
//                httppost.setEntity(entity);
//                HttpResponse response = httpclient.execute(httppost);// calling server
//                HttpEntity r_entity = response.getEntity();  //get response
//
//                String result = EntityUtils.toString(r_entity);
//FIN
            if (!result.contains("<soap-env:Fault>")) {
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(result)));

                logger.info("Document: " + doc);

                NodeList headerList = doc.getElementsByTagName("TO_COMPRET_HEADER").item(0).getChildNodes();
                /*NodeList itemList = doc.getElementsByTagName("TO_COMPRET_DETAIL").item(0).getChildNodes();
                NodeList totalList = doc.getElementsByTagName("TO_ZWITHTOTAL").item(0).getChildNodes();
                NodeList sociedadList = doc.getElementsByTagName("TO_T001").item(0).getChildNodes();*/

                List<SapTableCRHeaderDto> headerDtoList = Optional.ofNullable(ComprobanteRetencionExtractorMapper.getHeaderDtoList(headerList)).orElse(new ArrayList<>());
               /* List<SapTableCRItemDto> itemDtoList = Optional.ofNullable(ComprobanteRetencionExtractorMapper.getItemDtoList(itemList)).orElse(new ArrayList<>());
                List<SapTableCRTotalDto> totalDtoList = Optional.ofNullable(ComprobanteRetencionExtractorMapper.getTotalDtoList(totalList)).orElse(new ArrayList<>());
                List<SapTableCRSociedadDto> sociedadDtoList = Optional.ofNullable(ComprobanteRetencionExtractorMapper.getSociedadDtoList(sociedadList)).orElse(new ArrayList<>());*/

                logger.error("BUSQUEDA COMPROBANTES DE RETENCION EN SAP [ENCONTRADOS]: comprobantes = " + headerDtoList.size());
                //logger.error("BUSQUEDA COMPROBANTES DE RETENCION EN SAP [ENCONTRADOS]: items = " + itemDtoList.size());

                headerDtoList.forEach(h -> {
                    ComprobanteRetencionDto compRetencionDto = new ComprobanteRetencionDto();

                    compRetencionDto.setSociedad(h.getSociedad());
                    if (h.getSociedad() != null) {
                        Sociedad sociedad = sociedadRepository.getByCodigoSociedad(h.getSociedad());
                        compRetencionDto.setSociedadDescripcion(sociedad.getDireccionFiscal());
                    }
                    compRetencionDto.setNumeroDocumentoErp(h.getNumeroDocumentoErp());
                    compRetencionDto.setEjercicio(h.getEjercicio());
                    compRetencionDto.setTipoComprobante(ComprobanteTipoEnum.RETENCION.getDescripcion());
                    compRetencionDto.setFechaEmision(h.getFechaEmision());
                    compRetencionDto.setFechaContabilizacion(h.getFechaContabilizacion());
                    compRetencionDto.setSerieCorrelativo(h.getSerieCorrelativoDocumento());
                    compRetencionDto.setMonedaDocumento(h.getMonedaDocumento());
                    compRetencionDto.setMonedaLocal(h.getMonedaLocal());
                    compRetencionDto.setProveedorRuc(h.getProveedorRuc());
                    compRetencionDto.setProveedorRazonSocial(h.getProveedorRazonSocial());
                    compRetencionDto.setProveedorEmail(h.getProveedorEmail());
                    //compRetencionDto.setEstado(h.getEstado().equalsIgnoreCase(ComprobanteRetencionEstadoEnum.ANULADO.getCodigo()) ? ComprobanteRetencionEstadoEnum.ANULADO.getDescripcion() : ComprobanteRetencionEstadoEnum.ACEPTADO.getDescripcion());

                    compRetencionDto.setImporteBaseRetencion(h.getImporteBaseCalculoRetencion());
                    compRetencionDto.setImporteBaseMonedaLocal(h.getImporteBaseMonedaLocal());
                    compRetencionDto.setImporteRetencion(h.getImporteRetencion());
                    compRetencionDto.setImporteRetencionMonedaLocal(h.getImporteRetencionMonedaLocal());

                    /*
                    totalDtoList.stream()
                            .filter(t -> t.getNumeroDocumentoErp().equals(h.getNumeroDocumentoErp()) && t.getSociedad().equals(h.getSociedad()) && (t.getEjercicio().compareTo(h.getEjercicio()) == 0))
                            .findFirst()
                            .ifPresent(t -> compRetencionDto.setImporteRetencionTotalSoles(t.getImporteTotalRetencionMonedaLocal()));

                    sociedadDtoList.stream()
                            .filter(s -> s.getSociedad().equals(h.getSociedad()))
                            .findFirst()
                            .ifPresent(s -> {
                                compRetencionDto.setSociedadRazonSocial(s.getRazonSocial());
                                compRetencionDto.setSociedadDireccion1(s.getCalle() + (!s.getNumeroEdificio().equals("") ? (" - Nro " + s.getNumeroEdificio()) : ""));
                                compRetencionDto.setSociedadDireccion2(s.getPoblacion() + " - " + s.getDistrito());
                                compRetencionDto.setSociedadTelefono(s.getTelefono());
                                compRetencionDto.setSociedadRuc(s.getRuc());
                            });

                    List<SapTableCRItemDto> filteredItemDtoList = itemDtoList.stream()
                            .filter(i -> i.getNumeroDocumentoErp().equals(h.getNumeroDocumentoErp()) && i.getSociedad().equals(h.getSociedad()) && (i.getEjercicio().compareTo(h.getEjercicio()) == 0))
                            .collect(Collectors.toList());

                    BigDecimal importeNetoPagadoTotalSoles = filteredItemDtoList.stream()
                            .map(SapTableCRItemDto::getImporteNetoSoles)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);

                    compRetencionDto.setImporteNetoPagadoTotalSoles(importeNetoPagadoTotalSoles);

                    List<ComprobanteRetencionItemDto> comprobanteRetencionItemDtoList = new ArrayList<>();

                    filteredItemDtoList.forEach(i -> {
                        ComprobanteRetencionItemDto compRetencionItemDto = new ComprobanteRetencionItemDto();

                        compRetencionItemDto.setNumeroDocumentoErp(i.getNumeroDocumentoErp());
                        compRetencionItemDto.setSociedad(i.getSociedad());
                        compRetencionItemDto.setEjercicio(i.getEjercicio());
                        compRetencionItemDto.setTipoComprobante(i.getTipoComprobante().equals(ComprobanteTipoEnum.FACTURA.getCodigo()) ? ComprobanteTipoEnum.FACTURA.getDescripcion() : ""); // solo reconoce tipo FACTURA
                        compRetencionItemDto.setSerieFactura(i.getSerieFactura());
                        compRetencionItemDto.setCorrelativoFactura(i.getCorrelativoFactura());
                        compRetencionItemDto.setFechaEmision(i.getFechaEmision());
                        compRetencionItemDto.setMoneda(i.getMoneda());
                        compRetencionItemDto.setImporteTotalComprobante(i.getImporteTotalComprobante());
                        compRetencionItemDto.setFechaPago(h.getFechaEmision());
                        compRetencionItemDto.setNumeroPago("1"); // hardcode a la fuerza
                        compRetencionItemDto.setImportePago(i.getImportePago());
                        compRetencionItemDto.setImporteRetencionSoles(i.getImporteRetencionSoles());
                        compRetencionItemDto.setImporteNetoSoles(i.getImporteNetoSoles());

                        comprobanteRetencionItemDtoList.add(compRetencionItemDto);
                    });

                    compRetencionDto.setComprobanteRetencionItemDtoList(comprobanteRetencionItemDtoList);
                    */
                    comprobanteRetencionDtoList.add(compRetencionDto);
                });
            } else {
                logger.error("Error en el servidor SOAP");
            }

            return comprobanteRetencionDtoList;

        } catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }

   /* private void mapFilters(JCoFunction function, String fechaInicio, String fechaFin, String sociedad, String ruc) {
        JCoParameterList paramList = function.getImportParameterList();

        if (!fechaInicio.isEmpty() && !fechaFin.isEmpty()){
            paramList.setValue("I_DATE_INI", fechaInicio);
            paramList.setValue("I_DATE_FIN", fechaFin);
        }

        if (sociedad != null && !sociedad.isEmpty()){
            paramList.setValue("I_BUKRS", sociedad);
        }

        if (ruc != null && !ruc.isEmpty()){
            paramList.setValue("I_STCD1", ruc);
        }

        JCoTable jcoTableWITHT = paramList.getTable("IT_RGE_WITHT");
        jcoTableWITHT.appendRow();
        jcoTableWITHT.setRow(0);
        jcoTableWITHT.setValue("SIGN", "I");
        jcoTableWITHT.setValue("OPTION", "EQ");
        jcoTableWITHT.setValue("LOW", "RE");

        jcoTableWITHT.appendRow();
        jcoTableWITHT.setRow(1);
        jcoTableWITHT.setValue("SIGN", "I");
        jcoTableWITHT.setValue("OPTION", "EQ");
        jcoTableWITHT.setValue("LOW", "RM");


        JCoTable jcoTableWITHCD = paramList.getTable("IT_RGE_WITHCD");
        jcoTableWITHCD.appendRow();
        jcoTableWITHCD.setRow(0);
        jcoTableWITHCD.setValue("SIGN", "I");
        jcoTableWITHCD.setValue("OPTION", "EQ");
        jcoTableWITHCD.setValue("LOW", "R1");
    }*/

    public String tramaFilters1(String fechaInicio, String fechaFin, String codSociedad, String ruc, String numeroDocumentoErp, String yearEjercicio) throws ParserConfigurationException, TransformerException {
        String trama = "";
        trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_COMPROBANTE_RETENCION>\n" +
                "         <IT_BUKRS>\n" +
/*                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <SIGN>I</SIGN>\n" +
                "               <OPTION>EQ</OPTION>\n" +
                "               <LOW>"+codSociedad+"</LOW>\n" +
                "               <HIGHT></HIGHT>\n" +
                "            </item>\n" +*/
                "         </IT_BUKRS>\n" +
                "         <IT_RANGO_FECHA>\n" +
                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <SIGN>I</SIGN>\n" +
                "               <OPTION>BT</OPTION>\n" +
                "               <LOW>" + fechaInicio + "</LOW>\n" +
                "               <HIGH>" + fechaFin + "</HIGH>\n" +
                "            </item>\n" +
                "         </IT_RANGO_FECHA>\n" +
                "         <IT_RUC>\n" +
/*                "            <!--Zero or more repetitions:-->\n" +
                "            <item>\n" +
                "               <SIGN></SIGN>\n" +
                "               <OPTION></OPTION>\n" +
                "               <LOW>"+ruc+"</LOW>\n" +
                "               <HIGHT></HIGHT>\n" +
                "            </item>\n" +*/
                "         </IT_RUC>\n" +
                "      </urn:ZMM_COMPROBANTE_RETENCION>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        return trama;
    }

    public String tramaFilters(String fechaInicio, String fechaFin, String codSociedad, String ruc, String numeroDocumentoErp, String yearEjercicio) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementation = builder.getDOMImplementation();

        Document documento = implementation.createDocument(null, "root", null);

        documento.getDocumentElement().setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        documento.getDocumentElement().setAttribute("xmlns:urn", "urn:sap-com:document:sap:rfc:functions");

        Element header = documento.createElement("soapenv:Header");
        Element body = documento.createElement("soapenv:Body");
        Element functionRfc = documento.createElement("urn:ZMM_COMPROBANTE_RETENCION");

        Element IT_RGE_BLART = documento.createElement("IT_RGE_BLART");
        Element items_IT_RGE_BLART = documento.createElement("item");

        Element SIGN_BLART = documento.createElement("SIGN");
        Text textSIGN_BLART = documento.createTextNode("I");
        SIGN_BLART.appendChild(textSIGN_BLART);
        items_IT_RGE_BLART.appendChild(SIGN_BLART);

        Element OPTION_BLART = documento.createElement("OPTION");
        Text textOPTION_BLART = documento.createTextNode("EQ");
        OPTION_BLART.appendChild(textOPTION_BLART);
        items_IT_RGE_BLART.appendChild(OPTION_BLART);

        Element LOW_BLART = documento.createElement("LOW");
        Text textLOW_BLART = documento.createTextNode("SA");
        LOW_BLART.appendChild(textLOW_BLART);
        items_IT_RGE_BLART.appendChild(LOW_BLART);

        Element HIGH_BLART = documento.createElement("HIGH");
        Text textHIGH_BLART = documento.createTextNode("");
        HIGH_BLART.appendChild(textHIGH_BLART);
        items_IT_RGE_BLART.appendChild(HIGH_BLART);

        IT_RGE_BLART.appendChild(items_IT_RGE_BLART);
        functionRfc.appendChild(IT_RGE_BLART);

        Element IT_RGE_WITHCD = documento.createElement("IT_RGE_WITHCD");
        Element items_IT_RGE_WITHCD = documento.createElement("item");

        Element SIGN_WITHCD = documento.createElement("SIGN");
        Text textSIGN_WITHCD = documento.createTextNode("I");
        SIGN_WITHCD.appendChild(textSIGN_WITHCD);
        items_IT_RGE_WITHCD.appendChild(SIGN_WITHCD);

        Element OPTION_WITHCD = documento.createElement("OPTION");
        Text textOPTION_WITHCD = documento.createTextNode("EQ");
        OPTION_WITHCD.appendChild(textOPTION_WITHCD);
        items_IT_RGE_WITHCD.appendChild(OPTION_WITHCD);

        Element LOW_WITHCD = documento.createElement("LOW");
        Text textLOW_WITHCD = documento.createTextNode("03");
        LOW_WITHCD.appendChild(textLOW_WITHCD);
        items_IT_RGE_WITHCD.appendChild(LOW_WITHCD);

        Element HIGH_WITHCD = documento.createElement("HIGH");
        Text textHIGH_WITHCD = documento.createTextNode("");
        HIGH_BLART.appendChild(textHIGH_WITHCD);
        items_IT_RGE_WITHCD.appendChild(HIGH_WITHCD);

        IT_RGE_WITHCD.appendChild(items_IT_RGE_WITHCD);
        functionRfc.appendChild(IT_RGE_WITHCD);

        Element IT_RGE_WITHT = documento.createElement("IT_RGE_WITHT");
        Element items_IT_RGE_WITHT = documento.createElement("item");

        Element SIGN_WITHT = documento.createElement("SIGN");
        Text textSIGN_WITHT = documento.createTextNode("I");
        SIGN_WITHT.appendChild(textSIGN_WITHT);
        items_IT_RGE_WITHT.appendChild(SIGN_WITHT);

        Element OPTION_WITHT = documento.createElement("OPTION");
        Text textOPTION_WITHT = documento.createTextNode("EQ");
        OPTION_WITHT.appendChild(textOPTION_WITHT);
        items_IT_RGE_WITHT.appendChild(OPTION_WITHT);

        Element LOW_WITHT = documento.createElement("LOW");
        Text textLOW_WITHT = documento.createTextNode("RP");
        LOW_WITHT.appendChild(textLOW_WITHT);
        items_IT_RGE_WITHT.appendChild(LOW_WITHT);

        Element HIGH_WITHT = documento.createElement("HIGH");
        Text textHIGH_WITHT = documento.createTextNode("");
        HIGH_WITHT.appendChild(textHIGH_WITHT);
        items_IT_RGE_WITHT.appendChild(HIGH_WITHT);

        IT_RGE_WITHT.appendChild(items_IT_RGE_WITHT);
        functionRfc.appendChild(IT_RGE_WITHT);

        Element I_BELNR = documento.createElement("I_BELNR");
        if (!numeroDocumentoErp.isEmpty()) {
            Text textI_BELNR = documento.createTextNode(numeroDocumentoErp);
            I_BELNR.appendChild(textI_BELNR);
        }
        functionRfc.appendChild(I_BELNR);

        Element I_BUKRS = documento.createElement("I_BUKRS");
        if (!codSociedad.isEmpty()) {
            Text textI_BUKRS = documento.createTextNode(codSociedad);
            I_BUKRS.appendChild(textI_BUKRS);
        }
        functionRfc.appendChild(I_BUKRS);

        Element I_DATE_FIN = documento.createElement("I_DATE_FIN");
        if (!fechaFin.isEmpty()) {
            Text textI_DATE_FIN = documento.createTextNode(fechaFin);
            I_DATE_FIN.appendChild(textI_DATE_FIN);
        }
        functionRfc.appendChild(I_DATE_FIN);

        Element I_DATE_INI = documento.createElement("I_DATE_INI");
        if (!fechaInicio.isEmpty()) {
            Text textI_DATE_INI = documento.createTextNode(fechaInicio);
            I_DATE_INI.appendChild(textI_DATE_INI);
        }
        functionRfc.appendChild(I_DATE_INI);

        Element I_GJAHR = documento.createElement("I_GJAHR");
        if (!yearEjercicio.isEmpty()) {
            Text textI_GJAHR = documento.createTextNode(yearEjercicio);
            I_GJAHR.appendChild(textI_GJAHR);
        }
        functionRfc.appendChild(I_GJAHR);

        Element I_KTOKK = documento.createElement("I_KTOKK");
        functionRfc.appendChild(I_KTOKK);

        Element I_LIFNR = documento.createElement("I_LIFNR");
        functionRfc.appendChild(I_LIFNR);

        Element I_PARVW = documento.createElement("I_PARVW");
        functionRfc.appendChild(I_PARVW);

        Element I_STCD1 = documento.createElement("I_STCD1");
        if (!ruc.isEmpty()) {
            Text textI_STCD1 = documento.createTextNode(ruc);
            I_STCD1.appendChild(textI_STCD1);
        }
        functionRfc.appendChild(I_STCD1);

        body.appendChild(functionRfc);

        documento.getDocumentElement().appendChild(header);
        documento.getDocumentElement().appendChild(body);

        Source source = new DOMSource(documento);
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);
        String trama = writer.toString();
        trama = trama.replaceAll("root", "soapenv:Envelope");
        trama = trama.substring(54);
        return trama;
    }
}