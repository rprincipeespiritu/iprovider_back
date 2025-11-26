package com.incloud.hcp.jco.comprobantePago.service.impl;

import com.incloud.hcp.domain.Sociedad;
import com.incloud.hcp.enums.ComprobantePagoEstadoEnum;
import com.incloud.hcp.jco.comprobantePago.dto.ComprobantePagoDto;
import com.incloud.hcp.jco.comprobantePago.dto.ComprobantePagoItemDto;
import com.incloud.hcp.jco.comprobantePago.service.JCOComprobantePagoService;
import com.incloud.hcp.repository.SociedadRepository;
import com.incloud.hcp.util.DateUtils;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.io.StringWriter;


@Service
public class JCOComprobantePagoServiceImpl implements JCOComprobantePagoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SociedadRepository sociedadRepository;

    @Value("${destination.rfc.profit}")
    private String destinationProfit;

    @Autowired
    public JCOComprobantePagoServiceImpl(SociedadRepository sociedadRepository) {
        this.sociedadRepository = sociedadRepository;
    }

    @Override
    public List<ComprobantePagoDto> extraerComprobantePagoListRFC(String fechaInicio, String fechaFin, String ruc) throws Exception {
        try {
//            String FUNCION_RFC = "ZPE_MM_COMPROBANTE_PAGO";
           List<ComprobantePagoDto> comprobantePagoDtoList = new ArrayList<>();
//
//
            String trama = this.tramaFilters1(fechaInicio, fechaFin, ruc);
//I
            logger.info("RFC: DESTINATION - " + destinationProfit);
            Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

            logger.info(String.valueOf(destination2.get()));
            HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
            logger.info(String.valueOf(client));

            String urlbase = String.valueOf(destination2.get().asHttp().getUri());
            logger.info("URL BASE: " + urlbase);
            String url = urlbase + "/sap/bc/srt/rfc/sap/ZWS_COMPROBANTE_PAGO?sap-client=400";
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

            soap.append("");

            logger.info(soap.toString());

            HttpEntity strEntity = new StringEntity(trama, "text/xml", "UTF-8");

            HttpPost post = new HttpPost(url);
            post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_COMPROBANTE_PAGO:ZMM_COMPROBANTE_PAGORequest");
            post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_COMPROBANTE_PAGO:ZMM_COMPROBANTE_PAGORequest");
            post.setHeader("Content-Type", "text/xml;charset=UTF-8");
            post.setHeader("Accept-Encoding", "gzip,deflate");

            post.setEntity(strEntity);

            logger.info("RFC: Trama Entity: " + strEntity);

            logger.info("HTTP POS: " + post.toString());

            HttpResponse response4 = client.execute(post);
            HttpEntity respEntity = response4.getEntity();
            String result = EntityUtils.toString(respEntity);

            logger.info("HTTP RESULT: " + result);
//F

//I
/*            String body = this.tramaFilters1(fechaInicio, fechaFin, ruc);

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            int timeoutConnection = 360000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 360000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
            String encoding = Base64.getEncoder().encodeToString(("CMENDEZ"+":"+"Iprovider2022+").getBytes(StandardCharsets.   UTF_8));

            HttpPost httppost = new HttpPost("http://191.1.64.127:8002/sap/bc/srt/rfc/sap/ZWS_COMPROBANTE_PAGO?sap-client=400");
            httppost.setHeader("soapaction", "urn:sap-com:document:sap:rfc:functions:ZWS_COMPROBANTE_PAGO:ZMM_COMPROBANTE_PAGORequest");
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
            soap.append("" + body);
            soap.append(" \n");
            soap.append(" \n");


            HttpEntity entity = new StringEntity(soap.toString(), HTTP.UTF_8);
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);// calling server
            HttpEntity r_entity = response.getEntity();  //get response

            String result = EntityUtils.toString(r_entity);*/
//F
            logger.info("Respuesta SOAP: " + result);

            if (!result.contains("<soap-env:Fault>")) {

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                domFactory.setNamespaceAware(true);
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(result)));

                logger.info("Document: " + doc);

                /*Traemos los detalles y guardamos*/
                List<ComprobantePagoItemDto> comprobantePagoItemDtoList = new ArrayList<>();
                NodeList comprobantesPagoDet = doc.getElementsByTagName("OT_DD_INV_REC").item(0).getChildNodes();

                logger.info("comprobantesPagoDet x2: " + comprobantesPagoDet.toString());
                if (comprobantesPagoDet != null) {

                    for (int i = 0; i < comprobantesPagoDet.getLength(); i++) {
                        if (comprobantesPagoDet.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elemento = (Element) comprobantesPagoDet.item(i);

                            logger.info("elemento x2: " + elemento.toString());
                            logger.info("elemento x2: " + Utils.getValueNodo(elemento, "BELNR"));

                            ComprobantePagoItemDto comprobantePagoItem = new ComprobantePagoItemDto();
                            comprobantePagoItem.setCodigoDocumentoSap(Utils.getValueNodo(elemento, "BELNR"));
                            comprobantePagoItem.setEjercicio(Utils.getValueNodo(elemento, "GJAHR"));
                            comprobantePagoItem.setNumeroItem(Utils.getValueNodoNum(elemento, "BUZEI").intValue());
                            comprobantePagoItem.setNumeroOrdenCompra(Utils.getValueNodo(elemento, "EBELN"));
                            comprobantePagoItem.setNumeroPosicion(Utils.getValueNodo(elemento, "EBELP"));
                            comprobantePagoItem.setDescripcionProducto(Utils.getValueNodo(elemento, "TXZ01"));
                            comprobantePagoItem.setCantidad(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "MENGE")).setScale(4, RoundingMode.HALF_UP));
                            comprobantePagoItem.setPrecioUnitario(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "NETPR")).setScale(4, RoundingMode.HALF_UP));
                            comprobantePagoItem.setImporteItem(BigDecimal.valueOf(Utils.getValueNodoNum(elemento, "WRBTR")).setScale(4, RoundingMode.HALF_UP));


                            logger.info("elemento x2: " + comprobantePagoItem.toString());
                            comprobantePagoItemDtoList.add(comprobantePagoItem);

                        }
                    }
                }



                NodeList comprobantesPago = doc.getElementsByTagName("OT_DH_INV_REC").item(0).getChildNodes();
                logger.info("Comprobantes: " + comprobantesPago);
                if (comprobantesPago != null) {
                    for (int i = 0; i < comprobantesPago.getLength(); i++) {
                        if (comprobantesPago.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element elemento = (Element) comprobantesPago.item(i);

                           /* if(!numeroComprobantePago.isEmpty()){
                                String numComp = Utils.getValueNodo(elemento, "XBLNR");
                                if(!numeroComprobantePago.equals(numComp)) continue;
                            }*/
                            ComprobantePagoDto comprobantePago = new ComprobantePagoDto();

                            comprobantePago.setRazonSocial(Utils.getValueNodo(elemento, "RAZONSOCIAL"));
                            comprobantePago.setCodigoDocumentoSap(Utils.getValueNodo(elemento, "BELNR"));
                            comprobantePago.setEjercicio(Utils.getValueNodo(elemento, "GJAHR"));
                            comprobantePago.setProveedorRuc(Utils.getValueNodo(elemento, "STCD1"));
                            comprobantePago.setNumeroComprobantePago(Utils.getValueNodo(elemento, "XBLNR"));

                            comprobantePago.setFechaContabilizacion(Utils.getDateFromString(Utils.getValueNodo(elemento, "BUDAT")).orElse(null));
                            comprobantePago.setFechaEmision(Utils.getDateFromString(Utils.getValueNodo(elemento, "BLDAT")).orElse(null));
                            comprobantePago.setFechaRegistroDocContable(Utils.getDateFromString(Utils.getValueNodo(elemento, "CPUDT")).orElse(null));
                            comprobantePago.setFormaPago(Integer.parseInt(Utils.getValueNodo(elemento, "DZBD1T")));

                            comprobantePago.setCodigoMoneda(Utils.getValueNodo(elemento, "WAERS"));
                            comprobantePago.setSubTotal(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WMWST1"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                            comprobantePago.setIgv(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "WSKTO"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));

                            comprobantePago.setTotal(Optional.ofNullable(new BigDecimal(Utils.getValueNodo(elemento, "RMWWR"))).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
                            comprobantePago.setObservaciones(Utils.getValueNodo(elemento, "BKTXT"));

                            comprobantePago.setFechaVencimiento(Utils.getDateFromString(Utils.getValueNodo(elemento, "FDTAG")).orElse(null));


                            comprobantePago.setNumeroDocumentoCompensacion(Utils.getValueNodo(elemento, "AUGBL"));
                            comprobantePago.setCodigoSociedad(Utils.getValueNodo(elemento, "BUKRS_REF"));
                            Sociedad sociedad = sociedadRepository.getByCodigoSociedad(comprobantePago.getCodigoSociedad());
                            comprobantePago.setSociedad(sociedad);

                            comprobantePago.setNumeroDocumentoContable(Utils.getValueNodo(elemento, "BELNR_REF"));

                            comprobantePago.setFechaRealPago(Utils.getDateFromString(Utils.getValueNodo(elemento, "AUGDT")).orElse(null));
                            comprobantePago.setFechaBase(Utils.getDateFromString(Utils.getValueNodo(elemento, "ZFBDT")).orElse(null));
                            comprobantePago.setFechaPosiblePago(Utils.getDateFromString(Utils.getValueNodo(elemento, "FDPAGO")).orElse(null));


                            comprobantePago.setEstado(Utils.getValueNodo(elemento, "STATUS"));

                            comprobantePago.setViaPago(Utils.getValueNodo(elemento, "ZLSCH"));

                            comprobantePago.setTipoPago(Utils.getValueNodo(elemento, "TEXT1"));
                            comprobantePago.setBanco(Utils.getValueNodo(elemento, "BANKA"));
                            comprobantePago.setFechaPagoNuevo(Utils.getDateFromString(Utils.getValueNodo(elemento, "FDPAGO")).orElse(null));


                            comprobantePago.setComprobantePagoItemDtoList(comprobantePagoItemDtoList.stream().filter(p -> p.getCodigoDocumentoSap().equals(Utils.getValueNodo(elemento, "BELNR"))).collect(Collectors.toList()));

                            comprobantePagoDtoList.add(comprobantePago);
                        }
                    }
                }
            } else {
                logger.error("Error en el servidor SOAP");
            }
            return comprobantePagoDtoList;
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }


    @Override
    public ComprobantePagoDto setFactoryRFC(String nroComprobante, String codSociedad, String Anio) throws Exception {
        try {

            ComprobantePagoDto comprobantePagoDto = new ComprobantePagoDto();

            logger.info("RFC: DESTINATION - " + destinationProfit);
            Try<Destination> destination2 = DestinationAccessor.tryGetDestination(destinationProfit);

            logger.info(String.valueOf(destination2.get()));
            HttpClient client = HttpClientAccessor.getHttpClient(destination2.get().asHttp().decorate(DefaultErpHttpDestination::new));
            logger.info(String.valueOf(client));
            /*
            String hostx = "connectivityproxy.internal.cf.us10.hana.ondemand.com";
            Integer portx = 20003;

            DefaultHttpClientFactory customFactory = new DefaultHttpClientFactory() {
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
            logger.info("URL BASE: " + urlbase);
            String url = urlbase + "/sap/bc/srt/rfc/sap/zws_pago_factory/100/zws_pago_factory/zws_pago_factory";
            logger.info("RFC: URL " + url);

            String trama = this.tramaFactoring(nroComprobante, codSociedad, Anio);

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
            post.setHeader("soap_action", "urn:sap-com:document:sap:rfc:functions:ZWS_PAGO_FACTORY:ZMM_PAGO_FACTORYRequest");
            post.setHeader("SOAPAction", "urn:sap-com:document:sap:rfc:functions:ZWS_PAGO_FACTORY:ZMM_PAGO_FACTORYRequest");
            post.setHeader("Content-Type", "text/xml;charset=UTF-8");
            post.setHeader("Accept-Encoding", "gzip,deflate");

            post.setEntity(strEntity);

            logger.info("RFC: Trama Entity: " + strEntity);

            logger.info("HTTP POS: " + post.toString());

            HttpResponse response4 = client.execute(post);
            HttpEntity respEntity = response4.getEntity();
            String result = EntityUtils.toString(respEntity);

            logger.info("Respuesta SOAP: " + result);

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(result)));

            logger.info("Document: " + doc);

            String codigo = doc.getElementsByTagName("O_CODE").item(0).getChildNodes().item(0).getNodeValue();
            String mensaje = doc.getElementsByTagName("O_MENSAJE").item(0).getChildNodes().item(0).getNodeValue();

            comprobantePagoDto.setCodigoRpta(codigo);
            comprobantePagoDto.setMensajeRpta(mensaje);

            return comprobantePagoDto;
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            throw new Exception(e);
        }
    }

    public String tramaFilters1(String fechaInicio, String fechaFin,String ruc) throws ParserConfigurationException, TransformerException {
        String trama = "";
        if(!ruc.isEmpty()) {

            trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <urn:ZMM_COMPROBANTE_PAGO>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_RGE_BLART>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </IT_RGE_BLART>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_RGE_BLDAT>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "            <item>\n" +
                    "               <SIGN>I</SIGN>\n" +
                    "               <OPTION>BT</OPTION>\n" +
                    "               <LOW>"+ fechaInicio +"</LOW>\n" +
                    "               <HIGH>"+ fechaFin +"</HIGH>\n" +
                    "            </item>\n" +
                    "         </IT_RGE_BLDAT>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_RGE_BUKRS>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </IT_RGE_BUKRS>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_STCD1>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "            <item>\n" +
                    "               <STCD1>" + Optional.ofNullable(ruc).orElse("") + "</STCD1>\n" +
                    "               <BUKRS></BUKRS>\n" +
                    "               <LIFNR></LIFNR>\n" +
                    "               <NAME1></NAME1>\n" +
                    "               <NAME2></NAME2>\n" +
                    "            </item>\n" +
                    "         </IT_STCD1>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_FILWITHT></I_FILWITHT>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_FILWT_WITHCD></I_FILWT_WITHCD>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_KTOKK></I_KTOKK>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_PARVW></I_PARVW>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_SEP></I_SEP>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_XBLNR></I_XBLNR>\n" +
                    "         <!--Optional:-->\n" +
                    "         <OT_DC_INV_REC>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </OT_DC_INV_REC>\n" +
                    "         <!--Optional:-->\n" +
                    "         <OT_DD_INV_REC>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </OT_DD_INV_REC>\n" +
                    "         <!--Optional:-->\n" +
                    "         <OT_DH_INV_REC>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </OT_DH_INV_REC>\n" +
                    "      </urn:ZMM_COMPROBANTE_PAGO>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
        }else{
            trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <urn:ZMM_COMPROBANTE_PAGO>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_RGE_BLART>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </IT_RGE_BLART>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_RGE_BLDAT>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "            <item>\n" +
                    "               <SIGN>I</SIGN>\n" +
                    "               <OPTION>BT</OPTION>\n" +
                    "               <LOW>"+ fechaInicio +"</LOW>\n" +
                    "               <HIGH>"+ fechaFin +"</HIGH>\n" +
                    "            </item>\n" +
                    "         </IT_RGE_BLDAT>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_RGE_BUKRS>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </IT_RGE_BUKRS>\n" +
                    "         <!--Optional:-->\n" +
                    "         <IT_STCD1>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </IT_STCD1>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_FILWITHT></I_FILWITHT>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_FILWT_WITHCD></I_FILWT_WITHCD>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_KTOKK></I_KTOKK>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_PARVW></I_PARVW>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_SEP></I_SEP>\n" +
                    "         <!--Optional:-->\n" +
                    "         <I_XBLNR></I_XBLNR>\n" +
                    "         <!--Optional:-->\n" +
                    "         <OT_DC_INV_REC>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </OT_DC_INV_REC>\n" +
                    "         <!--Optional:-->\n" +
                    "         <OT_DD_INV_REC>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </OT_DD_INV_REC>\n" +
                    "         <!--Optional:-->\n" +
                    "         <OT_DH_INV_REC>\n" +
                    "            <!--Zero or more repetitions:-->\n" +
                    "         </OT_DH_INV_REC>\n" +
                    "      </urn:ZMM_COMPROBANTE_PAGO>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
        }

        return trama;
    }
    public String tramaFilters(String fechaInicio, String fechaFin, String numeroComprobantePago, String ruc, String codigoProv, String codSociedad) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementation = builder.getDOMImplementation();

        Document documento = implementation.createDocument(null, "root", null);

        documento.getDocumentElement().setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        documento.getDocumentElement().setAttribute("xmlns:urn", "urn:sap-com:document:sap:rfc:functions");

        Element header = documento.createElement("soapenv:Header");
        Element body = documento.createElement("soapenv:Body");
        Element functionRfc = documento.createElement("urn:ZMM_COMPROBANTE_PAGO");

        Element IT_RGE_BLART = documento.createElement("IT_RGE_BLART");

        Element IT_RGE_BLDAT = documento.createElement("IT_RGE_BLDAT");
        Element items_IT_RGE_BLDAT = documento.createElement("item");

        Element IT_RGE_BUKRS = documento.createElement("IT_RGE_BUKRS");
        Element items_IT_RGE_BUKRS = documento.createElement("item");

        Element IT_STCD1 = documento.createElement("IT_STCD1");
        Element items_IT_STCD1 = documento.createElement("item");

        Element I_FILWITHT = documento.createElement("I_FILWITHT");

        Element I_FILWT_WITHCD = documento.createElement("I_FILWT_WITHCD");

        Element I_KTOKK = documento.createElement("I_KTOKK");

        Element I_PARVW = documento.createElement("I_PARVW");

        Element I_SEP = documento.createElement("I_SEP");

        Element I_XBLNR = documento.createElement("I_XBLNR");

        Element OT_DC_INV_REC = documento.createElement("OT_DC_INV_REC");
        Element items_OT_DC_INV_REC = documento.createElement("item");

        Element OT_DD_INV_REC = documento.createElement("OT_DD_INV_REC");
        Element items_OT_DD_INV_REC = documento.createElement("item");

        Element OT_DH_INV_REC = documento.createElement("OT_DH_INV_REC");
        Element items_OT_DH_INV_REC = documento.createElement("item");

        functionRfc.appendChild(IT_RGE_BLART);

        String sign = "I";
        String option = "EQ";
        String fechaInicioLocal = "";
        String fechaFinLocal = "";

        if (!fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
            fechaInicioLocal = fechaInicio;
            if (!fechaInicio.equals(fechaFin)) {
                option = "BT";
                fechaFinLocal = fechaFin;
            }
        }

        Element SIGN_BLDAT = documento.createElement("SIGN");
        Text textSIGN_BLDAT = documento.createTextNode(sign);
        SIGN_BLDAT.appendChild(textSIGN_BLDAT);
        items_IT_RGE_BLDAT.appendChild(SIGN_BLDAT);

        Element OPTION_BLDAT = documento.createElement("OPTION");
        Text textOPTION_BLDAT = documento.createTextNode(option);
        OPTION_BLDAT.appendChild(textOPTION_BLDAT);
        items_IT_RGE_BLDAT.appendChild(OPTION_BLDAT);

        Element LOW_BLDAT = documento.createElement("LOW");
        Text textLOW_BLDAT = documento.createTextNode(fechaInicioLocal);
        LOW_BLDAT.appendChild(textLOW_BLDAT);
        items_IT_RGE_BLDAT.appendChild(LOW_BLDAT);

        Element HIGH_BLDAT = documento.createElement("HIGH");
        Text textHIGH_BLDAT = documento.createTextNode(fechaFinLocal);
        HIGH_BLDAT.appendChild(textHIGH_BLDAT);
        items_IT_RGE_BLDAT.appendChild(HIGH_BLDAT);

        IT_RGE_BLDAT.appendChild(items_IT_RGE_BLDAT);
        functionRfc.appendChild(IT_RGE_BLDAT);

        if (!ruc.isEmpty() && !codigoProv.isEmpty() && !codSociedad.isEmpty()) {

            Element SIGN = documento.createElement("SIGN");
            Text textSIGN = documento.createTextNode("I");
            SIGN.appendChild(textSIGN);
            items_IT_RGE_BUKRS.appendChild(SIGN);

            Element OPTION = documento.createElement("OPTION");
            Text textOPTION = documento.createTextNode("EQ");
            OPTION.appendChild(textOPTION);
            items_IT_RGE_BUKRS.appendChild(OPTION);

            Element LOW = documento.createElement("LOW");
            Text textLOW = documento.createTextNode(codSociedad);
            LOW.appendChild(textLOW);
            items_IT_RGE_BUKRS.appendChild(LOW);

            Element HIGH = documento.createElement("HIGH");
            Text textHIGH = documento.createTextNode("");
            HIGH.appendChild(textHIGH);
            items_IT_RGE_BUKRS.appendChild(HIGH);

            IT_RGE_BUKRS.appendChild(items_IT_RGE_BUKRS);

            Element STCD1 = documento.createElement("STCD1");
            Text textSTCD1 = documento.createTextNode(ruc);
            STCD1.appendChild(textSTCD1);
            items_IT_STCD1.appendChild(STCD1);

            Element BUKRS = documento.createElement("BUKRS");
            Text textBUKRS = documento.createTextNode(codSociedad);
            BUKRS.appendChild(textBUKRS);
            items_IT_STCD1.appendChild(BUKRS);

            Element LIFNR = documento.createElement("LIFNR");
            Text textLIFNR = documento.createTextNode(codigoProv);
            LIFNR.appendChild(textLIFNR);
            items_IT_STCD1.appendChild(LIFNR);

            Element NAME1 = documento.createElement("NAME1");
            Text textNAME1 = documento.createTextNode("");
            NAME1.appendChild(textNAME1);
            items_IT_STCD1.appendChild(NAME1);

            Element NAME2 = documento.createElement("NAME2");
            Text textNAME2 = documento.createTextNode("");
            NAME2.appendChild(textNAME2);
            items_IT_STCD1.appendChild(NAME2);

            IT_STCD1.appendChild(items_IT_STCD1);
        }

        functionRfc.appendChild(IT_RGE_BUKRS);

        functionRfc.appendChild(IT_STCD1);

        functionRfc.appendChild(I_FILWITHT);

        functionRfc.appendChild(I_FILWT_WITHCD);

        functionRfc.appendChild(I_KTOKK);

        functionRfc.appendChild(I_PARVW);

        functionRfc.appendChild(I_SEP);

        functionRfc.appendChild(I_XBLNR);

        Element BUKRS_1 = documento.createElement("BUKRS");
        items_OT_DC_INV_REC.appendChild(BUKRS_1);

        Element BELNR_1 = documento.createElement("BELNR");
        items_OT_DC_INV_REC.appendChild(BELNR_1);

        Element GJAHR_1 = documento.createElement("GJAHR");
        items_OT_DC_INV_REC.appendChild(GJAHR_1);

        Element WITHT_1 = documento.createElement("WITHT");
        items_OT_DC_INV_REC.appendChild(WITHT_1);

        Element WAERS_1 = documento.createElement("WAERS");
        items_OT_DC_INV_REC.appendChild(WAERS_1);

        Element WT_QBSHH_1 = documento.createElement("WT_QBSHH");
        items_OT_DC_INV_REC.appendChild(WT_QBSHH_1);

        Element WT_WITHCD_1 = documento.createElement("WT_WITHCD");
        items_OT_DC_INV_REC.appendChild(WT_WITHCD_1);

        Element AUGBL_1 = documento.createElement("AUGBL");
        items_OT_DC_INV_REC.appendChild(AUGBL_1);

        Element DCTNUMBER_1 = documento.createElement("DCTNUMBER");
        items_OT_DC_INV_REC.appendChild(DCTNUMBER_1);

        Element WT_QSSHB_1 = documento.createElement("WT_QSSHB");
        items_OT_DC_INV_REC.appendChild(WT_QSSHB_1);

        Element BUZEI_1 = documento.createElement("BUZEI");
        items_OT_DC_INV_REC.appendChild(BUZEI_1);

        Element WT_QBSHB_1 = documento.createElement("WT_QBSHB");
        items_OT_DC_INV_REC.appendChild(WT_QBSHB_1);

        Element AUGDT_1 = documento.createElement("AUGDT");
        items_OT_DC_INV_REC.appendChild(AUGDT_1);

        Element CGJAHR_1 = documento.createElement("CGJAHR");
        items_OT_DC_INV_REC.appendChild(CGJAHR_1);

        Element WT_QSSH2_1 = documento.createElement("WT_QSSH2");
        items_OT_DC_INV_REC.appendChild(WT_QSSH2_1);

        Element INDADEL_1 = documento.createElement("INDADEL");
        items_OT_DC_INV_REC.appendChild(INDADEL_1);

        Element WT_QBSH2_1 = documento.createElement("WT_QBSH2");
        items_OT_DC_INV_REC.appendChild(WT_QBSH2_1);

        Element XBLNR_1 = documento.createElement("XBLNR");
        items_OT_DC_INV_REC.appendChild(XBLNR_1);

        Element LIFNR_1 = documento.createElement("LIFNR");
        items_OT_DC_INV_REC.appendChild(LIFNR_1);

        Element RBELNR_1 = documento.createElement("RBELNR");
        items_OT_DC_INV_REC.appendChild(RBELNR_1);

        Element LBELNR_1 = documento.createElement("LBELNR");
        items_OT_DC_INV_REC.appendChild(LBELNR_1);

        OT_DC_INV_REC.appendChild(items_OT_DC_INV_REC);
        functionRfc.appendChild(OT_DC_INV_REC);

        Element BELNR_2 = documento.createElement("BELNR");
        items_OT_DD_INV_REC.appendChild(BELNR_2);

        Element GJAHR_2 = documento.createElement("GJAHR");
        items_OT_DD_INV_REC.appendChild(GJAHR_2);

        Element BUZEI_2 = documento.createElement("BUZEI");
        items_OT_DD_INV_REC.appendChild(BUZEI_2);

        Element EBELN_2 = documento.createElement("EBELN");
        items_OT_DD_INV_REC.appendChild(EBELN_2);

        Element EBELP_2 = documento.createElement("EBELP");
        items_OT_DD_INV_REC.appendChild(EBELP_2);

        Element MATNR_2 = documento.createElement("MATNR");
        items_OT_DD_INV_REC.appendChild(MATNR_2);

        Element TXZ01_2 = documento.createElement("TXZ01");
        items_OT_DD_INV_REC.appendChild(TXZ01_2);

        Element MENGE_2 = documento.createElement("MENGE");
        items_OT_DD_INV_REC.appendChild(MENGE_2);

        Element MEINS_2 = documento.createElement("MEINS");
        items_OT_DD_INV_REC.appendChild(MEINS_2);

        Element NETPR_2 = documento.createElement("NETPR");
        items_OT_DD_INV_REC.appendChild(NETPR_2);

        Element WRBTR_2 = documento.createElement("WRBTR");
        items_OT_DD_INV_REC.appendChild(WRBTR_2);

        Element XBLNR_2 = documento.createElement("XBLNR");
        items_OT_DD_INV_REC.appendChild(XBLNR_2);

        OT_DD_INV_REC.appendChild(items_OT_DD_INV_REC);
        functionRfc.appendChild(OT_DD_INV_REC);

        Element BELNR_3 = documento.createElement("BELNR");
        items_OT_DH_INV_REC.appendChild(BELNR_3);

        Element GJAHR_3 = documento.createElement("GJAHR");
        items_OT_DH_INV_REC.appendChild(GJAHR_3);

        Element STCD1_3 = documento.createElement("STCD1");
        items_OT_DH_INV_REC.appendChild(STCD1_3);

        Element XBLNR_3 = documento.createElement("XBLNR");
        items_OT_DH_INV_REC.appendChild(XBLNR_3);

        Element STCEG_3 = documento.createElement("STCEG");
        items_OT_DH_INV_REC.appendChild(STCEG_3);

        Element BUDAT_3 = documento.createElement("BUDAT");
        items_OT_DH_INV_REC.appendChild(BUDAT_3);

        Element BLDAT_3 = documento.createElement("BLDAT");
        items_OT_DH_INV_REC.appendChild(BLDAT_3);

        Element CPUDT_3 = documento.createElement("CPUDT");
        items_OT_DH_INV_REC.appendChild(CPUDT_3);

        Element DZBD1T_3 = documento.createElement("DZBD1T");
        items_OT_DH_INV_REC.appendChild(DZBD1T_3);

        Element ZLSCH_3 = documento.createElement("ZLSCH");
        items_OT_DH_INV_REC.appendChild(ZLSCH_3);

        Element WAERS_3 = documento.createElement("WAERS");
        items_OT_DH_INV_REC.appendChild(WAERS_3);

        Element WMWST1_3 = documento.createElement("WMWST1");
        items_OT_DH_INV_REC.appendChild(WMWST1_3);

        Element WSKTO_3 = documento.createElement("WSKTO");
        items_OT_DH_INV_REC.appendChild(WSKTO_3);

        Element MWSKZ_BNK_3 = documento.createElement("MWSKZ_BNK");
        items_OT_DH_INV_REC.appendChild(MWSKZ_BNK_3);

        Element RMWWR_3 = documento.createElement("RMWWR");
        items_OT_DH_INV_REC.appendChild(RMWWR_3);

        Element BKTXT_3 = documento.createElement("BKTXT");
        items_OT_DH_INV_REC.appendChild(BKTXT_3);

        Element BLART_3 = documento.createElement("BLART");
        items_OT_DH_INV_REC.appendChild(BLART_3);

        Element FDTAG_3 = documento.createElement("FDTAG");
        items_OT_DH_INV_REC.appendChild(FDTAG_3);

        Element AWKEY_3 = documento.createElement("AWKEY");
        items_OT_DH_INV_REC.appendChild(AWKEY_3);

        Element STBLG_3 = documento.createElement("STBLG");
        items_OT_DH_INV_REC.appendChild(STBLG_3);

        Element STJAH_3 = documento.createElement("STJAH");
        items_OT_DH_INV_REC.appendChild(STJAH_3);

        Element AUGBL_3 = documento.createElement("AUGBL");
        items_OT_DH_INV_REC.appendChild(AUGBL_3);

        Element BUKRS_REF_3 = documento.createElement("BUKRS_REF");
        items_OT_DH_INV_REC.appendChild(BUKRS_REF_3);

        Element BELNR_REF_3 = documento.createElement("BELNR_REF");
        items_OT_DH_INV_REC.appendChild(BELNR_REF_3);

        Element GJAHR_REF_3 = documento.createElement("GJAHR_REF");
        items_OT_DH_INV_REC.appendChild(GJAHR_REF_3);

        Element AUGDT_3 = documento.createElement("AUGDT");
        items_OT_DH_INV_REC.appendChild(AUGDT_3);

        Element ZFBDT_3 = documento.createElement("ZFBDT");
        items_OT_DH_INV_REC.appendChild(ZFBDT_3);

        Element RAZONSOCIAL_3 = documento.createElement("RAZONSOCIAL");
        items_OT_DH_INV_REC.appendChild(RAZONSOCIAL_3);

        Element DIRECCION_3 = documento.createElement("DIRECCION");
        items_OT_DH_INV_REC.appendChild(DIRECCION_3);

        Element LIFNR_3 = documento.createElement("LIFNR");
        items_OT_DH_INV_REC.appendChild(LIFNR_3);

        Element STATUS_3 = documento.createElement("STATUS");
        items_OT_DH_INV_REC.appendChild(STATUS_3);

        Element B2MINING_3 = documento.createElement("B2MINING");
        items_OT_DH_INV_REC.appendChild(B2MINING_3);

        Element TEXT1_3 = documento.createElement("TEXT1");
        items_OT_DH_INV_REC.appendChild(TEXT1_3);

        Element BANKA_3 = documento.createElement("BANKA");
        items_OT_DH_INV_REC.appendChild(BANKA_3);

        Element FDPAGO_3 = documento.createElement("FDPAGO");
        items_OT_DH_INV_REC.appendChild(FDPAGO_3);

        OT_DH_INV_REC.appendChild(items_OT_DH_INV_REC);
        functionRfc.appendChild(OT_DH_INV_REC);

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
    public String tramaFactoring(String nroComprobante, String codSociedad, String Anio)
    {
        String trama = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:ZMM_PAGO_FACTORY>\n" +
                "         <I_BELNR>"+ nroComprobante +"</I_BELNR>\n" +
                "         <I_BUKRS>"+ codSociedad +"</I_BUKRS>\n" +
                "         <I_GJAHR>"+ Anio +"</I_GJAHR>\n" +
                "      </urn:ZMM_PAGO_FACTORY>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return trama;
    }
}